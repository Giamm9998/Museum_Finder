package view;

import controller.Controller;
import controller.MuseumController;
import controller.SearchController;
import controller.Window;
import model.LocationStrategy;
import model.Museum;
import model.RatingStrategy;
import model.ScoreStrategy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class SearchView extends GeneralView {
    int moreCount = 0;
    SearchController controller;

    public SearchView(SearchController controller) {
        this.controller = controller;
        setup();
    }

    @Override
    protected void setup() {
        super.setup();

        JButton backButton = setBackButton(controller, false);
        add(backButton, "wrap");

        setTitle("Search");

        //Results box
        String[] columnName = {"RESULT", "ID"};
        NonEditableModel model = new NonEditableModel(columnName, 0);
        JTable resultBox = new JTable(model);
        resultBox.getColumnModel().getColumn(1).setMinWidth(0);
        resultBox.getColumnModel().getColumn(1).setMaxWidth(0);
        JScrollPane scrollPane = setupResultBox(resultBox, controller);

        JTextField searchBar = new JTextField(50);
        searchBar.setBorder(shadow);
        searchBar.setPreferredSize(new Dimension(300, 40));
        add(searchBar, "align center, gaptop 30, split 3");
        JButton search = createStandardButton("SEARCH");
        add(search);
        JComboBox<String> strategyBox = new JComboBox<>(new String[]{"Keywords", "Location", "Ratings"});
        strategyBox.setPreferredSize(new Dimension(100, 40));
        add(strategyBox, "wrap");

        JLabel location = new JLabel("Location");
        add(location, "align center, gaptop10, split 2");
        JTextField locationText = new JTextField(20);
        locationText.setBorder(shadow);
        locationText.setPreferredSize(new Dimension(200, 40));
        add(locationText, "wrap");
        locationText.setVisible(false);
        location.setVisible(false);

        strategyBox.addActionListener(e -> {
            String strategy = (String) strategyBox.getSelectedItem();
            if (strategy != null) {
                processStrategy(strategy, locationText, location);
            }
        });
        backButton.addActionListener(e -> {
            clear(model);
            searchBar.setText("");
            locationText.setText("");
            controller.refreshMuseumList();
            controller.changeWindow(Window.HOME);
        });
        search.addActionListener(e -> {
            clear(model);
            moreCount = 0;
            String query = searchBar.getText();
            controller.processSearch(model, query, locationText.getText());
        });
        add(scrollPane, "align center, gaptop 30, wrap");
        JButton moreResult = createStandardButton("MORE RESULTS", 150);
        moreResult.addActionListener(e -> {
            moreCount++;
            controller.processMore(moreCount, model);
        });
        add(moreResult, "align center, gaptop 10");
    }

    private JScrollPane setupResultBox(JTable resultBox, Controller controller) {
        resultBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultBox.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                String museumId = (String) table.getValueAt(row, 1);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    Museum m = controller.getMuseum(Integer.parseInt(museumId));
                    MuseumView mv = new MuseumView(new MuseumController(), m);
                    controller.storeAccess(m.getMuseumId());
                    controller.getWindow().getMainPanel().add(mv, Window.MUSEUM);
                }
                controller.changeWindow(Window.MUSEUM);
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultBox);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        scrollPane.setBorder(shadow);
        return scrollPane;
    }

    public void clear(DefaultTableModel model) {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    public void processStrategy(String strategy, JTextField locationText, JLabel location) {
        if (strategy.equals("Location")) {
            controller.changeStrategy(new LocationStrategy());
            locationText.setVisible(true);
            location.setVisible(true);
        } else if (strategy.equals("Ratings")) {
            controller.changeStrategy(new RatingStrategy());
            locationText.setVisible(false);
            location.setVisible(false);
            locationText.setText("");
        } else {
            controller.changeStrategy(new ScoreStrategy());
            locationText.setVisible(false);
            location.setVisible(false);
            locationText.setText("");
        }
    }
}
