package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Review;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.neo4j.driver.Values.parameters;

public class ReviewManager {
    private static ReviewManager reviewManager;
    //private final MongoManager mongoManager;
    private final MongoCollection<Document> reviewsCollection;
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

    /* Add a new Review both on MongoDB and Neo4J */
    public boolean addNewReview(Review review, DetailedBeer beer) {
        boolean result_1 = addReviewMongo(review, beer);
        boolean result_2 = addReview(review, beer);
        return (result_1&&result_2);
    }

    /* Delete a review both from MongoDB and Neo4J */
    public boolean deleteReview(Review review) {
        boolean result_1 = deleteReviewMongo(review);
        boolean result_2 = removeReview(review.getUsername(), review.getBeerID());
        return (result_1&&result_2);
    }

    /* ************************************************************************************************************/
    /* *************************************  MongoDB Section  ****************************************************/
    /* ************************************************************************************************************/

    public Review getReview(String username, String beerID) {
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

    /* add new review in mongoDB*/
    private boolean addReviewMongo(Review review, DetailedBeer beer) {
        if (!existsReview(review)) {
            try {
                reviewsCollection.insertOne(review.getReview());
                return BeerManager.getInstance().updateBeerRating(review, beer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean deleteReviewMongo(Review review) {
        DeleteResult deleteResult = reviewsCollection.deleteOne(and(eq("username", review.getUsername()),
                eq("beer_id", new ObjectId(review.getBeerID()))));
        return (deleteResult.getDeletedCount() == 1);
    }

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


    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/


    /* Function used to add the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer hasn't been reviewed from this user yet to avoid multiple
     * reviews from the same user which can lead to inconsistency or fake values of the avg. score */
    private boolean addReview(Review review, Beer beer){
        try(Session session = NeoDBMS.getDriver().session()){
            //Check if user exists
            UserManager.getInstance().addStandardUser(review.getUsername());
            //Check if beer exists
            BeerManager.getInstance().AddBeer(beer);
            //Put the date in the right format
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String str = formatter.format(review.getReviewDateNeo());
            //Create the relationship
            session.run("MATCH\n" +
                            "  (B:Beer),\n" +
                            "  (U:User)\n" +
                            "WHERE U.Username = $Username AND B.ID = $BeerID\n" +
                            "CREATE (U)-[R:Reviewed{date:date($Date)}]->(B)\n",
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