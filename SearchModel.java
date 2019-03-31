
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.event.EventListenerList;

public class SearchModel {
    protected PreparedStatement ps = null;
    protected EventListenerList listenerList = new EventListenerList();
    private Connection con = null;

    /*
     * Default constructor
     * Precondition: The Connection object in MvbOracleConnection must be
     * a valid database connection.
     */
    public SearchModel() {
        con = DBConnection.getInstance().getConnection();
    }
    /*Each function controles a query specified by its name*/
    public ResultSet searchDepManager(){
        try {
            ps = con.prepareStatement("select e.first_name, e.last_name, d.dept_name from  dept_manager dm , departments d, employees e where e.emp_no = dm.emp_no and dm.dept_no = d.dept_no and dm.emp_no in (select dm1.emp_no from dept_manager dm1) order by d.dept_name");

            ResultSet result = ps.executeQuery();

            ps.clearParameters();
            return result;

        } catch (SQLException e) {
            ExceptionEvent event = new ExceptionEvent(this, e.getMessage());
            fireExceptionGenerated(event);
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet searchEmpSal(int sal) {
        try {
            ps = con.prepareStatement("SELECT E.emp_no, E.first_name, E.last_name, S.salary from employees E, salaries S where S.emp_no = E.emp_no and S.salary > ?");

            ps.setInt(1,sal);
            ResultSet result = ps.executeQuery();

            ps.clearParameters();
            return result;

        } catch (SQLException e) {
            ExceptionEvent event = new ExceptionEvent(this, e.getMessage());
            fireExceptionGenerated(event);
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet searchEmpBdate(Date birthdate) {

        try {
            ps = con.prepareStatement("SELECT first_name, last_name from employees where birth_date = ?");
            Calendar cal = Calendar.getInstance();
            cal.setTime(birthdate);
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Date date = cal.getTime();
            java.sql.Date sqlbirthdate = new java.sql.Date(date.getTime());
            ps.setDate(1,sqlbirthdate);
            ResultSet result = ps.executeQuery();
            //con.commit();
            ps.clearParameters();
            return result;

        } catch (SQLException e) {
            ExceptionEvent event = new ExceptionEvent(this, e.getMessage());
            fireExceptionGenerated(event);
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet searchEmpTitleDept(String title){
        try{
            // find all employees with specific title and compare them with
            // the employees that are dept managers and keep the ones that
            // cover both demands
            ps = con.prepareStatement("SELECT e1.emp_no ,e1.first_name, e1.last_name FROM employees.employees e1 WHERE e1.emp_no = (SELECT t1.emp_no FROM employees.titles t1 WHERE title = ? and t1.emp_no= e1.emp_no GROUP BY t1.emp_no) and e1.emp_no = (SELECT d1.emp_no FROM employees.dept_manager d1 where e1.emp_no =d1.emp_no GROUP BY d1.emp_no)");
            ps.setString(1,title);
            ResultSet rs = ps.executeQuery();
            ps.clearParameters();
            return rs;

        } catch (SQLException e) {
            ExceptionEvent event = new ExceptionEvent(this, e.getMessage());
            fireExceptionGenerated(event);
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet searchDepEmployees(String dept){
        try{
            // find all employees with specific title and compare them with
            // the employees that are dept managers and keep the ones that
            // cover both demands
            ps = con.prepareStatement("select e.first_name, e.last_name, d.dept_name from  dept_emp de , departments d, employees e where e.emp_no = de.emp_no and de.dept_no = d.dept_no and d.dept_name = ? and de.emp_no in (select de1.emp_no from dept_emp de1) ;");
            ps.setString(1,dept);
            ResultSet rs = ps.executeQuery();
            ps.clearParameters();
            return rs;

        } catch (SQLException e) {
            ExceptionEvent event = new ExceptionEvent(this, e.getMessage());
            fireExceptionGenerated(event);
            e.printStackTrace();
            return null;
        }
    }

    public void addExceptionListener(ExceptionListener l)
    {
        listenerList.add(ExceptionListener.class, l);
    }

    public ResultSet sumBonus() {
        try {
            ps = con.prepareStatement("SELECT SUM(b.bonus) AS Sum_Bonus FROM employees.bonus_table b");

            ResultSet result = ps.executeQuery();

            ps.clearParameters();
            return result;

        } catch (SQLException e) {
            ExceptionEvent event = new ExceptionEvent(this, e.getMessage());
            fireExceptionGenerated(event);
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet countFemaleDeptManagers() {
        try {
            ps = con.prepareStatement("SELECT COUNT(dm.emp_no) AS Female FROM employees.dept_manager dm WHERE dm.emp_no = (SELECT e.emp_no FROM employees.employees e WHERE e.gender = \"F\" and e.emp_no = dm.emp_no)");

            ResultSet result = ps.executeQuery();

            ps.clearParameters();
            return result;

        } catch (SQLException e) {
            ExceptionEvent event = new ExceptionEvent(this, e.getMessage());
            fireExceptionGenerated(event);
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet femaleDeptManagers() {
        try {
            ps = con.prepareStatement("SELECT e.first_name, e.last_name FROM employees.employees e WHERE  e.gender = \"F\" and e.emp_no = (SELECT dm.emp_no FROM employees.dept_manager dm WHERE dm.emp_no = e.emp_no)");

            ResultSet result = ps.executeQuery();

            ps.clearParameters();
            return result;

        } catch (SQLException e) {
            ExceptionEvent event = new ExceptionEvent(this, e.getMessage());
            fireExceptionGenerated(event);
            e.printStackTrace();
        }
        return null;
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

    public Connection getConnection(){
        return con;
    }
}
