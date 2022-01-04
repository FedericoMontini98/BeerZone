package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.*;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;

public class BeerManager {
    //private Beer beer;
    private static BeerManager beerManager;
    //private final MongoManager mongoManager;
    private final MongoCollection<Document> beersCollection;


    private BeerManager(){
        //mongoManager = MongoManager.getInstance();
        beersCollection = MongoManager.getInstance().getCollection("beer");
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



    /*
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
