package controller;

import model.User;
import view.AdminView;
import view.OwnerView;

import javax.naming.SizeLimitExceededException;
import javax.swing.*;
import java.sql.SQLException;

public class SessionController extends Controller {

    SessionController() {
        super();
    }

    public void processLogin(String email, String password) {
        try {
            window.setUser(gatewayFactory.getUserGateway().login(email, password));
            //currentUser = userGateway.login(email, password);
            JOptionPane.showMessageDialog(null, "Login completed successfully");
            logger.info("User has role " + window.getUser().getRole());
            if (window.getUser().getRole().equals(User.USER))
                changeWindow(Window.SEARCH);
            else if (window.getUser().getRole().equals(User.OWNER)) {
                window.getMainPanel().add(new OwnerView(new OwnerController()), Window.OWNER);
                changeWindow(Window.OWNER);
            } else {
                AdminView adminView = new AdminView(new AdminController());
                window.getMainPanel().add(adminView, Window.ADMIN);
                changeWindow(Window.ADMIN);
            }
        } catch (IllegalArgumentException e) {
            logger.warning(e.getMessage());
            JOptionPane.showMessageDialog(null, "Wrong credentials");
        } catch (SizeLimitExceededException e) {
            logger.severe(e.getMessage());
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void processRegistration(String email, String password, String name, String surname, String role) {
        try {
            gatewayFactory.getUserGateway().register(email, password, name, surname, role);
            JOptionPane.showMessageDialog(null, "Registration completed successfully");
            changeWindow(Window.HOME);
        } catch (SQLException e) {
            handleError(e, "Something went wrong. User might already be registered.");
        }
    }
}
