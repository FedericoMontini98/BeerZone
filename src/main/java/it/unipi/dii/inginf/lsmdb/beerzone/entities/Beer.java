package it.unipi.dii.inginf.lsmdb.beerzone.entities;

public class Beer {
    protected int beerID;
    protected String beerName;

    public Beer(int beerID, String beerName) {
        this.beerID = beerID;
        this.beerName = beerName;
    }

    public int getBeerID() {
        return beerID;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerID(int beerID) {
        this.beerID = beerID;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }
}
