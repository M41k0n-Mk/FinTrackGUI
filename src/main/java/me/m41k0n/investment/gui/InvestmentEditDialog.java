package me.m41k0n.investment.gui;

import me.m41k0n.investment.dto.InvestmentDTO;
import me.m41k0n.investment.service.InvestmentService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class InvestmentEditDialog extends JDialog {
    private final InvestmentService investmentService;
    private final InvestmentDTO original;
    private final Consumer<Void> refreshCallback;

    private JTextField nameField, typeField, brokerField;
    private JFormattedTextField valueField, rateField;
    private JTextField dateField;
    private JComboBox<String> operationTypeComboBox;

    public InvestmentEditDialog(InvestmentDTO dto, InvestmentService service, Consumer<Void> refreshCallback) {
        this.original = dto;
        this.investmentService = service;
        this.refreshCallback = refreshCallback;
        setTitle("Edit Investment");
        setModal(true);
        setSize(420, 400);
        setLocationRelativeTo(null);
        setupUI();
        fillFields(dto);
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Name:"));
        nameField = new JTextField(20); panel.add(nameField);

        panel.add(new JLabel("Type:"));
        typeField = new JTextField(20); panel.add(typeField);

        panel.add(new JLabel("Broker:"));
        brokerField = new JTextField(20); panel.add(brokerField);

        panel.add(new JLabel("Value:"));
        valueField = new JFormattedTextField(); panel.add(valueField);

        panel.add(new JLabel("Rate:"));
        rateField = new JFormattedTextField(); panel.add(rateField);

        panel.add(new JLabel("Operation Type:"));
        operationTypeComboBox = new JComboBox<>(new String[]{"COMPRA", "VENDA"});
        panel.add(operationTypeComboBox);

        panel.add(new JLabel("Purchase Date (yyyy-MM-dd):"));
        dateField = new JTextField(20); panel.add(dateField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> handleSave());
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        setContentPane(panel);
    }

    private void fillFields(InvestmentDTO dto) {
        nameField.setText(dto.name());
        typeField.setText(dto.type());
        brokerField.setText(dto.broker());
        valueField.setValue(dto.investmentValue());
        rateField.setValue(dto.purchaseRate());
        operationTypeComboBox.setSelectedItem(dto.operationType());
        dateField.setText(dto.purchaseDate().toString());
    }

    private void handleSave() {
        try {
            InvestmentDTO updated = getInvestmentDTO();
            String result = investmentService.update(updated);

            if ("success".equals(result)) {
                JOptionPane.showMessageDialog(this, "Investment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                refreshCallback.accept(null);
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + result, "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private InvestmentDTO getInvestmentDTO() {
        String id = original.id();
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String broker = brokerField.getText().trim();
        String operationType = (String) operationTypeComboBox.getSelectedItem();
        String dateText = dateField.getText().trim();
        LocalDate date;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = LocalDate.parse(dateText, formatter);
        } catch (java.time.format.DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }

        String valueText = valueField.getText().trim().replace(",", ".");
        String rateText = rateField.getText().trim().replace(",", ".");
        BigDecimal value, rate;
        try {
            value = new BigDecimal(valueText);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid value: '" + valueText + "'. Please enter a valid decimal number.");
        }
        try {
            rate = new BigDecimal(rateText);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid rate: '" + rateText + "'. Please enter a valid decimal number.");
        }

        return new InvestmentDTO(id, name, type, broker, value, rate, date, operationType);
    }
}