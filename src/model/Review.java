package model;

public class Review {
    private final int id;
    private final int museumId;
    private final int writerId;
    private final String text;
    private final int score;

    public Review(int id, int museumId, int writerId, String text, int score) {
        this.id = id;
        this.museumId = museumId;
        this.writerId = writerId;
        this.text = text;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public int getMuseumId() {
        return museumId;
    }

    public int getWriterId() {
        return writerId;
    }

    public String getText() {
        return text;
    }

    public int getScore() {
        return score;
    }
}
