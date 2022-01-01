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

    public Beer(String beerID, String beerName, String style, String abv) {
        this(beerID, beerName, style, abv, 0);
    }

    public Beer(String beerID, String beerName) {
        this(beerID, beerName, null, null, 0);
    }

    public Beer (Document beer) {
        beerID = beer.getString("_id");
        beerName = beer.getString("name");
        style = beer.getString("style");
        abv = beer.getString("abv");
        score = Double.parseDouble(beer.getString("rating"));
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

    public Document getBeerDoc(boolean update) {
        Document doc = new Document();
        if (update)
            doc.append("_id", beerID);
        doc.append("name", beerName)
                .append("style", style)
                .append("abv", abv)
                .append("rating", score);
        return doc;
    }
}
