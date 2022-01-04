package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static org.neo4j.driver.Values.parameters;

public class BeerManager {
    private Beer beer;
    private MongoManager mongoManager;
    private MongoCollection<Document> beersCollection;
    private final Neo4jManager NeoDBMS;


    public BeerManager(){
        mongoManager = MongoManager.getInstance();
        beersCollection = mongoManager.getCollection("beer");
        NeoDBMS = Neo4jManager.getInstance();
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

    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/



    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
    public boolean AddBeer (String BeerID,String style,String name){
        try(Session session = NeoDBMS.getDriver().session()){
            //I First have to see if the style node for this beer is already in the graph
            session.run("MERGE (S:Style{nameStyle: $Style})",parameters("BeerID",BeerID));
            //I then create the node for the new beer
            session.run("CREATE (B:Beer{ID: $BeerID,Name: $name,Style: $style})",parameters("BeerID",BeerID,"Name",name,"Style",style));
            //I create the relationship between the style node and the beer node
            session.run("MATCH\n" +
                            "(B:Beer),\n" +
                            "(S:Style)\n " +
                            "WHERE B.BeerID = $BeerID AND S.styleName = $style \n" +
                            "CREATE (B)-[Ss:SameStyle]->(S)\n",
                    parameters( "BeerID", BeerID, "styleName", style));
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

    /* Function that based on the user current research find some beers to suggest him based on the beer style and favorites of
    *  others users */
    public List<String> getSuggested(String Style){
        try(Session session = NeoDBMS.getDriver().session()) {
            return session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run("MATCH (B:Beer{Style:$Style}) With B," +
                        " SIZE(()-[:Favorite]-(B)) as FavoritesCount ORDER BY FavoritesCount DESC LIMIT 3" +
                        " RETURN B.ID as ID",parameters("Style",Style));
                ArrayList<String> Suggested = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    Suggested.add(r.get("ID").asString());
                }
                return Suggested;
            });
        }
        catch(Exception e){
            e.printStackTrace();
            return Collections.emptyList();
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
