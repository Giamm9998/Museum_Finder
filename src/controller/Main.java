package controller;

import gateway.ConnectionPool;
import model.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Main {
    static final Logger logger = Log.getInstance().getLogger();

    public static void main(String[] arg) throws SQLException {
        ConnectionPool.create();
        Window viewWindow = Window.getInstance();
        viewWindow.setUp();
        JFrame window = new JFrame("Museum Finder");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setBounds(0, 0, screenSize.width, screenSize.height);
        window.setLayout(new BorderLayout());
        window.add(viewWindow.getMainPanel(), BorderLayout.CENTER);
        window.setVisible(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ConnectionPool.getSize() != 0) {
                    try {
                        ConnectionPool.empty();
                    } catch (SQLException exc) {
                        logger.severe("Couldn't empty the connection pool");
                        logger.severe(Log.getStringStackTrace(exc));
                    }
                }
                e.getWindow().dispose();
            }
        });

    }
}
