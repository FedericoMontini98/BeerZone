package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.StandardUser;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static org.neo4j.driver.Values.parameters;

import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class UserManager {
    private final MongoManager mongoManager;
    private MongoCollection<Document> users;
        // users collection include both standard users (type 0) and breweries (type 1)
    private Neo4jManager NeoDBMS;

    public UserManager() {
        mongoManager = MongoManager.getInstance();
        users = mongoManager.getCollection("users");
    }

    public void addBrewery(String username, String password, String email, String location) {
        Document doc = new Brewery(email, username, password, location).getBrewery();
        registerUser(doc);
    }

    public void addUser(String username, String password, String email, String location, int age) {
        Document doc = new StandardUser(email, username, password, age, location).getUser();
        registerUser(doc);
    }

    public void registerUser(Document userDoc) {
        try {
            users.insertOne(userDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String email) {
        try {
            users.deleteOne(eq("email", email));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUsername(String email, String newUsername) {
        UpdateResult result = users.updateOne(eq("email", email), set("username", newUsername));
    }

    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/

    /* Function used to add StandardUser Nodes in the graph, the only property that they have is Username which is common
     *  Both to reviews and User's files */
    public boolean AddStandardUser(String ID, String Username){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("CREATE (U:User{username: $Username, ID: $id})",parameters("Username",Username,"ID",ID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to add a favorite beer from the users favorites list. To identify a relationship we need the
     *  Username and the BeerID, this functionality has to be available on a specific beer only if a User hasn't
     *  it already in its favorites */
    public boolean addFavorite(String Username, String BeerID) {
        try (Session session = NeoDBMS.getDriver().session()) {
            LocalDateTime MyLDTObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj  = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String str = MyLDTObj.format(myFormatObj);
            session.run("MATCH\n" +
                            "  (B:Beer),\n" +
                            "  (U:User)\n" +
                            "WHERE U.username = $Username AND B.id = $BeerID'\n" +
                            "CREATE (U)-[F:Favorites{InDate:$Date}]->(B)\n",
                    parameters("Username", Username, "BeerID", BeerID, "Date", str));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove a favorite beer from the users favorites list. To identify a relationship we need the
     *  Username and the BeerID, this functionality has to be available on a specific beer only if a User has it in its
     *  favorites */
    public boolean removeFavorite(String Username, String BeerID){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (U:User {username: $Username})-[F:Favorites]-(B:Beer {id: $BeerID}) \n" +
                            "DELETE F",
                    parameters( "Username", Username, "BeerID", BeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /* Function used to add the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer hasn't been reviewed from this user yet to avoid multiple
     * reviews from the same user which can lead to inconsistency or fake values of the avg. score */
    public boolean addReview(String Username, String BeerID){
        try(Session session = NeoDBMS.getDriver().session()){
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            String str = formatter.format(date);
            session.run("MATCH\n" +
                            "  (B:Beer),\n" +
                            "  (U:User)\n" +
                            "WHERE U.username = $Username AND B.id = $BeerID\n" +
                            "CREATE (U)-[R:Reviewed{InDate:$Date}]->(B)\n",
                    parameters( "Username", Username, "BeerID", BeerID,"Date", str));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer has been reviewed from this user */
    public boolean removeReview(String Username, String BeerID){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (U:User {username: $Username})-[R:Reviewed]-(B:Beer {id: $BeerID}) \n" +
                            "DELETE R",
                    parameters( "Username", Username, "BeerID", BeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove a user from Neo4J graph DB */
    public boolean removeUser(String ID, String username){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (U {username: $username, ID: $ID})\n" +
                            "DETACH DELETE U",
                    parameters( "username", username));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
