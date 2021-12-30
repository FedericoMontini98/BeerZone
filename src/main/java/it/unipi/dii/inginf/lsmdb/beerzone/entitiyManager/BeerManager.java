package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;

public class BeerManager {
    private Beer beer;
    private MongoManager mongoManager;
    private MongoCollection<Document> beersCollection;


    public BeerManager(){
        mongoManager = MongoManager.getInstance();
        beersCollection = mongoManager.getCollection("beer");
    }

    public BeerManager (Beer beer) {
        this();
        this.beer = beer;
    }

    public void addNewBeer(Document beerDoc) {
        beersCollection.insertOne(beerDoc);
    }

    /* page start from 0 ? */
    public ArrayList<Beer> showBeers(int page) {
        int limit = 20;
        int n = page * limit;

        FindIterable iterator = beersCollection.find().skip(n).limit(limit);

        ArrayList<Beer> beerList = new ArrayList<>();
        return beerList;
    }

    public ArrayList<Beer> findBeers(String beerName) {
        ArrayList<Beer> beerList = new ArrayList<>();
        try {

            for (Document beerDoc : beersCollection.find(
                            regex("name", ".*" + beerName + ".*", "-i"))
                    .limit(25)) {
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
