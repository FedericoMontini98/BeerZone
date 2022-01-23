package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Review;
import it.unipi.dii.inginf.lsmdb.beerzone.entityDBManager.BeerDBManager;
import org.bson.Document;
import java.util.ArrayList;

public class ReviewManager {
    private static ReviewManager reviewManager;
    private final BeerDBManager beerManagerDB;

    private ReviewManager() {
        beerManagerDB = BeerDBManager.getInstance();
    }

    public static ReviewManager getInstance() {
        if (reviewManager == null)
            reviewManager = new ReviewManager();
        return reviewManager;
    }

    /* ****************************************  MongoDB Section  *************************************************/

    /** method to get a review from the review list in the beer object, given a username
     * @param username user who wrote the review
     * @param beer Beer for which the review was written, that stores also a local list of reviews
     * @return a Review object of found review, null if no matching
     * */
    public Review getReview(String username, DetailedBeer beer) {
        for (Review r: beer.getReviewList()) {
            if (username.equalsIgnoreCase(r.getUsername()))
                return r;
        }
        return null;
    }

    /** method to manage adding of a review in both databases and in the review list of the local Beer object,
     * computing also the new score of the beer
     * @param review Review to add
     * @param beer beer for which the review was written
     * @return true if all operation was successful
     * */
    public boolean addNewReview(Review review, DetailedBeer beer) {
        if (!beerManagerDB.existsReview(review.getUsername(), beer)) {
            double new_rating = computeNewBeerRating(review, beer, true);
            //System.out.println(beer.getNumRating() + " rev");
            if (beerManagerDB.addReviewToBeersCollection(review, beer, new_rating)) {
                beer.addReviewToBeer(review);   // add review to local list in DetailedBeer
                return beerManagerDB.addReviewNeo(review, beer);
            }
        }
        return false;
    }

    /* Delete a review both from MongoDB and Neo4J */
    /** method to manage the deletion of a review from MongoDB and the corresponding relationship on Neo4J
     * @param username user who wrote the review and want to delete it
     * @param beer Beer for which the review was written
     * @return true if all operations were successful
     * */
    public boolean deleteReview(String username, DetailedBeer beer) {
        if(beerManagerDB.existsReview(username, beer)) {
            double rating = computeNewBeerRating(getReview(username, beer), beer, false);
            if (beerManagerDB.deleteReviewMongo(username, beer, rating)) {
                beer.removeReviewFromBeer(getReview(username, beer));   // remove review to local list in DetailedBeer
                return beerManagerDB.removeReviewNeo(username, beer.getBeerID());
            }
        }
        return false;
    }

    /** method to request the deletion of the username in the reviews, when a user delete its own account
     * @param username User to delete from reviews he wrote
     * @return true if the operation was successful
     * */
    protected boolean deleteUserFromReviews(String username) {
        return beerManagerDB.deleteUserFromReviews(username);
    }

    /** method to calculate the new rating of a beer when a review is added or removed
     * @param review Review object to add or to remove from which get the score for the computation
     * @param beer Beer for which the review was written
     * @param add a boolean that indicate if the operation is an addition or a deletion of the review
     * @return the computed value, -1 if error or the beer no longer has reviews
     * */
    private double computeNewBeerRating(Review review, DetailedBeer beer, boolean add) {
        try {
            Document doc = beerManagerDB.getDetailedBeer(beer.getBeerID());
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
                        newRating = -1;
                        num_rating = 0;
                    }
                    else
                        newRating = (oldTotalRating - review.getNumericScore()) / (--num_rating);
                }
                newRating = (double) (Math.round(newRating * 100)) / 100;
//                System.out.println("new rating: " + newRating);
                beer.setNumRating(num_rating);
                beer.setScore(newRating);

                return newRating;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /* *******************************************  Neo4J Section  ************************************************/

    /* Function used to calculate the IDs of the most reviewed beers this month */
    public ArrayList<Beer> mostReviewedBeers() {
        return beerManagerDB.mostReviewedBeers();
    }

}