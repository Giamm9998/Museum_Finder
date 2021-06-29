package model;

public interface SearchStrategy {
    String buildSelect(String[] keywords, String location);
}
