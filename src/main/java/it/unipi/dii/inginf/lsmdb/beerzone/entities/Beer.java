package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Beer {
   // protected ObjectId beerID;
    protected String beerID;
    protected String beerName;
    protected String style;
    protected double abv;
    protected double score;

    public Beer() {}

    public Beer(@Nullable String beerID, String beerName, String style, @Nullable String abv, double score) {
        this.beerID = beerID != null ? beerID : new ObjectId().toString();
        this.beerName = beerName;
        this.style = style;
        this.abv = abv != null ? Double.parseDouble(abv) : -1;
        this.score = score;
    }

    public Beer(String beerName, String style, String abv, double score) {
        this(null, beerName, style, abv, score);
    }

    public Beer(String beerID, String beerName, String style, String abv) {
        this(beerID, beerName, style, abv, 0);
    }

    public Beer(String beerID, String beerName) {
        this(beerID, beerName, "-", "-1", 0);
    }

    public Beer (Document beer) {
        this(beer.getObjectId("_id").toString(), beer.getString("name"), beer.getString("style"),
                beer.getString("abv"), Double.parseDouble(beer.getString("rating")));
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
        return String.valueOf(abv);
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
        this.abv = Double.parseDouble(abv);
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Document getBeerDoc() {
        return new Document("_id", new ObjectId(beerID))
                .append("name", beerName)
                .append("style", style)
                .append("abv", abv)
                .append("rating", score);
    }

    public Document getBeerNameDoc() {
        return new Document("beer_id", new ObjectId(beerID))
                .append("beer_name", beerName);
    }
}
