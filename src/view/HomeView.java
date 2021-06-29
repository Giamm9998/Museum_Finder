package view;

import controller.Controller;
import controller.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HomeView extends GeneralView {
    Controller controller;

    public HomeView(Controller controller) {
        this.controller = controller;
        setup();
    }

    @Override
    protected void setup() {
        super.setup();

        setupWelcomeLabel();
        setupCoverImage();
        setupAuthButtons();
    }

    private void setupWelcomeLabel() {
        JLabel welcome = new JLabel("Welcome in Museum Finder!");
        welcome.setFont(new Font("Latin Modern Roman", Font.PLAIN, 30));
        add(welcome, "span, align center, gaptop 30, wrap");
    }

    private void setupCoverImage() {
        BufferedImage coverImage = controller.getLocalPicture("cover.jpg");
        if (coverImage != null) {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(coverImage).getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(imageIcon);
            picLabel.setBorder(shadow);
            add(picLabel, "span, align center, gaptop 30, wrap");
        }
    }

    private void setupAuthButtons() {
        FlowLayout buttons = new FlowLayout(FlowLayout.CENTER, 40, 0);
        JPanel buttonsPanel = new JPanel(buttons);
        buttonsPanel.setBackground(Color.WHITE);

        JButton loginButton = createStandardButton("LOGIN");
        JButton registerButton = createStandardButton("REGISTER");
        loginButton.addActionListener(e -> controller.changeWindow(Window.LOGIN));
        registerButton.addActionListener(e -> controller.changeWindow(Window.REGISTER));
        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);

        add(buttonsPanel, "gaptop 20px, align center, wrap");
    }
}
