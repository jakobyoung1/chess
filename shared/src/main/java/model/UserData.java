package model;

public record UserData(String username, String password, String email) {
    // You can add validation or other methods if necessary.
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
}
