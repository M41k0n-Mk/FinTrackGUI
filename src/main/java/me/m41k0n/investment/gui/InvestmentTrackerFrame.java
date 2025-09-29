package me.m41k0n.investment.gui;

import me.m41k0n.investment.service.InvestmentService;

import javax.swing.*;
import java.awt.*;

public class InvestmentTrackerFrame extends JFrame {

    private final InvestmentService investmentService;

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
        tabbedPane.addTab("Cadastrar Investimento", new InvestmentRegisterPanel(investmentService));
        tabbedPane.addTab("Editar Investimento", new InvestmentEditPanel(investmentService));
        add(tabbedPane, BorderLayout.CENTER);
    }
}