package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entityDBManager.GeneralUserDBManager;
import org.bson.Document;

public class UserManager {
    private static UserManager userManager;
    private final GeneralUserDBManager generalUserManagerDB;

    private UserManager() {
        generalUserManagerDB = GeneralUserDBManager.getInstance();
    }

    public static UserManager getInstance() {
        if (userManager == null)
            userManager = new UserManager();
        return userManager;
    }

    /** method to manage the deletion of a user from both databases
     * @param user User to delete
     * @return true if all operations were successful
     * */
    public boolean deleteUser(StandardUser user) {
        boolean result_2=false;
        if (deleteStandardUser(user))
            result_2=removeUser(user.getUsername());
        return (result_2);
    }

    /** Add a favorite beer both on the StandardUser ArrayList and Neo4J, call it from the GUI
     * @param fb FavoriteBeer to add
     * @param s StandardUser who liked the beer
     * @return true if alla operations were successful
     * */
    public boolean addAFavorite(FavoriteBeer fb, StandardUser s){
        return (s.addToFavorites(fb) && addFavorite(s.getUsername(),fb));
    }

    /** Remove a favorite both on the StandardUser ArrayList and Neo4J, call it from the GUI
     * @param s StandardUser who removes the Beer from favorite list
     * @param fb Beer to remove from favorite
     * @return true if all operations were successful
     * */
    public boolean removeAFavorite(StandardUser s, FavoriteBeer fb){
        return (s.removeFromFavorites(fb) && removeFavorite(s.getUsername(),fb.getBeerID()));
    }


    /* ************************************************************************************************************/
    /* *************************************  MongoDB Section  ****************************************************/
    /* ************************************************************************************************************/


    /** request to add a new user in the MongoDB database,
     * called when a Standard User want to register to the application
     * @param user User to add
     * @return true if the operation was successful
     * */
    public boolean addUser(StandardUser user) {
        return generalUserManagerDB.registerUser(user);
    }

    /** method called when a user (Standard or Brewery) wants to sign in into the application
     * @param email email of the user who wants to log in
     * @param password password inserted by the user in the login form
     * @return the User object of the user that requested access
     * */
    public GeneralUser login(String email, String password) {
        try {
            Document doc = generalUserManagerDB.getUser(email, password);
            //System.out.println(doc);
            if (doc != null) {
                if (doc.getInteger("type") == 0) {
                    //System.out.println("standard: " + doc.getString("username"));
                    return new StandardUser(doc);
                } else {
                    //System.out.println("brewery: " + doc.getString("username"));
                    return new Brewery(doc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** method to manage the deletion of a user from all the reviews he wrote and from user collection in MongoDB
     * @param user User who wants to delete its account
     * @return true if all operations were successful
     * */
    private boolean deleteStandardUser(StandardUser user) {
        try {
            if(ReviewManager.getInstance().deleteUserFromReviews(user.getUsername()))
                return generalUserManagerDB.deleteUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** method to request an update of user information
     * @param user StandardUser object containing the new values to update
     * @return true if the operation was successful
     * */
    public boolean updateUser(StandardUser user) {
        return generalUserManagerDB.updateUser(user.getUserDoc(), user.getUserID());
    }


    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/

    /* Function used to add StandardUser Nodes in the graph, the only property that they have is Username which is common
     *  Both to reviews and User's files */
    public boolean addStandardUser(String Username){
        return generalUserManagerDB.addStandardUser(Username);

    }

    /* Function used to add a favorite beer from the users favorites list. To identify a relationship we need the
     *  Username and the BeerID, this functionality has to be available on a specific beer only if a User hasn't
     *  it already in its favorites */
    private boolean addFavorite(String Username, FavoriteBeer fv) { //Correct it
        return generalUserManagerDB.addFavorite(Username, fv);
    }

    /* Function used to remove a favorite beer from the users favorites list. To identify a relationship we need the
     *  Username and the BeerID, this functionality has to be available on a specific beer only if a User has it in its
     *  favorites */
    private boolean removeFavorite(String Username, String BeerID){
        return generalUserManagerDB.removeFavorite(Username, BeerID);
    }

    /* Function used to remove a user and all its relationships from Neo4J graph DB */
    private boolean removeUser(String username){
        return generalUserManagerDB.removeUser(username);
    }

    /* Function used to return to GUI a list of beers that the user has in its favorites */
    public void getFavorites(StandardUser user){
        generalUserManagerDB.getFavorites(user);
    }
}
