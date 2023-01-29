package Login;
import ProjectMain.ProjectMenu;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.*;

import Registration.Constants;

import static Registration.Registration.validateField;

public class Login {

    public String handleInput(String field, String value){

        if(value.equalsIgnoreCase("X")){
            System.out.println("Exiting Login");
            return "X";
        }
        else{
            if(validateField(field,value)){
                return value;
            }
            else{
                return "";
            }
        }
    }

    private Boolean exitLogin(String value){
        if(value.equalsIgnoreCase("X")){return true;}
        return false;
    }

    public void validateUser() {
        String user;
        String pass;
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("=====================================================");
            System.out.println("                    USER LOGIN                       ");
            System.out.println("=====================================================");
            System.out.println("===Enter your credentials or X to exit to main menu==");
            System.out.println("=====================================================");

            System.out.println("Enter Username (Enter only alphabets or numbers) : ");
            String value = "";
            do {value = sc.nextLine();} while (handleInput("username",value).equals(""));
            String username=value;
            if(exitLogin(value)){return;}
            value = DigestUtils.md5Hex(value.toLowerCase()).toUpperCase();
            user = value;
            value = "";
            System.out.println("Enter Password (Enter only alphabets or numbers and minimum length should be 4) : ");
            do {value = sc.nextLine();} while (handleInput("password",value).equals(""));
            if(exitLogin(value)){return;}
            value = DigestUtils.md5Hex(value).toUpperCase();
            pass = value;
            value = "";
            File user_profile = new File("src/main/resources/user/User_Profile.txt");
            if (user_profile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(user_profile));
                String st;
                ArrayList<String> users=new ArrayList<>();
                ArrayList<String> passwords=new ArrayList<>();
                ArrayList<String> sq =new ArrayList<>();

                int sqIndex = getSecurityQuestion();

                while ((st = br.readLine()) != null) {

                    String[] stArray = st.split("\\|");
                    users.add(stArray[0]);
                    passwords.add(stArray[1]);
                    sq.add(stArray[sqIndex+2]);
                }
                if ((users.indexOf(user)!=-1)) {
                    if(pass.equals(passwords.get(users.indexOf(user)))){

                        System.out.println("=====================================================");
                        System.out.println("                 SECURITY QUESTIONS                  ");
                        System.out.println("=====================================================");
                        System.out.println(Constants.SEQURITY_QUESTIONS.get(sqIndex));
                        int qNum = sqIndex+1;
                        do {value = sc.nextLine();} while (handleInput("q"+qNum,value).equals(""));
                        if(value.equals(sq.get(users.indexOf(user)))){
                            System.out.println("User is registered");
                            System.out.println("Welcome " + username);
                            ProjectMenu.isLoggedIn=true;
                            ProjectMenu.username=username;
                        }
                        else{
                            throw new LoginUserException("Invalid security answer");
                        }
                    }
                    else{
                        throw new LoginUserException("Invalid user credentials");
                    }
                } else {
                    throw new LoginUserException("Invalid user credentials");
                }
            }
            else{
                throw new LoginUserException("Invalid user credentials");
            }
        } catch (LoginUserException e) {
            System.out.println("=====================================================");
            System.out.println(e.getMessage());
            System.out.println("=====================================================");
        } catch (FileNotFoundException e) {
            System.out.println("=====================================================");
            System.out.println("            User Profiles does not exist             ");
            System.out.println("=====================================================");
        } catch (IOException e) {
            System.out.println("=====================================================");
            System.out.println("         Some error occurred in Login Module         ");
            System.out.println("=====================================================");
        }
    }

    private Integer getSecurityQuestion() {

        Random rand = new Random();
        int index = rand.nextInt(Constants.SEQURITY_QUESTIONS.size());

        return index;
    }
}
