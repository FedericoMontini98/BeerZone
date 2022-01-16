package it.unipi.dii.inginf.lsmdb.beerzone.entities;

public class Style {
    String name;
    double score;

    public Style(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return String.valueOf(score);
    }
}
