package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.*;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import org.bson.types.ObjectId;


import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static org.neo4j.driver.Values.parameters;

public class BeerManager {
    //private Beer beer;
    private final MongoCollection<Document> beersCollection;
    private final Neo4jManager NeoDBMS;
    private static BeerManager beerManager;
    //private final MongoManager mongoManager;

    private BeerManager(){
        beersCollection = MongoManager.getInstance().getCollection("beer");
        NeoDBMS = Neo4jManager.getInstance();

    }

    public static BeerManager getInstance() {
        if (beerManager == null)
            beerManager = new BeerManager();
        return beerManager;
    }
/*
    public BeerManager (Beer beer) {
        this();
        this.beer = beer;
    }
*/
    public void addNewBeer(DetailedBeer beer) {
        Document beerDoc = beer.getBeerDoc(false);
        beersCollection.insertOne(beerDoc);
    }

// browse beer by brewery -> typeUser, @Nullable _idBrewery

    public ArrayList<Beer> browseBeers(int page, @Nullable String name) {
        //check string
        name = name != null ? name : "";
        int limit = 20;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = beersCollection.find(or(
                regex("name", ".*" + name + ".*", "i"),
                regex("style", ".*" + name + ".*", "i")))
                .skip(n).limit(limit+1)
                .projection(include("name", "style", "abv", "rating"));

        ArrayList<Beer> beerList = new ArrayList<>();
        for (Document beer: iterable) {
            beerList.add(new Beer(beer));
        }
        return beerList;
    }

    public ArrayList<Beer> browseBeersByBrewery(int page, String breweryID) {
        if (breweryID == null)
            return null;
        int limit = 3;
        int n = (page-1) * limit;

        ArrayList<Beer> beerList = new ArrayList<>();
        //ArrayList<String> beers = BreweryManager.getInstance().getBeerList(page, breweryID);
        try {
            for (Document beerDoc : beersCollection.find(eq("brewery_id", breweryID))
                    .skip(n).limit(limit+1)) {
                beerList.add(new Beer(beerDoc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beerList;
    }

    public ArrayList<Beer> browseBeersByStyle(String styleName) {
        ArrayList<Beer> beerList = new ArrayList<>();
        try {

            for (Document beerDoc : beersCollection.find(
                            regex("style", ".*" + styleName + ".*", "-i"))
                    .limit(20)) {
                beerList.add(new Beer(beerDoc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beerList;
    }

    public ArrayList<Beer> getBeersFromBrewery(Brewery brewery) {
        ArrayList<Beer> beers = new ArrayList<>();
        try {
            for (Document beer : beersCollection.find(in("_id", brewery.getBeers()))) {
                beers.add(new Beer(beer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beers;
    }


    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/



    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
    public boolean AddBeer (Beer beer){
        try(Session session = NeoDBMS.getDriver().session()){
            //I First have to see if the style node for this beer is already in the graph
            session.run("MERGE (S:Style{nameStyle: $Style})" +
                    "ON CREATE" +
                    "SET nameStyle= $Style",parameters("Style",beer.getStyle()));
            //I then create the node for the new beer
            session.run("CREATE (B:Beer{ID: $BeerID,Name: $name})",parameters("BeerID",beer.getBeerID(),"Name",beer.getBeerName()));
            //I create the relationship between the style node and the beer node
            session.run("MATCH\n" +
                            "(B:Beer{ID:$BeerID}),\n" +
                            "(S:Style{nameStyle:$style})\n " +
                            "CREATE (B)-[Ss:SameStyle]->(S)\n",
                    parameters( "BeerID", beer.getBeerID(), "styleName", beer.getStyle()));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function that based on the user current research find some beers to suggest him based on the beer style and favorites of
    *  others users */ /* TO BE CHECKED */
    public List<String> getSuggested(String Username){
        //Lookin for how many different style this user have in his favorites
        try(Session session = NeoDBMS.getDriver().session()) {
            Result n_style = session.run("match (U:User{Username:$Username})-[F:Favorite]->(B)-[Ss:SameStyle]->(S) \n" +
                    "return distinct count(S.nameStyle)",parameters("Username",Username));
            //If none i return an empty list
            if (n_style.single().get(0).asInt()==0){
                return Collections.emptyList();
            }
            Result Style_records= session.run("match (U:User{Username:$Username})-[F:Favorite]->(B:Beer) \n" +
                    "return  B.Style",parameters("Username",Username));
            String Style_1= Style_records.next().get("Style").asString();
            String Style_2=Style_records.next().get("Style").asString();
            //If less than 4 I return two suggestions for that style
            if(n_style.single().get(0).asInt()<2){
                return session.readTransaction((TransactionWork<List<String>>) tx -> {
                    Result result = tx.run("MATCH (B:Beer{Style:$Style}) With B," + /* CORREGGI QUA */
                            " SIZE(()-[:Favorite]-(B)) as FavoritesCount ORDER BY FavoritesCount DESC LIMIT 4" +
                            " RETURN B.ID as ID", parameters("Style", Style_1));
                    ArrayList<String> Suggested = new ArrayList<>();
                    while (result.hasNext()) {
                        Record r = result.next();
                        Suggested.add(r.get("ID").asString());
                    }
                    return Suggested;
                });
            }
            //If more than 2 I take the two with most records and return suggestions on those two
            else{
                return session.readTransaction((TransactionWork<List<String>>) tx -> {
                    Result result = tx.run("MATCH (B:Beer{Style:$Style}) With B," +
                            " SIZE(()-[:Favorite]-(B)) as FavoritesCount ORDER BY FavoritesCount DESC LIMIT 2" +
                            " RETURN B.ID as ID", parameters("Style", Style_1));
                    ArrayList<String> Suggested = new ArrayList<>();
                    while (result.hasNext()) {
                        Record r = result.next();
                        Suggested.add(r.get("ID").asString());
                    }
                    Result result_2 = tx.run("MATCH (B:Beer{Style:$Style}) With B," +
                            " SIZE(()-[:Favorite]-(B)) as FavoritesCount ORDER BY FavoritesCount DESC LIMIT 2" +
                            " RETURN B.ID as ID", parameters("Style",Style_2));
                    while (result_2.hasNext()) {
                        Record r = result_2.next();
                        Suggested.add(r.get("ID").asString());
                    }
                    return Suggested;
                });
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /* Function that calculate the most favorite beers in the past month */
    public List<String> getMostFavoriteThisMonth (){
        try(Session session = NeoDBMS.getDriver().session()){
            //Get the current date
            LocalDateTime MyLDTObj = LocalDateTime.now();
            //Subtract a month
            MyLDTObj.minus(Period.ofMonths(1));
            DateTimeFormatter myFormatObj  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //Convert it into a string with the chosen format
            String Starting_date = MyLDTObj.format(myFormatObj);
            //I commit the query and return the value
            return session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run("MATCH ()-[F1:Favorite]->(B1)\n" +
                                "MATCH ()-[F2:Favorite]->(B2) \n" +
                                "WHERE F1.date>=date($starting_Date) AND F2.date>=date($starting_Date) AND B1.ID=B2.ID\n" +
                                "RETURN B1.ID as ID,(toFloat(COUNT(F2.date))/2) AS Num ORDER BY Num DESC LIMIT 10; ",
                        parameters( "starting_Date", Starting_date));
                ArrayList<String> MostLiked = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    MostLiked.add(r.get("ID").asString());
                }
                return MostLiked;
            });
        }
        catch(Exception e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    /*
    *
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
