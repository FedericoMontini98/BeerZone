package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.StandardUser;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;
import org.neo4j.driver.Session;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.neo4j.driver.Values.parameters;

public class ReviewManager {
    private static ReviewManager reviewManager;
    //private final MongoManager mongoManager;
    private MongoCollection<Document> reviewsCollection;
    private final Neo4jManager NeoDBMS;

    private ReviewManager() {
        //mongoManager = MongoManager.getInstance();
        reviewsCollection = MongoManager.getInstance().getCollection("reviews");
        NeoDBMS = Neo4jManager.getInstance();
    }

    public static ReviewManager getInstance() {
        if (reviewManager == null)
            reviewManager = new ReviewManager();
        return reviewManager;
    }


    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/


    /* Function used to add the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer hasn't been reviewed from this user yet to avoid multiple
     * reviews from the same user which can lead to inconsistency or fake values of the avg. score */
    public boolean addReview(Beer beer, StandardUser user ){
        try(Session session = NeoDBMS.getDriver().session()){
            //Check if user exists
            session.run("MERGE (U:User{Username: $username})" +
                    "ON CREATE" +
                    "   SET U.Username=$username, U.ID=$id ",parameters("username",user.getUsername(),"id",user.getUserID()));
            //Check if beer exists
            session.run("MERGE (B:Beer{ID: $id})" +
                    "ON CREATE" +
                    "   SET B.Name=$name, B.ID=$id ,B.Style=$style",parameters("name",beer.getBeerName(),"id",beer.getBeerID(),"style",beer.getStyle()));
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            String str = formatter.format(date);
            session.run("MATCH\n" +
                            "  (B:Beer),\n" +
                            "  (U:User)\n" +
                            "WHERE U.Username = $Username AND B.ID = $BeerID\n" +
                            "CREATE (U)-[R:Reviewed{InDate:$Date}]->(B)\n",
                    parameters( "Username", user.getUsername(), "BeerID", beer.getBeerID(),"Date", str));
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

}
