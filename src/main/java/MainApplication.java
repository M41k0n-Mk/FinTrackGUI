import me.m41k0n.investment.InvestmentTrackerFrame;
import me.m41k0n.investment.service.InvestmentService;

import javax.swing.*;

public class MainApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            InvestmentService service = new InvestmentService();
            new InvestmentTrackerFrame(service);
        });
    }
}