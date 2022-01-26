package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entityDBManager.GeneralUserDBManager;
import org.bson.Document;

import java.util.ArrayList;

public class BreweryManager {
    private static BreweryManager breweryManager;
    private final GeneralUserDBManager generalUserManagerDB;

    private BreweryManager() {
        generalUserManagerDB = GeneralUserDBManager.getInstance();
    }

    public static BreweryManager getInstance() {
        if (breweryManager == null)
            breweryManager = new BreweryManager();
        return breweryManager;
    }

    /** method used to manage the addition of a new beer in the database and in the local Brewery object
     * @param beer Beer object to add
     * @return true if all operations was successful
     * */
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

    /** method used to manage the deletion of a beer from the database and from the local instance of a Brewery
     * @param beer Beer object to remove
     * @param brewery Brewery from which to remove the beer
     * @return true if all operations was successful
     * */
    public boolean removeBeer(DetailedBeer beer, Brewery brewery){
        if(generalUserManagerDB.deleteBeerFromBrewery(beer)) {
            brewery.deleteBeerFromBrewery(beer);
            return BeerManager.getInstance().removeBeer(beer);
        }
        return false;
    }

    /** method to obtain the score for a Brewery by BeerManager
     * @param breweryID id of the brewery you want the score
     * @return the computed score
     * */
    public double getBreweryScore(String breweryID) {
        try {
            return BeerManager.getInstance().getBreweryScore(breweryID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /* *************************************  MongoDB Section  ****************************************************/

    /** method called when a new Brewery want to register into the application,
     * used to add it into the user collection
     * @param brewery Brewery object to add
     * @return true if the operation was successful
     * */
    public boolean addBrewery(Brewery brewery) {
        return generalUserManagerDB.registerUser(brewery);
    }

    /** method to get a Brewery from its id
     * @param breweryID id of brewery to search
     * @return the Brewery found, null if no matching
     * */
    public Brewery getBrewery(String breweryID) {
        Brewery brewery = null;
        try {
            Document doc = generalUserManagerDB.getUser(breweryID);
            if (doc != null)
                brewery = new Brewery(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brewery;
    }

    /** method used to send a request for browsing through the collection of breweries in MongoDB
     * @param page page of the table displayed in the Gui, used to skip the breweries already shown
     * @param name initial characters of brewery name
     * @return a list of Brewery object created with Documents found in the collection
     * */
    public ArrayList<Brewery> browseBreweries(int page, @Nullable String name) {

        ArrayList<Brewery> breweryList = new ArrayList<>();
        for (Document brewery: generalUserManagerDB.browseBreweries(page, name)) {
            breweryList.add(new Brewery(brewery));
        }
        return breweryList;
    }

    /** method to update brewery information
     * @param brewery object containing the new values
     * @return true if the operation was successful
     * */
    public boolean updateBrewery(Brewery brewery) {
        return generalUserManagerDB.updateUser(brewery.getBreweryDoc(true), brewery.getUserID());
    }

    /** method for managing the deletion of a Brewery from the database,
     * and update also the corresponding field in the beers it produces
     * @param brewery Brewery to delete
     * @return true if all operations was successful */
    public boolean deleteBrewery(Brewery brewery) {
        long ret = BeerManager.getInstance().deleteBreweryFromBeers(brewery.getUserID());   // matched beers
        return generalUserManagerDB.deleteUser(brewery) && ret >= brewery.getBeers().size();
    }

    public boolean updateBeerInBrewery(Beer beer, Brewery brewery) {
        if (generalUserManagerDB.updateBeerInBrewery(beer, brewery)) {
            brewery = getBrewery(brewery.getUserID());
            return brewery != null;
        }
        return false;
    }

}