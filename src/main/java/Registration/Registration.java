package Registration;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Registration {
    private void createUser(ArrayList<String> args,String username) {
        try {
            new File("src/main/resources/user").mkdirs();
            File user_profile = new File("src/main/resources/user/User_Profile.txt");
            FileWriter fileWriter = new FileWriter(user_profile,true);

            ArrayList<String> users=new ArrayList<>();

            if(user_profile.exists()){
                BufferedReader br=new BufferedReader(new FileReader(user_profile));
                String st;
                while((st=br.readLine())!=null){
                    users.add(st.split("\\|")[0]);
                }
            }

            if(users.indexOf(args.get(0))==-1){
                for (int i = 0; i < args.size(); i++) {
                    if(i== args.size()-1){
                        fileWriter.append(args.get(i)+"\n");
                    }
                    else{
                        fileWriter.append(args.get(i)+"|");
                    }
                }
                System.out.println("=====================================================");
                System.out.println("User \""+username+"\" created successfully!");
                System.out.println("=====================================================");
            }
            else{
                throw new UserExistsException("User already exists!");
            }
            fileWriter.close();

        } catch (IOException e) {
            System.out.println("File could not be created");
        } catch (UserExistsException e) {
            System.out.println("=====================================================");
            System.out.println(e.getMessage());
            System.out.println("=====================================================");
            this.registrationMenu();
        }
    }

    public String handleInput(String field, String value){
        if(value.equalsIgnoreCase("X")){
            System.out.println("Exiting Registration");
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

    public static boolean validateField(String field, String value){
        Boolean bool=false;
        switch (field){
            case "username": bool=(value.matches("^[A-Za-z][A-Za-z0-9]+$"));
                break;
            case "password": bool=(value.matches("^[A-Za-z0-9@ ]{4,}$"));
                break;
            case "q1" :      bool=(value.matches("^[A-Za-z][A-Za-z0-9 ]+$"));
                break;
            case "q2" :      bool=(value.matches("^[A-Za-z]+$"));
                break;
            case "q3" :      bool=(value.matches("^[0-9]$"));
                break;
            default:         bool=false;
                break;
        }
        if(!bool){
            System.out.println("Incorrect input, enter again!");
        }
        return bool;
    }

    private Boolean exitRegistration(String value){
        if(value.equalsIgnoreCase("X")){return true;}
        return false;
    }

    public void registrationMenu(){
        System.out.println("=====================================================");
        System.out.println("                 USER REGISTRATION                   ");
        System.out.println("=====================================================");
        System.out.println("======Enter values or X to exit to main menu=========");
        System.out.println("=====================================================");
        Scanner sc=new Scanner(System.in);
        ArrayList<String> parameters=new ArrayList<>();

        System.out.println("Enter Username (Enter only alphabets or numbers) : ");
        String value="";
        do{value=sc.nextLine();}while(handleInput("username",value).equals(""));
        if(exitRegistration(value)){return;}
        String username=value;
        value = DigestUtils.md5Hex(value.toLowerCase()).toUpperCase();
        parameters.add(value);
        value="";

        System.out.println("Enter Password (Enter only alphabets or numbers and minimum length should be 4) : ");
        do{value=sc.nextLine();}while(handleInput("password",value).equals(""));
        if(exitRegistration(value)){return;}
        value = DigestUtils.md5Hex(value).toUpperCase();
        parameters.add(value);
        value="";

        System.out.println("=====================================================");
        System.out.println("                 SECURITY QUESTIONS                  ");
        System.out.println("=====================================================");
        System.out.println("1. "+Constants.SEQURITY_QUESTIONS.get(0));
        do{value=sc.nextLine();}while(handleInput("q1",value).equals(""));
        if(exitRegistration(value)){return;}
        parameters.add(value);
        value="";

        System.out.println("2. "+Constants.SEQURITY_QUESTIONS.get(1));
        do{value=sc.nextLine();}while(handleInput("q2",value).equals(""));
        if(exitRegistration(value)){return;}
        parameters.add(value);
        value="";

        System.out.println("3. "+Constants.SEQURITY_QUESTIONS.get(2));
        do{value=sc.nextLine();}while(handleInput("q3",value).equals(""));
        if(exitRegistration(value)){return;}
        parameters.add(value);
        value="";

        this.createUser(parameters,username);
    }
}

