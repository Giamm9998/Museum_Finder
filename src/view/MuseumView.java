package view;

import controller.MuseumController;
import model.Event;
import model.Museum;
import model.Review;
import model.User;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MuseumView extends GeneralView {
    MuseumController controller;
    private Museum museum;

    MuseumView(MuseumController controller, Museum museum) {
        this.museum = museum;
        this.controller = controller;
        controller.storeAccess(museum.getMuseumId());

        setup();
    }

    @Override
    protected void setup() {
        super.setup();

        setupButtonsPane();
        setupTitle();
        setupMuseumPicture();
        setupInfo();
        setupReviews();
        setupBookings();
    }

    private void setupButtonsPane() {
        JPanel buttonsPane = new JPanel(new MigLayout("fillx"));
        buttonsPane.setBackground(Color.WHITE);
        buttonsPane.add(setBackButton(controller, true), "span, split 3");

        JButton eventButton = createStandardButton("NEW EVENTS");
        eventButton.addActionListener(e -> {
            ArrayList<Event> events = museum.getEvents();
            StringBuilder eventView = new StringBuilder();
            for (model.Event event : events)
                eventView.append(event.getDescription()).append("\n");
            if (eventView.toString().equals(""))
                JOptionPane.showMessageDialog(null, "There aren't any events");
            else JOptionPane.showMessageDialog(null, eventView.toString(), "EVENTS", JOptionPane.PLAIN_MESSAGE);
        });

        JButton modifyButton = createStandardButton("MODIFY");
        modifyButton.addActionListener(e -> {
            JPanel modifyPane = new JPanel(new MigLayout("fillx"));
            JTextField name = setField(modifyPane, "Name");
            name.setText(museum.getName());
            JTextField wikiLink = setField(modifyPane, "Wikilink");
            wikiLink.setText(museum.getWikiLink());
            JTextField website = setField(modifyPane, "Website");
            website.setText(museum.getWebsite());
            JTextField location = setField(modifyPane, "Location");
            location.setText(museum.getLocation());
            JTextField lat = setField(modifyPane, "Latitude");
            lat.setText(String.valueOf(museum.getLat()));
            JTextField lng = setField(modifyPane, "Longitude");
            lng.setText(String.valueOf(museum.getLng()));
            JTextArea descriptionText = new JTextArea(3, 49);
            descriptionText.setText(museum.getDescription());
            descriptionText.setLineWrap(true);
            descriptionText.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(descriptionText);
            JLabel description = new JLabel("Description");
            modifyPane.add(description, "span, align right, split 2");
            modifyPane.add(scrollPane, "gapleft 10, wrap");

            JTextField address = setField(modifyPane, "Address");
            address.setText(museum.getAddress());

            JLabel category = new JLabel("Category");
            modifyPane.add(category, "span, align left, split 2");
            JComboBox<String> categoriesList = new JComboBox<>(controller.getCategories());
            modifyPane.add(categoriesList, "gapleft 10, wrap");
            int confirm = JOptionPane.showConfirmDialog(null, modifyPane, "Insert museum data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (confirm == JOptionPane.OK_OPTION)
                controller.processModify(museum.getMuseumId(), name.getText(), wikiLink.getText(), website.getText(), location.getText(), lat.getText(), lng.getText(), descriptionText.getText(), address.getText(), (String) categoriesList.getSelectedItem());

        });

        JButton reportButton = createStandardButton("REPORT");
        reportButton.addActionListener(e -> {
            JPanel reportPane = new JPanel(new BorderLayout());
            JTextArea reportText = new JTextArea(20, 30);
            reportText.setLineWrap(true);
            reportText.setWrapStyleWord(true);
            JScrollPane reportBox = new JScrollPane(reportText);
            reportPane.add(reportBox);
            int confirm = JOptionPane.showConfirmDialog(null, reportPane, "Which error do you want to report?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (confirm == JOptionPane.OK_OPTION)
                controller.storeReport(museum.getMuseumId(), reportText.getText());
        });

        JButton ownerButton = createStandardButton("ADD OWNER");
        ownerButton.addActionListener(e -> {
            JTextField textField = new JTextField(20);
            int confirm = JOptionPane.showConfirmDialog(null, textField, "Insert owner e-mail", JOptionPane.OK_CANCEL_OPTION);
            if (confirm == JOptionPane.OK_OPTION)
                controller.addOwner(textField.getText(), museum.getMuseumId());
        });

        buttonsPane.add(eventButton, "align left, dock east");
        if (!controller.isUserAnAdmin()) {
            ownerButton.setVisible(false);
            modifyButton.setVisible(false);
        }
        buttonsPane.add(reportButton, "dock east, gapright 30");
        buttonsPane.add(modifyButton, "dock east, gapright 30");
        buttonsPane.add(ownerButton, "dock east, gapright 30");
        add(buttonsPane, "span, grow, wrap");

    }

    public void setupTitle() {
        JLabel title = new JLabel(this.museum.getName());
        title.setFont(new Font(title.getFont().getName(), title.getFont().getStyle(), 20));
        add(title, "span, align center,wrap");
    }

    private void setupMuseumPicture() {
        if (museum.getImageUrl() != null) {
            BufferedImage museumImage = controller.getRemotePicture(museum.getImageUrl());
            if (museumImage != null) {
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(museumImage).getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH));
                JLabel picLabel = new JLabel(imageIcon);
                picLabel.setBorder(shadow);
                add(picLabel, "gapleft 10, split 2, grow");
            }
        }
    }

    private void setupInfo() {
        int FONT_SIZE_BIG = 18;
        JPanel infoPane = new JPanel(new MigLayout("fillx"));
        FlowLayout locationFlow = new FlowLayout(FlowLayout.LEFT);
        locationFlow.setAlignOnBaseline(true);
        JPanel locationPane = new JPanel(locationFlow);
        JPanel imagesPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        FlowLayout categoriesFlow = new FlowLayout(FlowLayout.LEFT);
        categoriesFlow.setAlignOnBaseline(true);
        JPanel categoriesPane = new JPanel(categoriesFlow);


        JLabel location = new JLabel("LOCATION: ");
        String address = "";
        if (this.museum.getAddress() != null)
            address = this.museum.getAddress() + " - ";
        if (this.museum.getLocation() != null)
            address = address + this.museum.getLocation();
        else address = address + "Unknown";
        JLabel locationText = new JLabel(address);
        location.setFont(new Font(location.getFont().getName(), Font.BOLD, FONT_SIZE_BIG));
        locationPane.setBackground(Color.WHITE);
        locationPane.add(location);
        locationPane.add(locationText);
        infoPane.add(locationPane);


        if (museum.getWebsite() != null) {
            BufferedImage globeImage = controller.getLocalPicture("globe.png");
            if (globeImage != null) {
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(globeImage).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
                JLabel globe = new JLabel(imageIcon);
                globe.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        controller.visitUrl(museum.getWebsite());
                    }
                });
                imagesPane.add(globe);
            }
        }

        if (museum.getWikiLink() != null) {
            BufferedImage wikiImage = controller.getLocalPicture("wiki.png");
            ImageIcon wikiIcon = new ImageIcon(new ImageIcon(wikiImage).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            JLabel wiki = new JLabel(wikiIcon);
            wiki.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    controller.visitUrl(museum.getWikiLink());
                }
            });
            imagesPane.add(wiki);
        }
        imagesPane.setBackground(Color.WHITE);
        infoPane.add(imagesPane, "align right, wrap");

        if (this.museum.getCategories() != null) {
            JLabel categoriesLabel = new JLabel("CATEGORIES: ");
            categoriesLabel.setFont(new Font(location.getFont().getName(), Font.BOLD, FONT_SIZE_BIG));
            JLabel categoriesText = new JLabel(this.museum.getCategories());
            categoriesPane.add(categoriesLabel);
            categoriesPane.add(categoriesText);
            categoriesPane.setBackground(Color.WHITE);
            infoPane.add(categoriesPane, "wrap");
        }

        JLabel description = new JLabel("DESCRIPTION");
        description.setFont(new Font(location.getFont().getName(), Font.BOLD, FONT_SIZE_BIG));
        infoPane.add(description, "gaptop 10, span, align center, wrap");
        JTextArea descriptionContent = new JTextArea(this.museum.getDescription(), 30, 70);
        descriptionContent.setLineWrap(true);
        descriptionContent.setWrapStyleWord(true);
        descriptionContent.setEditable(false);
        JScrollPane descriptionText = new JScrollPane(descriptionContent);
        infoPane.add(descriptionText, "span, grow");

        infoPane.setBackground(Color.WHITE);
        infoPane.setBorder(shadow);
        add(infoPane, "align left, gapright 10, grow, wrap");
    }

    public void setupReviews() {
        JPanel reviewPane = new JPanel(new BorderLayout());
        reviewPane.setBorder(shadow);

        String[] columnName = {"REVIEW", "SCORE"};
        NonEditableModel model = new NonEditableModel(columnName, 0);
        JTable reviewBox = new JTable(model);
        reviewBox.getColumnModel().getColumn(1).setMaxWidth(100);
        addData(model);
        reviewBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reviewBox.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                String cellSelected = (String) table.getValueAt(row, 0);
                String score = (String) table.getValueAt(row, 1);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    JOptionPane.showMessageDialog(null, cellSelected, score, JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        JScrollPane reviewsScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        reviewsScrollPane.setViewportView(reviewBox);
        reviewsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        reviewPane.add(reviewsScrollPane);
        JButton reviewButton = createStandardButton("WRITE A REVIEW");
        reviewButton.addActionListener(e -> {
            JPanel reviewPanePopup = new JPanel(new MigLayout("fillx"));
            JTextArea reviewTextPopup = new JTextArea(10, 50);
            reviewTextPopup.setLineWrap(true);
            reviewTextPopup.setWrapStyleWord(true);
            JScrollPane reviewBoxPopup = new JScrollPane(reviewTextPopup);
            reviewBoxPopup.setBorder(shadow);
            SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 5, 1);
            JSpinner rating = new JSpinner(spinnerModel);
            reviewPanePopup.add(reviewBoxPopup, "span,wrap");
            reviewPanePopup.add(new JLabel("Rating"), "align center, split 2");
            reviewPanePopup.add(rating, "gapleft 10");
            int reviewConfirmed = JOptionPane.showConfirmDialog(null, reviewPanePopup, "Write your review", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
            if (reviewConfirmed == JOptionPane.YES_OPTION) {
                controller.processReview(museum.getMuseumId(), controller.getWindow().getUser().getId(), reviewTextPopup.getText(), (int) rating.getValue());
            }
        });
        reviewPane.add(reviewButton, BorderLayout.SOUTH);
        if (controller.getWindow().getUser().getRole().equals(User.OWNER) || controller.getWindow().getUser().getRole().equals(User.ADMIN))
            reviewButton.setVisible(false);
        add(reviewPane, "gapleft 10,grow,push,split 2");
    }

    public void setupBookings() {
        JPanel bookingPane = new JPanel(new MigLayout("fillx"));
        bookingPane.setBorder(shadow);
        bookingPane.setBackground(Color.WHITE);
        int FONT_SIZE = 15;
        float MUSEUM_PRICE = museum.getTicketPrice();
        JLabel bookVisit = new JLabel("Book a visit");
        setFontSize(bookVisit, FONT_SIZE);
        bookingPane.add(bookVisit, "span, align center,wrap");

        JLabel nVisitors = new JLabel("Number of visitors");
        setFontSize(nVisitors, FONT_SIZE);
        bookingPane.add(nVisitors, "gaptop 25, align center, split 4");
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        bookingPane.add(spinner);

        JLabel date = new JLabel("Date");
        setFontSize(date, FONT_SIZE);
        bookingPane.add(date, "gapleft 30");
        JXDatePicker datePicker = new JXDatePicker();
        bookingPane.add(datePicker, "gaptop 25, wrap");

        JLabel notes = new JLabel("Further information");
        setFontSize(notes, FONT_SIZE);
        bookingPane.add(notes, "gaptop 15, align center, span, split 2");
        JTextArea notesText = new JTextArea(3, 30);
        notesText.setLineWrap(true);
        notesText.setWrapStyleWord(true);
        JScrollPane notesPane = new JScrollPane(notesText);
        notesPane.setBorder(shadow);
        bookingPane.add(notesPane, "wrap");

        JLabel price = new JLabel("Tickets price: ");
        setFontSize(price, FONT_SIZE);
        bookingPane.add(price, "gaptop 20,span,align center, split 2");
        JButton book = createStandardButton("BOOK");
        book.addActionListener(e -> {
            if (controller.isMuseumOpen(museum.getMuseumId(), datePicker.getDate())) {
                int result = JOptionPane.showConfirmDialog(null, "Do you confirm your booking?");
                if (result == JOptionPane.YES_OPTION) {
                    controller.processBooking(museum.getMuseumId(), datePicker.getDate(), (int) spinner.getValue(), notesText.getText());
                    notesText.setText("");
                    spinnerModel.setValue(0);
                    datePicker.setDate(null);
                }
            }
        });
        bookingPane.add(book, "gapleft 50, wrap");

        spinner.addChangeListener(e -> {
            JSpinner spinner1 = (JSpinner) e.getSource();
            int value = (int) spinner1.getValue();
            price.setText("Tickets price: " + value * MUSEUM_PRICE + '\u20ac');
        });

        add(bookingPane, "gapright 10,grow,push, wrap 10");
    }

    public void addData(NonEditableModel model) {
        for (Review r : museum.getReviews()) {
            String[] s = new String[]{r.getText(), ""};
            for (int j = r.getScore(); j > 0; j--)
                s[1] = s[1] + '\u2605';
            model.addRow(s);
        }
    }

    public void setFontSize(JLabel label, int size) {
        label.setFont(new Font(label.getFont().getName(), label.getFont().getStyle(), size));
    }

    public void setMuseum(Museum m) {
        museum = m;
    }
}
