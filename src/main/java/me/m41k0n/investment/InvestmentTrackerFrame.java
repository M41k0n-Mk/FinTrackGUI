package me.m41k0n.investment;

import me.m41k0n.investment.dto.InvestmentDTO;
import me.m41k0n.investment.service.InvestmentService;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class InvestmentTrackerFrame extends JFrame {

    private final InvestmentService investmentService;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> nameComboBox;
    private JTextField brokerField;
    private JFormattedTextField valueField, purchaseRateField;
    private JDatePickerImpl datePicker;

    private static final Map<String, String[]> TYPE_TO_NAMES = new LinkedHashMap<>();
    static {
        TYPE_TO_NAMES.put("Renda Fixa", new String[] {
                "CDB", "LCI", "LCA", "Tesouro Selic", "Tesouro IPCA+", "Tesouro Prefixado", "CRI", "CRA", "Debêntures", "Fundos DI"
        });
        TYPE_TO_NAMES.put("Renda Variável", new String[] {
                "Ações", "ETF", "BDR", "FII (Fundo Imobiliário)", "Small Caps", "Fundos de Ações", "Opções", "Derivativos"
        });
        TYPE_TO_NAMES.put("Tesouro Direto", new String[] {
                "Tesouro Selic", "Tesouro IPCA+", "Tesouro Prefixado"
        });
        TYPE_TO_NAMES.put("Fundos", new String[] {
                "Fundo de Renda Fixa", "Fundo de Ações", "Fundo Multimercado", "Fundo Cambial", "Fundo Imobiliário"
        });
        TYPE_TO_NAMES.put("Previdência", new String[] {
                "PGBL", "VGBL"
        });
        TYPE_TO_NAMES.put("Criptoativos", new String[] {
                "Bitcoin", "Ethereum", "Solana", "Cardano", "BNB", "Litecoin", "Polkadot", "XRP", "Stablecoin", "Fundos Cripto", "Outros"
        });
        TYPE_TO_NAMES.put("Imóveis", new String[] {
                "Casa", "Apartamento", "Terreno", "Galpão", "Sala Comercial", "Fazenda"
        });
        TYPE_TO_NAMES.put("Outros", new String[] {
                "Ouro", "Prata", "Commodities", "Colecionáveis", "Startup", "Private Equity"
        });
    }

    public InvestmentTrackerFrame(InvestmentService service) {
        this.investmentService = service;
        setTitle("FinTrack - Gerenciador de Investimentos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setupMainUI();
        setVisible(true);
    }

    private void setupMainUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.addTab("Cadastrar Investimento", createRegisterPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Font labelFont = new Font("Arial", Font.BOLD, 15);
        Font fieldFont = new Font("Arial", Font.PLAIN, 15);

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(4);

        int y = 0;

        formPanel.add(createLabel("Tipo:", labelFont), gbc(0, y, GridBagConstraints.LINE_END));
        typeComboBox = new JComboBox<>(TYPE_TO_NAMES.keySet().toArray(new String[0]));
        typeComboBox.setFont(fieldFont);
        formPanel.add(typeComboBox, gbc(1, y++, GridBagConstraints.LINE_START));

        formPanel.add(createLabel("Nome:", labelFont), gbc(0, y, GridBagConstraints.LINE_END));
        nameComboBox = new JComboBox<>(TYPE_TO_NAMES.get(typeComboBox.getSelectedItem()));
        nameComboBox.setFont(fieldFont);
        formPanel.add(nameComboBox, gbc(1, y++, GridBagConstraints.LINE_START));

        typeComboBox.addActionListener(e -> {
            String selectedType = (String) typeComboBox.getSelectedItem();
            nameComboBox.setModel(new DefaultComboBoxModel<>(TYPE_TO_NAMES.get(selectedType)));
        });

        formPanel.add(createLabel("Corretora/Banco:", labelFont), gbc(0, y, GridBagConstraints.LINE_END));
        brokerField = createTextField(fieldFont);
        formPanel.add(brokerField, gbc(1, y++, GridBagConstraints.LINE_START));

        formPanel.add(createLabel("Valor Total (R$):", labelFont), gbc(0, y, GridBagConstraints.LINE_END));
        valueField = createFormattedField(currencyFormat, fieldFont);
        valueField.setValue(0.00);
        formPanel.add(valueField, gbc(1, y++, GridBagConstraints.LINE_START));

        formPanel.add(createLabel("Preço/Taxa na Compra:", labelFont), gbc(0, y, GridBagConstraints.LINE_END));
        purchaseRateField = createFormattedField(currencyFormat, fieldFont);
        purchaseRateField.setValue(0.00);
        formPanel.add(purchaseRateField, gbc(1, y++, GridBagConstraints.LINE_START));

        formPanel.add(createLabel("Data de Compra:", labelFont), gbc(0, y, GridBagConstraints.LINE_END));
        UtilDateModel model = new UtilDateModel();
        model.setValue(new java.util.Date());
        JDatePanelImpl datePanel = new JDatePanelImpl(model, new Properties());
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        model.setSelected(true);
        datePicker.getJFormattedTextField().setFont(fieldFont);
        datePicker.getJFormattedTextField().setColumns(22);
        datePicker.getJFormattedTextField().setEnabled(true);
        datePicker.getJFormattedTextField().setEditable(true);
        formPanel.add(datePicker, gbc(1, y++, GridBagConstraints.LINE_START));

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton saveButton = new JButton("Salvar");
        saveButton.setFont(new Font("Arial", Font.BOLD, 15));
        saveButton.addActionListener(e -> handleSaveOrUpdate());
        JButton clearButton = new JButton("Limpar");
        clearButton.setFont(new Font("Arial", Font.BOLD, 15));
        clearButton.addActionListener(e -> clearFields());
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        formPanel.add(buttonPanel, gbc);
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private JTextField createTextField(Font font) {
        JTextField field = new JTextField(22);
        field.setFont(font);
        return field;
    }

    private JFormattedTextField createFormattedField(NumberFormat format, Font font) {
        JFormattedTextField field = new JFormattedTextField(format);
        field.setColumns(22);
        field.setFont(font);
        return field;
    }

    private GridBagConstraints gbc(int x, int y, int anchor) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = (x == 1) ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        c.weightx = (x == 1) ? 1.0 : 0.0;
        c.gridx = x;
        c.gridy = y;
        c.anchor = anchor;
        return c;
    }

    private void handleSaveOrUpdate() {
        String type = (String) typeComboBox.getSelectedItem();
        String name = (String) nameComboBox.getSelectedItem();
        String broker = brokerField.getText().trim();
        String valueStr = valueField.getText().trim().replace(",", ".");
        String rateStr = purchaseRateField.getText().trim().replace(",", ".");
        java.util.Date purchaseUtilDate = (java.util.Date) datePicker.getModel().getValue();

        if (name == null || type == null || broker.isEmpty() || valueStr.isEmpty() || rateStr.isEmpty() || purchaseUtilDate == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal value = new BigDecimal(valueStr);
            BigDecimal purchaseRate = new BigDecimal(rateStr);
            LocalDate purchaseDate = purchaseUtilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            InvestmentDTO request = new InvestmentDTO(name, type, broker, value, purchaseRate, purchaseDate);
            investmentService.save(request);
            JOptionPane.showMessageDialog(this, "Investimento salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Formato inválido para números. Use apenas números (ex: 1000.50).", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o investimento: " + ex.getMessage(), "Erro interno", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        typeComboBox.setSelectedIndex(0);
        nameComboBox.setSelectedIndex(0);
        brokerField.setText("");
        valueField.setValue(0.00);
        purchaseRateField.setValue(0.00);
        UtilDateModel model = (UtilDateModel) datePicker.getModel();
        java.util.Date newDate = new java.util.Date();
        model.setValue(newDate);
        model.setSelected(true);
        try {
            JFormattedTextField.AbstractFormatter formatter = datePicker.getJFormattedTextField().getFormatter();
            if (formatter != null) {
                datePicker.getJFormattedTextField().setText(formatter.valueToString(newDate));
            }
        } catch (ParseException e) {
            datePicker.getJFormattedTextField().setText("");
        }
        typeComboBox.requestFocusInWindow();
    }

    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String datePattern = "yyyy-MM-dd";
        private final transient SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value == null) return "";
            if (value instanceof java.util.Date) {
                return dateFormatter.format((java.util.Date) value);
            } else if (value instanceof java.util.Calendar) {
                return dateFormatter.format(((java.util.Calendar) value).getTime());
            }
            return value.toString();
        }
    }
}