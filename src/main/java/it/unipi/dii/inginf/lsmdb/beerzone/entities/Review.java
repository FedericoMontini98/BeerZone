package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

public class Review {
    //private String beerID;
    private String username;
    private Date reviewDate;
    private double look;
    private double smell;
    private double taste;
    private double feel;
    private double overall;
    private double score;


    public Review(String username, Date reviewDate, String look, String smell,
                  String taste, String feel, String overall, String score) {
        //this.beerID = beerID;
        this.username = username;
        this.reviewDate = reviewDate;
        this.look = Double.parseDouble(look);
        this.smell = Double.parseDouble(smell);
        this.taste = Double.parseDouble(taste);
        this.feel = Double.parseDouble(feel);
        this.overall = Double.parseDouble(overall);
        this.score = Double.parseDouble(score);
        //computeScore();
    }

    public Review(Document review) {
        this(review.get("username") != null ? review.getString("username") : "--",
                review.get("date") != null ? review.getDate("date") : new Date(),
                review.get("look") != null ? review.get("look").toString() : "0",
                review.get("smell") != null ? review.get("smell").toString() : "0",
                review.get("taste") != null ? review.get("taste").toString() : "0",
                review.get("feel") != null ? review.get("feel").toString() : "0",
                review.get("overall") != null ? review.get("overall").toString() : "0",
                review.get("score") != null ? review.get("score").toString() : "0");
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

    public double getNumericScore() {
        return score;
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

    protected void computeScore() {
        score = (double) (Math.round(((look + smell + taste + feel + overall) / 5) * 100)) / 100;
    }

    public Document getReviewDoc() {
        return new Document("username", username)
                .append("date", reviewDate)
                .append("look", look)
                .append("smell", smell)
                .append("taste", taste)
                .append("feel", feel)
                .append("overall", overall)
                .append("score", score);
    }

    @Override
    public boolean equals(Object review) {
        if (review instanceof Review)
            return this.username.equalsIgnoreCase(((Review) review).getUsername());
        return false;
    }
}
