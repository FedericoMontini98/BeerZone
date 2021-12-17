package entities;

public abstract class GeneralUser {
    protected int userID;
    protected String email;
    protected String username;
    //protected String password;
    protected int type; // 0: standard user, 1: brewery

    public GeneralUser() {}

    public GeneralUser(int id, String email, String username, String pwd, int type) {
        this.userID = id;
        this.email = email;
        this.username = username;
        this.type = type;
    }

    public int getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public int getType() {
        return type;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setType(int type) {
        this.type = type;
    }

    public abstract boolean isStandard();
}
