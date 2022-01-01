package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import com.mongodb.lang.Nullable;
import org.bson.Document;

public class GeneralUser {
    protected String userID;   // _id, if -1 is not yet in database
    protected String email;
    protected String username;
    private String password;
    protected String location;
    protected int type; // 0: standard user, 1: brewery

    public GeneralUser(String email, String username, String password, String location, int type) {
        this(null, email, username, password, location, type);
    }

    public GeneralUser(@Nullable String id, String email, String username, String password, String location, int type) {
        this.userID = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.location = location;
        this.type = type;
    }

    public GeneralUser(Document user) {
        this.userID = user.getString("_id");
        this.username = user.getString("username");
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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isStandard() {
        return type == 0;
    }

    protected Document getUserDoc(boolean update) {
        Document doc = new Document();
        if (update)
            doc.append("_id", userID);
        doc.append("username", username)
                .append("password", password)
                .append("email", email)
                .append("location", location)
                .append("type", type);
        return doc;
    }

    protected Document getUserIdDoc() {
        return new Document("username", username)
                .append("password", password)
                .append("email", email)
                .append("location", location)
                .append("type", type);
    }

    public boolean checkPassword(String pwd) {
        return password.equals(pwd);
    }
}
