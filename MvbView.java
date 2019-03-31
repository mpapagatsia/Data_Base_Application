
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;

/*
 * MvbView allows a user to view and manipulate the Employees Schema.
 */ 
public class MvbView extends JFrame {
    // initial position of the main frame
	private int framePositionX;
	private int framePositionY;

	// initial size of main frame
	private Rectangle frameBounds = null;

	// the status text area for displaying error messages
	private JTextArea statusField = new JTextArea(5, 0);

	// the scrollpane that will hold the table of database data
	private JScrollPane tableScrPane = new JScrollPane();

	private JMenu createMenu; // the Create Table menu
	private JMenu editMenu; //The Edit Table menu
	private JMenu viewMenu; //The View menu
	private JMenu searchMenu; //The Search menu (dynamic and static selects)
    private JButton exitopt;

	/*
	 * Default constructor. Constructs the main window.
	 */
	public MvbView() {
		super("Employer 's DB - Administration");
		setSize(650, 450);

		// the content pane;
		// components will be spaced vertically 10 pixels apart
		JPanel contentPane = new JPanel(new BorderLayout(0, 10));
		setContentPane(contentPane);

		// leave some space around the content pane
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// setup the menubar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// indent first menu
		menuBar.add(Box.createRigidArea(new Dimension(10, 0)));

		// sets up the Create Table menu and adds it to the menu bar
		setupBonusTable(menuBar);
		// sets up the Edit Table menu and adds it to the menu bar
		setupEditTable(menuBar);
        // sets up the View menu and adds it to the menu bar
		setupView(menuBar);
        // sets up the Search menu and adds it to the menu bar
		setupSearchMenu(menuBar);
        // sets up the Exit option and adds it to the menu bar
        exitOption(menuBar);


		// the scrollpane for the status text field
		JScrollPane statusScrPane = new JScrollPane(statusField);
		statusScrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		statusScrPane.setBorder(BorderFactory.createLoweredBevelBorder());

		// set status field properties
		statusField.setEditable(false);
		statusField.setLineWrap(true);
		statusField.setWrapStyleWord(true);

		// add the panes to the content pane
		contentPane.add(tableScrPane, BorderLayout.CENTER);
		contentPane.add(statusScrPane, BorderLayout.NORTH);

		// center the main window
		Dimension screenSize = getToolkit().getScreenSize();
		frameBounds = getBounds();
		framePositionX = (screenSize.width - frameBounds.width) / 2;
		framePositionY = (screenSize.height - frameBounds.height) / 2;
		setLocation(framePositionX, framePositionY);

		// anonymous inner class to terminate program
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private void exitOption(JMenuBar mb){
        exitopt = new JButton("Exit");
        exitopt.setMnemonic(KeyEvent.VK_X);
        mb.add(exitopt);
        exitopt.addActionListener(new ExitListener(this));
    }

    class ExitListener implements ActionListener{
	    MvbView mvb;
	    public ExitListener(MvbView mb){
	        mvb = mb;
        }
        public void actionPerformed(ActionEvent e) {
	        Connection con = DBConnection.getInstance().getConnection();
            try {
                con.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            mvb.dispose();
        }
    }
	
			
	/*
	 * Functions that Add menu items to the menus and then
	 * add each menu to the menubar
	 */
	 
	private void setupBonusTable(JMenuBar mb) {
		createMenu = new JMenu("Bonus Table");
		createMenu.setMnemonic(KeyEvent.VK_B);

		createMenuItem(createMenu, "Create Table",
				KeyEvent.VK_C, "Create Table");

		createMenuItem(createMenu, "Delete Table",
				KeyEvent.VK_D, "Delete Table");
        mb.add(createMenu);

	}

    private void setupEditTable(JMenuBar mb){
        editMenu= new JMenu("Edit Table");
        editMenu.setMnemonic(KeyEvent.VK_E);

        createMenuItem(editMenu, "New Employee",
                KeyEvent.VK_D, "New Employee");

        createMenuItem(editMenu, "Remove Employee",
                KeyEvent.VK_R, "Remove Employee");

        createMenuItem(editMenu, "New Department",
                KeyEvent.VK_D, "New Department");

        createMenuItem(editMenu, "New Department Manager",
                KeyEvent.VK_D, "New Department Manager");

        createMenuItem(editMenu, "New Department Employee",
                KeyEvent.VK_D, "New Department Employee");

        createMenuItem(editMenu, "Insert title for new employee",
                KeyEvent.VK_I, "Insert title for new employee");

        createMenuItem(editMenu, "Insert Salary for new employee",
                KeyEvent.VK_I, "Insert Salary for new employee");

        mb.add(editMenu);

    }

    private void setupSearchMenu(JMenuBar mb){
        searchMenu = new JMenu("Search");
        searchMenu.setMnemonic(KeyEvent.VK_S);

        createMenuItem(searchMenu, "Employee's Birthdate",
                KeyEvent.VK_B, "Employee's Birthdate");

        createMenuItem(searchMenu, "Salary Check",
                KeyEvent.VK_S, "Salary Check");

        createMenuItem(searchMenu, "Department Managers",
                KeyEvent.VK_D, "Department Managers");

        createMenuItem(searchMenu, "Employees on each Department",
                KeyEvent.VK_D, "Employees on each Department");

        createMenuItem(searchMenu, "Employee with title and manager position",
                KeyEvent.VK_T, "Employee with title and manager position");

        createMenuItem(searchMenu, "Sum of this year's bonus",
                KeyEvent.VK_B, "Sum of this year's bonus");

        createMenuItem(searchMenu, "Count of female Department Managers",
                KeyEvent.VK_C, "Count of female Department Managers");

        createMenuItem(searchMenu, "Female Department Managers",
                KeyEvent.VK_C, "Female Department Managers");


        mb.add(searchMenu);
    }

    private void setupView(JMenuBar mb){
        viewMenu = new JMenu("Views");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        createMenuItem(viewMenu, "Create Employees View",
                KeyEvent.VK_E, "Create Employees View");

        createMenuItem(viewMenu, "Create Bonus View",
                KeyEvent.VK_B, "Create Bonus View");

        createMenuItem(viewMenu, "Employees View",
                KeyEvent.VK_E, "Employees View");

        createMenuItem(viewMenu, "Bonus View",
                KeyEvent.VK_B, "Bonus View");

        mb.add(viewMenu);
    }

	public JMenu getEditMenu(){return editMenu;}

	public JMenu getSearchMenu(){return searchMenu;} //The Search menu (dynamic and static selects)

    public JMenu getViewMenu(){
	    return viewMenu;
    }
    public JMenu getCreateMenu(){return createMenu;}
	/*
	 * Creates a menu item and adds it to the given menu.  If the menu item
	 * has no mnemonic, set mnemonicKey to a negative integer. If it has no
	 * action command, set actionCommand to the empty string "". By setting
	 * the menu item's action command, the event handler can determine which
	 * menu item was selected by the user. This method returns the menu item.
	 */
	private JMenuItem createMenuItem(JMenu menu, String label, int mnemonicKey, String actionCommand) {
		JMenuItem menuItem = new JMenuItem(label);

		if (mnemonicKey > 0) {
			menuItem.setMnemonic(mnemonicKey);
		}

		if (actionCommand.length() > 0) {
			menuItem.setActionCommand(actionCommand);
		}

		menu.add(menuItem);

		return menuItem;
	}

	/*
	 * Places the given window approximately at the center of the screen
	 */
	public void centerWindow(Window w) {
		Rectangle winBounds = w.getBounds();
		w.setLocation(framePositionX + (frameBounds.width - winBounds.width) / 2,
				framePositionY + (frameBounds.height - winBounds.height) / 2);
	}

	/*
	 * This method adds the given string to the status text area
	 */
	public void updateStatusBar(String s) {
		// trim() removes whitespace and control characters at both ends of the string
		statusField.append(s.trim() + "\n");

		// This informs the scroll pane to update itself and its scroll bars.
		// The scroll pane does not always automatically scroll to the message that was
		// just added to the text area. This line guarantees that it does.
		statusField.revalidate();
	}

	/*
	 * This method adds the given JTable into tableScrPane
	 */
	public void addTable(JTable data) {
		tableScrPane.setViewportView(data);
	}

	/*
	 * This method registers the controllers for all items in each menu. This
	 * method should only be executed once.
	 */
	public void registerControllers() {
		JMenuItem menuItem;

		// BonusController handles events on the Create Table menu items (i.e. when they are clicked)
		BonusController bc = new BonusController(this);
		for (int i = 0; i < createMenu.getItemCount(); i++) {
			menuItem = createMenu.getItem(i);
			menuItem.addActionListener(bc);
		}

		// EditController handles events on the Edit Table menu items (i.e. when they are clicked)
		EditController ec = new EditController(this);
		for (int i = 0; i < editMenu.getItemCount(); i++) {
			menuItem = editMenu.getItem(i);
			menuItem.addActionListener(ec);
		}

        // ViewController handles events on the View menu items (i.e. when they are clicked)
		ViewController vc = new ViewController(this);
		for (int i = 0; i < viewMenu.getItemCount(); i++) {
			menuItem = viewMenu.getItem(i);
			menuItem.addActionListener(vc);
		}

        //SearchController handles events on the Search menu items (i.e. when they are clicked)
        SearchController sc = new SearchController(this);
		for (int i = 0; i < searchMenu.getItemCount(); i++) {
			menuItem = searchMenu.getItem(i);
			menuItem.addActionListener(sc);
		}
	}

}