import java.sql.*;
import javax.swing.event.EventListenerList;

public class BonusModel {
    protected PreparedStatement ps = null;
    protected EventListenerList listenerList = new EventListenerList();
    private Connection con = null;

    public BonusModel() {
    	con = DBConnection.getInstance().getConnection();
    }

    /*Creates the Bonus Table */
    public Boolean createBonus(){
    	try{
    		ps = con.prepareStatement("create table bonus_table( emp_no int not null, bonus double null,constraint bonus_table_pk primary key (emp_no))");

    		ps.executeUpdate();

    		ps = con.prepareStatement("Select emp_no, salary from salaries S where S.from_date = (SELECT MAX(S2.from_date) from salaries S2 where S.emp_no= S2.emp_no GROUP BY S2.emp_no)");

    		ResultSet rs = ps.executeQuery();

    		String temp;

    		Statement statement = con.createStatement();

    		con.setAutoCommit(false);

    		while(rs.next()){
    			double temp_bonus = rs.getInt(2) * 0.05;
				temp = "INSERT INTO bonus_table VALUES ('" + rs.getInt(1) + "','" + temp_bonus + "')";
				statement.addBatch(temp);
			}

    		int[] count = statement.executeBatch();

    		con.setAutoCommit(true);
			ps.clearParameters();

    		return true;
		}catch(SQLException ex){
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			return false;
		}
	}

	/*Delete the Bonus Table*/
	public Boolean deleteBonus(){
		try{
			con.setAutoCommit(false);

			Statement stm = con.createStatement();
			ps = con.prepareStatement("DROP table bonus_table");

			ps.executeUpdate();
			con.commit();

			return true;
		}catch(SQLException ex){
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);
			return false;
		}

	}

	/*Show the Bonus Table*/
    public ResultSet showBonus(){
		try {
			ps = con.prepareStatement("SELECT b.* FROM bonus_table b",
						  ResultSet.TYPE_SCROLL_INSENSITIVE,
						  ResultSet.CONCUR_READ_ONLY);

			ResultSet rs1 = ps.executeQuery();

			return rs1;
		}
		catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);

			return null;
		}
    }

    /*Returns the database connection*/
    public Connection getConnection()
    {
	return con; 
    }

    /*
     * This method allows members of this class to clean up after itself 
     * before it is garbage collected. It is called by the garbage collector.
     */
	protected void finalize() throws Throwable {
		if (ps != null){
			ps.close();
		}

		// finalize() must call super.finalize() as the last thing it does
		super.finalize();
	}

	/*
	 * Returns true if the branch exists; false
	 * otherwise.
	 */
	public boolean findBranch(int bid) {
		try {
			ps = con.prepareStatement("SELECT branch_id FROM branch where branch_id = ?");

			ps.setInt(1, bid);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (SQLException ex) {
			ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
			fireExceptionGenerated(event);

			return false;
		}
	}

    /******************************************************************************
     * Below are the methods to add and remove ExceptionListeners.
     * 
     * Whenever an exception occurs in BonusModel, an exception event
     * is sent to all registered ExceptionListeners.
     ******************************************************************************/ 
    
    public void addExceptionListener(ExceptionListener l) 
    {
	listenerList.add(ExceptionListener.class, l);
    }


    public void removeExceptionListener(ExceptionListener l) 
    {
	listenerList.remove(ExceptionListener.class, l);
    }
    
    /*
     * This method notifies all registered ExceptionListeners.
     * The code below is similar to the example in the Java 2 API
     * documentation for the EventListenerList class.
     */ 
    public void fireExceptionGenerated(ExceptionEvent ex) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event.
		// I have no idea why the for loop counts backwards by 2
		// and the array indices are the way they are.
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ExceptionListener.class) {
				((ExceptionListener)listeners[i+1]).exceptionGenerated(ex);
			}
		}
     }
}
