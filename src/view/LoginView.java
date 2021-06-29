package view;

import controller.SessionController;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LoginView extends GeneralView {
    SessionController controller;

    public LoginView(SessionController controller) {
        this.controller = controller;
        setup();
    }

    @Override
    protected void setup() {
        super.setup();

        int CHARS_NUMBER = 20;
        JTextField emailText = new JTextField(CHARS_NUMBER);
        JPasswordField passwordText = new JPasswordField(CHARS_NUMBER);

        JButton backButton = setBackButton(controller, false);
        add(backButton, "wrap");
        backButton.addActionListener(e -> {
            emailText.setText("");
            passwordText.setText("");
        });

        setupInstructions();
        setupLoginImage();
        setupTextFields(emailText, passwordText);

        //LOGIN BUTTON
        JButton loginButton = createStandardButton("LOGIN");
        add(loginButton, "align center, gaptop 100");
        loginButton.addActionListener(e -> {
            String em = emailText.getText();
            String pw = new String(passwordText.getPassword());
            controller.processLogin(em, pw);
            emailText.setText("");
            passwordText.setText("");
        });


    }

    private void setupInstructions() {
        JLabel instructions = new JLabel("Enter your credentials in the spaces provided");
        instructions.setFont(new Font(instructions.getFont().getName(), instructions.getFont().getStyle(), 20));
        add(instructions, "span, align center, gaptop 30 ,wrap");
    }

    private void setupLoginImage() {
        BufferedImage myPicture = controller.getLocalPicture("user.png");
        if (myPicture != null) {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(myPicture).getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH));
            JLabel picLabel = new JLabel(imageIcon);
            add(picLabel, "span, align center, gaptop 100, wrap");
        }
    }

    private void setupTextFields(JTextField emailText, JPasswordField passwordText) {
        int FONT_SIZE = 12;
        JLabel email = new JLabel("E-mail");
        email.setFont(new Font(email.getFont().getName(), email.getFont().getStyle(), FONT_SIZE));
        emailText.setBorder(shadow);
        JLabel password = new JLabel("Password");
        password.setFont(new Font(password.getFont().getName(), password.getFont().getStyle(), FONT_SIZE));
        passwordText.setBorder(shadow);

        add(email, "align center, gaptop 25,split 4");
        add(emailText);
        add(password, "gapleft 50");
        add(passwordText, "wrap");
    }
}

