package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;

public class ReviewManager {
    private final MongoManager mongoManager;
    private MongoCollection<Document> reviewsCollection;

    public ReviewManager() {
        mongoManager = MongoManager.getInstance();
        reviewsCollection = mongoManager.getCollection("reviews");
    }
}
