package DataModelling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Scanner;

public class DataModelling {

    public void createERD(String dbname) throws Exception {

        try {
            String structurePath = Constants.METADATA_PATH + dbname + "/Structure.txt";
            String keyPath = Constants.METADATA_PATH + dbname + "/Keys.txt";

            File structureFile = new File(structurePath);
            File keyFile = new File(keyPath);

            if (structureFile.exists() && keyFile.exists()) {

                BufferedReader inStructure = new BufferedReader(new FileReader(structurePath));
                BufferedReader inKey = new BufferedReader(new FileReader(keyPath));
                File erdDir = new File(Constants.ERD_PATH);

                if (!erdDir.exists()) {
                    erdDir.mkdirs();
                }

                FileWriter writer = new FileWriter(new File(Constants.ERD_PATH + "/" + dbname+".txt"));

                String lineStructure;
                String lineKey;

                while ((lineStructure = inStructure.readLine()) != null && (lineKey = inKey.readLine()) != null) {
                    String[] structureArray = lineStructure.split("=");
                    String tableName = structureArray[0];
                    String columns = structureArray[1];
                    String[] columnArray = columns.split("\\|");

                    String header = String.format("%-10s %-20s", "Table Name: ", tableName);
                    System.out.println("\n\n" + header);
                    writer.append(header + "\n");

                    writer.append("===============================================\n");
                    String col = String.format("%-20s %-20s", "Column Name", "Column Type");
                    writer.append(col + "\n");
                    writer.append("===============================================\n");

                    System.out.println("===============================================");
                    System.out.println(col);
                    System.out.println("===============================================");

                    for (String column : columnArray) {
                        String[] columnMeta = column.split(" ");
                        String colMeta = String.format("%-20s %-20s", columnMeta[0], columnMeta[1]);
                        System.out.println(colMeta);
                        writer.append(colMeta + "\n");
                    }
                    //for keys
                    String[] keyArray = lineKey.split("=");
                    String keysString = keyArray[1];
                    String[] keysArray = keysString.split(",");
                    System.out.println("");
                    String keyFormat = String.format("%-20s %-20s", "Key Type", "Key Name");

                    writer.append("===============================================\n");
                    writer.append(keyFormat + "\n");
                    writer.append("===============================================\n");

                    System.out.println("\n===============================================");
                    System.out.println(keyFormat);
                    System.out.println("===============================================");

                    int cardinalityCount = 0;

                    for (String key : keysArray) {
                        String[] keyMeta = key.split(" - ");
                        String keyMetaFormat = String.format("%-20s %-20s", keyMeta[0], keyMeta[1]);
                        System.out.println(keyMetaFormat);
                        writer.append(keyMetaFormat + "\n");
                        if (keyMeta[0].equals("Foreign Key")) {
                            cardinalityCount++;
                        }
                    }
                    if (cardinalityCount > 1) {
                        writer.append("\nCardinality: one to many");
                        System.out.println("\nCardinality: one to many");
                    } else if (cardinalityCount == 1) {
                        writer.append("\nCardinality: one to one");
                        System.out.println("\nCardinality: one to one");
                    }
                    writer.append("\n\n\n");
                }

                writer.flush();
                writer.close();

                System.out.println("\n\nER diagram exported to : "+ Constants.ERD_PATH+"/"+dbname+".txt");

            }
        } catch (Exception e) {
            throw new DataModellingException("Some error occurred in Data Modelling Module");
        }

    }

    public void init() {

        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the database name: ");
            String databaseName = sc.nextLine();
            String dbPath = Constants.DATABASE_PATH + databaseName;
            File database = new File(dbPath);
            if (!database.exists()) {
                System.out.println("No database found");
            } else {
                this.createERD(databaseName);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}

