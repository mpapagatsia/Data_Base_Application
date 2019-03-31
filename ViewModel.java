
import javax.swing.event.EventListenerList;
import java.sql.*;

public class ViewModel {
    protected PreparedStatement ps = null;
    protected EventListenerList listenerList = new EventListenerList();
    private Connection con = null;

    /*
     * Default constructor
     * Precondition: The Connection object in MvbOracleConnection must be
     * a valid database connection.
     */
    public ViewModel() {
        con = DBConnection.getInstance().getConnection();
    }

    public Boolean createEmpView(){
        try{
            ps = con.prepareStatement("CREATE VIEW employees_view AS SELECT emp_no, CONCAT(first_name, ' ', last_name) AS full_name FROM\n employees");

            ps.executeUpdate();

            ps.clearParameters();

            return true;
        }catch(SQLException ex){
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);
            return false;
        }
    }

    public ResultSet showEmployeeView(){
        try {
            ps = con.prepareStatement("SELECT * FROM employees_view ",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs1 = ps.executeQuery();
            return rs1;
        }
        catch (SQLException ex)
        {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);
            // no need to commit or rollback since it is only a query

            return null;
        }
    }

    public Boolean createBonusView(){
        try{
            ps = con.prepareStatement("CREATE VIEW bonus_view AS SELECT\n" +
                    "CONCAT(e.first_name, ' ', e.last_name) AS full_name ,\n" +
                    "s.salary,\n" +
                    "b.bonus\n" +
                    "FROM\n" +
                    "employees e, salaries s, bonus_table b\n" +
                    "where b.emp_no = e.emp_no and e.emp_no = s.emp_no");

            ps.executeUpdate();

            ps.clearParameters();

            return true;
        }catch(SQLException ex){
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);
            return false;
        }
    }

    public ResultSet showBonusView(){
        try {
            ps = con.prepareStatement("SELECT * FROM bonus_view ",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs1 = ps.executeQuery();
            return rs1;
        }
        catch (SQLException ex)
        {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);
            // no need to commit or rollback since it is only a query

            return null;
        }
    }

    public void addExceptionListener(ExceptionListener l){
        listenerList.add(ExceptionListener.class, l);
    }

    public void fireExceptionGenerated(ExceptionEvent ex){
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

    public Connection getConnection(){
        return con;
    }

}
