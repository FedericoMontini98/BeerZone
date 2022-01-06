package it.unipi.dii.inginf.lsmdb.beerzone.managerDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoManager {
    private static MongoManager mongoManager;
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    /* connection string */
    String localConnection = "mongodb://localhost:27018";
    //private String remoteConnection = "mongodb://172.16.4.57:27020";

    private MongoManager() {
        mongoClient = MongoClients.create(localConnection);
        database = mongoClient.getDatabase("BeerZone");
    }

    public static MongoManager getInstance() {
        if (mongoManager == null)
            mongoManager = new MongoManager();
        return mongoManager;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        if (mongoManager == null)
            throw new RuntimeException("Mongo Connection does not exist!");

        return database.getCollection(collectionName);
    }

    /* called when one closes application -> end of starts() */
    public static void closeConnection() {
        if (mongoClient != null)
            mongoClient.close();
        System.out.println("MongoDB connection closed");
    }
}
