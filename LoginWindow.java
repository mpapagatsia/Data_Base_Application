
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/*
 * The login window
 */ 
public class LoginWindow extends JDialog implements ActionListener {
	DBConnection dbconn = new DBConnection();

    // user is allowed 3 login attempts
    private int loginAttempts = 0;
    
    // components of the login window
    private JTextField usernameField = new JTextField(10);
    private JPasswordField passwordField = new JPasswordField(10);	   
    private JLabel usernameLabel = new JLabel("Enter username:  ");
    private JLabel passwordLabel = new JLabel("Enter password:  ");
    private JButton loginButton = new JButton("Log In");
	private MvbView mvb;

    /*
     * Default constructor. The login window is constructed here.
     */
    public LoginWindow(JFrame parent) {
		super(parent, "User Login", true);

		mvb = (MvbView)parent;

		/*Set up the frame*/
		setSize( 400, 300);
		setResizable(true);

		passwordField.setEchoChar('*');

		// content pane for the login window
		JPanel loginPane = new JPanel();
		setContentPane(loginPane);

		/*
		 * layout components using the GridBag layout manager
		 */
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		loginPane.setLayout(gb);
		loginPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

		// place the username label
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10, 10, 5, 0);
		gb.setConstraints(usernameLabel, c);
		loginPane.add(usernameLabel);

		// place the text field for the username
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 0, 5, 10);
		gb.setConstraints(usernameField, c);
		loginPane.add(usernameField);

		// place password label
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(0, 10, 10, 0);
		gb.setConstraints(passwordLabel, c);
		loginPane.add(passwordLabel);

		// place the password field
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 10, 10);
		gb.setConstraints(passwordField, c);
		loginPane.add(passwordField);

		// place the login button
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 10, 5, 10);
		c.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(loginButton, c);
		loginPane.add(loginButton);

		// end of layout

		// Register password field and OK button with action event handler.
		// An action event is generated when the return key is pressed while
		// the cursor is in the password field or when the OK button is pressed.
		passwordField.addActionListener(this);
		loginButton.addActionListener(this);

		// anonymous inner class for closing the window
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
			System.exit(0);
			}
		 });

		// initially, place the cursor in the username text field
		usernameField.requestFocus();
	}

    /*
     * event handler for password field and OK button
     */ 	    
    public void actionPerformed(ActionEvent e) {
		String name = usernameField.getText();
		String ps = String.valueOf(passwordField.getPassword());

		if (dbconn.createConnection(name, ps)) {
			/* The connection is valid.*/
			Connection connection = dbconn.getConnection();

			/* Check the privileges of the user that has been connected
			 * to the database.*/
			try {
				String insert_priv = "" ;
				String create_priv = "";

				PreparedStatement st = connection.prepareStatement("select * from mysql.user where User=? ");
				st.setString(1,name);

				ResultSet rs = st.executeQuery();

				/* Check insert and create privileges*/
				if(rs.next()){
					insert_priv = rs.getString("Insert_priv");
					create_priv = rs.getString("Create_priv");
				}

				/* If the user has not be granted with create privilege
				* remove the create options*/
				if(create_priv.equals("N")){
					(mvb.getJMenuBar()).remove(1);
					JMenu menu = mvb.getViewMenu();
					menu.remove(0);
					menu.remove(0);
				}

				/* If the user has not be granted with insert privilege
				 * remove the insert options*/
				if(insert_priv.equals("N")){
					(mvb.getJMenuBar()).remove(1);
				}

				mvb.setVisible(true);
			}catch(SQLException event){
				System.out.println("Error");
			}

			dispose();
		}
		else {
			loginAttempts++;

			if (loginAttempts >= 3) {
				dispose();
				System.exit(0);
			}
			else {
				// clear the password
				passwordField.setText("");
			}
		}
    }     
}
    
