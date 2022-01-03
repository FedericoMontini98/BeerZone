package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;

public class BreweryManager {
    private static BreweryManager breweryManager;
    //private final MongoManager mongoManager;
    private MongoCollection<Document> breweries;

    private BreweryManager() {
        //mongoManager = MongoManager.getInstance();
        breweries = MongoManager.getInstance().getCollection("finalUsers");
    }

    public static BreweryManager getInstance() {
        if (breweryManager == null)
            breweryManager = new BreweryManager();
        return breweryManager;
    }

   public ArrayList<ObjectId> getBeerList(int page, String name){
        int limit = 20;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = breweries.find(and(eq("type", 1), exists("beers"),
                        regex("username", ".*" + name + ".*", "i")))
                .skip(n).limit(limit+1)
                .projection(include("username", "beers"));

        ArrayList<ObjectId> beerList = new ArrayList<>();
        for (Document beer: iterable) {
            beerList.addAll(beer.getList("beers", ObjectId.class));
        }
        return beerList;
    }
}
