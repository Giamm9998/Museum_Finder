package controller;

import model.Museum;
import model.MuseumListInterface;
import model.MuseumListProxy;
import model.SearchStrategy;
import view.GeneralView;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchController extends Controller {
    MuseumListInterface museumList = new MuseumListProxy();

    SearchController() {
        super();
    }

    public void processSearch(GeneralView.NonEditableModel model, String query, String location) {
        try {
            museumList.getMuseums(query, location, true);
            ArrayList<Museum> museumArrayList = museumList.loadMuseums(0, 10);
            for (Museum m : museumArrayList) {
                model.addRow(new String[]{m.getName(), String.valueOf(m.getMuseumId())});
            }
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void processMore(int moreCount, GeneralView.NonEditableModel model) {
        logger.info("Processing more museums...");
        ArrayList<Museum> museumArrayList = museumList.loadMuseums(moreCount * 10, (moreCount + 1) * 10);
        for (Museum m : museumArrayList) {
            model.addRow(new String[]{m.getName(), String.valueOf(m.getMuseumId())});
        }
    }

    public void changeStrategy(SearchStrategy newStrategy) {
        gatewayFactory.getMuseumGateway().setStrategy(newStrategy);
    }


    public void refreshMuseumList() {
        museumList.refresh();
    }
}
