package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;
import org.neo4j.driver.Session;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static org.neo4j.driver.Values.parameters;

public class BeerManager {
    private Beer beer;
    private MongoManager mongoManager;
    private MongoCollection<Document> beersCollection;
    private Neo4jManager NeoDBMS;


    public BeerManager(){
        mongoManager = MongoManager.getInstance();
        beersCollection = mongoManager.getCollection("beer");
    }

    public BeerManager (Beer beer) {
        this();
        this.beer = beer;
    }

    public void addNewBeer(Document beerDoc) {
        beersCollection.insertOne(beerDoc);
    }

    /* page start from 0 ? */
    public ArrayList<Beer> showBeers(int page) {
        int limit = 20;
        int n = page * limit;

        FindIterable iterator = beersCollection.find().skip(n).limit(limit);

        ArrayList<Beer> beerList = new ArrayList<>();
        return beerList;
    }

    public ArrayList<Beer> findBeers(String beerName) {
        ArrayList<Beer> beerList = new ArrayList<>();
        try {

            for (Document beerDoc : beersCollection.find(
                            regex("name", ".*" + beerName + ".*", "-i"))
                    .limit(25)) {
                beerList.add(new Beer(beerDoc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beerList;
    }


    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
    public boolean AddBeer (String BeerID){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("CREATE (B:Beer{id: $BeerID})",parameters("BeerID",BeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/

    /* Function used to add the relationship of "SameStyle" between two beers.
     *  It's used to suggest beers based on the common styles between the beers that can be suggested and the beers
     *  in their favorites */
    public boolean addSameStyle(String firstBeerID,String secondBeerID){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH\n" +
                            "(B1:Beer),\n" +
                            "(B2:Beer)\n " +
                            "WHERE B1.id = $BeerID AND B2.id = $BeerID \n" +
                            "CREATE (B1)-[S:SameStyle]->(B2)\n" +
                            "CREATE (B2)-[S:SameStyle]->(B1)\n",
                    parameters( "firstBeerID", firstBeerID, "secondBeerID", secondBeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeBeer(String beerID){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (B {id: $beerID})\n" +
                            "DETACH DELETE B",
                    parameters( "beerID", beerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /*
    * public Beer createBeerFromDoc(Document beerDoc)
    *
    * public List<Beer> readBeers(String beerName)  // also a substring of the name
    *
    * public void updateRating(...)
    *
    * public void deleteBeer(Beer beer);
    *
    * */
}
