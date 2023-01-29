package ProjectMain;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;

public class Main {

    public static String hostname;

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {            
        }
    }

    public static void main(String[] args) {
        ProjectMenu projectMenu=new ProjectMenu();
        projectMenu.showMenu();
    }
}
