package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.client.MongoCollection;
import com.mongodb.lang.Nullable;
import org.bson.Document;

import java.util.ArrayList;

public class DetailedBeer extends Beer {
    // id, name, style, score
    private String brewery;
    //private int beerScore;
    private int numRating;
    private String method;
    private double og;  // original gravity
    private double fg;  // final gravity
    private double ibu;
    private double batch;
    private double color;
    private double phMash;  // -1 if is not present on the source

    public DetailedBeer(String beerID, String beerName, String style, String abv, double score) {
        super(beerID, beerName, style, abv, score);
    }

    public DetailedBeer(String beerID, String beerName, String style, String abv, double score, @Nullable String brewery,
                        int numRating, String method, double og, double fg, double ibu,
                        @Nullable double batch, @Nullable double color, @Nullable double phMash) {
        super(beerID, beerName, style, abv, score);
        this.brewery = brewery;
        this.numRating = numRating;
        this.method = method;
        this.og = og;
        this.fg = fg;
        this.ibu = ibu;
        this.batch = batch;
        this.color = color;
        this.phMash = phMash;
    }

    public String getBrewery() {
        return brewery;
    }

    public String getNumRating() {
        return String.valueOf(numRating);
    }

    public String getMethod() {
        return method;
    }

    public String getOg() {
        return String.valueOf(og);
    }

    public String getFg() {
        return String.valueOf(fg);
    }

    public String getIbu() {
        return String.valueOf(ibu);
    }

    public String getBatch() {
        return String.valueOf(batch);
    }

    public String getColor() {
        return String.valueOf(color);
    }

    public String getPhMash() {
        return String.valueOf(phMash);
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    public void setNumRating(int numRating) {
        this.numRating = numRating;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setOg(double og) {
        this.og = og;
    }

    public void setFg(double fg) {
        this.fg = fg;
    }
}
