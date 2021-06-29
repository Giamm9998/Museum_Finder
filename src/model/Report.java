package model;

public class Report {
    private final int id;
    private final String description;
    private final int museumId;

    public Report(int id, String description, int museumId) {
        this.id = id;
        this.description = description;
        this.museumId = museumId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getMuseumId() {
        return museumId;
    }
}
