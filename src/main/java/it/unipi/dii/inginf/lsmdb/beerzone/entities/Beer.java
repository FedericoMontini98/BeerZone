package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;

public class Beer {
    protected String beerID;
    protected String beerName;

    public Beer(String beerID, String beerName) {
        this.beerID = beerID;
        this.beerName = beerName;
    }

    public Beer (Document beer) {
        beerID = beer.getObjectId("_id").toString();
    }

    public String getBeerID() {
        return beerID;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerID(String beerID) {
        this.beerID = beerID;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public Document getBeerDoc() {
        return new Document("_id", beerID).append("name", beerName);
    }
}
