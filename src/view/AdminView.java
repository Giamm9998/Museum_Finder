package view;

import controller.AdminController;
import controller.MuseumController;
import controller.Window;
import model.Museum;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminView extends GeneralView {
    AdminController controller;

    public AdminView(AdminController controller) {
        this.controller = controller;
        setup();
    }

    @Override
    protected void setup() {
        super.setup();

        add(setBackButton(controller, true), "wrap");
        setTitle("Admin panel");
        setupSearchBar();
        setupResultBox();
        setupAddMuseum();
        setupRemoveMuseum();
    }

    private void setupSearchBar() {
        JTextField searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(300, 40));
        searchBar.setBorder(shadow);
        JButton search = createStandardButton("SEARCH BY ID", 140);
        search.addActionListener(e -> {
            Integer museumId = controller.validateInteger(searchBar.getText());
            if (museumId != null) {
                Museum museum = controller.getMuseum(museumId);
                if (museum != null) {
                    MuseumView museumView = new MuseumView(new MuseumController(), museum);
                    controller.getWindow().getMainPanel().add(museumView, Window.MUSEUM);
                    controller.changeWindow(Window.MUSEUM);
                }
            }
        });

        add(searchBar, "align center, gaptop 30, split 2");
        add(search, "wrap");
    }

    private void setupResultBox() {
        String[] columnNames = {"REPORTS", "MUSEUM ID", "REPORT ID"};
        NonEditableModel reportsModel = new NonEditableModel(columnNames, 0);
        JTable reportsBox = new JTable(reportsModel);
        reportsBox.getColumnModel().getColumn(2).setMinWidth(0);
        reportsBox.getColumnModel().getColumn(2).setMaxWidth(0);
        controller.processReports(reportsModel);
        reportsBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsBox.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                int row = table.rowAtPoint(mouseEvent.getPoint());
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    JTextArea textArea = new JTextArea(10, 50);
                    textArea.setText((String) table.getValueAt(row, 0));
                    textArea.setLineWrap(true);
                    textArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    int confirm = JOptionPane.showConfirmDialog(null, scrollPane, "Do you want to delete this report?", JOptionPane.OK_CANCEL_OPTION);
                    if (confirm == JOptionPane.OK_OPTION) {
                        controller.processReportCancel((String) table.getValueAt(row, 2));
                        reportsModel.removeRow(row);
                    }
                }
            }
        });
        JScrollPane reportsPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        reportsPane.setViewportView(reportsBox);
        reportsPane.setBorder(shadow);
        add(reportsPane, "span,align center, gaptop 30, wrap");
    }


    private void setupAddMuseum() {
        JButton addMuseumButton = createStandardButton("ADD MUSEUM", 140);
        addMuseumButton.addActionListener(e -> {
            JPanel addPane = new JPanel(new MigLayout("fillx"));
            JTextField name = setField(addPane, "Name");
            JTextField wikiLink = setField(addPane, "Wikilink");
            JTextField website = setField(addPane, "Website");
            JTextField location = setField(addPane, "Location");
            JTextField lat = setField(addPane, "Latitude");
            JTextField lng = setField(addPane, "Longitude");
            JTextField description = setField(addPane, "Description");
            JTextField address = setField(addPane, "Address");

            JLabel category = new JLabel("Category");
            addPane.add(category, "span, align left, split 2");
            JComboBox<String> categoriesList = new JComboBox<>(controller.getCategories());
            addPane.add(categoriesList, "gapleft 10, wrap");

            int confirm = JOptionPane.showConfirmDialog(null, addPane, "Insert museum data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (confirm == JOptionPane.OK_OPTION) {
                controller.processAdd(name.getText(), wikiLink.getText(), website.getText(), location.getText(), lat.getText(), lng.getText(), description.getText(), address.getText(), (String) categoriesList.getSelectedItem());
            }
        });

        add(addMuseumButton, "span, align center, gaptop 30,split 2");
    }

    private void setupRemoveMuseum() {
        JButton removeMuseumButton = createStandardButton("REMOVE MUSEUM", 140);
        removeMuseumButton.addActionListener(e -> {
            String idQuery = JOptionPane.showInputDialog(null, "Insert the id of the museum to remove");
            if (idQuery != null) {
                Integer museumId = controller.validateInteger(idQuery);
                if (museumId != null) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure? Data will be lost forever", "Confirm selection", JOptionPane.OK_CANCEL_OPTION);
                    if (confirm == JOptionPane.OK_OPTION) {
                        controller.processRemove(museumId);
                    }
                }
            }
        });

        add(removeMuseumButton, "gapleft 20");
    }
}
