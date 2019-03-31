
import javax.swing.event.EventListenerList;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditModel {
    protected PreparedStatement ps = null;
    protected EventListenerList listenerList = new EventListenerList();
    private Connection con = null;
    int last_empno = 0;
    String last_deptno = "";

    public EditModel() {
        con = DBConnection.getInstance().getConnection();
    }

    public boolean insertSalary(int empno){
        Date hiredate = new Date();

        try {
            con.setAutoCommit(false);

            //get the title of the new employee
            ps = con.prepareStatement("SELECT s1.title FROM employees.titles s1 WHERE s1.emp_no = ?");
            ps.setInt(1, empno);
            ResultSet rs = ps.executeQuery();

            String title = "";

            //set the cursor at the first row
            if(rs.next()) {
                title += rs.getString("title");
            }

            ps.clearParameters();
            System.out.print(title);
            con.setAutoCommit(false);

            // get minimum salary considering the specific title
            ps = con.prepareStatement("SELECT  MIN(s1.salary) FROM employees.salaries s1 WHERE  s1.emp_no = (SELECT t1.emp_no FROM employees.titles t1 WHERE title = ? and s1.emp_no = t1.emp_no)");
            ps.setString(1,title);
            rs = ps.executeQuery();
            con.commit();

            int salary = 0;

            if (rs.next()) {
                salary = rs.getInt(1);
            }

            ps.clearParameters();
            con.setAutoCommit(false);

            //Add new salary of new employee
            ps = con.prepareStatement("INSERT INTO salaries VALUES (?,?,?,?)");

            java.sql.Date sqlhiredate = new java.sql.Date(hiredate.getTime());

            ps.setInt(1, empno);
            ps.setInt(2, salary);
            ps.setDate(3, sqlhiredate);
            ps.setDate(4, sqlhiredate);

            ps.executeUpdate();
            con.commit();
            ps.clearParameters();

            return true;
        }catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            try {
                con.rollback();
                return false;
            }
            catch (SQLException ex2) {
                event = new ExceptionEvent(this, ex2.getMessage());
                fireExceptionGenerated(event);
                return false;
            }
        }
    }

    public boolean insertTitle(int empno, String salary){
        Date hiredate = new Date();

        try {
            ps = con.prepareStatement("INSERT INTO titles VALUES (?,?,?,?)");

            java.sql.Date sqlhiredate = new java.sql.Date(hiredate.getTime());

            ps.setInt(1, empno);
            ps.setString(2, salary);
            ps.setDate(3,sqlhiredate);
            ps.setDate(4,null);

            ps.executeUpdate();
            con.commit();
            ps.clearParameters();

            return true;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            try {
                con.rollback();
                return false;
            }
            catch (SQLException ex2) {
                event = new ExceptionEvent(this, ex2.getMessage());
                fireExceptionGenerated(event);
                return false;
            }
        }
    }

    /* Insert a new department number. Select the max dept_no from the database, in
     * in order to give the new department a dept_no increased by one
     * (i.e. new_dept_no = max(dept_no) + 1)*/
    public boolean insertDepartment(String deptname) {
        try {
            con.setAutoCommit(false);

            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT MAX(dept_no) FROM departments");

            con.commit();

            String dnumber = "";

            if(rs.next()) { //set the cursor at the first row
                String temp = rs.getString(1);
                temp = temp.substring(1);

                int number = Integer.valueOf(temp) + 1;

                if(number > 9) {
                    dnumber = "d0" + number;
                }
                else if(number > 99){
                    dnumber = "d" + number;
                }
                else{
                    dnumber = "d00" + number;
                }
            }

            con.setAutoCommit(false);

            ps = con.prepareStatement("INSERT INTO departments VALUES (?,?)");

            ps.setString(1, dnumber);
            ps.setString(2, deptname);

            ps.executeUpdate();
            con.commit();
            ps.clearParameters();

            return true;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            try {
                con.rollback();
                return false;
            }
            catch (SQLException ex2) {
                event = new ExceptionEvent(this, ex2.getMessage());
                fireExceptionGenerated(event);
                return false;
            }
        }
    }

    /* Insert a new department manager*/
    public boolean insertDeptManager(int empno, String deptno, Date fromdate, Date todate) {
        last_empno = empno;
        last_deptno = deptno;

        try {
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO dept_manager VALUES (?,?,?,?)");

            Calendar cal = Calendar.getInstance();
            cal.setTime(fromdate);
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Date date = cal.getTime();
            java.sql.Date sqlfromdate = new java.sql.Date(date.getTime());

            cal.setTime(todate);
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            date = cal.getTime();
            java.sql.Date sqltodate = new java.sql.Date(date.getTime());

            ps.setInt(1, empno);
            ps.setString(2, deptno);
            ps.setDate(3,sqlfromdate);
            ps.setDate(4,sqltodate);

            ps.executeUpdate();
            con.commit();
            ps.clearParameters();

            return true;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            try {
                con.rollback();
                return false;
            }
            catch (SQLException ex2) {
                event = new ExceptionEvent(this, ex2.getMessage());
                fireExceptionGenerated(event);
                return false;
            }
        }
    }

    /* Insert a new department employee*/
    public boolean insertDeptEmployee(int empno, String deptno, Date fromdate, Date todate) {
        last_empno = empno;
        last_deptno = deptno;

        try {
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO dept_emp VALUES (?,?,?,?)");

            Calendar cal = Calendar.getInstance();
            cal.setTime(fromdate);
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Date date = cal.getTime();
            java.sql.Date sqlfromdate = new java.sql.Date(date.getTime());

            cal.setTime(todate);
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            date = cal.getTime();
            java.sql.Date sqltodate = new java.sql.Date(date.getTime());

            ps.setInt(1, empno);
            ps.setString(2, deptno);
            ps.setDate(3,sqlfromdate);
            ps.setDate(4,sqltodate);

            ps.executeUpdate();
            con.commit();
            ps.clearParameters();

            return true;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            try {
                con.rollback();
                return false;
            }
            catch (SQLException ex2) {
                event = new ExceptionEvent(this, ex2.getMessage());
                fireExceptionGenerated(event);
                return false;
            }
        }
    }

    /* Insert a new employee. (new_emp_no = max(emp_no) + 1)*/
    public boolean insertEmployee(Date birthdate,  String firstname, String lastname, String gender) {
        Date hiredate = new Date();

        try {
            con.setAutoCommit(false);

            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT MAX(emp_no) FROM employees");

            con.commit();

            int empno = 0;
            if(rs.next()) { //set the cursor at the first row
                empno = rs.getInt(1) + 1;
            }

            con.setAutoCommit(false);

            ps = con.prepareStatement("INSERT INTO employees VALUES (?,?,?,?,?,?)");

            Calendar cal = Calendar.getInstance();
            cal.setTime(birthdate);
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Date date = cal.getTime();
            java.sql.Date sqlbirthdate = new java.sql.Date(date.getTime());
            java.sql.Date sqlhiredate = new java.sql.Date(hiredate.getTime());

            ps.setInt(1, empno);
            ps.setDate(2,sqlbirthdate);
            ps.setString(3, firstname);
            ps.setString(4, lastname);
            ps.setString(5, gender);
            ps.setDate(6,sqlhiredate);

            ps.executeUpdate();
            con.commit();
            ps.clearParameters();

            return true;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            try {
                con.rollback();
                return false;
            }
            catch (SQLException ex2) {
                event = new ExceptionEvent(this, ex2.getMessage());
                fireExceptionGenerated(event);
                return false;
            }
        }
    }

    /* Remove an employee given the emp_no*/
    public boolean removeEmployee(int employee_no){
        try {
            con.setAutoCommit(false);
            ps = con.prepareStatement("DELETE FROM employees WHERE emp_no = ?");

            ps.setInt(1, employee_no);

            ps.executeUpdate();

            con.commit();

            return true;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            try {
                con.rollback();
                return false;
            } catch (SQLException ex2) {
                event = new ExceptionEvent(this, ex2.getMessage());
                fireExceptionGenerated(event);
                return false;
            }
        }
    }

    /* Return the employee that has been removed.*/
    public ResultSet showEmployeeremoved(int empno){
        try {
            ps = con.prepareStatement("SELECT e1.* FROM employees e1 WHERE e1.emp_no  = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ps.setInt(1, empno);
            ResultSet rs1 = ps.executeQuery();

            return rs1;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            return null;
        }
    }

    /* Returns the employee that we last insert into the employees table.
     * This employee has the max emp_no*/
    public ResultSet showEmployee(){
        try {
            ps = con.prepareStatement("SELECT e1.* FROM employees e1 WHERE e1.emp_no  = (SELECT MAX(e2.emp_no) from employees e2 )",
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

    /*Show which titles already exist.*/
    public ResultSet showTitle(){
        try {
            ps = con.prepareStatement("SELECT distinct(t.title) FROM titles t",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs1 = ps.executeQuery();

            return rs1;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);
            // no need to commit or rollback since it is only a query

            return null;
        }
    }

    /*Show which department names already exist.*/
    public ResultSet showDepts(){
        try {
            ps = con.prepareStatement("SELECT d.dept_no, d.dept_name FROM departments d",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs1 = ps.executeQuery();

            return rs1;
        }
        catch (SQLException ex) {
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);
            // no need to commit or rollback since it is only a query

            return null;
        }
    }

    /* Returns the department that we last insert into the departments table.
     * This department has the max dept_no*/
    public ResultSet showDepartment(){
        try {
            ps = con.prepareStatement("SELECT d1.* FROM departments d1 WHERE d1.dept_no = (SELECT MAX(d2.dept_no) from departments d2 )",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = ps.executeQuery();

            return rs;
        }
        catch (SQLException ex){
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            return null;
        }
    }

    /* Returns the department manager that we last insert into the dept_managers table. */
    public ResultSet showDeptManager(){
        try {
            ps = con.prepareStatement("SELECT d.* FROM dept_manager d WHERE d.dept_no = ? and d.emp_no = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            System.out.println(last_empno );
            System.out.println(last_deptno);
            ps.setString(1, last_deptno);
            ps.setInt(2, last_empno);
            ResultSet rs = ps.executeQuery();

            return rs;
        }
        catch (SQLException ex){
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

            return null;
        }
    }

    /* Returns the department employee that we last insert into the dept_employees table. */
    public ResultSet showDeptEmployee(){
        try {
            ps = con.prepareStatement("SELECT d.* FROM dept_emp d WHERE d.dept_no = ? and d.emp_no = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            System.out.println(last_empno );
            System.out.println(last_deptno);
            ps.setString(1, last_deptno);
            ps.setInt(2, last_empno);
            ResultSet rs = ps.executeQuery();

            return rs;
        }
        catch (SQLException ex){
            ExceptionEvent event = new ExceptionEvent(this, ex.getMessage());
            fireExceptionGenerated(event);

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
