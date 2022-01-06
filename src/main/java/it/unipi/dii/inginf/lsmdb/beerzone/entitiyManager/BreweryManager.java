package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;

public class BreweryManager {
    private static BreweryManager breweryManager;
    //private final MongoManager mongoManager;
    private final MongoCollection<Document> breweriesCollection;

    private BreweryManager() {
        //mongoManager = MongoManager.getInstance();
        breweriesCollection = MongoManager.getInstance().getCollection("finalUsers");
    }

    public static BreweryManager getInstance() {
        if (breweryManager == null)
            breweryManager = new BreweryManager();
        return breweryManager;
    }

    public ArrayList<Brewery> browseBreweries(int page, @Nullable String name) {
        name = name != null ? name : "";
        int limit = 20;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = breweriesCollection.find(and(eq("type", 1),
                regex("username", ".*" + name + ".*", "i")))
                .skip(n).limit(limit+1)
                .projection(include("name", "style", "abv", "rating"));

        ArrayList<Brewery> breweryList = new ArrayList<>();
        for (Document brewery: iterable) {
            breweryList.add(new Brewery(brewery));
        }
        return breweryList;
    }

    public boolean updateBrewery(Brewery brewery) {
        try {
            UpdateResult updateResult = breweriesCollection.replaceOne(eq("_id", new ObjectId(brewery.getUserID())),
                    (brewery.getBreweryDoc(true)));
            if (updateResult.getMatchedCount() == 1)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<ObjectId> getBeerList(int page, String name){
        int limit = 20;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = breweriesCollection.find(and(eq("type", 1), exists("beers"),
                        regex("username", ".*" + name + ".*", "i")))
                .skip(n).limit(limit+1)
                .projection(include("username", "beers"));

        ArrayList<ObjectId> beerList = new ArrayList<>();
        for (Document beer: iterable) {
            beerList.addAll(beer.getList("beers", ObjectId.class));
        }
        breweriesCollection.find(in("_id", beerList));
        return beerList;
    }
}