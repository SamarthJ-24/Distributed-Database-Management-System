package Analytics;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

public class QueryAnalyser {
    public void init() throws IOException {
        File file=new File(Constants.METADATA_PATH);
        if(!file.exists()){file.mkdirs();}
        FileWriter fileWriter=new FileWriter(new File(Constants.METADATA_PATH + "/userCount.txt"),true);
        fileWriter.close();
        getUserInput();
    }

    private void getUserInput() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=====================================================");
        System.out.println("                   Enter query");
        System.out.println("=====================================================");
        System.out.println("Query (Enter X to exit): ");
        while (scanner.hasNextLine()) {
            String query = scanner.nextLine();
            try {
                String queryOutput=this.handleInputQuery(query);
                if(queryOutput.equals("Quit")){break;};
            } catch (QueryExceptions e) {
                System.out.println("=====================================================");
                System.out.println(e.getMessage());
                System.out.println("=====================================================");
            }
            System.out.println("\nQuery (Enter X to exit): ");
        }
    }

    private String handleInputQuery(String query) throws QueryExceptions, IOException {
        if(query.equalsIgnoreCase("X")){return "Quit";}
        else{this.parseQuery(query);return "";}
    }

    private void parseQuery(String query) throws QueryExceptions, IOException{
        String lowerCaseQuery = query.trim().toLowerCase();
        if(lowerCaseQuery.contains(Constants.KEYWORD_COUNT_USER_QUERIES)){
            if(Pattern.matches(Constants.REGEX_COUNT_USER_QUERIES, lowerCaseQuery)){
                File analyticsDir=new File(Constants.ANALYTICS_PATH);
                if(!analyticsDir.exists()){analyticsDir.mkdirs();}
                FileWriter fileWriter=new FileWriter(new File(Constants.ANALYTICS_PATH+"output.txt"));
                this.countUserQueries(Constants.METADATA_PATH,1, fileWriter);
                this.countUserQueries(Constants.METADATA2_PATH,2, fileWriter);
                fileWriter.close();
            }
            else{
                throw new QueryExceptions("Error: Invalid count query");
            }
        }
        else if(lowerCaseQuery.contains(Constants.KEYWORD_COUNT)){
            if(Pattern.matches(Constants.REGEX_COUNT, lowerCaseQuery)){
                File analyticsDir=new File(Constants.ANALYTICS_PATH);
                if(!analyticsDir.exists()){analyticsDir.mkdirs();}
                FileWriter fileWriter=new FileWriter(new File(Constants.ANALYTICS_PATH+"output.txt"));
                this.countQueries(query, Constants.METADATA_PATH, fileWriter);
                this.countQueries(query, Constants.METADATA2_PATH, fileWriter);
                fileWriter.close();
            }
            else{
                throw new QueryExceptions("Error: Invalid count query");
            }
        }
        else{
            throw new QueryExceptions("Error: Invalid query");
        }
    }

    public void countQueries(String query, String metadataPath, FileWriter fileWriter) throws QueryExceptions {
        String[] queryArr=query.split(" ");
        ArrayList<String> queryTypes=new ArrayList<>(Arrays.asList("select","update","delete","insert"));
        try{
            String dbName = queryArr[2].substring(0, queryArr[2].length()-1);
            String dbPath = Constants.DATABASE_PATH + dbName;
            File database = new File(dbPath);
            if (database.exists()) {
                File file=new File(metadataPath+dbName+"/Structure.txt");
                if(file.exists()){
                    BufferedReader br=new BufferedReader(new FileReader(metadataPath+dbName+"/Structure.txt"));
                    ArrayList<String> tables=new ArrayList<>();
                    String temp;
                    while((temp=br.readLine())!=null){
                        tables.add(temp.split("=")[0]);
                    }
                    File metadataDir=new File(metadataPath+dbName);
                    if(metadataDir.exists()){
                        for (String table : tables) {
                            if (queryTypes.indexOf(queryArr[1]) != -1) {
                                String queryCountFile = metadataPath + dbName + "/queryCount.txt";
                                String tableName = table;
                                Properties properties = new Properties();
                                FileInputStream input = null;
                                input = new FileInputStream(queryCountFile);

                                properties.load(input);
                                input.close();

                                String count = properties.getProperty(tableName + "." + queryArr[1]);
                                String output="Total " + count + " " + queryArr[1] + " operations are performed on " + tableName + " table";
                                fileWriter.append(output+"\n");

                                System.out.println(output);
                            } else {
                                throw new QueryExceptions("Error: Invalid query type given in count query");
                            }
                        }

                    } else{
                        throw new QueryExceptions("Error: No tables in database");
                    }
                }
            }
            else{
                throw new QueryExceptions("Error: Selected database does not exist");
            }
        } catch (Exception e){
            throw new QueryExceptions(e.getMessage());
        }
    }

    public void countUserQueries(String path,int vm, FileWriter fileWriter) throws QueryExceptions {
        try{
            String userQueryCountFile = path + "/userCount.txt";
            File file=new File(userQueryCountFile);
            if(file.exists()){
                BufferedReader reader = new BufferedReader(new FileReader(userQueryCountFile));
                String header = reader.readLine();
                String line;
                while((line = reader.readLine()) != null){
                    String[] lineArr=line.split("=");
                    String[] userDetails=lineArr[0].split("\\.");
                    String output="User "+userDetails[0]+" submitted "+lineArr[1]+" queries for DB : "+userDetails[1]+" on Virtual Machine "+vm;
                    fileWriter.write(output+"\n");
                    System.out.println(output);
                }
            }
        } catch (Exception e){
            throw new QueryExceptions(e.getMessage());
        }
    }
}
