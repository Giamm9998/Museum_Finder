package controller;

import model.User;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Date;

public class MuseumController extends Controller {
    public MuseumController() {
        super();
    }

    public boolean isMuseumOpen(int museumId, Date date) {
        try {
            if (gatewayFactory.getEventGateway().isClosed(museumId, date)) {
                JOptionPane.showMessageDialog(null, "Museum is closed on the selected date");
                return false;
            }
        } catch (SQLException e) {
            handleError(e);
        }
        return true;
    }

    public void processBooking(int museumId, Date date, int participants, String notes) {
        try {
            gatewayFactory.getMuseumGateway().storeBooking(window.getUser().getId(), museumId, date, participants, notes);
            JOptionPane.showMessageDialog(null, "Booking complete");
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void processReview(int museumId, int userId, String review, int rating) {
        try {
            gatewayFactory.getReviewGateway().writeReview(museumId, userId, review, rating);
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void processModify(int museumId, String nameText, String wikiLinkText, String websiteText, String locationText, String latText, String lngText, String descriptionText, String addressText, String categoryText) {
        try {
            gatewayFactory.getMuseumGateway().modifyMuseum(museumId, nameText, wikiLinkText, websiteText, locationText, Float.parseFloat(latText), Float.parseFloat(lngText), descriptionText, addressText, categoryText);
            JOptionPane.showMessageDialog(null, "Museum modified correctly");
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void storeReport(int museumId, String reportDescription) {
        try {
            gatewayFactory.getReportGateway().report(window.getUser().getId(), museumId, reportDescription, window.getUser().getRole().equals(User.OWNER));
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void addOwner(String ownerEmail, int museumId) {
        try {
            gatewayFactory.getMuseumGateway().addOwner(ownerEmail, museumId);
            JOptionPane.showMessageDialog(null, "Owner added correctly");
        } catch (SQLException e) {
            handleError(e);
        }
    }
}
