package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;

public class StandardUser extends GeneralUser {
    //private final GeneralUser user;
    private int age;
    private ArrayList<FavoriteBeer> favorites;

    public StandardUser(String id, String email, String username, String password, int age, String location) {
        //user = new GeneralUser
        super(id, email, username, password, location, 0);
        this.age = age;
    }

    public StandardUser(String email, String username, String password, int age, String location) {
        this(null, email, username, password, age, location);
    }
/*
    public StandardUser(GeneralUser user, int age) {
        //this.user = user;
        this.age = age;
    }
*/
    public StandardUser(Document doc) {
        //this.user = new GeneralUser(doc);
        super(doc);
        this.age = doc.getInteger("age");
    }
/*
    public GeneralUser getUser() {
        return user;
    }
*/
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

    public Document getUserDoc(boolean update) {
        //return user.getUserDoc(update).append("age", age);
        return super.getUserDoc(update).append("age", age);
    }

}
