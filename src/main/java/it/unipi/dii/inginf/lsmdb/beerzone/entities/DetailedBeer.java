package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class DetailedBeer extends Beer {
    // id, name, style, score
    private String breweryID;
    private int numRating;
    private String availability;
    private String notes;
    private boolean retired;
    private String url;
    private String method;
    private double og;  // original gravity
    private double fg;  // final gravity
    private double ibu;
    private double color;
    private double phMash;  // -1 if is not present on the source
    private String fermentables;
    private String hops;
    private String other;
    private String yeast;
    private ArrayList<Review> reviews;

    public DetailedBeer() {}

    public DetailedBeer(@Nullable String beerID, String beerName, String style, String abv, String score) {
        super(beerID, beerName, style, abv, score);
    }

    public DetailedBeer(@Nullable String beerID, String beerName, String style, String abv, @Nullable String score,
                        @Nullable String breweryID, @Nullable  String availability, @Nullable String notes,
                        @Nullable  String url, String retired, @Nullable String method, @Nullable String og,
                        @Nullable String fg, @Nullable String ibu, @Nullable String color, @Nullable String phMash,
                        @Nullable String fermentables, @Nullable String hops, @Nullable String other, @Nullable String yeast) {
        super(beerID, beerName, style, abv, score);
        this.breweryID = breweryID != null ? breweryID : "";
        this.numRating = 0;
        this.availability = availability != null ? availability : "";
        this.notes = notes != null ? notes : "";
        this.url = url != null ? url : "";
        this.retired = retired.equalsIgnoreCase("Yes") || retired.equalsIgnoreCase("t");
        this.method = method != null ? method : "";
        this.og = og != null ? Double.parseDouble(og) : -1;
        this.fg = fg != null ? Double.parseDouble(fg) : -1;
        this.ibu = ibu != null ? Double.parseDouble(ibu) : -1;
        this.color = color != null ? Double.parseDouble(color) : -1;
        this.phMash = phMash != null ? Double.parseDouble(phMash) : -1;
        this.fermentables = fermentables != null ? fermentables : "";
        this.hops = hops != null ? hops : "";
        this.other = other != null ? other : "";
        this.yeast = yeast != null ? yeast : "";
        this.reviews = new ArrayList<>();
    }

    public DetailedBeer(String beerName, String style, String abv, @Nullable String score,
                        @Nullable String breweryID, @Nullable  String availability, @Nullable String notes,
                        @Nullable  String url, String retired, @Nullable String method, @Nullable String og,
                        @Nullable String fg, @Nullable String ibu, @Nullable String color, @Nullable String phMash,
                        @Nullable String fermentables, @Nullable String hops, @Nullable String other, @Nullable String yeast) {
        this(null, beerName, style, abv, score, breweryID, availability, notes, url, retired, method, og, fg, ibu,
                color, phMash, fermentables, hops, other, yeast);

    }

    public DetailedBeer (Document beer) {
        this(beer.getObjectId("_id").toString(), beer.getString("name"),
                beer.get("style") != null ? beer.getString("style") : "--",
                beer.get("abv") != null ? beer.get("abv").toString() : "-1",
                beer.get("rating") != null ? beer.get("rating").toString() : "0",
                beer.get("brewery_id") != null ? beer.getObjectId("brewery_id").toString() : "--",
                beer.get("availability") != null ? beer.getString("availability") : "--",
                beer.get("notes") != null ? beer.getString("notes") : "--",
                beer.get("url") != null ? beer.getString("url") : "--",
                beer.get("retired") != null ? beer.getString("retired") : "t",
                beer.get("method") != null ? beer.getString("method") : "--",
                beer.get("og") != null ? beer.get("og").toString() : "-1",
                beer.get("fg") != null ? beer.get("fg").toString() : "-1",
                beer.get("ibu") != null ? beer.get("ibu").toString() : "-1",
                beer.get("color") != null ? beer.get("color").toString() : "-1",
                beer.get("phMash") != null ? beer.get("phMash").toString() : "-1",
                beer.get("fermentables") != null ? beer.getString("fermentables") : "--",
                beer.get("hops") != null ? beer.getString("hops") : "--",
                beer.get("other") != null ? beer.getString("other") : "--",
                beer.get("yeast") != null ? beer.getString("yeast") : "--");
        this.numRating = beer.get("num_rating") != null ? beer.getInteger("num_rating") : 0;
        if (this.reviews == null)
            this.reviews = new ArrayList<>();
        if (beer.get("reviews") != null) {
            for (Document d : beer.getList("reviews", Document.class)) {
                reviews.add(new Review(d));
            }
        }
    }

    public String getBreweryID() {
        return breweryID;
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

    public ArrayList<Review> getReviewList() {
        return reviews;
    }

    public void addReviewToBeer(Review review) {
        this.reviews.add(review);
    }

    public boolean removeReviewFromBeer(Review review) {
        return reviews.remove(review);
    }

    public List<Document> getReviewListDoc() {
        ArrayList<Document> reviewsList = new ArrayList<>();
        for (Review r: reviews) {
            reviewsList.add(r.getReviewDoc());
        }
        return reviewsList;
    }

    public void setBreweryID(String breweryID) {
        this.breweryID = breweryID;
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
        Document doc = super.getBeerDoc()
                .append("num_rating", numRating);
        if (retired)
            doc.append("retired", "t");
        else
            doc.append("retired", "f");

        if (!breweryID.isEmpty())
            doc.append("brewery_id", new ObjectId(breweryID));
        if (!availability.isEmpty())
            doc.append("availability", availability);
        if (!notes.isEmpty())
            doc.append("notes", notes);
        if (!url.isEmpty())
            doc.append("url", url);
        if (!method.isEmpty())
            doc.append("method", method);
        if (og != -1)
            doc.append("og", og);
        if (fg != -1)
            doc.append("fg", fg);
        if (ibu != -1)
            doc.append("ibu", ibu);
        if (color != -1)
            doc.append("color", color);
        if (phMash != -1)
            doc.append("phMash", phMash);
        if (!fermentables.isEmpty())
            doc.append("fermentables", fermentables);
        if (!hops.isEmpty())
            doc.append("hops", hops);
        if (!other.isEmpty())
            doc.append("other", other);
        if (!yeast.isEmpty())
            doc.append("yeast", yeast);
        if (!reviews.isEmpty())
            doc.append("reviews", getReviewListDoc());
        return doc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DetailedBeer)
            return this.beerID.equals(((Beer) o).getBeerID());
        return false;
    }
}
