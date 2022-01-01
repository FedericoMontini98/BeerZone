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

    public DetailedBeer(String beerID, String beerName, String style, String abv, double score) {
        super(beerID, beerName, style, abv, score);
    }

    public DetailedBeer(String beerID, String beerName, String style, String abv, @Nullable String score,
                        @Nullable String brewery, String availability, String notes,
                        @Nullable  String url, String retired, String method, String og, String fg, String ibu,
                        @Nullable String color, @Nullable String phMash,
                        String fermentables, String hops, String other, String yeast) {
        super(beerID, beerName, style, abv, score != null ? Double.parseDouble(score) : -1);
        this.brewery_id = brewery != null ? brewery : "-";
        this.numRating = 0;
        this.availability = availability;
        this.notes = notes;
        this.url = url != null ? url : "-";
        this.retired = retired.equalsIgnoreCase("t");
        this.method = method;
        this.og = Double.parseDouble(og);
        this.fg = Double.parseDouble(fg);
        this.ibu = Double.parseDouble(ibu);
        this.color = color != null ? Double.parseDouble(color) : -1;
        this.phMash = phMash != null ? Double.parseDouble(phMash) : -1;
        this.fermentables = fermentables;
        this.hops = hops;
        this.other = other;
        this.yeast = yeast;
    }

    public DetailedBeer (Document beer) {
        this(beer.getString("_id"), beer.getString("name"), beer.getString("style"),
                beer.getString("abv"), beer.getString("rating"), beer.getString("brewery_id"),
                beer.getString("availability"), beer.getString("notes"), beer.getString("url"),
                beer.getString("retired"), beer.getString("method"), beer.getString("og"),
                beer.getString("fg"), beer.getString("ibu"), beer.getString("color"),
                beer.getString("ph mash"), beer.getString("fermentables"), beer.getString("hops"),
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

    public Document getBeerDoc(@Nullable boolean update) {
        Document doc = super.getBeerDoc(update);
        doc.append("brewery", brewery_id).append("numRating", numRating)
                .append("method", method)
                .append("og", og).append("fg", fg)
                .append("ibu", ibu)
                .append("color", color)
                .append("ph mash", phMash);

        return doc;
    }
}
