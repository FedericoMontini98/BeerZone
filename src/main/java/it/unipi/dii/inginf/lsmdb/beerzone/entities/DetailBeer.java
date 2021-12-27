package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;

public class DetailBeer extends Beer {
    private ArrayList<Integer> breweries;
    private int beerScore;
    private int numRating;
    private String style;
    private String method;
    private double og;  // original gravity
    private double fg;  // final gravity
    private double abv;
    private double ibu;
    private MongoCollection<Document> recipe;
    /* private double batch;
    * private double color;
    * private double phMash;  // -1 if is not present on the source
    */
    public DetailBeer(String beerID, String beerName) {
        super(beerID, beerName);
    }
}
