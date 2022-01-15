package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entityDBManager.GeneralUserManagerDB;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;

import java.util.ArrayList;

public class BreweryManager {
    private static BreweryManager breweryManager;
    private final GeneralUserManagerDB generalUserManagerDB;

    private BreweryManager() {
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

        ArrayList<Brewery> breweryList = new ArrayList<>();
        for (Document brewery: generalUserManagerDB.browseBreweries(page, name)) {
            breweryList.add(new Brewery(brewery));
        }
        return breweryList;
    }

    public boolean updateBrewery(Brewery brewery) {
        return generalUserManagerDB.updateUser(brewery.getBreweryDoc(true), brewery.getUserID());
    }

    public boolean deleteBrewery(Brewery brewery) {
        long ret = BeerManager.getInstance().deleteBreweryFromBeers(brewery.getUserID());   // matched beers
        return generalUserManagerDB.deleteUser(brewery) && ret >= brewery.getBeers().size();
    }

}