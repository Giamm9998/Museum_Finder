package view;

import controller.MuseumController;
import controller.OwnerController;
import controller.Window;
import model.Museum;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class OwnerView extends GeneralView {
    OwnerController controller;
    Museum museumOwned;

    public OwnerView(OwnerController controller) {
        this.controller = controller;
        museumOwned = controller.getMuseumOwned();

        setup();
    }

    @Override
    protected void setup() {
        super.setup();

        add(setBackButton(controller, true), "wrap");
        setTitle("Welcome to your private area");
        setupMuseumBox();
        setupEventsBox();
        setupStatsBox();
        setupReportsBox();
    }

    private void setupMuseumBox() {
        JPanel museumBox = new JPanel(new MigLayout("fillx"));
        setBox(museumBox);
        JButton museumButton = createStandardButton("OPEN MUSEUM PAGE");
        museumBox.add(museumButton, "align center, span, grow, wrap");
        museumButton.addActionListener(e -> {
            MuseumView mv = new MuseumView(new MuseumController(), museumOwned);
            controller.getWindow().getMainPanel().add(mv, Window.MUSEUM);
            controller.changeWindow(Window.MUSEUM);
        });
        if (museumOwned.getImageUrl() != null) {
            BufferedImage museumImage = controller.getRemotePicture(museumOwned.getImageUrl());
            if (museumImage != null) {
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(museumImage).getImage().getScaledInstance(315, 315, Image.SCALE_SMOOTH));
                JLabel picLabel = new JLabel(imageIcon);
                museumBox.add(picLabel, "grow");
            }
        }
        add(museumBox, "span, grow, push, split 2");
    }

    private void setupEventsBox() {
        JPanel eventBox = new JPanel(new MigLayout("fillx"));
        setBox(eventBox);
        add(eventBox, "grow");
        JLabel title = new JLabel("Create event");
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 16));
        eventBox.add(title, "span,align center, wrap");
        JComboBox<String> eventType = new JComboBox<>(new String[]{"Ticket's price", "Museum closed", "Special event"});
        eventBox.add(eventType, "align center, span, grow,wrap");

        CardLayout cardLayout = new CardLayout();
        JPanel eventView = new JPanel(cardLayout);
        JPanel specialEventView = new JPanel(new BorderLayout());
        JTextArea eventTextArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane(eventTextArea);
        specialEventView.add(jScrollPane, BorderLayout.CENTER);
        eventView.add(specialEventView, "SE");

        JPanel discountEventView = new JPanel(new MigLayout("fillx"));
        discountEventView.setBackground(Color.WHITE);
        JLabel discount = new JLabel("Discount percentage:  ");
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        JSpinner percentage = new JSpinner(spinnerModel);
        discountEventView.add(discount, "gaptop 50, align center,split 2");
        discountEventView.add(percentage, "gapleft 20, wrap");
        eventView.add(discountEventView, "DE");

        JPanel closedEventView = new JPanel(new MigLayout("fillx"));
        closedEventView.setBackground(Color.WHITE);
        closedEventView.add(new JLabel("Closed"), "span, align center, gaptop 30, wrap");
        eventView.add(closedEventView, "CE");

        JLabel from = new JLabel("from");
        JXDatePicker start = new JXDatePicker();
        JLabel to = new JLabel("to");
        JXDatePicker end = new JXDatePicker();
        JButton saveButton = createStandardButton("SAVE");

        cardLayout.show(eventView, "DE");
        eventBox.add(eventView, "grow,wrap");

        eventBox.add(from, "gaptop 30, span, align center, split 4");
        eventBox.add(start, "gaptop 30");
        eventBox.add(to, "gapleft 30");
        eventBox.add(end, "gaptop 30, wrap");
        eventBox.add(saveButton, "gaptop 30, span, align center");
        saveButton.addActionListener(e -> {
            try {
                String s = (String) eventType.getSelectedItem();
                String d1 = start.getDate().toString();
                String d2 = end.getDate().toString();
                int confirm = JOptionPane.showConfirmDialog(null, "Do you want to add this event?");
                if (s != null && confirm == JOptionPane.YES_OPTION) {
                    String description;
                    int typology;
                    int discount1 = 0;
                    if (s.equals("Ticket's price")) {
                        discount1 = (int) percentage.getValue();
                        description = "Price discount of " + discount1 + "% if you book from " + d1 + " to " + d2;
                        typology = 1;
                    } else if (s.equals("Museum closed")) {
                        description = "Museum closed from " + d1 + " to " + d2;
                        typology = 2;
                    } else {
                        description = eventTextArea.getText() + " (from " + d1 + " to " + d2 + ")";
                        typology = 3;
                    }
                    controller.processEvent(museumOwned.getMuseumId(), description, start.getDate(), end.getDate(), typology, discount1);
                }
            } catch (NullPointerException nullExc) {
                JOptionPane.showMessageDialog(null, "Fill in all fields");
            }
        });

        eventType.addActionListener(e -> {
            String string = (String) eventType.getSelectedItem();
            if (string != null) {
                processEventType(string, cardLayout, eventView);
            }
        });
    }

    private void setupStatsBox() {
        JPanel statsBox = new JPanel(new MigLayout("fillx"));
        JScrollPane scrollStatsBox = new JScrollPane(statsBox);
        scrollStatsBox.setBorder(BorderFactory.createEmptyBorder());
        setBox(statsBox);
        add(scrollStatsBox, "span, grow,push, split 2");


        JLabel statsLabel = new JLabel("Your museum's stats");
        statsLabel.setFont(new Font(statsLabel.getFont().getName(), Font.BOLD, 16));
        statsBox.add(statsLabel, "align center, span, wrap");

        JLabel accessesLabel = new JLabel("ACCESSES");
        accessesLabel.setFont(new Font(accessesLabel.getFont().getName(), Font.BOLD, 16));
        statsBox.add(accessesLabel, "gapleft 20px, gaptop 10px, wrap");

        String[] columnName = {"STATEMENT", "NUMBER"};
        NonEditableModel accessModel = new NonEditableModel(columnName, 0);
        JTable accessBox = new JTable(accessModel);
        accessBox.setFocusable(false);
        accessBox.setRowSelectionAllowed(false);
        accessBox.getColumnModel().getColumn(1).setMaxWidth(100);
        accessModel.addRow(new String[]{"Daily", String.valueOf(controller.getDailyAccesses(museumOwned.getMuseumId()))});
        accessModel.addRow(new String[]{"Weekly", String.valueOf(controller.getWeeklyAccesses(museumOwned.getMuseumId()))});
        accessModel.addRow(new String[]{"All time", String.valueOf(controller.getAllTimeAccesses(museumOwned.getMuseumId()))});
        statsBox.add(accessBox, "gapleft 20px, gaptop 10px, gapright 20px, grow, wrap");

        JLabel reviewsLabel = new JLabel("REVIEWS");
        reviewsLabel.setFont(new Font(reviewsLabel.getFont().getName(), Font.BOLD, 16));
        statsBox.add(reviewsLabel, "gapleft 20px, gaptop 10px, wrap");

        NonEditableModel reviewModel = new NonEditableModel(columnName, 0);
        JTable reviewBox = new JTable(reviewModel);
        reviewBox.setFocusable(false);
        reviewBox.setRowSelectionAllowed(false);
        reviewBox.getColumnModel().getColumn(1).setMaxWidth(100);
        reviewModel.addRow(new String[]{"Reviews number", String.valueOf(controller.getReviewsNumber(museumOwned.getMuseumId()))});
        reviewModel.addRow(new String[]{"Average review score", String.valueOf(controller.getAverageScore(museumOwned.getMuseumId()))});
        statsBox.add(reviewBox, "gapleft 20px, gaptop 10px, gapright 20px, grow, wrap");

        JLabel bookingsLabel = new JLabel("BOOKINGS");
        bookingsLabel.setFont(new Font(bookingsLabel.getFont().getName(), Font.BOLD, 16));
        statsBox.add(bookingsLabel, "gapleft 20px, gaptop 10px, wrap");

        NonEditableModel bookingModel = new NonEditableModel(columnName, 0);
        JTable bookingBox = new JTable(bookingModel);
        bookingBox.setFocusable(false);
        bookingBox.setRowSelectionAllowed(false);
        bookingBox.getColumnModel().getColumn(1).setMaxWidth(100);
        bookingModel.addRow(new String[]{"Future bookings", String.valueOf(controller.getFutureBookingsNumber(museumOwned.getMuseumId()))});
        bookingModel.addRow(new String[]{"Total bookings", String.valueOf(controller.getTotalBookingsNumber(museumOwned.getMuseumId()))});
        statsBox.add(bookingBox, "gapleft 20px, gaptop 10px, gapright 20px, grow, wrap");
    }

    private void setupReportsBox() {
        JPanel reportsBox = new JPanel(new MigLayout("fillx"));
        setBox(reportsBox);
        add(reportsBox, "grow");
        String[] columnName = {"REPORTS", "ID"};
        NonEditableModel model = new NonEditableModel(columnName, 0);
        JTable reportTable = new JTable(model);
        reportTable.getColumnModel().getColumn(1).setMinWidth(0);
        reportTable.getColumnModel().getColumn(1).setMaxWidth(0);
        controller.processOwnerReports(model);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                String cellSelected = (String) table.getValueAt(row, 0);
                String id = (String) table.getValueAt(row, 1);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    JTextArea textArea = new JTextArea(10, 50);
                    textArea.setText(cellSelected);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    textArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    int confirm = JOptionPane.showConfirmDialog(null, scrollPane, "Do you want to approve this report?", JOptionPane.YES_NO_CANCEL_OPTION);
                    controller.processReportApproval(confirm, id);
                    if (confirm == JOptionPane.YES_OPTION || confirm == JOptionPane.NO_OPTION)
                        model.removeRow(row);
                }
            }
        });
        JScrollPane reports = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        reports.setViewportView(reportTable);
        reportsBox.add(reports, "grow, push");
    }

    private void processEventType(String s, CardLayout cardLayout, JPanel eventView) {
        if (s.equals("Ticket's price")) {
            cardLayout.show(eventView, "DE");
        } else if (s.equals("Museum closed")) {
            cardLayout.show(eventView, "CE");
        } else {
            cardLayout.show(eventView, "SE");
        }
    }

    private void setBox(JPanel panel) {
        panel.setBackground(Color.WHITE);
        panel.setBorder(shadow);
    }
}
