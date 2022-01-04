package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.StandardUser;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class UserManager {
    private static UserManager userManager;
    //private final MongoManager mongoManager;
    private MongoCollection<Document> users;
        // users collection include both standard users (type 0) and breweries (type 1)

    private UserManager() {
        //mongoManager = MongoManager.getInstance();
        users = MongoManager.getInstance().getCollection("users");
    }

    public static UserManager getInstance() {
        if (userManager == null)
            userManager = new UserManager();
        return userManager;
    }

    /*
    public static Brewery getBrewery(String email) {
        Brewery b = null;
        Document userDoc = getUser(email, 1);
        if (userDoc != null && !userDoc.isEmpty())
            b = new Brewery(userDoc);
        return b;
    }

    public static StandardUser getStandardUser(String email) {
        StandardUser u = null;
        Document userDoc = getUser(email, 0);
        if (userDoc != null && !userDoc.isEmpty())
            u = new StandardUser(userDoc);
        return u;
    }

 */

    // use example: Brewery b = new Brewery(UserManager.getGeneralUser(email, type);
    public Document getUser(String email, int type) {
        return users.find(and(eq("type", type), eq("email", email))).first();
    }

    /* check if an email and/or an username already exist in the users collection */
    public boolean userExist(String email, int type, @Nullable String username) {
        Document doc = null;
        if (type == 1) {    // Brewery
            doc = users.find(eq("email", email)).first();
        } else if (type == 0) { // StandardUser
            if (username == null || username.isEmpty() || username.equals(" "))
                throw new RuntimeException("Username not valid");
            doc = users.find(or(eq("email", email), and(eq("type", type), eq("username", username)))).first();
        }

        return !(doc == null || doc.isEmpty());
    }

    public boolean addBrewery(String username, String password, String email, String location, String types) {
        try {
            if (userExist(email, 1, null))
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Document doc = new Brewery(email, username, password, location, types).getBreweryDoc(false);
        return registerUser(doc);
    }

    public boolean addUser(String username, String password, String email, String location, int age) {
        try {
            if (userExist(email, 0, username))
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Document doc = new StandardUser(email, username, password, age, location).getUserDoc(false);
        return registerUser(doc);
    }

    private boolean registerUser(Document userDoc) {
        try {
            users.insertOne(userDoc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int login(String email, String password) {
        int type = -1;
        try {
            Document userDoc = users.find(eq("email", email)).first();
            if (userDoc == null || userDoc.isEmpty())
                return -1;

            if (!password.equals(userDoc.getString("password")))
                return -1;

            type = userDoc.getInteger("type");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    public void deleteUser(String email) {
        try {
            users.deleteOne(eq("email", email));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUsername(String email, String newUsername) {
        UpdateResult result = users.updateOne(eq("email", email), set("username", newUsername));
    }

}
