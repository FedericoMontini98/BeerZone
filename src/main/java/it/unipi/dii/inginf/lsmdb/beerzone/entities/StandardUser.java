package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;

import java.util.ArrayList;

public class StandardUser extends GeneralUser {
    private int age;
    private ArrayList<FavoriteBeer> favorites;

    public StandardUser(String id, String email, String username, String password, int age, String location) {
        super(id, email, username, password, location, 0);
        this.age = age;
    }

    public StandardUser(String email, String username, String password, int age, String location) {
        this(null, email, username, password, age, location);
    }

    public StandardUser(Document doc) {
        super(doc);
        this.age = doc.get("age") != null ? doc.getInteger("age") : -1;
    }

    public int getAge() {
        return age;
    }

    public ArrayList<FavoriteBeer> getFavorites() {
        return favorites;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setFavorites(ArrayList<FavoriteBeer> favorites) {
        this.favorites = favorites;
    }

    public boolean addToFavorites(FavoriteBeer fb) {
        //I add it to the current instance of this user
        this.favorites.add(fb);
        return true;
    }

    public boolean removeFromFavorites(FavoriteBeer beer) {
            return favorites.remove(beer);
    }

    public Document getUserDoc() {
        return super.getUserDoc().append("age", age);
    }

}
