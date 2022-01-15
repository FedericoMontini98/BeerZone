package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.FindIterable;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entityDBManager.BeerManagerDB;
import org.bson.Document;

import java.util.*;

public class BeerManager {
    private static BeerManager beerManager;
    private final BeerManagerDB beerManagerDB;

    private BeerManager(){
        beerManagerDB = BeerManagerDB.getInstance();
    }

    public static BeerManager getInstance() {
        if (beerManager == null)
            beerManager = new BeerManager();
        return beerManager;
    }

    public void addNewBeer(DetailedBeer beer) {
        beerManagerDB.addNewBeerMongo(beer);
    }

    protected boolean removeBeer(Beer beer) {
        if(beerManagerDB.removeBeerMongo(beer)) {
            removeBeerFromNeo(beer);
            return true;
        }
        return false;
    }

    public ArrayList<Beer> browseBeers(int page, @Nullable String name) {
        ArrayList<Beer> beerList = new ArrayList<>();
        FindIterable<Document> iterable = beerManagerDB.browseBeers(page, name);

        for (Document beer: iterable) {
            beerList.add(new Beer(beer));
        }
        return beerList;
    }
    public Beer getBeer(String beerID) {
        Beer beer = null;
        Document doc = beerManagerDB.getBeer(beerID);
        if (doc != null)
            beer = new Beer(doc);

        return beer;
    }

    public DetailedBeer getDetailedBeer(String beerID) {
        DetailedBeer beer = null;
        Document doc = beerManagerDB.getDetailedBeer(beerID);

        if (doc != null)
            beer = new DetailedBeer(doc);

        return beer;
    }

    public long deleteBreweryFromBeers(String breweryID) {
        return beerManagerDB.deleteBreweryFromBeers(breweryID);
    }

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

    public boolean updateBeer(DetailedBeer beer) {
        return beerManagerDB.updateBeer(beer);
    }

    public void recomputeBeerRating(DetailedBeer beer) {
        Document docRating = beerManagerDB.updateBeerRating(beer);

        if (docRating != null) {
            if (docRating.get("rating") != null)
                beer.setScore(Double.parseDouble(docRating.get("rating").toString()));
            if (docRating.get("num_rating") != null)
                beer.setNumRating(docRating.getInteger("num_rating"));
        }
    }

    protected double getBreweryScore(String breweryID) {
        Document doc = beerManagerDB.getBreweryScore(breweryID);

        if (doc != null)
            return doc.get("brewery_score") != null ? Double.parseDouble(doc.get("brewery_score").toString()) : -1;

        return -1;
    }

    protected double getWeightedBreweryScore(String breweryID) {
        Document doc = beerManagerDB.getWeightedBreweryScore(breweryID);

        if (doc != null) {
            return doc.get("brewery_score") != null ? Double.parseDouble(doc.get("brewery_score").toString()) : -1;
        }
        return -1;
    }

    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
    public boolean addBeer(Beer beer) {
        return beerManagerDB.addBeer(beer);
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

    protected void removeBeerFromNeo(Beer beer) {
        beerManagerDB.removeBeerFromNeo(beer);
    }

}
