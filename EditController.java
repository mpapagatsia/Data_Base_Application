
import java.awt.event.ActionListener;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditController implements ActionListener, ExceptionListener  {
    private MvbView mvb = null;
    private EditModel emodel = null;

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2;

    public EditController(MvbView mvb) {
        this.mvb = mvb;
        emodel = new EditModel();

        emodel.addExceptionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals("New Employee")) {
            EmployeeInsertDialog iDialog = new EmployeeInsertDialog(mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }else if (actionCommand.equals("Remove Employee")) {
            EmployeeRemovalDialog iDialog = new EmployeeRemovalDialog(mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }else if (actionCommand.equals("New Department")) {
            ResultSet res = emodel.showDepts();
            showEditTable(res);
            DepartmentInsertDialog iDialog = new DepartmentInsertDialog(mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }else if (actionCommand.equals("New Department Manager")) {
            DepManagerInsertDialog iDialog = new DepManagerInsertDialog(mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }
        else if (actionCommand.equals("New Department Employee")) {
            DepEmployeeInsertDialog iDialog = new DepEmployeeInsertDialog(mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }
        else if (actionCommand.equals("Insert title for new employee")) {
            ResultSet res = emodel.showTitle();
            showEditTable(res);
            TitleInsertDialog iDialog = new TitleInsertDialog(mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }
        else if (actionCommand.equals("Insert Salary for new employee")) {
            SalaryInsertDialog iDialog = new SalaryInsertDialog(mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }
    }

    /* Shows the info of the employee that has been removed*/
    private void showRemoveEmployee(int empno) {
        ResultSet rs = emodel.showEmployeeremoved(empno);

        CustomTableModel model = new CustomTableModel(emodel.getConnection(), rs);
        CustomTable data = new CustomTable(model);

        model.addExceptionListener(this);
        data.addExceptionListener(this);

        mvb.addTable(data);
    }

    /* Shows the data that have been inserted to a table*/
    private void showEditTable(ResultSet rs) {
        CustomTableModel model = new CustomTableModel(emodel.getConnection(), rs);
        CustomTable data = new CustomTable(model);

        model.addExceptionListener(this);
        data.addExceptionListener(this);

        mvb.addTable(data);
    }

    /* Given the number of the new employee, a salary will be chosen
     * considering the employee's current title.*/
    class SalaryInsertDialog extends JDialog implements ActionListener {
        private JTextField empNo = new JTextField(10);

        /*
         * Constructor. Creates the dialog's GUI.
         */
        public SalaryInsertDialog(JFrame parent) {
            super(parent, "Insert Salary for New Employee", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Given the number of the new employee, a salary will be chosen considering the employee's current title."),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager
            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place label
            JLabel label = new JLabel("Employee Number: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place emp_no field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(empNo, c);
            inputPane.add(empNo);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            empNo.addActionListener(this);
            empNo.setActionCommand("OK");

            // panel for the OK and cancel buttons
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

            JButton OKButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            OKButton.addActionListener(this);
            OKButton.setActionCommand("OK");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            // add the buttons to buttonPane
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(OKButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);

            contentPane.add(inputPane, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }

        public void actionPerformed(ActionEvent e){
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK")) {
                if (validateInsert() != VALIDATIONERROR) {
                    dispose();
                }
                else {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private int validateInsert() {
            try {
                int empno;

                if (empNo.getText().trim().length() != 0){
                    empno = Integer.parseInt(empNo.getText());
                    System.out.println(empno);
                }else {
                    return VALIDATIONERROR;
                }

                mvb.updateStatusBar("Inserting salary...");

                if (emodel.insertSalary(empno)) {
                    mvb.updateStatusBar("Operation successful.");

                    return OPERATIONSUCCESS;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    mvb.updateStatusBar("Operation failed.");
                    return OPERATIONFAILED;
                }
            } catch (NumberFormatException ex) {
                // this exception is thrown when a string
                // cannot be converted to a number
                return VALIDATIONERROR;
            }
        }
    }

    /* Insert a new title in titles Table*/
    class TitleInsertDialog extends JDialog implements ActionListener {
        private JTextField empNo = new JTextField(10);
        private JTextField title = new JTextField(10);

        /*
         * Constructor. Creates the dialog's GUI.
         */
        public TitleInsertDialog(JFrame parent) {
            super(parent, "Insert Title for New Employee", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Title Fields"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place emp_no label
            JLabel label = new JLabel("Employee Number: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place emp_no field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(empNo, c);
            inputPane.add(empNo);

            // create and place title label
            label = new JLabel("Work title: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(5, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place title field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(title, c);
            inputPane.add(title);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            title.addActionListener(this);
            title.setActionCommand("OK");

            // panel for the OK and cancel buttons
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

            JButton OKButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            OKButton.addActionListener(this);
            OKButton.setActionCommand("OK");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            // add the buttons to buttonPane
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(OKButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);

            contentPane.add(inputPane, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK")) {
                if (validateInsert() != VALIDATIONERROR) {
                    dispose();
                }
                else {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private int validateInsert() {
            try {
                int empno;
                String titlename;

                if (empNo.getText().trim().length() != 0){
                    empno = Integer.parseInt(empNo.getText());
                }else {
                    return VALIDATIONERROR;
                }
                if (title.getText().trim().length() != 0) {
                    titlename = title.getText().trim();
                } else {
                    return VALIDATIONERROR;
                }

                mvb.updateStatusBar("Inserting title...");

                if (emodel.insertTitle(empno, titlename)) {
                    mvb.updateStatusBar("Operation successful.");

                    return OPERATIONSUCCESS;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    mvb.updateStatusBar("Operation failed.");
                    return OPERATIONFAILED;
                }
            } catch (NumberFormatException ex) {
                // this exception is thrown when a string
                // cannot be converted to a number
                return VALIDATIONERROR;
            }
        }
    }

    /* Insert a new Department Manager in dept_managers Table*/
    class DepManagerInsertDialog extends JDialog implements ActionListener {
        private JTextField empno = new JTextField(15);
        private JTextField deptno = new JTextField(15);
        private JTextField fromDate = new JTextField(15);
        private JTextField toDate = new JTextField(15);

        /*
         * Constructor. Creates the dialog's GUI.
         */
        public DepManagerInsertDialog(JFrame parent) {
            super(parent, "Insert New Department Manager", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Department Manager Fields"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place emp_no label
            JLabel label = new JLabel("Employee Number: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place emp_no field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(empno, c);
            inputPane.add(empno);

            // create and place dept_no label
            label = new JLabel("Department Number: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place dept_no field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(deptno, c);
            inputPane.add(deptno);

            // create and place fromdate label
            label = new JLabel("From Date: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place fromdate field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(fromDate, c);
            inputPane.add(fromDate);

            // create and place todate label
            label = new JLabel("To Date: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place todate field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(toDate, c);
            inputPane.add(toDate);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            toDate.addActionListener(this);
            toDate.setActionCommand("OK");

            // panel for the OK and cancel buttons
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

            JButton OKButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            OKButton.addActionListener(this);
            OKButton.setActionCommand("OK");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            // add the buttons to buttonPane
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(OKButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);

            contentPane.add(inputPane, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK")) {
                if (validateInsert() != VALIDATIONERROR) {
                    dispose();
                }
                else {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private int validateInsert() {
            try {
                int enumber;
                String dnumber;
                Date fdate = new SimpleDateFormat("yyyy/MM/dd").parse(fromDate.getText().trim());
                Date tdate = new SimpleDateFormat("yyyy/MM/dd").parse(toDate.getText().trim());


                if (deptno.getText().trim().length() != 0) {
                    dnumber = deptno.getText().trim();
                } else {
                    return VALIDATIONERROR;
                }

                if (empno.getText().trim().length() != 0) {
                    enumber = Integer.valueOf(empno.getText().trim());
                } else {
                    return VALIDATIONERROR;
                }

                mvb.updateStatusBar("Inserting Department Manager...");

                if (emodel.insertDeptManager(enumber,dnumber,fdate,tdate)) {
                    mvb.updateStatusBar("Operation successful.");
                    ResultSet rs = emodel.showDeptManager();
                    showEditTable(rs);
                    return OPERATIONSUCCESS;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    mvb.updateStatusBar("Operation failed.");
                    return OPERATIONFAILED;
                }
            } catch (NumberFormatException ex) {
                // this exception is thrown when a string
                // cannot be converted to a number
                return VALIDATIONERROR;
            } catch (ParseException e) {
                e.printStackTrace();
                return  VALIDATIONERROR;
            }
        }
    }

    /* Insert a new Department Employee in dept_employees Table*/
    class DepEmployeeInsertDialog extends JDialog implements ActionListener {
        private JTextField empno = new JTextField(15);
        private JTextField deptno = new JTextField(15);
        private JTextField fromDate = new JTextField(15);
        private JTextField toDate = new JTextField(15);

        /*
         * Constructor. Creates the dialog's GUI.
         */
        public DepEmployeeInsertDialog(JFrame parent) {
            super(parent, "Insert New Department Employee", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Department Employee Fields"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place emp_no label
            JLabel label = new JLabel("Employee Number: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place emp_no field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(empno, c);
            inputPane.add(empno);

            // create and place dept_no label
            label = new JLabel("Department Number: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place dept_no field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(deptno, c);
            inputPane.add(deptno);

            // create and place fromdate label
            label = new JLabel("From Date: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place fromdate field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(fromDate, c);
            inputPane.add(fromDate);

            // create and place todate label
            label = new JLabel("To Date: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place todate field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(toDate, c);
            inputPane.add(toDate);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            toDate.addActionListener(this);
            toDate.setActionCommand("OK");

            // panel for the OK and cancel buttons
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

            JButton OKButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            OKButton.addActionListener(this);
            OKButton.setActionCommand("OK");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            // add the buttons to buttonPane
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(OKButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);

            contentPane.add(inputPane, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK")) {
                if (validateInsert() != VALIDATIONERROR) {
                    dispose();
                }
                else {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private int validateInsert() {
            try {
                int enumber;
                String dnumber;
                Date fdate = new SimpleDateFormat("yyyy/MM/dd").parse(fromDate.getText().trim());
                Date tdate = new SimpleDateFormat("yyyy/MM/dd").parse(toDate.getText().trim());


                if (deptno.getText().trim().length() != 0) {
                    dnumber = deptno.getText().trim();
                } else {
                    return VALIDATIONERROR;
                }

                if (empno.getText().trim().length() != 0) {
                    enumber = Integer.valueOf(empno.getText().trim());
                } else {
                    return VALIDATIONERROR;
                }

                mvb.updateStatusBar("Inserting Department Employee...");

                if (emodel.insertDeptEmployee(enumber,dnumber,fdate,tdate)) {
                    mvb.updateStatusBar("Operation successful.");
                    ResultSet rs = emodel.showDeptEmployee();
                    showEditTable(rs);
                    return OPERATIONSUCCESS;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    mvb.updateStatusBar("Operation failed.");
                    return OPERATIONFAILED;
                }
            } catch (NumberFormatException ex) {
                // this exception is thrown when a string
                // cannot be converted to a number
                return VALIDATIONERROR;
            } catch (ParseException e) {
                e.printStackTrace();
                return  VALIDATIONERROR;
            }
        }
    }

    /* Insert a new department in departments Table*/
    class DepartmentInsertDialog extends JDialog implements ActionListener {
        private JTextField deptName = new JTextField(15);

        /*
         * Constructor. Creates the dialog's GUI.
         */
        public DepartmentInsertDialog(JFrame parent) {
            super(parent, "Insert New Department", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Department Fields"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place deptname label
            JLabel label = new JLabel("Name: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place deptname field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(deptName, c);
            inputPane.add(deptName);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            deptName.addActionListener(this);
            deptName.setActionCommand("OK");

            // panel for the OK and cancel buttons
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

            JButton OKButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            OKButton.addActionListener(this);
            OKButton.setActionCommand("OK");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            // add the buttons to buttonPane
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(OKButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);

            contentPane.add(inputPane, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK")) {
                if (validateInsert() != VALIDATIONERROR) {
                    dispose();
                }
                else {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private int validateInsert() {
            try {
                String name;

                if (deptName.getText().trim().length() != 0) {
                    name = deptName.getText().trim();
                } else {
                    return VALIDATIONERROR;
                }

                mvb.updateStatusBar("Inserting Department...");

                if (emodel.insertDepartment(name)) {
                    mvb.updateStatusBar("Operation successful.");
                    ResultSet rs = emodel.showDepartment();
                    showEditTable(rs);
                    return OPERATIONSUCCESS;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    mvb.updateStatusBar("Operation failed.");
                    return OPERATIONFAILED;
                }
            } catch (NumberFormatException ex) {
                // this exception is thrown when a string
                // cannot be converted to a number
                return VALIDATIONERROR;
            }
        }
    }

    /* Insert a new Employee in employees Table*/
    class EmployeeInsertDialog extends JDialog implements ActionListener {
        private JTextField birthDate = new JTextField(15);
        private JTextField firstName = new JTextField(15);
        private JTextField lastName = new JTextField(15);
        private JTextField gender = new JTextField(15);

        /*
         * Constructor. Creates the dialog's GUI.
         */
        public EmployeeInsertDialog(JFrame parent) {
            super(parent, "Insert New Employee", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Employee Fields"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place birthDate label
            JLabel label = new JLabel("Birth Date: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place birthDate field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(birthDate, c);
            inputPane.add(birthDate);

            // create and place firstName label
            label = new JLabel("First Name: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(5, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place firstName field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(firstName, c);
            inputPane.add(firstName);

            // create and place lastName label
            label = new JLabel("Last Name: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(5, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place lastName field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(lastName, c);
            inputPane.add(lastName);

            // create and place gender label
            label = new JLabel("Gender: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(5, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place gender field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(gender, c);
            inputPane.add(gender);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            gender.addActionListener(this);
            gender.setActionCommand("OK");

            // panel for the OK and cancel buttons
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

            JButton OKButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            OKButton.addActionListener(this);
            OKButton.setActionCommand("OK");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            // add the buttons to buttonPane
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(OKButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);

            contentPane.add(inputPane, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK")) {
                if (validateInsert() != VALIDATIONERROR) {
                    dispose();
                }
                else {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private int validateInsert() {
            try {
                Date birthdate = new SimpleDateFormat("yyyy/MM/dd").parse(birthDate.getText().trim());
                String fname;
                String lname;
                String g = gender.getText().trim();

                if (firstName.getText().trim().length() != 0) {
                    fname = firstName.getText().trim();
                } else {
                    return VALIDATIONERROR;
                }
                if (lastName.getText().trim().length() != 0) {
                    lname = lastName.getText().trim();
                } else {
                    return VALIDATIONERROR;
                }

                mvb.updateStatusBar("Inserting Employee...");

                if (emodel.insertEmployee(birthdate, fname, lname, g)) {
                    mvb.updateStatusBar("Operation successful.");
                    ResultSet rs = emodel.showEmployee();
                    showEditTable(rs);
                    return OPERATIONSUCCESS;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    mvb.updateStatusBar("Operation failed.");
                    return OPERATIONFAILED;
                }
            } catch (NumberFormatException ex) {
                // this exception is thrown when a string
                // cannot be converted to a number
                return VALIDATIONERROR;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return OPERATIONSUCCESS;
        }
    }

    /* Removes an employee given his emp_no*/
    class EmployeeRemovalDialog extends JDialog implements ActionListener {
        private JTextField employee_no = new JTextField(15);


        public EmployeeRemovalDialog(JFrame parent) {
            super(parent, "Remove an Employee", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "  "),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place employee_no label
            JLabel label = new JLabel("Employee number: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place employee_no field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(employee_no, c);
            inputPane.add(employee_no);

            // panel for the OK and cancel buttons
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 2));

            JButton OKButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            OKButton.addActionListener(this);
            OKButton.setActionCommand("OK");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            // add the buttons to buttonPane
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(OKButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);

            contentPane.add(inputPane, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });

        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK")) {
                if (validateRemoval() != VALIDATIONERROR) {
                    dispose();
                }
                else {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private int validateRemoval() {
            try {
                int empno;

                if (employee_no.getText().trim().length() != 0){
                    empno = Integer.parseInt(employee_no.getText());
                }else {
                    return VALIDATIONERROR;
                }

                mvb.updateStatusBar("Removing Employee...");
                showRemoveEmployee(empno);
                if (emodel.removeEmployee(empno)) {
                    mvb.updateStatusBar("Operation successful.");

                    return OPERATIONSUCCESS;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    mvb.updateStatusBar("Operation failed.");
                    return OPERATIONFAILED;
                }
            } catch (NumberFormatException ex) {
                // this exception is thrown when a string
                // cannot be converted to a number
                return VALIDATIONERROR;
            }
        }
    }

    /*
     * This event handler gets called when an exception event
     * is generated. It displays the exception message on the status
     * text area of the main GUI.
     */
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
