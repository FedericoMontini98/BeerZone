package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.FindIterable;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entityDBManager.BeerDBManager;
import org.bson.Document;

import java.util.*;

public class BeerManager {
    private static BeerManager beerManager;
    private final BeerDBManager beerManagerDB;

    private BeerManager(){
        beerManagerDB = BeerDBManager.getInstance();
    }

    public static BeerManager getInstance() {
        if (beerManager == null)
            beerManager = new BeerManager();
        return beerManager;
    }

    /** method used when a new beer have to be added in the database
     * @param beer Beer object that has to be inserted in the database
     */
    public void addNewBeer(DetailedBeer beer) {
        beerManagerDB.addNewBeerMongo(beer);
    }

    /** method that manages the deletion of a beer from both databases
     * @param beer Beer object to remove from DBs
     * @return true if operation on both DBs was successful
     * */
    protected boolean removeBeer(Beer beer) {
        if(beerManagerDB.removeBeerMongo(beer)) {
            beerManagerDB.removeBeerFromNeo(beer);
            return true;
        }
        return false;
    }

    /** method used to send a request for browsing through the collection of beers in MongoDB
     * @param page page of the table displayed in the Gui, used to skip the beers already shown
     * @param name initial characters of beer name or style name
     * @return a list of Beers object created with Documents found in the collection
     * */
    public ArrayList<Beer> browseBeers(int page, @Nullable String name) {
        ArrayList<Beer> beerList = new ArrayList<>();
        FindIterable<Document> iterable = beerManagerDB.browseBeers(page, name);

        for (Document beer: iterable) {
            beerList.add(new Beer(beer));
        }
        return beerList;
    }

    /** method used to send a search request for a beer by ID in MongoDB
     * @param beerID id of the required beer
     * @return the Beer object required, null if no beer with that id is found
     * */
    public Beer getBeer(String beerID) {
        Beer beer = null;
        Document doc = beerManagerDB.getBeer(beerID);
        if (doc != null)
            beer = new Beer(doc);

        return beer;
    }

    /** method used to send a search request for a detailed beer by ID in MongoDB
     * @param beerID id of the required beer
     * @return the DetailedBeer object required, null if no beer with that id is found
     * */
    public DetailedBeer getDetailedBeer(String beerID) {
        DetailedBeer beer = null;
        Document doc = beerManagerDB.getDetailedBeer(beerID);

        if (doc != null)
            beer = new DetailedBeer(doc);

        return beer;
    }

    /** method used to request a deletion of the link between a Brewery and all its Beers
     * @param breweryID id of the brewery to remove
     * @return the number of beer of that brewery, -1 if no beers are found
     * */
    public long deleteBreweryFromBeers(String breweryID) {
        return beerManagerDB.deleteBreweryFromBeers(breweryID);
    }

    /** method to request the list of the best beers of the month
     * @return a list of top beers
     * */
    public ArrayList<Beer> getHighestAvgScoreBeers() {
        ArrayList<Beer> beers = new ArrayList<>();
        for (Document doc: beerManagerDB.getHighestAvgScoreBeers()) {
            Document idDoc = (Document) doc.get("_id");
            if (idDoc != null) {
                Beer b = new Beer(idDoc);
                b.setScore(doc.get("monthly_score") != null ? Double.parseDouble(doc.get("monthly_score").toString()) : -1);
                beers.add(b);
            }
        }
        return beers;
    }

    /** method to get the list of beers below the brewery score for a given feature
     * @param brewery Brewery for which you want to know the beer list
     * @return the list of beers required
     * */
    public ArrayList<Beer> getBeersUnderAvgFeatureScore(Brewery brewery, String feature) {
        ArrayList<Beer> beers = new ArrayList<>();
        for (Document doc: beerManagerDB.getBeersUnderAvgFeatureScore(brewery.getUserID(), feature,
                getBreweryScore(brewery.getUserID()))) {
            Document idDoc = (Document) doc.get("_id");
            if (idDoc != null) {
                Beer b = new Beer(idDoc);
                b.setScore(doc.get("feature_score") != null ? Double.parseDouble(doc.get("feature_score").toString()) : -1);
                beers.add(b);
            }
        }
        return beers;
    }

    /** method to update beer information
     * @param beer Beer object containing the new values
     * @return true if the operation was successful
     * */
    public boolean updateBeer(DetailedBeer beer, Brewery brewery) {
        if(beerManagerDB.updateBeer(beer)) {
           if(BreweryManager.getInstance().updateBeerInBrewery(beer, brewery))
               return beerManagerDB.updateBeerNeo(beer);
        }
        return false;
    }

    /** method that can be used to compute and update the rating for a beer
     * with the scores received in the reviews
     * @param beer Beer you want to calculate the rating for
     * */
    public void recomputeBeerRating(DetailedBeer beer) {
        Document docRating = beerManagerDB.recomputeBeerRating(beer);

        if (docRating != null) {
            if (docRating.get("rating") != null)
                beer.setScore(Double.parseDouble(docRating.get("rating").toString()));
            if (docRating.get("num_rating") != null)
                beer.setNumRating(docRating.getInteger("num_rating"));
        }
    }

    /** method to obtain the score for a Brewery
     * @param breweryID id of the brewery you want the score
     * @return the computed score
     * */
    protected double getBreweryScore(String breweryID) {
        Document doc = beerManagerDB.getBreweryScore(breweryID);
        if (doc != null)
            return doc.get("brewery_score") != null ? Double.parseDouble(doc.get("brewery_score").toString()) : -1;

        return -1;
    }

    /** method to get the top 3 favorite styles, based on review scores
     * @return the list of the styles found
     * */
    public ArrayList<Style> getTopStyleScore() {
        ArrayList<Style> topStyles = new ArrayList<>();
        for (Document doc: beerManagerDB.getTopStyleScore()) {
            if (doc != null) {
                if (doc.get("_id") != null ) {
                    String style = doc.getString("_id");
                    double score = doc.get("style_score") != null ? Double.parseDouble(doc.get("style_score").toString()) : -1;
                    topStyles.add(new Style(style, score));
                    //System.out.println(style + ": " + score);
                }
            }
        }
        return topStyles;
    }


    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
    public boolean addBeer(Beer beer) {
        return beerManagerDB.addBeerNeo(beer);
    }

    /* Function that based on the user current research find some beers to suggest him based on the beer style and favorites of
     *  others users */
    public ArrayList<String> getSuggested(StandardUser user) {
        return beerManagerDB.getSuggested(user);
    }

    /* Function that calculate the most favorite beers in the past month */
    public ArrayList<FavoriteBeer> getMostFavoriteThisMonth () {
        return beerManagerDB.getMostFavoriteThisMonth();
    }

}
