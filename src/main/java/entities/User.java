package entities;

import entities.GeneralUser;

import java.util.ArrayList;
import java.util.Date;

public class User extends GeneralUser {
    private int age;
    private String location;
    private ArrayList<FavoriteBeer> favorites;

    public User(int id, String email, String username, String password, int age, String location) {
        super(id, email, username, password, 0);
        this.age = age;
        this.location = location;
    }

    public int getAge() {
        return age;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<FavoriteBeer> getFavorites() {
        return favorites;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setLocation(String location) {
        this.location = location;
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

    @Override
    public boolean isStandard() {
        return true;
    }
}
