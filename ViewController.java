
import java.awt.event.ActionListener;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class ViewController implements ActionListener, ExceptionListener{
    private MvbView mvb = null;
    private ViewModel vmodel = null;

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2;

    public ViewController(MvbView mvb) {
        this.mvb = mvb;
        vmodel = new ViewModel();

        // register to receive exception events from branch
        vmodel.addExceptionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals("Employees View")) {
            ResultSet rs = vmodel.showEmployeeView();
            showView(rs);
            return;
        }
        else if(actionCommand.equals("Create Employees View")){
            ResultSet rs = null;
            if (vmodel.createEmpView()) {
                mvb.updateStatusBar("View created successfully");
                rs = vmodel.showEmployeeView();
                showView(rs);
                return;
            }
            else {
                mvb.updateStatusBar("Operation Failed");
                return;
            }
        }else if (actionCommand.equals("Bonus View")) {
            ResultSet rs = vmodel.showBonusView();
            showView(rs);
            return;
        }
        else if(actionCommand.equals("Create Bonus View")){
            ResultSet rs = null;
            if (vmodel.createBonusView()) {
                mvb.updateStatusBar("View created successfully");
                rs = vmodel.showBonusView();
                showView(rs);
                return;
            }
            else {
                mvb.updateStatusBar("Operation Failed");
                return;
            }
        }
    }

    private void showView(ResultSet rs) {
        CustomTableModel model = new CustomTableModel(vmodel.getConnection(), rs);
        JTable data = new JTable(model);

        // register to be notified of any exceptions that occur in the model and table
        model.addExceptionListener(this);
        model.addExceptionListener(this);

        // Adds the table to the scrollpane.
        // By default, a JTable does not have scroll bars.
        mvb.addTable(data);
    }

   /* private void showViewUser(ResultSet rs) {

        CustomTableModel model = new CustomTableModel(vmodel.getConnection(), rs);
        CustomTable data = new CustomTable(model);

        // register to be notified of any exceptions that occur in the model and table
        model.addExceptionListener(this);
        data.addExceptionListener(this);

        // Adds the table to the scrollpane.
        // By default, a JTable does not have scroll bars.
        mvb.addTable(data);
    }*/

    public void exceptionGenerated(ExceptionEvent ex) {
        String message = ex.getMessage();

        // annoying beep sound
        Toolkit.getDefaultToolkit().beep();

        if (message != null) {
            mvb.updateStatusBar(ex.getMessage());
        }
        else {
            mvb.updateStatusBar("An exception occurred!");
        }
    }
}
