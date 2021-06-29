package controller;

import model.Log;
import model.Report;
import view.GeneralView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdminController extends Controller {
    AdminController() {
        super();
    }

    public void processAdd(String nameText, String wikiLinkText, String websiteText, String locationText, String latText, String lngText, String descriptionText, String addressText, String categoryText) {
        try {
            gatewayFactory.getMuseumGateway().addMuseum(nameText, wikiLinkText, websiteText, locationText, Float.parseFloat(latText), Float.parseFloat(lngText), descriptionText, addressText, categoryText);
            JOptionPane.showMessageDialog(null, "Museum added correctly");
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void processRemove(int id) {
        try {
            gatewayFactory.getMuseumGateway().removeMuseum(id);
            JOptionPane.showMessageDialog(null, "Museum removed correctly");
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void processReports(GeneralView.NonEditableModel model) {
        ArrayList<Report> reports = new ArrayList<>();
        try {
            reports = gatewayFactory.getReportGateway().getAllReports();
        } catch (SQLException e) {
            handleError(e);
        }
        for (Report r : reports) {
            model.addRow(new String[]{r.getDescription(), String.valueOf(r.getMuseumId()), String.valueOf(r.getId())});
        }
    }

    public void processReportCancel(String id) {
        try {
            gatewayFactory.getReportGateway().resolveReport(Integer.parseInt(id));
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public Integer validateInteger(String query) {
        Integer parsedInt = null;
        try {
            parsedInt = Integer.parseInt(query);
        } catch (Exception e) {
            logger.warning(query + " is not an integer");
            logger.warning(Log.getStringStackTrace(e));
            JOptionPane.showMessageDialog(null, "Provided id is not an integer!");
        }
        return parsedInt;
    }
}
