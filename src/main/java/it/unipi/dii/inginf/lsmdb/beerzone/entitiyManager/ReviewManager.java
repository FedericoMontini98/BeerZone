package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Review;
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

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.pull;
import static org.neo4j.driver.Values.parameters;

public class ReviewManager {
    private static ReviewManager reviewManager;
    //private final MongoManager mongoManager;
    //private final MongoCollection<Document> reviewsCollection;
    private final MongoCollection<Document> beersCollection;
    private final Neo4jManager NeoDBMS;

    private ReviewManager() {
        //mongoManager = MongoManager.getInstance();
        //reviewsCollection = MongoManager.getInstance().getCollection("reviews");
        beersCollection = MongoManager.getInstance().getCollection("beers");
        NeoDBMS = Neo4jManager.getInstance();
    }

    public static ReviewManager getInstance() {
        if (reviewManager == null)
            reviewManager = new ReviewManager();
        return reviewManager;
    }

    /* Add a new Review both on MongoDB and Neo4J */
    public boolean addNewReview(Review review, DetailedBeer beer) {
        if (addReviewMongo(review, beer)) {
            beer.addReviewToBeer(review);
            updateBeerRating(beer);
            return addReview(review, beer);
        }
        return false;
    }

    /* Delete a review both from MongoDB and Neo4J */
    public boolean deleteReview(String username, DetailedBeer beer) {
        if (deleteReviewMongo(username, beer.getBeerID())) {
            beer.removeReviewFromBeer(getReview(username, beer));
            updateBeerRating(beer);
            return removeReview(username, beer.getBeerID());
        }
        return false;
    }

    /* ************************************************************************************************************/
    /* *************************************  MongoDB Section  ****************************************************/
    /* ************************************************************************************************************/
/*
    public Review getReviewOld(String username, String beerID) {
        Review review = null;
        try {
            Document doc = reviewsCollection.find(and(eq("beer_id", new ObjectId(beerID)),
                            eq("username", username))).first();
            if (doc != null)
                review = new Review(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return review;
    }

 */

    public Review getReview(String username, DetailedBeer beer) {
        for (Review r: beer.getReviewList()) {
            if (username.equalsIgnoreCase(r.getUsername()))
                return r;
        }
        return null;
    }
/*
    private boolean existsReview(Review review) {
        try {
            Document doc = reviewsCollection.find(and(eq("username", review.getUsername()),
                    eq("beer_id", new ObjectId(review.getBeerID())))).first();
            //eq("beer", review.getBeerID()))).first();
            if (doc != null)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

 */

    private boolean existsReview(String username, DetailedBeer beer) {
        try {
            Document doc = beersCollection.find(and(eq("_id", new ObjectId(beer.getBeerID())),
                    eq("reviews.username", username))).first();
            if (doc != null)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* add new review in mongoDB*/
/*    private boolean addReviewMongoOld(Review review, DetailedBeer beer) {
        if (!existsReview(review)) {
            try {
                reviewsCollection.insertOne(review.getReview());
                return updateBeerRating(beer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

 */

    /* add new review in mongoDB*/
    private boolean addReviewMongo(Review review, DetailedBeer beer) {
        if (!existsReview(review.getUsername(), beer)){
            try {
                UpdateResult updateResult = beersCollection.updateOne(eq("_id", new ObjectId(beer.getBeerID())),
                        addToSet("reviews", review.getReviewDoc()));
                return updateResult.getMatchedCount() == 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteReviewMongo(String username, String beerID) {
        UpdateResult updateResult = beersCollection.updateOne(eq("_id", new ObjectId(beerID)),
                pull("reviews", eq("username", username)));
        return updateResult.getMatchedCount() == 1;
    }
/*
    public ArrayList<Review> getBeerReviews(String beerID) {    // o Beer?
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            FindIterable<Document> iterable = reviewsCollection.find(eq("beer_id", new ObjectId(beerID)));
            for (Document d: iterable) {
                reviews.add(new Review(d));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

 */

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Aggregations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
/*
    boolean updateBeerRatingOld(DetailedBeer beer) {
        try {
            Bson matchBeers = match(eq("beer_id", new ObjectId(beer.getBeerID())));
            Bson groupBeers = group("$beer_id", avg("avg_score", "$score"),
                    sum("num_rating", 1));
            Bson projectRoundScore = project(new Document("rating",
                    new Document("$round", Arrays.asList("$avg_score", 2L))).append("num_rating", 1L));
            Bson mergeResult = merge("beers");
            Document aggregation = reviewsCollection.aggregate(Arrays.asList(matchBeers, groupBeers,
                    projectRoundScore, mergeResult)).first();
            if (aggregation != null) {
                if (aggregation.get("rating") != null)
                    beer.setScore(Double.parseDouble(aggregation.get("rating").toString()));
                if (aggregation.get("num_rating") != null)
                    beer.setNumRating(aggregation.getInteger("num_rating"));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

 */

    private void updateBeerRating(DetailedBeer beer) {
        try {
            Bson matchBeer = match(eq("_id", new ObjectId(beer.getBeerID())));
            Bson unwindReviews = unwind("$reviews");
            Bson groupBeers = group("$_id", avg("avg_score", "$reviews.score"),
                    sum("num_rating", 1));
            Bson projectRoundScore = project(new Document("rating",
                    new Document("$round", Arrays.asList("$avg_score", 2L))).append("num_rating", 1L));
            Bson mergeResult = merge("beers");
            Document aggregation = beersCollection.aggregate(Arrays.asList(matchBeer, unwindReviews, groupBeers,
                    projectRoundScore, mergeResult)).first();
            if (aggregation != null) {
                if (aggregation.get("rating") != null)
                    beer.setScore(Double.parseDouble(aggregation.get("rating").toString()));
                if (aggregation.get("num_rating") != null)
                    beer.setNumRating(aggregation.getInteger("num_rating"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    protected AggregateIterable<Document> getHighestAvgScoreBeers() {
        AggregateIterable<Document> list = null;
        try {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime last_month = LocalDateTime.now().minusMonths(1);
            Bson matchDate = match(and(lt("date", today), gt("date", last_month)));
            Bson groupBeer = group("$beer_id", avg("monthly_score", "$score"));
            Bson projectRoundScore = project(new Document("monthly_score",
                    new Document("$round", Arrays.asList("$monthly_score", 2))));
            Bson sortScore = sort(descending("monthly_score"));
            Bson limitResult = limit(8);
            Bson lookupBeers =  lookup("beers", "_id", "_id", "beer");
            Bson newRoot = new Document("$replaceRoot", new Document("newRoot", new Document("$mergeObjects",
                    Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$beer", 0L)), "$$ROOT"))));
            Bson projectResult = project(fields(include("name", "style", "abv", "rating", "monthly_score")));
            list = reviewsCollection.aggregate(Arrays.asList(matchDate, groupBeer, sortScore, limitResult, projectRoundScore,
                    lookupBeers, newRoot, projectResult));
            for (Document d: list) {
                System.out.println(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public AggregateIterable<Document> getBeersUnderAvgFeatureScore(Brewery brewery, String feature, double breweryScore){
        AggregateIterable<Document> list = null;
        try {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime past = LocalDateTime.now().minusMonths(6);
            Bson initialMatch = match(and(lt("date", today), gt("date", past),
                    in("beer_id", brewery.getBeersID())));
            Bson groupBeer = group("$beer_id", avg("feature_score", "$" + feature));
            Bson projectRoundScore = project(new Document("feature_score",
                    new Document("$round", Arrays.asList("$feature_score", 2))));
            Bson matchBreweryScore = match(lt("feature_score", breweryScore));
            Bson sortResult = sort(ascending("feature_score"));
            Bson lookupBeers =  lookup("beers", "_id", "_id", "beer");
            Bson newRoot = new Document("$replaceRoot", new Document("newRoot", new Document("$mergeObjects",
                    Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$beer", 0L)), "$$ROOT"))));
            Bson projectResult = project(fields(include("name", "style", "abv", "rating", "feature_score")));
            list = reviewsCollection.aggregate(Arrays.asList(initialMatch, groupBeer, projectRoundScore,
                    matchBreweryScore, sortResult, lookupBeers, newRoot, projectResult));
            for (Document d: list) {
                System.out.println(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

 */


    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/


    /* Function used to add the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer hasn't been reviewed from this user yet to avoid multiple
     * reviews from the same user which can lead to inconsistency or fake values of the avg. score */
    private boolean addReview(Review review, Beer beer){
        try(Session session = NeoDBMS.getDriver().session()){
            System.out.println(review.getUsername() + "-" + review.getBeerID());
            //Check if user exists
            UserManager.getInstance().addStandardUser(review.getUsername());
            //Check if beer exists
            BeerManager.getInstance().AddBeer(beer);
            //Put the date in the right format
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String str = formatter.format(review.getReviewDateNeo());
            //Create the relationship
            session.run("MATCH\n" +
                            "  (B:Beer{ID:$BeerID}),\n" +
                            "  (U:User{Username:$Username})\n" +
                            "MERGE (U)-[R:Reviewed]->(B)\n" +
                            "ON CREATE\n" +
                            "SET R.date=date($Date)",
                    parameters( "Username", review.getUsername(), "BeerID", review.getBeerID(),"Date", str));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer has been reviewed from this user */
    private boolean removeReview(String Username, String BeerID){
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