package controller;

import model.User;
import view.HomeView;
import view.LoginView;
import view.RegisterView;
import view.SearchView;

import javax.swing.*;
import java.awt.*;

public class Window {
    public static final String HOME = "HOME";
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String SEARCH = "SEARCH";
    public static final String OWNER = "OWNER";
    public static final String ADMIN = "ADMIN";
    public static final String MUSEUM = "MUSEUM";
    private static Window instance = null;
    CardLayout cardLayout;
    JPanel mainPanel;
    User user;

    private Window() {
    }

    public static Window getInstance() {
        if (instance == null) {
            instance = new Window();
        }
        return instance;
    }

    public void setUp() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel();
        mainPanel.setLayout(cardLayout);
        SessionController sessionController = new SessionController();
        HomeView homeView = new HomeView(new Controller());
        mainPanel.add(homeView, Window.HOME);
        RegisterView registerView = new RegisterView(sessionController);
        mainPanel.add(registerView, Window.REGISTER);
        LoginView loginView = new LoginView(sessionController);
        mainPanel.add(loginView, Window.LOGIN);
        SearchView searchView = new SearchView(new SearchController());
        mainPanel.add(searchView, Window.SEARCH);
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
