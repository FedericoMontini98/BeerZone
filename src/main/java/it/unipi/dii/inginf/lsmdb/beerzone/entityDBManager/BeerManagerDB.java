package it.unipi.dii.inginf.lsmdb.beerzone.entityDBManager;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.UserManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.*;
import static org.neo4j.driver.Values.parameters;

public class BeerManagerDB {
    private MongoCollection<Document> beersCollection;
    private final MongoManager mongoManager;
    private final Neo4jManager NeoDBMS;
    private static BeerManagerDB beerManager;

    private BeerManagerDB(){
        mongoManager = MongoManager.getInstance();
        //beersCollection = mongoManager.getCollection("beers");
        NeoDBMS = Neo4jManager.getInstance();
    }

    public static BeerManagerDB getInstance() {
        if (beerManager == null)
            beerManager = new BeerManagerDB();
        return beerManager;
    }

    /* *********************************************************************************************************** */
    /* ****************************************  MongoDB Section  ************************************************ */
    /* *********************************************************************************************************** */


    public void addNewBeerMongo(DetailedBeer beer) {
        try {
            beersCollection = mongoManager.getCollection("beers");
            Document beerDoc = beer.getBeerDoc();
            beersCollection.insertOne(beerDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean removeBeerMongo(Beer beer){
        try {
            beersCollection = mongoManager.getCollection("beers");
            DeleteResult deleteResult = beersCollection.deleteOne(eq("_id", new ObjectId(beer.getBeerID())));
            return (deleteResult.getDeletedCount() == 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public FindIterable<Document> browseBeers(int page, @Nullable String name) {
        //check string
        name = name != null ? name : "";
        int limit = 13;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = null;

        try {
            beersCollection = mongoManager.getCollection("beers");
            iterable = beersCollection.find(or(
                    regex("name", "^" + name + ".*", "i"),
                            regex("style", "^" + name + ".*", "i")))
                    .skip(n).limit(limit+1)
                    .projection(include("name", "style", "abv", "rating"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return iterable;
    }

    public Document getBeer(String beerID) {
        Document beer = null;
        try {
            beersCollection = mongoManager.getCollection("beers");
            beer = beersCollection.find(eq("_id", new ObjectId(beerID)))
                    .projection(include("name", "style", "abv", "rating")).first();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beer;
    }

    public Document getDetailedBeer(String beerID) {
        Document beer = null;
        try {
            beersCollection = mongoManager.getCollection("beers");
            beer = beersCollection.find(eq("_id", new ObjectId(beerID))).first();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return beer;
    }

    /* returned value: matched beers in the beersCollection */
    public long deleteBreweryFromBeers(String breweryID) {
        try {
            beersCollection = mongoManager.getCollection("beers");
            UpdateResult updateResult = beersCollection.updateMany(eq("brewery_id", new ObjectId(breweryID)),
                    combine(unset("brewery_id"), set("retired", "t")));
            return updateResult.getMatchedCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateBeer(DetailedBeer beer) {
        try {
            beersCollection = mongoManager.getCollection("beers");
            UpdateResult updateResult = beersCollection.replaceOne(eq("_id", new ObjectId(beer.getBeerID())),
                    (beer.getBeerDoc()));
            if (updateResult.getMatchedCount() == 1)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Reviews manager ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/


    private boolean existsReview(String username, Beer beer) {
        try {
            beersCollection = mongoManager.getCollection("beers");
            Document doc = beersCollection.find(and(eq("_id", new ObjectId(beer.getBeerID())),
                    eq("reviews.username", username))).first();
            if (doc != null)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUserFromReviews(String username) {
        try {
            beersCollection = mongoManager.getCollection("beers");
            String pattern = "^" + username + "$";
            String optionsRegEx = "i";
            UpdateOptions updateOptions = new UpdateOptions().arrayFilters(
                    Collections.singletonList(regex("item.username", pattern, optionsRegEx)));
            UpdateResult updateResult = beersCollection.updateMany(
                    regex("reviews.username", pattern, optionsRegEx),
                    set("reviews.$[item].username", "deleted_user"), updateOptions);
            //System.out.println(updateResult.getMatchedCount() + ", modified: " + updateResult.getModifiedCount());
            return updateResult.getMatchedCount() == updateResult.getModifiedCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addReviewToBeersCollection(Review review, DetailedBeer beer, double newRating) {
        if (!existsReview(review.getUsername(), beer)) {
            try {
                beersCollection = mongoManager.getCollection("beers");
                UpdateResult updateResult = beersCollection.updateOne(eq("_id", new ObjectId(beer.getBeerID())),
                        combine(set("rating", newRating), inc("num_rating", 1),
                                addToSet("reviews", review.getReviewDoc())));
                return updateResult.getMatchedCount() == updateResult.getModifiedCount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean deleteReviewMongo(String username, DetailedBeer beer, double updatedRating) {
        try {
            beersCollection = mongoManager.getCollection("beers");
            UpdateResult updateResult = beersCollection.updateOne(eq("_id", new ObjectId(beer.getBeerID())),
                    combine(set("rating", updatedRating), inc("num_rating", -1),
                            pull("reviews", eq("username", username))));
            return updateResult.getMatchedCount() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Aggregations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

    // first aggregation on mongodb
    public AggregateIterable<Document> getHighestAvgScoreBeers() {
        AggregateIterable<Document> list = null;
        try {
            beersCollection = mongoManager.getCollection("beers");
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime last_month = LocalDateTime.now().minusMonths(1);
            Bson matchDate = match(and(lt("reviews.date", today), gt("reviews.date", last_month)));
            Bson unwindReviews = unwind("$reviews");
            Bson groupBeer = new Document("$group", new Document("_id",
                    new Document("_id", "$_id")
                            .append("name", "$name")
                            .append("style", "$style")
                            .append("abv", "$abv")
                            .append("rating", "$rating"))
                    .append("monthly_score", new Document("$avg", "$reviews.score")));
            Bson projectRoundScore = project(new Document("monthly_score",
                    new Document("$round", Arrays.asList("$monthly_score", 2))));
            Bson sortScore = sort(descending("monthly_score"));
            Bson limitResult = limit(8);

            list = beersCollection.aggregate(Arrays.asList(matchDate, unwindReviews, matchDate, groupBeer, sortScore,
                    limitResult, projectRoundScore));
            /* for (Document d: list) {
                System.out.println(d);
            }
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // second aggregation on mongodb
    public AggregateIterable<Document> getBeersUnderAvgFeatureScore(String breweryID, String feature, double breweryScore){
        AggregateIterable<Document> list = null;
        try {
            beersCollection = mongoManager.getCollection("beers");
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime past = LocalDateTime.now().minusMonths(6);
            Bson initialMatch = match(and(eq("brewery_id", new ObjectId(breweryID)),
                    lt("reviews.date", today), gt("reviews.date", past)));
            Bson unwindReviews = unwind("$reviews");
            Bson groupBeer = new Document("$group", new Document("_id",
                    new Document("_id", "$_id")
                            .append("name", "$name")
                            .append("style", "$style")
                            .append("abv", "$abv")
                            .append("rating", "$rating"))
                    .append("feature_score", new Document("$avg", "$reviews."+ feature)));
            Bson projectRoundScore = project(new Document("feature_score",
                    new Document("$round", Arrays.asList("$feature_score", 2))));
            Bson matchBreweryScore = match(lt("feature_score", breweryScore));
            Bson sortResult = sort(ascending("feature_score"));
            list = beersCollection.aggregate(Arrays.asList(initialMatch, unwindReviews, groupBeer, projectRoundScore,
                    matchBreweryScore, sortResult));
            for (Document d: list) {
                System.out.println(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // third aggregation on mongodb
    public Document getBreweryScore(String breweryID) {
        Document doc = null;
        try {
            beersCollection = mongoManager.getCollection("beers");
            Bson matchBrewery = match(and(eq("brewery_id",new ObjectId(breweryID)),gt("num_rating",0)));
            Bson groupBrewery = group("$brewery_id", avg("avg_score", "$rating"));
            Bson projectResult = project(new Document("brewery_score",
                    new Document("$round", Arrays.asList("$avg_score", 2))));

            doc = beersCollection.aggregate(
                    Arrays.asList(matchBrewery, groupBrewery, projectResult)).first();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    // variation of third aggregation on mongodb
    public Document getWeightedBreweryScore(String breweryID) {
        Document doc = null;
        try {
            beersCollection = mongoManager.getCollection("beers");
            Bson initialMatch = match(and(eq("brewery_id", new ObjectId(breweryID)), gt("num_rating", 0)));
            Bson groupBrewery = group(new Document("_id", "$brewery_id")
                    .append("rating_sum", new Document("$sum",
                            new Document("$multiply", Arrays.asList("$rating", "$num_rating"))))
                    .append("tot_num_rating",
                            new Document("$sum", "$num_rating")));
            Bson projectRound = project(new Document("brewery_score", new Document("$round",
                    Arrays.asList(new Document("$divide", Arrays.asList("$rating_sum", "$tot_num_rating")), 2))));

            doc = beersCollection.aggregate(
                    Arrays.asList(initialMatch, groupBrewery, projectRound)).first();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public Document updateBeerRating(DetailedBeer beer) {
        Document doc = null;
        try {
            beersCollection = mongoManager.getCollection("beers");
            Bson matchBeer = match(eq("_id", new ObjectId(beer.getBeerID())));
            Bson unwindReviews = unwind("$reviews");
            Bson groupBeers = group("$_id", avg("avg_score", "$reviews.score"),
                    sum("num_rating", 1));
            Bson projectRoundScore = project(new Document("rating",
                    new Document("$round", Arrays.asList("$avg_score", 2L))).append("num_rating", 1L));
            Bson mergeResult = merge("beers");
            doc = beersCollection.aggregate(Arrays.asList(matchBeer, unwindReviews, groupBeers,
                    projectRoundScore, mergeResult)).first();

            /*if (aggregation != null) {
                if (aggregation.get("rating") != null)
                    beer.setScore(Double.parseDouble(aggregation.get("rating").toString()));
                if (aggregation.get("num_rating") != null)
                    beer.setNumRating(aggregation.getInteger("num_rating"));
                return true;
            }

             */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }


    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/



    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
    public boolean addBeer(Beer beer){
        try(Session session = NeoDBMS.getDriver().session()){
            //I First have to see if the style node for this beer is already in the graph
            session.run("MERGE (S:Style{nameStyle: $Style})\n" +
                    "ON CREATE\n" +
                    "SET S.nameStyle= $Style",parameters("Style",beer.getStyle()));
            //I then create the node for the new beer
            session.run("MERGE (B:Beer{ID: $BeerID, Name:$name})",
                    parameters("BeerID",beer.getBeerID(),"name", beer.getBeerName()));
            //I create the relationship between the style node and the beer node
            session.run("MERGE (B:Beer{ID: $BeerID})-[Ss:SameStyle]-(S:Style{nameStyle:$style})",
                    parameters( "BeerID", beer.getBeerID(), "style", beer.getStyle()));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function that based on the user current research find some beers to suggest him based on the beer style and favorites of
     *  others users */
    public ArrayList<String> getSuggested(StandardUser user){
        //Looking for how many style this user have in his favorites
        try(Session session = NeoDBMS.getDriver().session()) {
            int n_style=0;
            Result Style_records= session.run("match (U:User{Username:$Username})-[F:Favorite]->(B:Beer)-[Ss:SameStyle]->(S:Style) \n" +
                    "return  distinct S.nameStyle as Style",parameters("Username",user.getUsername()));
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
                return new ArrayList<>();
            }
            //If less than 4 I return two suggestions for that style
            if(n_style==1){
                String finalStyle_ = Style_1;
                return session.readTransaction(tx -> {
                    Result result = tx.run("MATCH (B:Beer)-[F:Favorite]-(U:User{Username:$Username}) \n"+
                            "WITH COLLECT (B.ID) as BeersToNotSuggest\n" +
                            "MATCH (B1:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n"+
                            "WHERE NOT B1.ID IN BeersToNotSuggest\n"  +
                            "WITH COLLECT(B1) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 4", parameters("Username",user.getUsername(),"Style", finalStyle_));
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
                return session.readTransaction(tx -> {
                    Result result = tx.run("MATCH (B:Beer)-[F:Favorite]-(U:User{Username:$Username}) \n"+
                            "WITH COLLECT (B.ID) as BeersToNotSuggest\n" +
                            "MATCH (B1:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n"+
                            "WHERE NOT B1.ID IN BeersToNotSuggest\n"  +
                            "WITH COLLECT(B1) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 2", parameters("Username",user.getUsername(),"Style", finalStyle_1));
                    ArrayList<String> suggested = new ArrayList<>();
                    while (result.hasNext()) {
                        Record r = result.next();
                        suggested.add(r.get("ID").asString());
                    }
                    Result result_2 = tx.run("MATCH (B:Beer)-[F:Favorite]-(U:User{Username:$Username}) \n"+
                            "WITH COLLECT (B.ID) as BeersToNotSuggest\n" +
                            "MATCH (B1:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n"+
                            "WHERE NOT B1.ID IN BeersToNotSuggest\n"  +
                            "WITH COLLECT(B1) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 2", parameters("Username",user.getUsername(),"Style", finalStyle_2));
                    while (result_2.hasNext()) {
                        Record r = result_2.next();
                        suggested.add(r.get("ID").asString());
                    }
                    return suggested;
                });
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    /* Function that calculate the most favorite beers in the past month */
    public ArrayList<FavoriteBeer> getMostFavoriteThisMonth (){
        try(Session session = NeoDBMS.getDriver().session()){
            //Get the current date
            LocalDateTime MyLDTObj = LocalDateTime.now();
            //Subtract a month
            MyLDTObj=MyLDTObj.minus(Period.ofMonths(1));
            DateTimeFormatter myFormatObj  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //Convert it into a string with the chosen format
            String Starting_date = MyLDTObj.format(myFormatObj);
            //I commit the query and return the value
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH ()-[F:Favorite]->(B:Beer)\n" +
                                "WHERE F.date>=date($starting_Date)\n" +
                                "WITH collect(B) as Fv\n" +
                                "MATCH ()-[F1:Favorite]->(B1:Beer)\n" +
                                "WHERE (B1) in Fv AND F1.date>=date($starting_Date)\n" +
                                "RETURN COUNT(DISTINCT F1) AS Count,B1.ID AS ID ORDER BY Count DESC LIMIT 8",
                        parameters( "starting_Date", Starting_date));
                ArrayList<FavoriteBeer> MostLiked = new ArrayList<>();
                //Saving the results in a List before returning it
                while (result.hasNext()) {
                    Record r = result.next();
                    MostLiked.add(new FavoriteBeer(r.get("ID").asString(),null));
                }
                return MostLiked;
            });
        }
        catch(Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void removeBeerFromNeo(Beer beer){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (B:Beer {ID: $ID})\n" +
                            "DETACH DELETE B;",
                    parameters( "ID", beer.getBeerID()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Reviews manager ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/


    /* Function used to add the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer hasn't been reviewed from this user yet to avoid multiple
     * reviews from the same user which can lead to inconsistency or fake values of the avg. score */
    public boolean addReviewNeo(Review review, Beer beer){
        try(Session session = NeoDBMS.getDriver().session()){
            //Check if user exists
            UserManager.getInstance().addStandardUser(review.getUsername());
            //Check if beer exists
            this.addBeer(beer);
            //Put the date in the right format
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String str = formatter.format(review.getReviewDateNeo());
            //Create the relationship
            session.run("MATCH (U:User{Username:$Username})" +
                            "MATCH (B:Beer{ID:$BeerID,Name:$BeerName})\n" +
                            "MERGE (U)-[R:Reviewed]-(B)\n" +
                            "ON CREATE\n" +
                            "SET R.date=date($Date)",
                    parameters( "Username", review.getUsername(), "BeerID", beer.getBeerID(),"BeerName",beer.getBeerName(),"Date", str));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer has been reviewed from this user */
    public boolean removeReviewNeo(String Username, String BeerID){
        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (U:User {Username: $Username})-[R:Reviewed]-(B:Beer {ID: $BeerID}) \n" +
                            "DELETE R",
                    parameters( "Username", Username, "BeerID", BeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to calculate the IDs of the most reviewed beers this month */
    public ArrayList<String> mostReviewedBeers(){
        try(Session session = NeoDBMS.getDriver().session()){
            //Get the current date
            LocalDateTime MyLDTObj = LocalDateTime.now();
            //Subtract a month
            MyLDTObj=MyLDTObj.minus(Period.ofMonths(1));
            DateTimeFormatter myFormatObj  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //Convert it into a string with the chosen format
            String Starting_date = MyLDTObj.format(myFormatObj);
            //I commit the query and return the value
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH ()-[R:Reviewed]->(B:Beer)\n" +
                                "WHERE R.date>=date($starting_Date)\n" +
                                "WITH collect(B) as Rw\n" +
                                "MATCH ()-[R1:Reviewed]->(B1:Beer)\n" +
                                "WHERE (B1) in Rw AND R1.date>=date($starting_Date)\n" +
                                "RETURN COUNT(DISTINCT R1) AS Conta,B1.Name AS Name ORDER BY Conta DESC LIMIT 8",
                        parameters( "starting_Date", Starting_date));
                ArrayList<String> MostReviewed= new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    MostReviewed.add(r.get("Name").asString());
                }
                return MostReviewed;
            });
        }
        catch(Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}

