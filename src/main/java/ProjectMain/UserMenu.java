package ProjectMain;

import Analytics.QueryAnalyser;
import DataModelling.DataModelling;
import Login.Login;
import QueryProcessor.QueryProcessor;
import Registration.Registration;
import SQLDump.SQLDump;

import java.io.IOException;
import java.util.Scanner;

public class UserMenu {
    private boolean handleInput(int input) throws IOException {
        switch (input){
            case 1:
                QueryProcessor qp = new QueryProcessor();
                try {
                    qp.init();
                } catch (Exception e) {
                    System.out.println("Error : Some error occurred in query processor");
                }
                break;
            case 2:
                try{
                    SQLDump dump= new SQLDump();
                    dump.extractDb();
                } catch (Exception e){
                    System.out.println("Error : Some error occurred in export module");
                }

                break;
            case 3:
                DataModelling dataModelling=new DataModelling();
                dataModelling.init();
                break;
            case 4:
                QueryAnalyser qa = new QueryAnalyser();
                try {
                    qa.init();
                } catch (IOException e) {
                    System.out.println("Some error occurred in analytics module");
                }
                break;
            case 5:
                System.out.println("=====================================================");
                System.out.println("                User logging out                     ");
                System.out.println("=====================================================");
                return false;
            default:
                System.out.println("Incorrect Input! Please try again");
                break;
        }
        return true;
    }


    public void showUserMenu(){
        boolean bool=true;
        while(bool){
            System.out.println("=====================================================");
            System.out.println("                    USER MENU                        ");
            System.out.println("=====================================================");
            System.out.println("====Enter choice number or X to exit to main menu====");
            System.out.println("=====================================================");
            System.out.println("1 - Write Queries");
            System.out.println("2 - Export");
            System.out.println("3 - Data Model");
            System.out.println("4 - Analytics");
            System.out.println("5 - Logout");
            System.out.println("=====================================================");
            System.out.println("Enter your desired choice number : ");
            Scanner scanner=new Scanner(System.in);
            try{
                bool=handleInput(scanner.nextInt());
                if(!bool){
                    ProjectMenu.isLoggedIn=false;
                    ProjectMenu.username="";
                }
            }
            catch(Exception e){
                System.out.println("=====================================================");
                System.out.println("              Invalid Input given");
                System.out.println("=====================================================");

            }
        }

    }
}
