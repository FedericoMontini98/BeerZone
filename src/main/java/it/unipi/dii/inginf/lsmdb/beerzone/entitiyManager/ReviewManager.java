package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Review;
import it.unipi.dii.inginf.lsmdb.beerzone.entityManagerDB.BeerManagerDB;
import org.bson.Document;
import java.util.ArrayList;
import java.util.*;

public class ReviewManager {
    private static ReviewManager reviewManager;
    //private final MongoManager mongoManager;
    //private final MongoCollection<Document> reviewsCollection;
    //private final MongoCollection<Document> beersCollection;
    //private final Neo4jManager NeoDBMS;
    private final BeerManagerDB beerManagerDB;

    private ReviewManager() {
        //mongoManager = MongoManager.getInstance();
        //reviewsCollection = MongoManager.getInstance().getCollection("reviews");
        //beersCollection = MongoManager.getInstance().getCollection("beers");
        //NeoDBMS = Neo4jManager.getInstance();
        beerManagerDB = BeerManagerDB.getInstance();
    }

    public static ReviewManager getInstance() {
        if (reviewManager == null)
            reviewManager = new ReviewManager();
        return reviewManager;
    }

    /* ****************************************  MongoDB Section  *************************************************/

    public Review getReview(String username, DetailedBeer beer) {
        for (Review r: beer.getReviewList()) {
            if (username.equalsIgnoreCase(r.getUsername()))
                return r;
        }
        return null;
    }

    /* Add a new Review both on MongoDB and Neo4J */
    public boolean addNewReview(Review review, DetailedBeer beer) {
        //if (addReviewMongo(review, beer)) {
        double new_rating = computeNewBeerRating(review, beer, true);
        if (beerManagerDB.addReviewToBeersCollection(review, beer, new_rating)) {
            beer.addReviewToBeer(review);   // add review to local list in DetailedBeer
            //updateBeerRating(beer);
            return beerManagerDB.addReviewNeo(review, beer);
        }
        return false;
    }

    /* Delete a review both from MongoDB and Neo4J */
    public boolean deleteReview(String username, DetailedBeer beer) {
        double rating = computeNewBeerRating(getReview(username, beer), beer, false);
        //if (deleteReviewMongo(username, beer.getBeerID())) {
        if (beerManagerDB.deleteReviewMongo(username, beer, rating)) {
            beer.removeReviewFromBeer(getReview(username, beer));   // remove review to local list in DetailedBeer
            //updateBeerRating(beer);
            return beerManagerDB.removeReviewNeo(username, beer.getBeerID());
        }
        return false;
    }

    protected boolean deleteUserFromReviews(String username) {
        return beerManagerDB.deleteUserFromReviews(username);
    }

    private double computeNewBeerRating(Review review, DetailedBeer beer, boolean add) {
        try {
            Document doc = beerManagerDB.getDetailedBeer(beer.getBeerID());//beersCollection.find(eq("beer_id", new ObjectId(review.getBeerID()))).first();
            if (doc != null) {
                double rating = doc.get("rating") != null ? Double.parseDouble(doc.get("rating").toString()) : 0;
                int num_rating = doc.get("num_rating") != null ? doc.getInteger("num_rating") : 0;
                double oldTotalRating = rating * num_rating;
                double newRating;
                if (add) {
                    newRating = (oldTotalRating + review.getNumericScore()) / (++num_rating);
                }
                else {
                    if (num_rating <= 1) {
                        newRating = 0;
                        num_rating = 0;
                    }
                    else
                        newRating = (oldTotalRating - review.getNumericScore()) / (--num_rating);
                }
                beer.setNumRating(num_rating);
                beer.setScore(newRating);

                return newRating;
                /*
                UpdateResult updateResult = beersCollection.updateOne(eq("_id", new ObjectId(review.getBeerID())),
                        combine(set("rating", newRating), set("num_rating", num_rating),
                                addToSet("reviews", review.getReviewDoc())));
                return updateResult.getMatchedCount() == 1;

                 */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /* *******************************************  Neo4J Section  ************************************************/

    /* Function used to calculate the IDs of the most reviewed beers this month */
    public ArrayList<String> mostReviewedBeers() {
        return beerManagerDB.mostReviewedBeers();
    }

/*
    public boolean recomputeBeerScore(DetailedBeer beer) {
        Document docRating = beerManagerDB.updateBeerRating(beer);

        if (docRating != null) {
            if (docRating.get("rating") != null)
                beer.setScore(Double.parseDouble(docRating.get("rating").toString()));
            if (docRating.get("num_rating") != null)
                beer.setNumRating(docRating.getInteger("num_rating"));
            return true;
        }

        return false;
    }

 */

    /* ************************************************************************************************************/
    /* ****************************************  MongoDB Section  *************************************************/
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
/*
    private boolean existsReviewV(String username, DetailedBeer beer) {
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

 */

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
  /*  private boolean addReviewMongoV(Review review, DetailedBeer beer) {
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

    public boolean deleteReviewMongoV(String username, String beerID) {
        UpdateResult updateResult = beersCollection.updateOne(eq("_id", new ObjectId(beerID)),
                combine(set("rating", newRating), inc("num_rating", -1),
                        pull("reviews", eq("username", username))));
        return updateResult.getMatchedCount() == 1;
    }

   */
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
/*
    private boolean updateBeerRatingV(DetailedBeer beer) {
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
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

 */
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
    /* *******************************************  Neo4J Section  ************************************************/
    /* ************************************************************************************************************/


    /* Function used to add the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer hasn't been reviewed from this user yet to avoid multiple
     * reviews from the same user which can lead to inconsistency or fake values of the avg. score */
  /*  private boolean addReviewNeo(Review review, Beer beer){
        try(Session session = NeoDBMS.getDriver().session()){
            //Check if user exists
            UserManager.getInstance().addStandardUser(review.getUsername());
            //Check if beer exists
            BeerManager.getInstance().addBeer(beer);
            //Put the date in the right format
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String str = formatter.format(review.getReviewDateNeo());
            //Create the relationship
            session.run("MATCH\n" +
                            "  (B:Beer),\n" +
                            "  (U:User)\n" +
                            "WHERE U.Username = $Username AND B.ID = $BeerID\n" +
                            "MERGE (U)-[R:Reviewed]->(B)\n" +
                            "ON CREATE\n" +
                            "SET R.date=date($Date)",
                    parameters( "Username", review.getUsername(), "BeerID", beer.getBeerID(),"Date", str));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

   */

    /* Function used to remove the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer has been reviewed from this user */
 /*   private boolean removeReviewNeo(String Username, String BeerID){
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

  */

    /* Function used to calculate the IDs of the most reviewed beers this month */
  /*    public ArrayList<String> mostReviewedBeers(){
        return beerManagerDB.mostReviewedBeers();

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
         */
}