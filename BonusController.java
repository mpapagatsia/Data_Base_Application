import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;

public class BonusController implements ActionListener, ExceptionListener {
    private MvbView mvb = null;
    private BonusModel bonus = null;

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2;

    public BonusController(MvbView mvb) {
        this.mvb = mvb;
        bonus = new BonusModel();

        // register to receive exception events from Bonus
        bonus.addExceptionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals("Create Table")) {
            createBonusTable();
            return;
        }else if (actionCommand.equals("Delete Table")){
            deleteBonusTable();
        }
    }

    private void deleteBonusTable() {

        if (bonus.deleteBonus()) {
            mvb.updateStatusBar("Table deleted successfully");
        }
    }

    private void createBonusTable() {
        if (bonus.createBonus()) {
            mvb.updateStatusBar("Table created successfully");
            ResultSet rs = bonus.showBonus();
        }

        ResultSet rs = bonus.showBonus();

        CustomTableModel model = new CustomTableModel(bonus.getConnection(), rs);
        JTable data = new JTable(model);

        model.addExceptionListener(this);
        model.addExceptionListener(this);

        mvb.addTable(data);
    }

    public void exceptionGenerated(ExceptionEvent ex) {
        String message = ex.getMessage();

        Toolkit.getDefaultToolkit().beep();

        if (message != null) {
            mvb.updateStatusBar(ex.getMessage());
        }
        else {
            mvb.updateStatusBar("An exception occurred!");
        }
    }
}
