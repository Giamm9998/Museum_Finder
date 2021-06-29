package controller;

import gateway.EventGateway;
import model.Event;
import model.Museum;
import model.Report;
import model.Review;
import view.GeneralView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class OwnerController extends Controller {
    OwnerController() {
        super();
    }

    public Museum getMuseumOwned() {
        int museumId;
        Museum museum = null;
        try {
            EventGateway eventGateway = gatewayFactory.getEventGateway();
            museumId = gatewayFactory.getUserGateway().getMuseumOwned(window.getUser().getId());
            ArrayList<Review> reviews = gatewayFactory.getReviewGateway().getReviews(museumId);
            ArrayList<Event> events = eventGateway.getEvents(museumId);
            float ticketPrice = eventGateway.getTicketPrice(museumId);
            museum = gatewayFactory.getMuseumGateway().getMuseum(museumId, reviews, events, ticketPrice);
        } catch (SQLException e) {
            handleError(e);
        }
        return museum;
    }

    public int getDailyAccesses(int museumId) {
        int accesses = 0;
        try {
            accesses = gatewayFactory.getUserGateway().getDailyAccesses(museumId);
        } catch (SQLException e) {
            handleError(e);
        }
        return accesses;
    }

    public int getWeeklyAccesses(int museumId) {
        int accesses = 0;
        try {
            accesses = gatewayFactory.getUserGateway().getWeeklyAccesses(museumId);
        } catch (SQLException e) {
            handleError(e);
        }
        return accesses;
    }

    public int getAllTimeAccesses(int museumId) {
        int accesses = 0;
        try {
            accesses = gatewayFactory.getUserGateway().getAllTimeAccesses(museumId);
        } catch (SQLException e) {
            handleError(e);
        }
        return accesses;
    }

    public int getReviewsNumber(int museumId) {
        int reviews = 0;
        try {
            reviews = gatewayFactory.getReviewGateway().getReviewsNumber(museumId);
        } catch (SQLException e) {
            handleError(e);
        }
        return reviews;
    }

    public float getAverageScore(int museumId) {
        float score = 0;
        try {
            score = gatewayFactory.getReviewGateway().getAverageScore(museumId);
        } catch (SQLException e) {
            handleError(e);
        }
        return score;
    }

    public int getFutureBookingsNumber(int museumId) {
        int bookings = 0;
        try {
            bookings = gatewayFactory.getMuseumGateway().getFutureBookingsNumber(museumId);
        } catch (SQLException e) {
            handleError(e);
        }
        return bookings;
    }

    public int getTotalBookingsNumber(int museumId) {
        int bookings = 0;
        try {
            bookings = gatewayFactory.getMuseumGateway().getTotalBookingsNumber(museumId);
        } catch (SQLException e) {
            handleError(e);
        }
        return bookings;
    }

    public void processEvent(int museumId, String description, Date start, Date end, int typology, int discount) {
        try {
            gatewayFactory.getEventGateway().createEvent(museumId, description, start, end, typology, discount);
            JOptionPane.showMessageDialog(null, "Event correctly created");
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void processReportApproval(int confirm, String id) {
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gatewayFactory.getReportGateway().approveReport(Integer.parseInt((id)));
            } catch (SQLException e) {
                handleError(e);
            }
        } else {
            try {
                gatewayFactory.getReportGateway().rejectReport(Integer.parseInt(id));
            } catch (SQLException e) {
                handleError(e);
            }
        }
    }

    public void processOwnerReports(GeneralView.NonEditableModel model) {
        ArrayList<Report> reports = null;
        try {
            reports = gatewayFactory.getReportGateway().getOwnerReports(window.getUser().getId());
        } catch (SQLException e) {
            handleError(e);
        }
        if (reports != null) {
            for (Report r : reports)
                model.addRow(new String[]{r.getDescription(), String.valueOf(r.getId())});
        }
    }
}
