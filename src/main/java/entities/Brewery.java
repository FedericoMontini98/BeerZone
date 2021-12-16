package entities;

import java.util.ArrayList;

public class Brewery extends GeneralUser {
    private String city;
    private String state;
    private ArrayList<Integer> beers;

    public Brewery(int id, String email, String username, String password, String city, String state) {
        super(id, email, username, password, 1);
        this.city = city;
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public ArrayList<Integer> getBeers() {
        return beers;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setBeers(ArrayList<Integer> beers) {
        this.beers = beers;
    }

    // mettere solo in beerManager ?
    public boolean addBeer(int beer) {
        return beers.add(beer);
    }

    public boolean deleteBeer(Integer beer) {
        return beers.remove(beer);
    }
}
