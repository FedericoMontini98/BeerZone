package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import java.util.ArrayList;

public class DetailBeer extends Beer {
    private ArrayList<Integer> breweries;
    private int beerScore;
    private int numRating;
    private String style;
    private String method;
    private double batch;
    private double og;  // original gravity
    private double fg;  // final gravity
    private double abv;
    private double ibu;
    private double color;
    private double phMash;  // -1 if is not present on the source

    public DetailBeer(int beerID, String beerName) {
        super(beerID, beerName);
    }
}
