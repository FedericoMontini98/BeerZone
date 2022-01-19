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
                System.out.println("new rating: " + newRating);
                beer.setNumRating(num_rating);
                beer.setScore(newRating);

                return newRating;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // compute score from reviews in the beers collection
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


    /* *******************************************  Neo4J Section  ************************************************/

    /* Function used to calculate the IDs of the most reviewed beers this month */
    public ArrayList<Beer> mostReviewedBeers() {
        return beerManagerDB.mostReviewedBeers();
    }

}