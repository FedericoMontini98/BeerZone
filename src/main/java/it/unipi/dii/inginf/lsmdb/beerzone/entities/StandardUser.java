package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;

public class StandardUser{
    private final GeneralUser user;
    private int age;
    private ArrayList<FavoriteBeer> favorites;

    public StandardUser(int id, String email, String username, String password, int age, String location) {
        user = new GeneralUser(id, email, username, password, location, 0);
        this.age = age;
    }

    public StandardUser(String email, String username, String password, int age, String location) {
        this(-1, email, username, password, age, location);
    }

    public StandardUser(GeneralUser user, int age) {
        this.user = user;
        this.age = age;
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

    public boolean addToFavorites(Beer beer) {
        return favorites.add(new FavoriteBeer(beer, new Date()));
    }

    public boolean removeFromFavorites(FavoriteBeer beer) {
        return favorites.remove(beer);
    }

    public Document getUser() {
        return user.getUser().append("age", age);
    }

}
