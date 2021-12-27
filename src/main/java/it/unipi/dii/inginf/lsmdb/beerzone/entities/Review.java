package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import java.util.Date;

public class Review {
    private int beerID;
    private int breweryID;
    private String username;
    private Date reviewDate;
    private String text;
    private double look;
    private double smell;
    private double taste;
    private double feel;
    private double overall;
    private double score;

    public Review(){}

    public Review(int beerID, int breweryID, String username) {
        this.beerID = beerID;
        this.breweryID = breweryID;
        this.username = username;
    }

    public int getBeerID() {
        return beerID;
    }

    public int getBreweryID() {
        return breweryID;
    }

    public String getUsername() {
        return username;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public String getText() {
        return text;
    }

    public double getLook() {
        return look;
    }

    public double getSmell() {
        return smell;
    }

    public double getTaste() {
        return taste;
    }

    public double getFeel() {
        return feel;
    }

    public double getOverall() {
        return overall;
    }

    public double getScore() {
        return score;
    }

    public void setBeerID(int beerID) {
        this.beerID = beerID;
    }

    public void setBreweryID(int breweryID) {
        this.breweryID = breweryID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLook(double look) {
        this.look = look;
    }

    public void setSmell(double smell) {
        this.smell = smell;
    }

    public void setTaste(double taste) {
        this.taste = taste;
    }

    public void setFeel(double feel) {
        this.feel = feel;
    }

    public void setOverall(double overall) {
        this.overall = overall;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
