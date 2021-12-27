package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;

import java.util.ArrayList;

public class Brewery {
    private final GeneralUser brewery;
    private ArrayList<Integer> beers;

    /* _id is from database, if -1 is a new brewery */
    public Brewery(int _id, String email, String username, String password, String location) {
        brewery = new GeneralUser(_id, email, username, password, location, 1);
    }

    public Brewery(String email, String username, String password, String location) {
        this(-1, email, username, password, location);
    }

    public Brewery(GeneralUser user) {
        brewery = user;
    }

    public ArrayList<Integer> getBeers() {
        return beers;
    }

    public void setBeers(ArrayList<Integer> beers) {
        this.beers = beers;
    }

    // mettere solo in beerManager ?
    public boolean addBeer(int beer) {
        return beers.add(beer);
    }

    public boolean deleteBeer(Integer beer) {
        return beers.remove(beer);
    }

    public Document getBrewery() {
        return brewery.getUser().append("beers", new ArrayList<Integer>());
        //  adding or not an empty Array for beerlist?
    }

}
