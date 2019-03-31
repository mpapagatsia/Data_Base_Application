
import java.awt.event.WindowEvent;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public String name, password;
        public static void main(String[] args) {
            
            //creating frames for UI
            MvbView mvb = new MvbView();
            
            LoginWindow login = new LoginWindow(mvb);
            login.addWindowListener(new ControllerRegister(mvb));
            login.pack();

            mvb.centerWindow(login);

            login.setVisible(true);
    }
}

/*
 * Event handler for login window. After the user logs in (after login
 * window closes), the controllers that handle events on the menu items
 * are created. The controllers cannot be created before the user logs
 * in because the database connection is not valid at that time. The
 * models that are created by the controllers require a valid database
 * connection.
 */
class ControllerRegister extends WindowAdapter {
    private MvbView mvb;

    public ControllerRegister(MvbView mvb)
    {
        this.mvb = mvb;
    }

    public void windowClosed(WindowEvent e)
    {
        mvb.registerControllers();
    }
}
