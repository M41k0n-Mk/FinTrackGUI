package me.m41k0n.investment.gui;

import me.m41k0n.investment.dto.InvestmentDTO;
import me.m41k0n.investment.service.InvestmentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvestmentEditPanel extends JPanel {
    private final InvestmentService investmentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public InvestmentEditPanel(InvestmentService service) {
        this.investmentService = service;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setupUI();
        loadInvestments();
    }

    private void setupUI() {
        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Name", "Type", "Broker", "Value", "Rate", "Purchase Date"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = getJPanel();

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel getJPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton editButton = new JButton("Edit Selected");
        editButton.setFont(new Font("Arial", Font.BOLD, 15));
        editButton.addActionListener(e -> handleEdit());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 15));
        refreshButton.addActionListener(e -> loadInvestments());

        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        return buttonPanel;
    }

    public void loadInvestments() {
        List<InvestmentDTO> investments = investmentService.findAll();
        tableModel.setRowCount(0);

        if (investments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load investments or no investments found.",
                    "Loading Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        for (InvestmentDTO dto : investments) {
            tableModel.addRow(new Object[]{
                    dto.id(),
                    dto.name(),
                    dto.type(),
                    dto.broker(),
                    dto.investmentValue(),
                    dto.purchaseRate(),
                    dto.purchaseDate()
            });
        }
    }

    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an investment to edit.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 2);
        String broker = (String) tableModel.getValueAt(selectedRow, 3);
        BigDecimal value = (BigDecimal) tableModel.getValueAt(selectedRow, 4);
        BigDecimal rate = (BigDecimal) tableModel.getValueAt(selectedRow, 5);
        Object dateObj = tableModel.getValueAt(selectedRow, 6);

        LocalDate date;
        if (dateObj instanceof LocalDate) {
            date = (LocalDate) dateObj;
        } else {
            JOptionPane.showMessageDialog(this, "Invalid date type in table. Cannot edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        InvestmentDTO dto = new InvestmentDTO(id, name, type, broker, value, rate, date);

        InvestmentEditDialog dialog = new InvestmentEditDialog(dto, investmentService, v -> loadInvestments());
        dialog.setVisible(true);
    }
}