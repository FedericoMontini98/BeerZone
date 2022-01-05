package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.StandardUser;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;


import org.bson.Document;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserManager {
    private static UserManager userManager;
    //private final MongoManager mongoManager;
    private final MongoCollection<Document> users;
        // users collection include both standard users (type 0) and breweries (type 1)
    private final Neo4jManager NeoDBMS;

    private UserManager() {
        NeoDBMS = Neo4jManager.getInstance();
        //mongoManager = MongoManager.getInstance();
        users = MongoManager.getInstance().getCollection("users");
    }

    public static UserManager getInstance() {
        if (userManager == null)
            userManager = new UserManager();
        return userManager;
    }

    /*
    public static Brewery getBrewery(String email) {
        Brewery b = null;
        Document userDoc = getUser(email, 1);
        if (userDoc != null && !userDoc.isEmpty())
            b = new Brewery(userDoc);
        return b;
    }

    public static StandardUser getStandardUser(String email) {
        StandardUser u = null;
        Document userDoc = getUser(email, 0);
        if (userDoc != null && !userDoc.isEmpty())
            u = new StandardUser(userDoc);
        return u;
    }

 */

    // use example: Brewery b = new Brewery(UserManager.getGeneralUser(email, type);
    public Document getUser(String email, int type) {
        return users.find(and(eq("type", type), eq("email", email))).first();
    }

    /* check if an email and/or an username already exist in the users collection */
    public boolean userExist(String email, int type, @Nullable String username) {
        Document doc = null;
        if (type == 1) {    // Brewery
            doc = users.find(eq("email", email)).first();
        } else if (type == 0) { // StandardUser
            if (username == null || username.isEmpty() || username.equals(" "))
                throw new RuntimeException("Username not valid");
            doc = users.find(or(eq("email", email), and(eq("type", type), eq("username", username)))).first();
        }

        return !(doc == null || doc.isEmpty());
    }

    public boolean addBrewery(String username, String password, String email, String location, String types) {
        try {
            if (userExist(email, 1, null))
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Document doc = new Brewery(email, username, password, location, types).getBreweryDoc(false);
        return registerUser(doc);
    }

    public boolean addUser(String username, String password, String email, String location, int age) {
        try {
            if (userExist(email, 0, username))
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Document doc = new StandardUser(email, username, password, age, location).getUserDoc();
        return registerUser(doc);
    }

    private boolean registerUser(Document userDoc) {
        try {
            users.insertOne(userDoc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int login(String email, String password) {
        int type = -1;
        try {
            Document userDoc = users.find(eq("email", email)).first();
            if (userDoc == null || userDoc.isEmpty())
                return -1;

            if (!password.equals(userDoc.getString("password")))
                return -1;

            type = userDoc.getInteger("type");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
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
            session.run("CREATE (U:User{Username: $Username, ID: $id})",parameters("Username",Username,"ID",ID));
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
    public boolean addFavorite(StandardUser user, Beer beer) {
        try (Session session = NeoDBMS.getDriver().session()) {
            //Check if user exists
            session.run("MERGE (U:User{Username: $username})" +
                    "ON CREATE" +
                    "   SET U.Username=$username, U.ID=$id ",parameters("username",user.getUsername(),"id",user.getUserID()));
            //Check if beer exists
            session.run("MERGE (B:Beer{ID: $id})" +
                    "ON CREATE" +
                    "   SET B.Name=$name, B.ID=$id ,B.Style=$style",parameters("name",beer.getBeerName(),"id",beer.getBeerID(),"style",beer.getStyle()));
            LocalDateTime MyLDTObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String str = MyLDTObj.format(myFormatObj);
            session.run("MATCH\n" +
                            "  (B:Beer{ID:$BeerID}),\n" +
                            "  (U:User{Username:$Username})\n" +
                            "CREATE (U)-[F:Favorite{date:$Date}]->(B)\n",
                    parameters("Username", user.getUsername(), "BeerID", beer.getBeerID(), "Date", str));
            //I add it to the entity instance
            user.addToFavorites(beer);
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
            session.run("MATCH (U:User {Username: $Username})-[F:Favorites]-(B:Beer {ID: $BeerID}) \n" +
                            "DELETE F",
                    parameters( "Username", Username, "BeerID", BeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove a user from Neo4J graph DB */
    public boolean removeUser(String username){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (U {Username: $username, ID: $ID})\n" +
                            "DETACH DELETE U",
                    parameters( "username", username));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to return to GUI a list of beers that the user has in its favorites */
    public void getFavorites(StandardUser user){
        try(Session session = NeoDBMS.getDriver().session()) {
            //I execute the query within the call for setFavorites to properly save them into the entity StandardUser
            user.setFavorites(session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run("MATCH (U:User{Username:$username})-[F:Favorites]->(B:Beer)" +
                                " RETURN B.ID as ID",
                        parameters("username", user.getUsername()));
                ArrayList<String> favorites = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    favorites.add(r.get("ID").asString());
                }
                return favorites;
            }));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
