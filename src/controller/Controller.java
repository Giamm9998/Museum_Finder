package controller;

import gateway.EventGateway;
import gateway.GatewayFactory;
import gateway.GatewayPSQLFactory;
import model.Event;
import model.*;
import view.GeneralView;
import view.MuseumView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class Controller {
    static final Logger logger = Log.getInstance().getLogger();
    GatewayFactory gatewayFactory = new GatewayPSQLFactory();
    Window window;

    Controller() {
        window = Window.getInstance();
    }

    public Window getWindow() {
        return window;
    }

    public void changeWindow(String newWindow) {
        logger.info("Changing window to " + newWindow + "...");
        window.getCardLayout().show(window.mainPanel, newWindow);
    }

    public void removeCurrentCard() {
        logger.info("Removing current card...");
        for (Component comp : window.getMainPanel().getComponents()) {
            if (comp.isVisible()) {
                window.getMainPanel().remove(comp);
            }
        }
    }

    public Museum getMuseum(int museumId) {
        Museum museum = null;
        try {
            EventGateway eventGateway = gatewayFactory.getEventGateway();
            ArrayList<Review> reviews = gatewayFactory.getReviewGateway().getReviews(museumId);
            ArrayList<Event> events = eventGateway.getEvents(museumId);
            float ticketPrice = eventGateway.getTicketPrice(museumId);
            museum = gatewayFactory.getMuseumGateway().getMuseum(museumId, reviews, events, ticketPrice);
        } catch (SQLException e) {
            handleError(e);
        } catch (IllegalArgumentException e) {
            logger.warning(Log.getStringStackTrace(e));
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return museum;
    }

    public void processBack(GeneralView view) {
        String card = null;
        if (view.getClass() == MuseumView.class) {
            String role = window.getUser().getRole();
            if (role.equals(User.USER)) {
                card = Window.SEARCH;
            } else if (role.equals(User.ADMIN)) {
                card = Window.ADMIN;
            } else card = Window.OWNER;
        } else card = Window.HOME;
        changeWindow(card);
    }

    public String[] getCategories() {
        ArrayList<String> categories = null;
        try {
            categories = gatewayFactory.getMuseumGateway().getCategories();
        } catch (SQLException e) {
            handleError(e);
        }
        if (categories != null) {
            String[] catArray = new String[categories.size()];
            catArray = categories.toArray(catArray);
            return catArray;
        } else return null;
    }

    public boolean isUserAnAdmin() {
        return window.getUser().getRole().equals(User.ADMIN);
    }

    void handleError(Exception e, String message) {
        logger.severe(Log.getStringStackTrace(e));
        JOptionPane.showMessageDialog(null, Objects.requireNonNullElse(message, "An error occurred, please contact the developers"));
    }

    void handleError(Exception e) {
        handleError(e, null);
    }

    public void storeAccess(int museumId) {
        if (window.getUser().getRole().equals("User")) {
            Date date = new Date();
            try {
                gatewayFactory.getUserGateway().access(museumId, window.getUser().getId(), date);
            } catch (SQLException e) {
                handleError(e);
            }
        }
    }

    public BufferedImage getLocalPicture(String filename) {
        logger.info("Retrieving image " + filename + "...");
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("resources/" + filename));
        } catch (IOException e) {
            logger.severe("Couldn't load image from memory");
            logger.severe(Log.getStringStackTrace(e));
        }
        if (image != null)
            logger.info(filename + " retrieved");
        return image;
    }

    public BufferedImage getRemotePicture(String urlString) {
        logger.info("Retrieving remote image from url " + urlString + "...");
        BufferedImage image = null;
        try {
            URL url = new URL(urlString);
            image = ImageIO.read(url);
        } catch (MalformedURLException e) {
            logger.severe("Provided url is malformed");
            logger.severe(Log.getStringStackTrace(e));
        } catch (IOException e) {
            logger.severe("Couldn't load image from url");
            logger.severe(Log.getStringStackTrace(e));
        }
        if (image != null)
            logger.info("Image retrieved");
        return image;
    }

    public void visitUrl(String website) {
        Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            URI oURL = new URI(website);
            desktop.browse(oURL);
        } catch (URISyntaxException | IOException e) {
            handleError(e);
        }
    }
}
