package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.StandardUser;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;

import org.bson.types.ObjectId;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;


import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.*;
import static org.neo4j.driver.Values.parameters;

public class BeerManager {
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

    public Beer getBeer(String beerID) {
        Beer beer = null;
        try {
            Document doc = beersCollection.find(eq("_id", new ObjectId(beerID)))
                    .projection(include("name", "style", "abv", "rating")).first();
            if (doc != null)
                beer = new Beer(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beer;
    }

    public DetailedBeer getDetailedBeer(String beerID) {
        DetailedBeer beer = null;
        try {
            Document doc = beersCollection.find(eq("_id", new ObjectId(beerID))).first();
            if (doc != null)
                beer = new DetailedBeer(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beer;
    }

    public long deleteBreweryFromBeers(String breweryID) {
        UpdateResult updateResult = beersCollection.updateMany(eq("brewery", breweryID),
                unset("brewery"));
        return updateResult.getMatchedCount();
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
            session.run("MERGE (B:Beer{ID: $BeerID})",parameters("BeerID",beer.getBeerID()));
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
    *  others users */
    public List<String> getSuggested(StandardUser user){
        //Looking for how many style this user have in his favorites
        try(Session session = NeoDBMS.getDriver().session()) {
            int n_style=0;
            Result Style_records= session.run("match (U:User{Username:$Username})-[F:Favorite]->(B:Beer)-[Ss:SameStyle]->(S:Style) \n" +
                    "return  S.nameStyle",parameters("Username",user.getUsername()));
            String Style_1="",Style_2="";
            if(Style_records.hasNext()) {
                Style_1= Style_records.next().get("Style").asString();
                if (Style_records.hasNext()) {
                    Style_2 = Style_records.next().get("Style").asString();
                    n_style = 2;
                }
                else{
                    n_style=1;
                }
            }
            if(n_style==0){ //If the user haven't any favorites I return an empty list
                return Collections.emptyList();
            }
            //If less than 4 I return two suggestions for that style
            if(n_style==1){
                String finalStyle_ = Style_1;
                return session.readTransaction((TransactionWork<List<String>>) tx -> {
                    Result result = tx.run("MATCH (B:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n" +
                            "WITH COLLECT(B) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 4", parameters("Style", finalStyle_));
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
                String finalStyle_1 = Style_1;
                String finalStyle_2 = Style_2;
                return session.readTransaction((TransactionWork<List<String>>) tx -> {
                    Result result = tx.run("MATCH (B:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n" +
                            "WITH COLLECT(B) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 2", parameters("Style", finalStyle_1));
                    ArrayList<String> Suggested = new ArrayList<>();
                    while (result.hasNext()) {
                        Record r = result.next();
                        Suggested.add(r.get("ID").asString());
                    }
                    Result result_2 = tx.run("MATCH (B:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n" +
                            "WITH COLLECT(B) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 2", parameters("Style", finalStyle_2));
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
                Result result = tx.run("MATCH ()-[F:Favorite]->(B:Beer)\n" +
                                "WHERE F.date>=date($starting_Date)\n" +
                                "WITH collect(B) as Fv\n" +
                                "MATCH ()-[F1:Favorite]->(B1:Beer)\n" +
                                "WHERE (B1) in Fv AND F1.date>=date($starting_Date)\n" +
                                "MATCH ()-[F2:Favorite]->(B1)\n" +
                                "WHERE F2.date>=date($starting_Date)\n" +
                                "RETURN COUNT(DISTINCT F2) AS Conta,B1.ID AS ID ORDER BY Conta DESC LIMIT 10",
                        parameters( "starting_Date", Starting_date));
                ArrayList<String> MostLiked = new ArrayList<>();
                //Saving the results in a List before returning it
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
}
