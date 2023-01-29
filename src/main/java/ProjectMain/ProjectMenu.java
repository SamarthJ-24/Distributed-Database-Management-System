package ProjectMain;

import Login.Login;
import Registration.Registration;

import java.io.File;
import java.util.Scanner;

public class ProjectMenu {

    public static boolean isLoggedIn=false;
    public static String username="";

    ProjectMenu(){
        File file=new File("./src/main/resources/database/");
        if(!file.exists()){file.mkdirs();}
    }

    private boolean handleInput(int input){
        switch (input){
            case 1:
                Registration registration=new Registration();
                registration.registrationMenu();
                break;
            case 2:
                Login login = new Login();
                login.validateUser();
                break;
            case 3:
                System.out.println("Exiting the application");
                System.out.println("=====================================================");
                System.out.println("                     GOOD-BYE!                       ");
                System.out.println("=====================================================");
                return false;
            default:
                System.out.println("Incorrect Input! Please try again");
                break;
        }
        return true;
    }

    public void showMenu(){
        boolean bool=true;
        while(bool){
            if(!isLoggedIn){
                System.out.println("=====================================================");
                System.out.println("                   D2_DB PROJECT                     ");
                System.out.println("                      GROUP 5                        ");
                System.out.println("=====================================================");
                System.out.println("Choose from the following options:");
                System.out.println("1 - Registration");
                System.out.println("2 - Login");
                System.out.println("3 - Exit");
                System.out.println("=====================================================");
                System.out.println("Enter your desired choice number : ");
                Scanner scanner=new Scanner(System.in);
                try{
                    bool=handleInput(scanner.nextInt());
                }
                catch(Exception e){
                    System.out.println("=====================================================");
                    System.out.println("              Invalid Input given");
                    System.out.println("=====================================================");
                }
            }
            else{
                UserMenu userMenu=new UserMenu();
                userMenu.showUserMenu();
            }
        }
    }
}
