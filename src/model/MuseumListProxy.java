package model;

import org.json.JSONArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MuseumListProxy implements MuseumListInterface {
    static private final Logger logger = Log.getInstance().getLogger();
    private MuseumListInterface museumList;

    @Override
    public JSONArray getMuseums(String query, String location, boolean shouldRefresh) throws SQLException {
        if (museumList == null || shouldRefresh) {
            museumList = new MuseumList(query, location);
        }
        logger.info("Museums for query '" + query + "' retrieved correctly");
        return museumList.getMuseums(query, location, false);
    }

    @Override
    public ArrayList<Museum> loadMuseums(int startIdx, int endIdx) {
        if (museumList != null) {
            logger.info("Loading museums with starting index " + startIdx + " and ending index " + endIdx);
            ArrayList<Museum> museumArrayList = museumList.loadMuseums(startIdx, endIdx);
            logger.info(museumArrayList.size() + " museums loaded");
            return museumArrayList;
        } else {
            throw new NullPointerException("Museum list has not yet been retrieved, you might want to call getMuseums before");
        }
    }

    @Override
    public void refresh() {
        if (museumList != null) {
            logger.info("Refreshing museum list...");
            museumList.refresh();
        }
    }
}
