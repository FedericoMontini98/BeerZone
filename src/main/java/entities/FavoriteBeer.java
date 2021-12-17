package entities;

import java.util.Date;

public class FavoriteBeer extends Beer {
    private Date favoriteDate;

    public FavoriteBeer(int beerID, String beerName, Date favoriteDate) {
        super(beerID, beerName);
        this.favoriteDate = favoriteDate;
    }

    public FavoriteBeer(Beer beer, Date date) {
        this(beer.getBeerID(), beer.getBeerName(), date);
    }

    public FavoriteBeer(int beerID, String beerName) {
        this(beerID, beerName, new Date());
    }

    public Date getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(Date favoriteDate) {
        this.favoriteDate = favoriteDate;
    }
}
