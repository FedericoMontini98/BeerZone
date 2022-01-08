package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.UserManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StandardUser extends GeneralUser {
    //private final GeneralUser user;
    private int age;
    private List<String> favorites;

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
        this.age = doc.get("age") != null ? doc.getInteger("age") : -1;
    }
/*
    public GeneralUser getUser() {
        return user;
    }
*/
    public int getAge() {
        return age;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    public void addToFavorites(Beer beer) {
        //I add it to the current instance of this user
        this.favorites.add(beer.getBeerID());
    }

    public boolean removeFromFavorites(FavoriteBeer beer) {
        return favorites.remove(beer);
    }

    public Document getUserDoc() {
        //return user.getUserDoc(update).append("age", age);
        return super.getUserDoc().append("age", age);
    }

}
