package SQLDump;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLDump {

    public void extractDb() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the database name: ");
        String databaseName = sc.nextLine();
        String dbPath = Constants.DATABASE_PATH + databaseName;
        File database = new File(dbPath);
        if (!database.exists()) {
            System.out.println("No database found");
            return;
        }

        ArrayList<String> output = new ArrayList<>();
        String tempdb = "";
        tempdb = "CREATE DATABASE " +  databaseName + ";";
        output.add(tempdb + "\n");

        File[] tables = database.listFiles();
        for (File file : tables) {
            String temp = "";
            String tableName = file.getName();
            temp = "CREATE TABLE " + tableName.substring(0, tableName.length() - 4) + " (";
            BufferedReader reader = new BufferedReader(new FileReader(dbPath+"/"+tableName));
            String header = reader.readLine();
            ArrayList<String> DataTypes=new ArrayList<>();
            for (String rawColumnName : header.split("\\|")) {
                Matcher matcher = Pattern.compile("(.*)\\((.*)\\)").matcher(rawColumnName);
                if (matcher.find()) {
                    String columnName = matcher.group(1);
                    String rawDataType = matcher.group(2);
                    if (rawDataType.contains("@")) {
                        String[] rawDataTypeArr=rawDataType.split("@");
                        if(rawDataTypeArr[1].contains("PK")){
                            temp = temp + columnName + " " + rawDataTypeArr[0] + " PRIMARY KEY" + ",";
                        } else{
                            temp = temp + " " + columnName + " " + rawDataTypeArr[0] + " REFERENCES " +rawDataTypeArr[2]+"("+rawDataTypeArr[3]+"),";
                        }

                        DataTypes.add(rawDataTypeArr[0]);
                    } else {
                        temp = temp + " " + columnName + " " + rawDataType + ",";
                        DataTypes.add(rawDataType);
                    }
                }

            }
            ArrayList<String> rows=new ArrayList<>();
            String line;
            while((line = reader.readLine())!=null){
                rows.add(line);
            }
            temp = temp.substring(0, temp.length() - 1) + ");\n";

            if (rows.size() >= 1) {
                for (int i = 0; i < rows.size(); i++) {
                    StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName.substring(0, tableName.length() - 4) + " VALUES (");
                    String[] splitted = rows.get(i).split("\\|");
                    for (int j = 0; j < splitted.length; j++) {
                        String value;
                        if(DataTypes.get(j).equals("int")){
                            value = splitted[j];
                        }
                        else{
                            value = "\""+splitted[j]+"\"";
                        }

                        insertQuery.append(value + ",");
                    }
                    temp = temp + insertQuery.toString().substring(0, insertQuery.length() - 1) + ");\n";
                }
            }
            output.add(temp);
        }

        File file=new File(Constants.SQL_DUMP_PATH);
        file.mkdirs();

        FileWriter outputFile=new FileWriter(new File(Constants.SQL_DUMP_PATH+"export.sql"));
        for(String query:output){
            outputFile.append(query+"\n");
        }
        outputFile.flush();
        outputFile.close();

        System.out.println("Database exported to : "+Constants.SQL_DUMP_PATH+"export.sql");
    }
}