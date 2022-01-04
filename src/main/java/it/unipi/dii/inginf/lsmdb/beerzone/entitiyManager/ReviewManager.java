package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;

public class ReviewManager {
    private static ReviewManager reviewManager;
    //private final MongoManager mongoManager;
    private MongoCollection<Document> reviewsCollection;

    private ReviewManager() {
        //mongoManager = MongoManager.getInstance();
        reviewsCollection = MongoManager.getInstance().getCollection("reviews");
    }

    public static ReviewManager getInstance() {
        if (reviewManager == null)
            reviewManager = new ReviewManager();
        return reviewManager;
    }
}
