package it.unipi.dii.inginf.lsmdb.beerzone.entityManagerDB;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.BeerManager;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.UserManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.pull;
import static org.neo4j.driver.Values.parameters;

public class GeneralUserManagerDB {
    private static GeneralUserManagerDB userManager;
    private final MongoManager mongoManager;
    private MongoCollection<Document> usersCollection;
        // users collection include both standard users (type 0) and breweries (type 1)
    private final Neo4jManager NeoDBMS;

    private GeneralUserManagerDB() {
        NeoDBMS = Neo4jManager.getInstance();
        mongoManager = MongoManager.getInstance();
        usersCollection = MongoManager.getInstance().getCollection("users");
    }

    public static GeneralUserManagerDB getInstance() {
        if (userManager == null)
            userManager = new GeneralUserManagerDB();
        return userManager;
    }


    /* *********************************************************************************************************** */
    /* ******************************************  MongoDB Section  ********************************************** */
    /* *********************************************************************************************************** */


    // use example: Brewery b = new Brewery(UserManager.getUser(email, type);
    public Document getUser(String email, String password) {
        Document doc = null;
        try {
            doc = usersCollection.find(and(regex("email", "^" + email + "$", "i"),
                    eq("password", password))).first();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public Document getUser(String userID) {
        Document doc = null;
        try {
            doc = usersCollection.find(eq("_id", new ObjectId(userID))).first();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    /* check if an email or a combination of an username/type=0 already exist in the users collection */
    public boolean userExists(GeneralUser user) {
        if (user.getUsername().equalsIgnoreCase("deletedUser")
                || user.getUsername().equalsIgnoreCase("deleted_user")
                || user.getUsername().equalsIgnoreCase("deleted user"))
            return true;
        Document doc = null;
        try {
            doc = usersCollection.find(or(regex("email", "^" + user.getEmail() + "$", "i"), //eq("email", user.getEmail()),eq("username", user.getUsername())
                    and(eq("type", user.getType()),
                            regex("username", "^" + user.getUsername() + "$", "i")))).first();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !(doc == null || doc.isEmpty());
    }

    public boolean registerUser(GeneralUser user) {
        if (!userExists(user)) {
            try {
                Document doc = user.isStandard() ? ((StandardUser) user).getUserDoc()
                        : ((Brewery) user).getBreweryDoc(false);
                usersCollection.insertOne(doc);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateUser(Document userDoc, String userID) {
        try {
            UpdateResult updateResult = usersCollection.replaceOne(eq("_id", new ObjectId(userID)), userDoc);
            return updateResult.getMatchedCount() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser (GeneralUser user) {
        DeleteResult deleteResult = usersCollection.deleteOne(eq("_id", new ObjectId(user.getUserID())));
        return deleteResult.getDeletedCount() == 1;
    }


    /* ******************************************** Brewery Manager ********************************************** */


    public FindIterable<Document> browseBreweries(int page, @Nullable String name) {
        name = name != null ? name : "";
        int limit = 13;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = null;
        try {
            iterable = usersCollection.find(and(eq("type", 1),
                            regex("username", "^" + name + ".*", "i")))
                    .skip(n).limit(limit+1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return iterable;
    }

    public boolean addBeerToBrewery(DetailedBeer beer) {
        try {
            UpdateResult updateResult = usersCollection.updateOne(eq("_id", new ObjectId(beer.getBreweryID())),
                    addToSet("beers", new Document("beer_id", new ObjectId(beer.getBeerID()))
                            .append("beer_name", beer.getBeerName())));
            return updateResult.getMatchedCount() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBeerFromBrewery(DetailedBeer beer) {
        UpdateResult updateResult = usersCollection.updateOne(eq("_id", new ObjectId(beer.getBreweryID())),
                pull("beers", eq("beer_id", new ObjectId(beer.getBeerID()))));
        return updateResult.getMatchedCount() == 1;
    }


    /* *********************************************************************************************************** */
    /* *******************************************  Neo4J Section  *********************************************** */
    /* *********************************************************************************************************** */


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
    public boolean addFavorite(String Username, FavoriteBeer fv) { //Correct it
        try (Session session = NeoDBMS.getDriver().session()) {
            //Check if user exists
            UserManager.getInstance().addStandardUser(Username);
            //Check if beer exists
            BeerManager.getInstance().addBeer(BeerManager.getInstance().getBeer(fv.getBeerID()));
            //Run the query
            session.run("MATCH\n" +
                            "  (B:Beer{ID:$BeerID}),\n" +
                            "  (U:User{Username:$Username})\n" +
                            "MERGE (U)-[F:Favorite]->(B)\n" +
                            " ON CREATE SET F.date=date($date)",
                    parameters("Username",Username, "BeerID", fv.getBeerID(), "date", fv.getFavoriteDate()));
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

    /* Function used to remove a user and all its relationships from Neo4J graph DB */
    public boolean removeUser(String username){
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
                                " RETURN B.ID as ID, toString(F.date) as Date",
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
