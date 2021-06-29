package model;

public class User {
    public static String USER = "User";
    public static String OWNER = "Museum Owner";
    public static String ADMIN = "Admin";
    private final int id;
    private final String name;
    private final String surname;
    private final String email;
    private final String role;

    public User(int id, String name, String surname, String email, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
