package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static org.neo4j.driver.Values.parameters;

public class UserManager {
    private static UserManager userManager;
    //private final MongoManager mongoManager;
    private final MongoCollection<Document> usersCollection;
        // users collection include both standard users (type 0) and breweries (type 1)
    private final Neo4jManager NeoDBMS;

    private UserManager() {
        NeoDBMS = Neo4jManager.getInstance();
        //mongoManager = MongoManager.getInstance();
        usersCollection = MongoManager.getInstance().getCollection("users");
    }

    public static UserManager getInstance() {
        if (userManager == null)
            userManager = new UserManager();
        return userManager;
    }

    public boolean deleteUser(StandardUser user) {
        boolean result_1=deleteStandardUser(user.getEmail());
        boolean result_2=removeUser(user.getUsername());
        return (result_1&&result_2);
    }


    /* ************************************************************************************************************/
    /* *************************************  MongoDB Section  ****************************************************/
    /* ************************************************************************************************************/


    // use example: Brewery b = new Brewery(UserManager.getUser(email, type);
    public Document getUser(String email, int type) {
        return usersCollection.find(and(eq("type", type), eq("email", email))).first();
    }

    /* check if an email or a combination of an username/type=0 already exist in the users collection */
    private boolean userExist(GeneralUser user) {
        Document doc = null;
        try {
            doc = usersCollection.find(or(eq("email", user.getEmail()),
                    and(eq("type", user.getType()), eq("username", user.getUsername())))).first();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !(doc == null || doc.isEmpty());
    }

    public boolean addUser(Brewery brewery) {
        try {
            if (userExist(brewery))
                return false;
            usersCollection.insertOne(brewery.getBreweryDoc(false));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(StandardUser user) {
        try {
            if (userExist(user))
                return false;
            usersCollection.insertOne(user.getUserDoc());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GeneralUser login(String email, String password) {
        try {
            Document doc = usersCollection.find(eq("email", email)).first();
            if (doc != null) {
                if (doc.getInteger("type") == 0) {
                    //System.out.println("standard: " + doc.getString("username"));
                    return new StandardUser(doc);
                } else {
                    //System.out.println("brewery: " + doc.getString("username"));
                    return new Brewery(doc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean deleteStandardUser(String email) {
        try {
            DeleteResult deleteResult = usersCollection.deleteOne(eq("email", email));
            return deleteResult.getDeletedCount() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(StandardUser user) {
        //return updateUser(user.getUserDoc(), user.getUserID());
        try {
            UpdateResult updateResult = usersCollection.replaceOne(eq("_id", new ObjectId(user.getUserID())),
                    user.getUserDoc());
            if (updateResult.getMatchedCount() == 1)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Add a favorite both on the StandardUser ArrayList and Neo4J, call it from the GUI */
    public boolean addAFavorite(FavoriteBeer fb, StandardUser s){
        return (s.addToFavorites(fb) && addFavorite(s.getUsername(),fb));
    }

    /* Remove a favorite both on the StandardUser ArrayList and Neo4J, call it from the GUI */
    public boolean removeAFavorite(StandardUser s, FavoriteBeer fb){
        return (s.removeFromFavorites(fb) && removeFavorite(s.getUsername(),fb.getBeerID()));
    }


    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/

    /* Function used to add StandardUser Nodes in the graph, the only property that they have is Username which is common
     *  Both to reviews and User's files */
    public boolean addStandardUser(String Username){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MERGE (U:User{Username: $Username}) \n" +
                    "ON CREATE \n" +
                    "SET U.Username=$Username",parameters("Username",Username));
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
    private boolean addFavorite(String Username, FavoriteBeer fv) { //Correct it
        try (Session session = NeoDBMS.getDriver().session()) {
            //Check if user exists
            UserManager.getInstance().addStandardUser(Username);
            //Check if beer exists
            BeerManager.getInstance().AddBeer(BeerManager.getInstance().getBeer(fv.getBeerID()));
            //I put the date in the right format
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String str = formatter.format(fv.getFavoriteDate());
            //Run the query
            session.run("MATCH\n" +
                            "  (B:Beer{ID:$BeerID}),\n" +
                            "  (U:User{Username:$Username})\n" +
                            "MERGE (U)-[F:Favorite]->(B)\n" +
                            " ON CREATE SET F.Date=date($date)",
                    parameters("Username",Username, "BeerID", fv.getBeerID(), "date", str));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove a favorite beer from the users favorites list. To identify a relationship we need the
     *  Username and the BeerID, this functionality has to be available on a specific beer only if a User has it in its
     *  favorites */
    private boolean removeFavorite(String Username, String BeerID){
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

    /* Function used to remove a user and all its relationships from Neo4J graph DB */
    private boolean removeUser(String username){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (U {Username: $username})\n" +
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
            user.setFavorites(session.readTransaction(tx -> {
                Result result = tx.run("MATCH (U:User{Username:$username})-[F:Favorite]->(B:Beer)" +
                                " RETURN B.ID as ID, F.date as Date",
                        parameters("username", user.getUsername()));
                ArrayList<FavoriteBeer> favorites = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    favorites.add(new FavoriteBeer(r.get("ID").asString(),r.get("Date").asString()));
                }
                return favorites;
            }));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
