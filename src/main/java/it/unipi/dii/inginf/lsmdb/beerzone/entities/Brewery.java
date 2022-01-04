package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;

import java.util.*;

public class Brewery extends GeneralUser {
    //private GeneralUser brewery;
    private String types;   //brewery type: bar, pub, etc.
    private List<Integer> beers;

    /* _id is from database, if null is a new brewery */
    public Brewery(String _id, String email, String username, String password, String location, String types) {
        //brewery = new GeneralUser
        super(_id, email, username, password, location, 1);
        this.types = types;
        beers = null;
    }

    public Brewery(String email, String username, String password, String location, String types) {
        this(null, email, username, password, location, types);
    }

    public Brewery(Document doc) {
        //this.brewery = new GeneralUser(doc);
        super(doc);
        this.types = doc.getString("types");
        this.beers = doc.getList("beers", Integer.class);
    }

    public Brewery(List<Integer> beers) {
        //this.brewery = null;
        this.types = null;
        this.beers = beers;
    }
/*
    public GeneralUser getBrewery() {
        return brewery;
    }
*/
    public String getTypes() {
        return types;
    }

    public List<Integer> getBeers() {
        return beers;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public void setBeers(ArrayList<Integer> beers) {
        this.beers = beers;
    }

    public boolean addToBrewery(int beer) {
        return beers.add(beer);
    }

    public boolean deleteFromBrewery(Integer beer) {
        return beers.remove(beer);
    }

    public Document getBreweryDoc(boolean update) {
        //return brewery.getUserDoc(update)
        return super.getUserDoc(update).append("types", types)
                .append("beers", new ArrayList<Integer>());
    }

}
