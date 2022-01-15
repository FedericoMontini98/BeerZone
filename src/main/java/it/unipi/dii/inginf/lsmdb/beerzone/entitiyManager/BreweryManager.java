package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entityManagerDB.GeneralUserManagerDB;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.*;

public class BreweryManager {
    private static BreweryManager breweryManager;
    private final GeneralUserManagerDB generalUserManagerDB;
    private final MongoCollection<Document> breweriesCollection;

    private BreweryManager() {
        breweriesCollection = MongoManager.getInstance().getCollection("users");
        generalUserManagerDB = GeneralUserManagerDB.getInstance();
    }

    public static BreweryManager getInstance() {
        if (breweryManager == null)
            breweryManager = new BreweryManager();
        return breweryManager;
    }

    public boolean addNewBeerToBrewery(Brewery brewery, DetailedBeer beer) {
        try {
            BeerManager.getInstance().addNewBeer(beer);
            generalUserManagerDB.addBeerToBrewery(beer);
            brewery.addBeerToBrewery(beer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeBeer(DetailedBeer beer, Brewery brewery){
        if(generalUserManagerDB.deleteBeerFromBrewery(beer)) {
            brewery.deleteBeerFromBrewery(beer);
            return BeerManager.getInstance().removeBeer(beer);
        }
        return false;
    }

    public double getBreweryScore(String breweryID) {
        try {
            return BeerManager.getInstance().getBreweryScore(breweryID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public double getWeightedBreweryScore(String breweryID) {
        try {
            return BeerManager.getInstance().getWeightedBreweryScore(breweryID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /* *************************************  MongoDB Section  ****************************************************/

    public boolean addBrewery(Brewery brewery) {
        return generalUserManagerDB.registerUser(brewery);
   /*     try {
            breweriesCollection.insertOne(brewery.getBreweryDoc(false));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    */
    }

    public Brewery getBrewery(String breweryID) {
        Brewery brewery = null;
        try {
            Document doc = generalUserManagerDB.getUser(breweryID); //breweriesCollection.find(eq("_id", new ObjectId(breweryID))).first();
            if (doc != null)
                brewery = new Brewery(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brewery;
    }

    public ArrayList<Brewery> browseBreweries(int page, @Nullable String name) {
        /*name = name != null ? name : "";
        int limit = 13;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = breweriesCollection.find(and(eq("type", 1),
                regex("username", "^" + name + ".*", "i")))
                .skip(n).limit(limit+1);
*/
        ArrayList<Brewery> breweryList = new ArrayList<>();
        for (Document brewery: generalUserManagerDB.browseBreweries(page, name)) {
            breweryList.add(new Brewery(brewery));
        }
        return breweryList;
    }

    public boolean updateBrewery(Brewery brewery) {
        return generalUserManagerDB.updateUser(brewery.getBreweryDoc(true), brewery.getUserID());
        /*
        try {
            UpdateResult updateResult = breweriesCollection.replaceOne(eq("_id", new ObjectId(brewery.getUserID())),
                    (brewery.getBreweryDoc(true)));
            if (updateResult.getMatchedCount() == 1)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

         */
    }

 /*   public ArrayList<Beer> getBreweryBeers(int page, String name){
        int limit = 13;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = breweriesCollection.find(and(eq("type", 1), exists("beers"),
                        regex("username", "^" + name + ".*", "i")))
                .skip(n).limit(limit+1)
                .projection(include("username", "beers"));

        ArrayList<Beer> beerList = new ArrayList<>();
        for (Document doc: iterable) {
            List<Document> list = doc.getList("beers", Document.class);
            for (Document d: list) {
                beerList.add(new Beer(d.getObjectId("beer_id").toString(), d.getString("beer_name")));
            }
        }
        //breweriesCollection.find(in("_id", beerList));
        return beerList;
    }

  */

    public boolean deleteBrewery(Brewery brewery) {
        long ret = BeerManager.getInstance().deleteBreweryFromBeers(brewery.getUserID());   // matched beers
        return generalUserManagerDB.deleteUser(brewery) && ret >= brewery.getBeers().size();
    }
/*
    protected boolean addBeerToBrewery(DetailedBeer beer) {
        try {
            UpdateResult updateResult = breweriesCollection.updateOne(eq("_id", new ObjectId(beer.getBreweryID())),
                    addToSet("beers", new Document("beer_id", new ObjectId(beer.getBeerID()))
                            .append("beer_name", beer.getBeerName())));
            return updateResult.getMatchedCount() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

 */

/*
    private boolean deleteBeerFromBrewery(DetailedBeer beer) {
        UpdateResult updateResult = breweriesCollection.updateOne(eq("_id", new ObjectId(beer.getBreweryID())),
                pull("beers", eq("beer_id", new ObjectId(beer.getBeerID()))));
        return updateResult.getMatchedCount() == 1;
    }

 */
}