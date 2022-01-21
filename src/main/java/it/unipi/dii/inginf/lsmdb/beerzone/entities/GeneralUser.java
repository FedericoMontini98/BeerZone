package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.lang.Nullable;
import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class GeneralUser {
    protected String userID;   // _id, if -1 is not yet in database
    protected String email;
    protected String username;
    private String password;
    protected String location;
    protected int type; // 0: standard user, 1: brewery

    public GeneralUser(@Nullable String id, String email, String username, String password, String location, int type) {
        this.userID = id != null ? id : "-1";   //new ObjectId().toString();
        this.email = email.toLowerCase();
        this.username = username;
        this.password = password;
        this.location = location;
        this.type = type;
    }

    public GeneralUser(Document user) {
        this.userID = user.getObjectId("_id").toString();
        this.username = user.getString("username");
        this.email = user.getString("email").toLowerCase();
        this.password = user.getString("password");
        this.location = user.getString("location");
        this.type = user.getInteger("type");

    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    public int getType() {
        return type;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isStandard() {
        return type == 0;
    }

    public Document getUserDoc() {
        return new Document("username", username)
                .append("password", password)
                .append("email", email)
                .append("location", location)
                .append("type", type);
    }
}
