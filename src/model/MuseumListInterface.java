package model;

import org.json.JSONArray;

import java.sql.SQLException;
import java.util.ArrayList;

public interface MuseumListInterface {
    JSONArray getMuseums(String query, String location, boolean shouldRefresh) throws SQLException;

    ArrayList<Museum> loadMuseums(int startIdx, int endIdx);

    void refresh();
}
