package view;

import controller.Controller;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class GeneralView extends JPanel {
    protected DropShadowBorder shadow = new DropShadowBorder();

    protected void setup() {
        //setting layout
        setBackground(Color.WHITE);
        MigLayout layout = new MigLayout("fillx");
        setLayout(layout);

        shadow.setShadowColor(Color.GRAY);
        shadow.setShowLeftShadow(true);
        shadow.setShowRightShadow(true);
        shadow.setShowBottomShadow(true);
        shadow.setShowTopShadow(true);
    }

    protected void setTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), titleLabel.getFont().getStyle(), 20));
        add(titleLabel, "span, align center, wrap");
    }

    protected JButton setBackButton(Controller controller, Boolean removeCard) {
        JButton backButton = createStandardButton("BACK", 90);
        ImageIcon backIcon = new ImageIcon("resources/arrow-left-24px.png");
        backButton.setIcon(backIcon);
        backButton.addActionListener(e -> {
            if (removeCard)
                controller.removeCurrentCard();
            controller.processBack(this);
        });
        return backButton;
    }

    protected JButton createStandardButton(String label) {
        return createStandardButton(label, 100);
    }

    protected JButton createStandardButton(String label, int width) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(width, 40));
        setButtonHover(button);
        button.setBorder(shadow);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    protected void setButtonHover(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                ((JButton) e.getSource()).setOpaque(true);
                ((JButton) e.getSource()).setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                ((JButton) e.getSource()).setOpaque(false);
                ((JButton) e.getSource()).setBackground(UIManager.getColor("control"));
            }
        });
    }

    protected JTextField setField(JPanel panel, String label) {
        JLabel fieldName = new JLabel(label);
        panel.add(fieldName, "span, align right, split 2");
        JTextField field = new JTextField(50);
        panel.add(field, "gapleft 10, wrap");
        return field;
    }

    public static class NonEditableModel extends DefaultTableModel {

        NonEditableModel(String[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
