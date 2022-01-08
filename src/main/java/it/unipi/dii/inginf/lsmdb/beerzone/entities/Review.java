package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

public class Review {
    private String reviewID;
    private String beerID;
    private String username;
    private Date reviewDate;
    private Double look;
    private Double smell;
    private Double taste;
    private Double feel;
    private Double overall;
    private Double score;

    public Review(){}

    public Review(String beerID, String username) {
        this.beerID = beerID;
        this.username = username;
    }

    public Review(String beerID, String username, Date reviewDate, String look, String smell,
                  String taste, String feel, String overall) {
        this.beerID = beerID;
        this.username = username;
        this.reviewDate = reviewDate;
        this.look = Double.parseDouble(look);
        this.smell = Double.parseDouble(smell);
        this.taste = Double.parseDouble(taste);
        this.feel = Double.parseDouble(feel);
        this.overall = Double.parseDouble(overall);
        this.reviewID = new ObjectId().toString();
        computeScore();
    }

    public Review(Document review) {
        this(review.get("beer") != null ? review.getString("beer") : "--",
                review.get("username") != null ? review.getString("username") : "--",
                review.get("date") != null ? review.getDate("date") : new Date(),
                review.get("look") != null ? review.get("look").toString() : "0",
                review.get("smell") != null ? review.get("smell").toString() : "0",
                review.get("taste") != null ? review.get("taste").toString() : "0",
                review.get("feel") != null ? review.get("feel").toString() : "0",
                review.get("overall") != null ? review.get("overall").toString() : "0");
        this.reviewID = review.getObjectId("_id").toString();
        this.score = review.get("score") != null ? Double.parseDouble(review.get("score").toString()) : 0;
    }

    public String getReviewID() {
        return reviewID;
    }

    public String getBeerID() {
        return beerID;
    }

    public String getUsername() {
        return username;
    }

    public String getReviewDate() {
        return reviewDate.toString();
    }

    public Date getReviewDateNeo() {
        return reviewDate;
    }

    public String getLook() {
        return String.valueOf(look);
    }

    public String getSmell() {
        return String.valueOf(smell);
    }

    public String getTaste() {
        return String.valueOf(taste);
    }

    public String getFeel() {
        return String.valueOf(feel);
    }

    public String getOverall() {
        return String.valueOf(overall);
    }

    public String getScore() {
        return String.valueOf(score);
    }

    public void setBeerID(String beerID) {
        this.beerID = beerID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setLook(String look) {
        this.look = Double.parseDouble(look);
    }

    public void setSmell(String smell) {
        this.smell = Double.parseDouble(smell);
    }

    public void setTaste(String taste) {
        this.taste = Double.parseDouble(taste);
    }

    public void setFeel(String feel) {
        this.feel = Double.parseDouble(feel);
    }

    public void setOverall(String overall) {
        this.overall = Double.parseDouble(overall);
    }

    public void setScore(String score) {
        this.score = Double.parseDouble(score);
    }

    public void computeScore() {
        score = look + smell + taste + feel + overall;
        score /= 5;
    }

    public Document getReview() {
        return new Document("_id", new ObjectId(reviewID))
                .append("beer", beerID)
                .append("username", username)
                .append("date", reviewDate)
                .append("look", look)
                .append("smell", smell)
                .append("taste", taste)
                .append("feel", feel)
                .append("overall", overall)
                .append("score", score);
    }
}
