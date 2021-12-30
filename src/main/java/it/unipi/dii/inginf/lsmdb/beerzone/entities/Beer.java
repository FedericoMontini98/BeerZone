package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;

public class Beer {
    protected String beerID;
    protected String beerName;
    protected String style;
    protected String abv;
    protected double score;

    public Beer(String beerID, String beerName, String style, String abv, double score) {
        this.beerID = beerID;
        this.beerName = beerName;
        this.style = style;
        this.abv = abv;
        this.score = score;
    }

    public Beer(String beerID, String beerName) {
        this(beerID, beerName, null, null, 0);
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

    public String getStyle() {
        return style;
    }

    public String getAbv() {
        return abv;
    }

    public String getScore() {
        return String.valueOf(score);
    }

    public void setBeerID(String beerID) {
        this.beerID = beerID;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Document getBeerDoc() {
        return new Document("_id", beerID)
                .append("name", beerName)
                .append("style", style)
                .append("abv", abv)
                .append("rating", score);
    }
}
