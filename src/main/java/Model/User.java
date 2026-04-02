package Model;

public abstract class User {
    // I used Eric's code here
    String userId;
    String name;
    String email;

    public User(String id, String name, String email) {
        this.userId = id;
        this.name = name;
        this.email = email;
    }
    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public abstract int getBookingLimit();

    public abstract String getUserType();

}
