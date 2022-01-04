package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;

public class BreweryManager {
    private MongoManager mongoManager;
    private MongoCollection<Document> breweries;

    public BreweryManager() {
        mongoManager = MongoManager.getInstance();
        breweries = mongoManager.getCollection("brewery");
    }
}
