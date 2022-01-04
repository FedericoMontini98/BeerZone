package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;
import org.neo4j.driver.Session;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.neo4j.driver.Values.parameters;

public class ReviewManager {
    private final MongoManager mongoManager;
    private MongoCollection<Document> reviewsCollection;
    private final Neo4jManager NeoDBMS;

    public ReviewManager() {
        mongoManager = MongoManager.getInstance();
        reviewsCollection = mongoManager.getCollection("reviews");
        NeoDBMS = Neo4jManager.getInstance();
    }


    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/


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
                            "WHERE U.Username = $Username AND B.ID = $BeerID\n" +
                            "CREATE (U)-[R:Reviewed{InDate:$Date}]->(B)\n",
                    parameters( "Username", Username, "BeerID", BeerID,"Date", str));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

