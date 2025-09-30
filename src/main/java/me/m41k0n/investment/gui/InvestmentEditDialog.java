package me.m41k0n.investment.gui;

import me.m41k0n.investment.dto.InvestmentDTO;
import me.m41k0n.investment.service.InvestmentService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Consumer;

public class InvestmentEditDialog extends JDialog {
    private final InvestmentService investmentService;
    private final InvestmentDTO original;
    private final Consumer<Void> refreshCallback;

    private JTextField nameField, typeField, brokerField;
    private JFormattedTextField valueField, rateField;
    private JTextField dateField;

    public InvestmentEditDialog(InvestmentDTO dto, InvestmentService service, Consumer<Void> refreshCallback) {
        this.original = dto;
        this.investmentService = service;
        this.refreshCallback = refreshCallback;
        setTitle("Edit Investment");
        setModal(true);
        setSize(420, 360);
        setLocationRelativeTo(null);
        setupUI();
        fillFields(dto);
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
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
        LocalDate date = LocalDate.parse(dateField.getText().trim());

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

        return new InvestmentDTO(id, name, type, broker, value, rate, date);
    }
}