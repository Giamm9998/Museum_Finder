package view;

import controller.SessionController;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends GeneralView {
    SessionController controller;

    public RegisterView(SessionController controller) {
        this.controller = controller;
        setup();
    }

    @Override
    protected void setup() {
        super.setup();

        JButton backButton = setBackButton(controller, false);
        add(backButton, "wrap");
        setupInstructions();

        int FONT_SIZE = 12;
        int CHARS_NUMBER = 20;
        JTextField name = setField("Name", FONT_SIZE, CHARS_NUMBER, "align center,gaptop 100,split 4", "");
        JTextField surname = setField("Surname", FONT_SIZE, CHARS_NUMBER, "gapleft 50", "wrap");
        JTextField email = setField("E-mail", FONT_SIZE, CHARS_NUMBER, "align center, gaptop 50,split 4", "");
        JTextField password = setField("Password", FONT_SIZE, CHARS_NUMBER, "gapleft 50", "wrap");
        JTextField[] textFields = new JTextField[]{name, surname, email, password};

        backButton.addActionListener(e -> {
            clear(textFields);
        });

        JComboBox<String> roleList = setRoleList(FONT_SIZE);
        JButton registerButton = createStandardButton("REGISTER");
        add(registerButton, "align center, gaptop 120");
        registerButton.addActionListener(e -> {
            String s = (String) roleList.getSelectedItem();
            if (s != null)
                controller.processRegistration(email.getText(), password.getText(), name.getText(), surname.getText(), s);
            clear(textFields);
        });
    }

    private void setupInstructions() {
        JLabel instruction = new JLabel("Enter your details in the spaces provided");
        instruction.setFont(new Font(instruction.getFont().getName(), instruction.getFont().getStyle(), 20));
        add(instruction, "span, align center, gaptop 30 ,wrap");
    }

    private JComboBox<String> setRoleList(int FONT_SIZE) {
        JLabel role = new JLabel("Role");
        role.setFont(new Font(role.getFont().getName(), role.getFont().getStyle(), FONT_SIZE));
        add(role, "align center, gaptop 50, split 2");
        String[] roles = {"User", "Museum Owner"};
        JComboBox<String> roleList = new JComboBox<>(roles);
        roleList.setBackground(Color.lightGray);
        add(roleList, "wrap");
        return roleList;
    }

    public JTextField setField(String fieldName, int FONT_SIZE, int NUMBER_CHARACTERS, String fieldCostraints, String fieldTexTCostraints) {
        JLabel label = new JLabel(fieldName);
        label.setFont(new Font(label.getFont().getName(), label.getFont().getStyle(), FONT_SIZE));
        add(label, fieldCostraints);
        JTextField fieldText = new JTextField(NUMBER_CHARACTERS);
        fieldText.setBorder(shadow);
        add(fieldText, fieldTexTCostraints);
        return fieldText;
    }

    public void clear(JTextField[] textFields) {
        for (JTextField jtf : textFields)
            jtf.setText("");
    }
}





