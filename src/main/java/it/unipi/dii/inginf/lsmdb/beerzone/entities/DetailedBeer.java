package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.lang.Nullable;
import org.bson.Document;

public class DetailedBeer extends Beer {
    // id, name, style, score
    private String brewery_id;
    //private int beerScore;
    private int numRating;
    //private String state;
    //private String country;
    private String availability;
    private String notes;
    private boolean retired;
    private String url;
    private String method;
    private double og;  // original gravity
    private double fg;  // final gravity
    private double ibu;
    //private double batch;
    private double color;
    private double phMash;  // -1 if is not present on the source
    private String fermentables;
    private String hops;
    private String other;
    private String yeast;

    public DetailedBeer() {}

    public DetailedBeer(@Nullable String beerID, String beerName, String style, String abv, double score) {
        super(beerID, beerName, style, abv, score);
    }

    public DetailedBeer(@Nullable String beerID, String beerName, String style, String abv, @Nullable String score,
                        @Nullable String brewery, @Nullable  String availability, @Nullable String notes,
                        @Nullable  String url, String retired, @Nullable String method, @Nullable String og,
                        @Nullable String fg, @Nullable String ibu, @Nullable String color, @Nullable String phMash,
                        @Nullable String fermentables, @Nullable String hops, @Nullable String other, @Nullable String yeast) {
        super(beerID, beerName, style, abv, score != null ? Double.parseDouble(score) : -1);
        this.brewery_id = brewery != null ? brewery : "";
        this.numRating = 0;
        this.availability = availability != null ? availability : "";
        this.notes = notes != null ? notes : "=";
        this.url = url != null ? url : "-";
        this.retired = retired.equalsIgnoreCase("t");
        this.method = method != null ? method : "-";
        this.og = og != null ? Double.parseDouble(og) : -1;
        this.fg = fg != null ? Double.parseDouble(fg) : -1;
        this.ibu = ibu != null ? Double.parseDouble(ibu) : -1;
        this.color = color != null ? Double.parseDouble(color) : -1;
        this.phMash = phMash != null ? Double.parseDouble(phMash) : -1;
        this.fermentables = fermentables != null ? fermentables : "-";
        this.hops = hops != null ? hops : "-";
        this.other = other != null ? other : "-";
        this.yeast = yeast != null ? yeast : "-";
    }

    public DetailedBeer(String beerName, String style, String abv, @Nullable String score,
                        @Nullable String brewery, @Nullable  String availability, @Nullable String notes,
                        @Nullable  String url, String retired, @Nullable String method, @Nullable String og,
                        @Nullable String fg, @Nullable String ibu, @Nullable String color, @Nullable String phMash,
                        @Nullable String fermentables, @Nullable String hops, @Nullable String other, @Nullable String yeast) {
        this(null, beerName, style, abv, score, brewery, availability, notes, url, retired, method, og, fg, ibu,
                color, phMash, fermentables, hops, other, yeast);

    }

    public DetailedBeer (Document beer) {
        this(beer.getString("_id"), beer.getString("name"), beer.getString("style"),
                beer.getString("abv"), beer.getString("rating"), beer.getString("brewery"),
                beer.getString("availability"), beer.getString("notes"), beer.getString("url"),
                beer.getString("retired"), beer.getString("method"), beer.getString("og"),
                beer.getString("fg"), beer.getString("ibu"), beer.getString("color"),
                beer.getString("phMash"), beer.getString("fermentables"), beer.getString("hops"),
                beer.getString("other"), beer.getString("yeast"));
        this.numRating = beer.getInteger("num_rating");
    }

    public String getBrewery_id() {
        return brewery_id;
    }

    public String getNumRating() {
        return String.valueOf(numRating);
    }

    public String getAvailability() {
        return availability;
    }

    public String getNotes() {
        return notes;
    }

    public String getUrl() {
        return url;
    }

    public boolean isRetired() {
        return retired;
    }

    public String getRetired() {
        return retired ? "Yes" : "No";
    }

    public String getMethod() {
        return method;
    }

    public String getOg() {
        return String.valueOf(og);
    }

    public String getFg() {
        return String.valueOf(fg);
    }

    public String getIbu() {
        return String.valueOf(ibu);
    }

    public String getColor() {
        return String.valueOf(color);
    }

    public String getPhMash() {
        return String.valueOf(phMash);
    }

    public String getFermentables() {
        return fermentables;
    }

    public String getHops() {
        return hops;
    }

    public String getOther() {
        return other;
    }

    public String getYeast() {
        return yeast;
    }

    public void setBrewery_id(String brewery_id) {
        this.brewery_id = brewery_id;
    }

    public void setNumRating(int numRating) {
        this.numRating = numRating;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRetired(String retired) {
        this.retired = retired.equalsIgnoreCase("Yes");
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setOg(String og) {
        this.og = Double.parseDouble(og);
    }

    public void setFg(String fg) {
        this.fg = Double.parseDouble(fg);
    }

    public void setIbu(String ibu) {
        this.ibu = Double.parseDouble(ibu);
    }

    public void setColor(String color) {
        this.color = Double.parseDouble(color);
    }

    public void setPhMash(String phMash) {
        this.phMash = Double.parseDouble(phMash);
    }

    public void setFermentables(String fermentables) {
        this.fermentables = fermentables;
    }

    public void setHops(String hops) {
        this.hops = hops;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public void setYeast(String yeast) {
        this.yeast = yeast;
    }

    public Document getBeerDoc() {
        Document doc = super.getBeerDoc();
        doc.append("brewery", brewery_id).append("numRating", numRating)
                .append("method", method)
                .append("og", og).append("fg", fg)
                .append("ibu", ibu)
                .append("color", color)
                .append("ph mash", phMash);

        return doc;
    }
}
