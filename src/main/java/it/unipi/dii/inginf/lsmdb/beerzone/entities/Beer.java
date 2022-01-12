package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Beer {
   // protected ObjectId beerID;
    protected String beerID;
    protected String beerName;
    protected String style;
    protected Double abv;
    protected Double score;

    public Beer() {}

    public Beer(@Nullable String beerID, String beerName, String style, @Nullable String abv, @Nullable String score) {
        this.beerID = beerID != null ? beerID : new ObjectId().toString();
        this.beerName = beerName;
        this.style = style;
        this.abv = abv != null ? Double.parseDouble(abv) : -1;
        this.score = score != null ? Double.parseDouble(score) : -1;
    }

    public Beer(String beerName, String style, String abv, @Nullable String score) {
        this(null, beerName, style, abv, score);
    }

    /*public Beer(String beerID, String beerName, String style, String abv) {
        this(beerID, beerName, style, abv, "-1");
    }
     */

    public Beer(String beerID, String beerName) {
        this(beerID, beerName, "-", "-1", "-1");
    }

    public Beer (Document beer) {
        this(beer.getObjectId("_id").toString(), beer.getString("name"),
                beer.get("style") != null ? beer.getString("style") : "--",
                beer.get("abv") != null ? beer.get("abv").toString() : "-1",
                beer.get("rating") != null ? beer.get("rating").toString() : "-1");
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
