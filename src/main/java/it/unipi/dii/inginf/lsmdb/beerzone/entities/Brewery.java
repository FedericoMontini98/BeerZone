package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class Brewery extends GeneralUser {
    //private GeneralUser brewery;
    private String types;   //brewery type: bar, pub, etc.
    private List<Beer> beers;

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
        this.beers = new ArrayList<>();
        List<Document> list = doc.getList("beers", Document.class);
        for (Document d: list) {
            beers.add(new Beer(d.get("beer_id").toString(), d.getString("beer_name")));
        }
    }

/*
    public GeneralUser getBrewery() {
        return brewery;
    }
*/
    public String getTypes() {
        return types;
    }

    public List<Beer> getBeers() {
        return beers;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public void setBeers(ArrayList<Beer> beers) {
        this.beers = beers;
    }

    public boolean addToBrewery(Beer beer) {
        return beers.add(beer);
    }

    public boolean deleteFromBrewery(Beer beer) {
        return beers.remove(beer);
    }

    public List<Document> getBeerList() {
        ArrayList<Document> beersList = new ArrayList<>();
        for (Beer b: beers) {
            beersList.add( b.getBeerNameDoc());
        }
        return beersList;
    }

    public Document getBreweryDoc(boolean update) {
        //return brewery.getUserDoc(update)
        Document doc = super.getUserDoc().append("types", types);
        if (update)
            doc.append("beers", getBeerList());
        else    // new Brewery
            doc.append("beers", new ArrayList<>());
        return doc;
    }

}
