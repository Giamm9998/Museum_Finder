package model;

import gateway.GatewayFactory;
import gateway.GatewayPSQLFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

public class MuseumList implements MuseumListInterface {
    private static final GatewayFactory gatewayFactory = new GatewayPSQLFactory();
    JSONArray museumList;

    public MuseumList(String query, String location) throws SQLException {
        museumList = gatewayFactory.getMuseumGateway().searchMuseums(query, location);
    }

    @Override
    public JSONArray getMuseums(String query, String location, boolean shouldRefresh) {
        return museumList;
    }

    @Override
    public ArrayList<Museum> loadMuseums(int startIdx, int endIdx) {
        ArrayList<Museum> museumArrayList = new ArrayList<>();
        for (int i = startIdx; i < Math.min(museumList.length(), endIdx); i++) {
            JSONObject jsonMuseum = museumList.getJSONObject(i);
            Museum museum = new Museum.Builder(jsonMuseum.getInt("museum_id"), jsonMuseum.getString("name")).build();
            museumArrayList.add(museum);
        }
        return museumArrayList;
    }

    @Override
    public void refresh() {
        museumList = null;
    }

}
