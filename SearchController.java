
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*Shows the dynamic and static queries*/
public class SearchController implements ActionListener, ExceptionListener{
    private MvbView mvb = null;
    private SearchModel smodel = null;

    // constants used for describing the outcome of an operation
    public static final int OPERATIONSUCCESS = 0;
    public static final int OPERATIONFAILED = 1;
    public static final int VALIDATIONERROR = 2;

    public SearchController(MvbView mvb) {
        this.mvb = mvb;
        smodel = new SearchModel();

        smodel.addExceptionListener(this);
    }

    private void showResults(ResultSet rs) {
        CustomTableModel model = new CustomTableModel(smodel.getConnection(), rs);
        CustomTable data = new CustomTable(model);

        // register to be notified of any exceptions
        model.addExceptionListener(this);
        data.addExceptionListener(this);

        // Adds the table to the scrollpane.
        // By default, a JTable does not have scroll bars.
        mvb.addTable(data);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        /*creates dialogs for dynamic queries*/
        if (actionCommand.equals("Employee's Birthdate")) {
            EmpBdateDialog iDialog = new EmpBdateDialog (mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        } else if (actionCommand.equals("Salary Check")) {
            EmpSalaryDialog iDialog = new EmpSalaryDialog (mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }else if (actionCommand.equals("Department Managers")) {
            ResultSet res = smodel.searchDepManager();
            if (res != null) {
                mvb.updateStatusBar("Operation successful.");
                showResults(res);
            } else {
                Toolkit.getDefaultToolkit().beep();
                mvb.updateStatusBar("Operation failed.");
            }

            return;
        }else if (actionCommand.equals("Employee with title and manager position")) {
            EditModel emod = new EditModel();
            ResultSet rs = emod.showTitle();
            showResults(rs);
            EmpTitleDeptDialog iDialog = new EmpTitleDeptDialog (mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }
        else if (actionCommand.equals("Employees on each Department")) {
            EditModel emod = new EditModel();
            ResultSet rs = emod.showDepts();
            showResults(rs);
            EmpDeptDialog iDialog = new EmpDeptDialog (mvb);
            iDialog.pack();
            mvb.centerWindow(iDialog);
            iDialog.setVisible(true);
            return;
        }else if (actionCommand.equals("Sum of this year's bonus")) {
            ResultSet res = smodel.sumBonus();
            String msg;
            System.out.println("HERE");

            if (res != null) {
                mvb.updateStatusBar("Operation successful.");
                showResults(res);


            } else {
                Toolkit.getDefaultToolkit().beep();
                mvb.updateStatusBar("Operation failed.");
            }
        }
        else if(actionCommand.equals("Count of female Department Managers")) {
            ResultSet res = smodel.countFemaleDeptManagers();
            if (res != null) {
                mvb.updateStatusBar("Operation successful.");
                showResults(res);
            } else {
                Toolkit.getDefaultToolkit().beep();
                mvb.updateStatusBar("Operation failed.");
            }

            return;
        }
        else if(actionCommand.equals("Female Department Managers")) {
            ResultSet res = smodel.femaleDeptManagers();
            if (res != null) {
                mvb.updateStatusBar("Operation successful.");
                showResults(res);
            } else {
                Toolkit.getDefaultToolkit().beep();
                mvb.updateStatusBar("Operation failed.");
            }

            return;
        }
    }
    /*Class that creates popup window in order to get info for the request*/
    class EmpDeptDialog extends JDialog implements ActionListener {
        private JTextField dept = new JTextField(15);
        //STATIC QUERY FOR TITLES!//
        public EmpDeptDialog(JFrame parent){
            super(parent, "Check Employees", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Department Field"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place branch id label
            JLabel label = new JLabel("Department: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place branch id field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(dept, c);
            inputPane.add(dept);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            dept.addActionListener(this);
            dept.setActionCommand("OK");

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
        public void actionPerformed(ActionEvent e)
        {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK"))
            {
                if (validateInsert() != VALIDATIONERROR)
                {
                    dispose();
                }
                else
                {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private int validateInsert() {
            try {
                String department;
                if (dept.getText().trim().length() != 0) {
                    department = dept.getText().trim();
                } else {
                    return VALIDATIONERROR;
                }
                mvb.updateStatusBar("Searching...");
                ResultSet result = smodel.searchDepEmployees(department);

                if (result != null) {
                    mvb.updateStatusBar("Operation successful.");

                    showResults(result);


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
    /*Class that creates popup window in order to get info for the request*/
    class EmpTitleDeptDialog extends JDialog implements ActionListener {
        private JTextField title = new JTextField(10);
        //STATIC QUERY FOR TITLES!//
        public EmpTitleDeptDialog(JFrame parent){
            super(parent, "You will be returned all the employees with the title you want that are also department managers", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Title Field"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place branch id label
            JLabel label = new JLabel("Title: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place branch id field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
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
        public void actionPerformed(ActionEvent e)
        {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK"))
            {
                if (validateInsert() != VALIDATIONERROR)
                {
                    dispose();
                }
                else
                {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private int validateInsert() {
            try {
                String titlename;
                if (title.getText().trim().length() != 0) {
                    titlename = title.getText().trim();
                } else {
                    return VALIDATIONERROR;
                }
                mvb.updateStatusBar("Searching...");
                ResultSet result = smodel.searchEmpTitleDept(titlename);

                if (result != null) {
                    mvb.updateStatusBar("Operation successful.");

                    showResults(result);


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
    /*Class that creates popup window in order to get info for the request*/
    class EmpSalaryDialog extends JDialog implements ActionListener {
        private JTextField salary = new JTextField(10);

        /*
         * Constructor. Creates the dialog's GUI.
         */
        public EmpSalaryDialog(JFrame parent) {
            super(parent, "Search Emplooye's Salary", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Search Fields"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place branch id label
            JLabel label = new JLabel("Salary limit: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place branch id field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(salary, c);
            inputPane.add(salary);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            salary.addActionListener(this);
            salary.setActionCommand("OK");

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
        public void actionPerformed(ActionEvent e)
        {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK"))
            {
                if (validateInsert() != VALIDATIONERROR)
                {
                    dispose();
                }
                else
                {
                    Toolkit.getDefaultToolkit().beep();

                    // display a popup to inform the user of the validation error
                    JOptionPane errorPopup = new JOptionPane();
                    errorPopup.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private int validateInsert() {
            try {
                //Date birthdate = new SimpleDateFormat("yyyy/MM/dd").parse(birthDate.getText().trim());
                int sal = 0;

                if (salary.getText().trim().length() != 0) {
                    sal = Integer.valueOf((salary.getText().trim()));
                }

                mvb.updateStatusBar("Searching...");
                ResultSet result = smodel.searchEmpSal(sal);

                if (result != null) {
                    mvb.updateStatusBar("Operation successful.");
                    //if(result.next()){
                    showResults(result);
                    //}

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

    class EmpBdateDialog extends JDialog implements ActionListener {
        private JTextField birthDate = new JTextField(15);
        private JTextField firstName = new JTextField(15);
        private JTextField lastName = new JTextField(15);
        private JTextField gender = new JTextField(15);

        /*
         * Constructor. Creates the dialog's GUI.
         */
        public EmpBdateDialog(JFrame parent) {
            super(parent, "Search Emplooye's Birthdate", true);
            setResizable(true);

            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // this panel will contain the text field labels and the text fields.
            JPanel inputPane = new JPanel();
            inputPane.setBorder(BorderFactory.createCompoundBorder(
                    new TitledBorder(new EtchedBorder(), "Search Fields"),
                    new EmptyBorder(5, 5, 5, 5)));

            // add the text field labels and text fields to inputPane
            // using the GridBag layout manager

            GridBagLayout gb = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            inputPane.setLayout(gb);

            // create and place branch id label
            JLabel label = new JLabel("Birth Date: ", SwingConstants.RIGHT);
            c.gridwidth = GridBagConstraints.RELATIVE;
            c.insets = new Insets(0, 0, 0, 5);
            c.anchor = GridBagConstraints.EAST;
            gb.setConstraints(label, c);
            inputPane.add(label);

            // place branch id field
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(0, 0, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            gb.setConstraints(birthDate, c);
            inputPane.add(birthDate);

            // when the return key is pressed in the last field
            // of this form, the action performed by the ok button
            // is executed
            birthDate.addActionListener(this);
            birthDate.setActionCommand("OK");

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
        public void actionPerformed(ActionEvent e)
        {
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals("OK"))
            {
                if (validateInsert() != VALIDATIONERROR)
                {
                    dispose();
                }
                else
                {
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

                mvb.updateStatusBar("Searching...");
                ResultSet result = smodel.searchEmpBdate(birthdate);

                if (result != null) {
                    mvb.updateStatusBar("Operation successful.");
                    //if(result.next()){
                    showResults(result);
                    //}

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
