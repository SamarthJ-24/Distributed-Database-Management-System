package QueryProcessor;

import MetadataTransfer.GCPTransfer;
import ProjectMain.ProjectMenu;
import LogManager.LogManager;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryProcessor {

    public String DB_IN_USE = null;

    public ArrayList<String> transactionQueryList=new ArrayList<>();
    public boolean transactionStartFlag=false;
    public boolean logFlag=true;

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
            Instant startTime = Instant.now();
            try {
                logFlag=true;
                String queryOutput=this.handleInputQuery(query);
                GCPTransfer gcpTransfer=new GCPTransfer();
                gcpTransfer.transferMetadata();
                if(queryOutput.equals("Quit")){break;};
                if(logFlag){
                    Instant endTime = Instant.now();
                    String dbName;
                    if(isDatabaseSelected()){dbName=DB_IN_USE;}else{dbName="null";}
                    LogManager.log(dbName,"null",query,startTime,endTime,"No Errors");
                }
            } catch (QueryExceptions e) {
                Instant endTime = Instant.now();
                String dbName;
                if(isDatabaseSelected()){dbName=DB_IN_USE;}else{dbName="null";}
                LogManager.log(dbName,"null",query,startTime,endTime,e.getMessage().substring(6,e.getMessage().length()));
                System.out.println("=====================================================");
                System.out.println(e.getMessage());
                System.out.println("=====================================================");
            }
            System.out.println("\nQuery (Enter X to exit): ");
        }
    }

    private boolean isDatabaseSelected() {
        return DB_IN_USE != null && !DB_IN_USE.isEmpty();
    }

    private String handleInputQuery(String query) throws QueryExceptions, IOException {
        if(query.equalsIgnoreCase("X")){return "Quit";}
        else{this.parseQuery(query);return "";}
    }

    private void parseQuery(String query) throws QueryExceptions, IOException{

        this.userQueryCounter();
        String lowerCaseQuery = query.trim().toLowerCase();
        /*Create database*/
        if(lowerCaseQuery.contains(Constants.KEYWORDS_CREATE_DATABASE)){

            if(Pattern.matches(Constants.REGEX_CREATE_DATABASE, lowerCaseQuery)){
                createDatabase(query);
            }
            else{
                throw new QueryExceptions("Error: Invalid create database query");
            }
        }
        /*Use database*/
        else if(lowerCaseQuery.contains(Constants.KEYWORDS_USE_DATABASE)){
            if(Pattern.matches(Constants.REGEX_USE_DATABASE, lowerCaseQuery)){
                useDatabase(query);
            }
            else{
                throw new QueryExceptions("Error: Invalid use database query");
            }
        }
        else if(lowerCaseQuery.contains(Constants.KEYWORD_CREATE_TABLE)){
            this.userQueryCounter();
            if(Pattern.matches(Constants.REGEX_CREATE_TABLE, lowerCaseQuery)){
                createTable(query);
            }
            else{
                throw new QueryExceptions("Error: Invalid create table query");
            }
        }
        /*Update table*/
        else if(lowerCaseQuery.contains(Constants.KEYWORD_UPDATE_TABLE)){
            this.userQueryCounter();
            if(Pattern.matches(Constants.REGEX_UPDATE_TABLE, lowerCaseQuery)){
                updateTable(query);
            }
            else{
                throw new QueryExceptions("Error: Invalid update table query");
            }
        }
        /*Delete table*/
        else if(lowerCaseQuery.contains(Constants.KEYWORD_DELETE_TABLE)){
            this.userQueryCounter();

            if(Pattern.matches(Constants.REGEX_DELETE_TABLE, lowerCaseQuery)){
                deleteTable(query);
            }
            else{
                throw new QueryExceptions("Error: Invalid delete query");
            }
        }
        /*Insert table*/
        else if (lowerCaseQuery.contains(Constants.KEYWORD_INSERT_TABLE)) {
            this.userQueryCounter();
            if (Pattern.matches(Constants.REGEX_INSERT_TABLE, lowerCaseQuery)) {
                insertTable(query);
            } else {
                throw new QueryExceptions("Error: Invalid insert query");
            }
        }
        /*Select table*/
        else if (lowerCaseQuery.contains(Constants.KEYWORD_SELECT_TABLE)) {
            this.userQueryCounter();
            if (Pattern.matches(Constants.REGEX_SELECT_TABLE, lowerCaseQuery)) {
                selectTable(query);
            } else {
                throw new QueryExceptions("Error: Invalid select query");
            }
        }
        /*Transaction*/
        else if(lowerCaseQuery.contains(Constants.KEYWORD_TRANSACTION_START)){
            this.userQueryCounter();
            if(Pattern.matches(Constants.REGEX_TRANSACTION_START, lowerCaseQuery)){
                this.transactionStart(query);
            }
            else{
                throw new QueryExceptions("Error: Invalid transaction query");
            }
        }
        else if(lowerCaseQuery.contains(Constants.KEYWORD_TRANSACTION_COMMIT)){
            this.userQueryCounter();
            if(Pattern.matches(Constants.REGEX_TRANSACTION_COMMIT, lowerCaseQuery)){
                this.transactionCommit(query);
            }
            else{
                throw new QueryExceptions("Error: Invalid transaction query");
            }
        }
        else if(lowerCaseQuery.contains(Constants.KEYWORD_TRANSACTION_ROLLBACK)){
            this.userQueryCounter();
            if(Pattern.matches(Constants.REGEX_TRANSACTION_ROLLBACK, lowerCaseQuery)){
                this.transactionRollback(query);
            }
            else{
                throw new QueryExceptions("Error: Invalid transaction query");
            }
        }
        else{
            this.userQueryCounter();
            throw new QueryExceptions("Error: Invalid query");
        }
    }

    private String getDbTableName(String query, int index) {
        String queryWithoutSemicolon = query.substring(0, query.length() - 1);

        String dbName = (queryWithoutSemicolon.trim().split(" ")[index]).toLowerCase();

        return dbName;
    }

    public void createDatabase(String query) throws QueryExceptions {

        String dbName = getDbTableName(query,2);
        String dbPath = Constants.DATABASE_PATH+dbName;
        String metadataPath = Constants.METADATA_PATH+dbName;

        File database = new File(dbPath);
        File metadata = new File(metadataPath);

        if(database.exists()){
            String errorDbExist =String.format("Database %s already exists",dbName);
            throw new QueryExceptions("Error: "+errorDbExist);
        }else{
            database.mkdirs();
            metadata.mkdirs();
            FileWriter fw= null;
            try {
                fw = new FileWriter(new File(Constants.METADATA_PATH+dbName+"/queryCount.txt"));
                fw.close();
                System.out.println("Created database at "+dbPath);
                GCPTransfer gcpTransfer=new GCPTransfer();
                gcpTransfer.createDatabase(dbName);
            } catch (IOException e) {
                throw new QueryExceptions("Error: creating query count file for database.");
            }
        }

    }

    public void useDatabase(String query) throws QueryExceptions {

        String dbName = getDbTableName(query,1);
        String dbPath = Constants.DATABASE_PATH+dbName;

        File database = new File(dbPath);

        if(database.exists()){
            DB_IN_USE = dbName;
            System.out.println("Using database "+dbName);
        }else{
            String errorDbNotExist =String.format("Database %s does not exist",dbName);
            throw new QueryExceptions("Error: "+errorDbNotExist);
        }

    }

    public void createTable(String query) throws QueryExceptions, IOException {

        Instant startTime = Instant.now();
        if(isDatabaseSelected()){

            String dbPath = Constants.DATABASE_PATH+DB_IN_USE;
            File database = new File(dbPath);

            if(database.exists()){
                String tableName = getDbTableName(query,2);
                String tablePath = dbPath+"/"+tableName+Constants.FILE_EXT;
                File table = new File(tablePath);

                if(table.exists()){
                    String errorDbExist =String.format("Table %s already exists",tableName);
                    throw new QueryExceptions("Error: "+errorDbExist);
                }
                else{
                    try {
                        table.createNewFile();

                        String queryWithoutSemicolon = query.substring(0, query.length() - 1);
                        Pattern pattern = Pattern.compile(Constants.REGEX_CREATE_TABLE_COLUMN);
                        Matcher matcher = pattern.matcher(queryWithoutSemicolon);

                        if (matcher.find()) {
                            String columns = matcher.group();
                            String[] columnsArray = columns.split(",");

                            String row = createTableColumns(tableName,columnsArray);

                            try {
                                FileWriter fileWriter = new FileWriter(tablePath);
                                fileWriter.append(row);
                                fileWriter.flush();
                                fileWriter.close();
                                String tableCreated = String.format("Table %s created at %s",tableName,tablePath);
                                System.out.println(tableCreated);

                                this.createQueryCountMetadata(tableName,0,0,0,0);
                                logFlag=false;
                                Instant endTime = Instant.now();
                                LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime,"No Errors");
                            } catch (IOException e) {
                                throw new QueryExceptions("Error writing table");
                            }

                        } else {
                            Instant endTime = Instant.now();
                            LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime,"Error creating table");
                            throw new QueryExceptions("Error: Error creating table");
                        }
                    } catch (IOException e) {
                        Instant endTime = Instant.now();
                        LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime,"Error creating table");
                        throw new QueryExceptions("Error creating table "+tableName);
                    }
                }
            }
            else{
                throw new QueryExceptions("Error: Selected database does not exist");
            }
        }
        else {
            throw new QueryExceptions("Error: Select database");
        }

    }

    private void createQueryCountMetadata(String tableName,int insert, int select, int update, int delete){
        try {
            String queryCountFile=Constants.METADATA_PATH+this.DB_IN_USE+"/queryCount.txt";
            FileWriter fw=new FileWriter(new File(queryCountFile),true);
            fw.close();
            Properties properties=new Properties();
            FileInputStream input = null;
            input = new FileInputStream(queryCountFile);

            properties.load(input);
            input.close();

            OutputStream output = new FileOutputStream(queryCountFile);
            String insertCount=properties.getProperty(tableName+".insert");
            properties.setProperty(tableName+".insert",insertCount!=null? String.valueOf(Integer.valueOf(insertCount)+insert) :"0");
            String selectCount=properties.getProperty(tableName+".select");
            properties.setProperty(tableName+".select",selectCount!=null? String.valueOf(Integer.valueOf(selectCount)+select) :"0");
            String updateCount=properties.getProperty(tableName+".update");
            properties.setProperty(tableName+".update", selectCount!=null? String.valueOf(Integer.valueOf(updateCount)+update) :"0");
            String deleteCount=properties.getProperty(tableName+".delete");
            properties.setProperty(tableName+".delete", selectCount!=null? String.valueOf(Integer.valueOf(deleteCount)+delete) :"0");
            properties.store(output, null);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot create metadata file");
        } catch (IOException e) {
            System.out.println("Error: I/O exception while creating metadata file");
        }
    }

    private String createTableColumns(String tableName,String[] columnsArray) throws IOException {
        File metadata=new File(Constants.METADATA_PATH+this.DB_IN_USE);
        if(!metadata.exists()){metadata.mkdirs();}
        FileWriter fileWriter=new FileWriter(new File(Constants.METADATA_PATH+this.DB_IN_USE+"/Structure.txt"),true);
        FileWriter fw=new FileWriter(new File(Constants.METADATA_PATH+this.DB_IN_USE+"/Keys.txt"),true);


        StringBuilder row = new StringBuilder();
        int count = 0;

        fileWriter.append(tableName+"=");
        fw.append(tableName+"=");

        for(String column : columnsArray){

            String columnArray[] = column.trim().split(" ");
            String columnName = columnArray[0];
            String columnDataType = columnArray[1];

            fileWriter.append(columnName+" "+columnDataType);

            if(columnArray.length == 2){
                row.append(columnName)
                        .append("(").append(columnDataType)
                        .append(")");
            }

            // query with primary key
            if(columnArray.length == 4 && columnArray[2].equalsIgnoreCase("primary")){
                fw.append("Primary Key - "+columnName+",");
                row.append(columnName)
                        .append("(").append(columnDataType)
                        .append(Constants.COLUMN_META_SEPARATOR)
                        .append("PK")
                        .append(")");
            }

            // query with foreign key
            if(columnArray.length == 4 && columnArray[2].equalsIgnoreCase("references")){
                String fkTable = columnArray[3].split("\\(")[0];
                String fkColumn = columnArray[3].split("\\(")[1].replaceAll("\\)", "");
                fw.append("Foreign Key - "+columnName+" references "+fkTable+"("+fkColumn+")"+",");
                row.append(columnName)
                        .append("(")
                        .append(columnDataType)
                        .append(Constants.COLUMN_META_SEPARATOR)
                        .append("FK")
                        .append(Constants.COLUMN_META_SEPARATOR)
                        .append(fkTable)
                        .append(Constants.COLUMN_META_SEPARATOR)
                        .append(fkColumn)
                        .append(")");
            }


            if(count < columnsArray.length -1){
                fileWriter.append(Constants.COLUMN_SEPARATOR);
                row.append(Constants.COLUMN_SEPARATOR);
            }

            count++;
        }

        fileWriter.append("\n");
        fw.append("\n");

        row.append("\n");

        fileWriter.flush();
        fw.flush();
        fileWriter.close();
        fw.close();

        return row.toString();
    }

    private void updateTable(String query) throws QueryExceptions, IOException {
        Instant startTime = Instant.now();
        ArrayList<String> modifiedData =new ArrayList<String>();
        if (isDatabaseSelected()) {

            String dbPath = Constants.DATABASE_PATH + DB_IN_USE;
            File database = new File(dbPath);

            if (database.exists()) {
                String tableName = getDbTableName(query, 1);
                String tablePath = dbPath + "/" + tableName + Constants.FILE_EXT;
                File table = new File(tablePath);
                if (table.exists()) {
                    if(this.transactionStartFlag){
                        this.transactionQueryList.add(query);
                    }
                    else {
                        query = query.replaceAll("\"", "");
                        String columnName = getDbTableName(query, 3);
                        String columnValue = getDbTableName(query, 5);
                        String referenceColumnName = getDbTableName(query, 7);
                        String referenceColumnValue = getDbTableName(query, 9);
                        String operator = getDbTableName(query, 8);
                        if (operator.equals(">") || operator.equals("<") || operator.equals(">=") || operator.equals(">=")) {
                            if (!Pattern.matches("[0-9]", referenceColumnValue)) {
                                Instant endTime = Instant.now();
                                LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Given value not Integer");
                                throw new QueryExceptions("Error: Given value not Integer");
                            }
                        }

                        BufferedReader tableFile = new BufferedReader(new FileReader(tablePath));
                        String header = tableFile.readLine();
                        modifiedData.add(header);
                        if (header.toLowerCase().contains(columnName) && header.toLowerCase().contains(referenceColumnName)) {
                            String Columns[] = header.trim().split("\\|", -1);
                            for(int i=0;i< Columns.length;i++){Columns[i]=Columns[i].substring(0,Columns[i].indexOf('('));}

                            int referenceColumnIndex = 0;
                            int columnIndex = 0;
                            String line;
                            for (int i = 0; i < Columns.length; i++) {
                                if (Columns[i].toLowerCase().equals(referenceColumnName)) {
                                    referenceColumnIndex = i;

                                }
                                if (Columns[i].toLowerCase().equals(columnName)) {
                                    columnIndex = i;
                                }

                            }
                            while ((line = tableFile.readLine()) != null) {

                                String b[] = line.split("\\|", -1);
                                switch (operator) {
                                    case "=":
                                        if (b[referenceColumnIndex].equalsIgnoreCase(referenceColumnValue)) {
                                            b[columnIndex] = columnValue;
                                        }
                                        break;
                                    case "<>":
                                        if (!b[referenceColumnIndex].equalsIgnoreCase(referenceColumnValue)) {
                                            b[columnIndex] = columnValue;
                                        }
                                        break;
                                    case ">":
                                        if (Integer.parseInt(b[referenceColumnIndex]) > Integer.parseInt(referenceColumnValue)) {
                                            b[columnIndex] = columnValue;
                                        }
                                        break;
                                    case "<":
                                        if (Integer.parseInt(b[referenceColumnIndex]) < Integer.parseInt(referenceColumnValue)) {
                                            b[columnIndex] = columnValue;
                                        }
                                        break;
                                    case "<=":
                                        if (Integer.parseInt(b[referenceColumnIndex]) <= Integer.parseInt(referenceColumnValue)) {
                                            b[columnIndex] = columnValue;
                                        }
                                        break;
                                    case ">=":
                                        if (Integer.parseInt(b[referenceColumnIndex]) >= Integer.parseInt(referenceColumnValue)) {
                                            b[columnIndex] = columnValue;
                                        }
                                        break;
                                }

                                String newLine = "";
                                for (int j = 0; j < b.length; j++) {
                                    if (j < b.length - 1)
                                        newLine += b[j] + "|";
                                    else

                                        newLine += b[j];
                                }
                                modifiedData.add(newLine);
                            }
                            PrintWriter w = new PrintWriter(tablePath);
                            w.print("");
                            w.close();
                            tableFile.close();

                            FileWriter fileWriter = new FileWriter(tablePath, true);
                            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
                            for (int k = 0; k < modifiedData.size(); k++) {
                                if (k < modifiedData.size() - 1) {
                                    bufferFileWriter.append(modifiedData.get(k));
                                    bufferFileWriter.newLine();
                                } else {
                                    bufferFileWriter.append(modifiedData.get(k));
                                }
                            }

                            bufferFileWriter.close();
                            Instant endTime = Instant.now();
                            logFlag=false;
                            LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime,"No Errors");
                            System.out.println("Record updated successfully");
                            this.createQueryCountMetadata(tableName,0,0,1,0);
                        } else {
                            Instant endTime = Instant.now();
                            LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Column does not exist");
                            throw new QueryExceptions("Error: Column does not exist");
                        }
                    }
                }else{
                    Instant endTime = Instant.now();
                    LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Selected table does not exist");
                    throw new QueryExceptions("Error: Selected table does not exist");
                }
            }
            else{
                throw new QueryExceptions("Error: Selected database does not exist");
            }

        } else {

            throw new QueryExceptions("Error: Select database");
        }


    }

    private void deleteTable(String query) throws QueryExceptions, IOException {
        Instant startTime = Instant.now();
        ArrayList<String> modifiedData=new ArrayList<String>();
    	if (isDatabaseSelected()) {

            String dbPath = Constants.DATABASE_PATH + DB_IN_USE;
            File database = new File(dbPath);

            if (database.exists()) {
                String tableName = getDbTableName(query, 2);
                String tablePath = dbPath + "/" + tableName + Constants.FILE_EXT;
                File table = new File(tablePath);
                if (table.exists()) {
                    if(this.transactionStartFlag) {
                        this.transactionQueryList.add(query);
                    }
                    else {
                        query = query.replaceAll("\"", "");
                        String referenceColumnName = getDbTableName(query, 4);
                        String referenceColumnValue = getDbTableName(query, 6);
                        String operator = getDbTableName(query, 5);
                        if (operator.equals(">") || operator.equals("<") || operator.equals(">=") || operator.equals(">=")) {
                            if (!Pattern.matches("[0-9]", referenceColumnValue)) {
                                Instant endTime = Instant.now();
                                LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Given value not Intege");
                                throw new QueryExceptions("Error: Given value not Integer");
                            }
                        }
                        BufferedReader tableFile = new BufferedReader(new FileReader(tablePath));
                        String header = tableFile.readLine();
                        modifiedData.add(header);
                        if (header.toLowerCase().contains(referenceColumnName) && header.toLowerCase().contains(referenceColumnName)) {
                            String Columns[] = header.trim().split("\\|", -1);
                            int referenceColumnIndex = 0;
                            String line;
                            for (int i = 0; i < Columns.length; i++) {
                                if (Columns[i].toLowerCase().contains(referenceColumnName)) {
                                    referenceColumnIndex = i;
                                }
                            }
                            while ((line = tableFile.readLine()) != null) {
                                String newLine = line;
                                String b[] = line.split("\\|", -1);
                                switch (operator) {
                                    case "=":
                                        if (b[referenceColumnIndex].equalsIgnoreCase(referenceColumnValue)) {
                                            newLine = "";
                                        }
                                        break;
                                    case "<>":
                                        if (!b[referenceColumnIndex].equalsIgnoreCase(referenceColumnValue)) {
                                            newLine = "";
                                        }
                                        break;
                                    case ">":
                                        if (Integer.parseInt(b[referenceColumnIndex]) > Integer.parseInt(referenceColumnValue)) {
                                            newLine = "";
                                        }
                                        break;
                                    case "<":
                                        if (Integer.parseInt(b[referenceColumnIndex]) < Integer.parseInt(referenceColumnValue)) {
                                            newLine = "";
                                        }
                                        break;
                                    case "<=":
                                        if (Integer.parseInt(b[referenceColumnIndex]) <= Integer.parseInt(referenceColumnValue)) {
                                            newLine = "";
                                        }
                                        break;
                                    case ">=":
                                        if (Integer.parseInt(b[referenceColumnIndex]) >= Integer.parseInt(referenceColumnValue)) {
                                            newLine = "";
                                        }
                                        break;
                                }
                                if (newLine != "") {
                                    modifiedData.add(newLine);
                                }
                            }
                            PrintWriter w = new PrintWriter(tablePath);
                            w.print("");
                            w.close();
                            tableFile.close();

                            FileWriter fileWriter = new FileWriter(tablePath, true);
                            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
                            for (int k = 0; k < modifiedData.size(); k++) {
                                if (k < modifiedData.size() - 1) {
                                    bufferFileWriter.append(modifiedData.get(k));
                                    bufferFileWriter.newLine();
                                } else {
                                    bufferFileWriter.append(modifiedData.get(k));
                                }
                            }
                            bufferFileWriter.close();

                            System.out.println("Record deleted successfully");
                            this.createQueryCountMetadata(tableName,0,0,0,1);

                        } else {
                            Instant endTime = Instant.now();
                            LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Column does not exist");
                            throw new QueryExceptions("Error: Column does not exist");
                        }
                    }
                }else{
                    Instant endTime = Instant.now();
                    LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Selected table does not exist");
                    throw new QueryExceptions("Error: Selected table does not exist");
                }
                logFlag=false;
                Instant endTime = Instant.now();
                LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "No Errors");
            }
            else{
                throw new QueryExceptions("Error: Selected database does not exist");
            }
        } else {
            throw new QueryExceptions("Error: Select database");
        }
    }

    private void insertTable(String query) throws QueryExceptions, IOException {
        Instant startTime = Instant.now();
        if (isDatabaseSelected()) {
            String dbPath = Constants.DATABASE_PATH + DB_IN_USE;
            File database = new File(dbPath);

            if (database.exists()) {
                String tableName = getDbTableName(query, 2);
                String type = getDbTableName(query, 3);
                String tablePath = dbPath + "/" + tableName + Constants.FILE_EXT;
                File table = new File(tablePath);
                if (table.exists()) {
                    if(this.transactionStartFlag){
                        this.transactionQueryList.add(query);
                    }
                    else {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(tablePath, true));
                        BufferedReader reader = new BufferedReader(new FileReader(tablePath));
                        String header = reader.readLine();
                        String headerArray[] = header.split("\\|", -1);
                        int totalColumnCount = headerArray.length;
                        if (type.equalsIgnoreCase("values")) {
                            int start = query.indexOf("(");
                            int end = query.indexOf(")");

                            String data = query.substring(start + 1, end);
                            String dataArray[] = data.split(",");
                            int givenColumnCount = dataArray.length;
                            if (givenColumnCount == totalColumnCount) {
                                String values = "";
                                for (int j = 0; j < totalColumnCount; j++) {

                                    String columnName = headerArray[j].replace("@PK", "");
                                    if (columnName.contains("int") || columnName.contains("float")) {
                                        if ((dataArray[j].matches("[0-9]+")) && !(dataArray[j].startsWith("\"") && dataArray[j].endsWith("\""))) {
                                            values += dataArray[j] + "|";
                                        } else {
                                            throw new QueryExceptions("Error :datatype mismatch ");
                                        }
                                    } else if (headerArray[j].contains("text")) {
                                        if (dataArray[j].startsWith("\"") && dataArray[j].endsWith("\"")) {
                                            dataArray[j] = dataArray[j].replace("\"", "");
                                            values += dataArray[j] + "|";

                                        } else {
                                            throw new QueryExceptions("Error :datatype mismatch");
                                        }
                                    }
                                }
                                values = values.substring(0, values.length() - 1);
                                writer.write(values+"\n");
                                writer.close();
                                Instant endTime = Instant.now();
                                LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "No Errors");
                                System.out.println("Record inserted successfully");
                                this.createQueryCountMetadata(tableName,1,0,0,0);
                            } else {
                                Instant endTime = Instant.now();
                                LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Error: table " + tableName + " has " + totalColumnCount + " columns but " + givenColumnCount + " values were supplied");
                                throw new QueryExceptions("Error: table " + tableName + " has " + totalColumnCount + " columns but " + givenColumnCount + " values were supplied");
                            }
                        }
                    }
                } else {
                    Instant endTime = Instant.now();
                    LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Selected table does not exist");
                    throw new QueryExceptions("Error: Selected table does not exist");
                }
            }
        } else {
            throw new QueryExceptions("Error: Select database");

        }
    }

    private void selectTable(String query) throws QueryExceptions, IOException{
        Instant startTime = Instant.now();
        if (isDatabaseSelected()) {
            String dbPath = Constants.DATABASE_PATH + DB_IN_USE;
            File database = new File(dbPath);
            if (database.exists()) {
                query = query.replaceAll("\"", "");
                int start=query.toLowerCase().indexOf("from");
                String subquery=query.substring(start,query.length()).toLowerCase();
                String tableName = getDbTableName(subquery, 1);
                String tablePath = dbPath + "/" + tableName + Constants.FILE_EXT;
                File table = new File(tablePath);
                if (table.exists()) {
                    if(this.transactionStartFlag){
                        this.transactionQueryList.add(query);
                    }
                    else {
                        if (!subquery.contains("where")) {
                            displayAllData(query,tablePath,tableName);
                        } else {
                            String referenceColumnName = getDbTableName(subquery, 3);
                            String referenceColumnValue = getDbTableName(subquery, 5);
                            String operator = getDbTableName(subquery, 4);
                            if (operator.equals(">") || operator.equals("<") || operator.equals(">=") || operator.equals(">=")) {
                                if (!Pattern.matches("[0-9]", referenceColumnValue)) {
                                    throw new QueryExceptions("Error: Given value not Integer");
                                }
                            }
                            String column = query.substring(7, start);
                            HashMap<String, Integer> columnIndexMap = new HashMap<String, Integer>();
                            String columnArray[] = column.split(",");
                            BufferedReader reader = new BufferedReader(new FileReader(tablePath));
                            String header = reader.readLine();
                            if (header.toLowerCase().contains(referenceColumnName) && header.toLowerCase().contains(referenceColumnName)) {
                                String Columns[] = header.trim().split("\\|", -1);
                                int referenceColumnIndex = 0;
                                String line;
                                for (int i = 0; i < Columns.length; i++) {
                                    int j = Columns[i].indexOf("(");
                                    String parse = Columns[i].substring(0, j).toLowerCase().trim();
                                    columnIndexMap.put(parse, i);
                                }
                                referenceColumnIndex = columnIndexMap.get(referenceColumnName);
                                while ((line = reader.readLine()) != null) {
                                    String newLine = line;
                                    String b[] = line.split("\\|", -1);
                                    switch (operator) {
                                        case "=":
                                            if (b[referenceColumnIndex].equalsIgnoreCase(referenceColumnValue)) {

                                                displayData(query, line, columnIndexMap, columnArray);

                                            }
                                            break;
                                        case "!=":
                                            if (!b[referenceColumnIndex].equalsIgnoreCase(referenceColumnValue)) {

                                                displayData(query, line, columnIndexMap, columnArray);
                                            }
                                            break;
                                        case ">":
                                            if (Integer.parseInt(b[referenceColumnIndex]) > Integer.parseInt(referenceColumnValue)) {

                                                displayData(query, line, columnIndexMap, columnArray);
                                            }
                                            break;
                                        case "<":
                                            if (Integer.parseInt(b[referenceColumnIndex]) < Integer.parseInt(referenceColumnValue)) {

                                                displayData(query, line, columnIndexMap, columnArray);
                                            }
                                            break;
                                        case "<=":
                                            if (Integer.parseInt(b[referenceColumnIndex]) <= Integer.parseInt(referenceColumnValue)) {

                                                displayData(query, line, columnIndexMap, columnArray);
                                            }
                                            break;
                                        case ">=":
                                            if (Integer.parseInt(b[referenceColumnIndex]) >= Integer.parseInt(referenceColumnValue)) {

                                                displayData(query, line, columnIndexMap, columnArray);
                                            }
                                            break;
                                    }
                                }

                                this.createQueryCountMetadata(tableName,0,1,0,0);
                            } else {
                                Instant endTime = Instant.now();
                                LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Column does not exist");
                                throw new QueryExceptions("Error: Column does not exist");
                            }
                        }
                    }
                }
                else{
                    Instant endTime = Instant.now();
                    LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "Selected table does not exist");
                    throw new QueryExceptions("Error: Selected table does not exist");
                }
                logFlag=false;
                Instant endTime = Instant.now();
                LogManager.log(DB_IN_USE,tablePath,query,startTime,endTime, "No Errors");
            }
        }
        else {
            throw new QueryExceptions("Error: Select database");
        }

    }

    private void displayData(String query, String line, HashMap<String, Integer> columnIndexMap, String[] columnArray) throws QueryExceptions {
        if (query.contains("*")) {
            System.out.println(line);
        } else {
            ArrayList<String> al = new ArrayList<String>();
            String b[] = line.trim().split("\\|", -1);
            for (int j = 0; j < b.length; j++) {
                al.add(b[j]);
            }
            for (int i = 0; i < columnArray.length; i++) {
                if (columnIndexMap.containsKey(columnArray[i].trim())) {
                    System.out.print(al.get(columnIndexMap.get(columnArray[i].trim())) + " ");
                } else {
                    throw new QueryExceptions("Error:  Column does not exist");
                }
            }
            System.out.println();

        }
    }

    private void displayAllData(String query,String tablePath, String tableName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(tablePath));
        String header = reader.readLine();
        if(query.contains("*")){
            System.out.println(header);
            String s = null;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
        }
        else{
            ArrayList<String> headerList=new ArrayList(Arrays.asList(header.split("\\|")));
            String columnString=query.substring(query.indexOf("select")+"select".length(),query.indexOf("from")).trim();
            ArrayList<String> columns=new ArrayList<>(Arrays.asList(columnString.split(",")));
            ArrayList<Integer> headerIndexes=new ArrayList<>();
            for(String column:columns){
                for(String headerColumn:headerList){
                    if(headerColumn.contains(column)){
                        if(columns.indexOf(column)!=0){
                            System.out.print("|");
                        }
                        headerIndexes.add(headerList.indexOf(headerColumn));
                        System.out.print(headerColumn);
                    }
                }
            }
            System.out.println("");
            String s = null;
            while ((s = reader.readLine()) != null) {
                String[] record=s.split("\\|");
                for(int i=0;i< record.length;i++){
                    int index=headerIndexes.indexOf(i);
                    if(index!=-1){
                        if(index!=0){
                            System.out.print("|");
                        }
                        System.out.print(record[i]);
                    }
                }
                System.out.println("");
            }
        }
        this.createQueryCountMetadata(tableName,0,1,0,0);

    }

    public void transactionStart(String query) throws QueryExceptions {
        if(isDatabaseSelected()) {
            String dbPath = Constants.DATABASE_PATH + DB_IN_USE;
            File database = new File(dbPath);

            if (database.exists()) {
                this.transactionStartFlag=true;
                this.transactionQueryList=new ArrayList<>();
            }
            else{
                throw new QueryExceptions("Error: Selected database does not exist");
            }
        } else{
            throw new QueryExceptions("Error: Select database");
        }
    }

    public void transactionCommit(String query) throws QueryExceptions, IOException {
        if(isDatabaseSelected()) {
            String dbPath = Constants.DATABASE_PATH + DB_IN_USE;
            File database = new File(dbPath);

            if (database.exists()) {
                this.transactionStartFlag=false;
                for(String transactionQuery:this.transactionQueryList){
                    this.parseQuery(transactionQuery);
                }
                this.transactionQueryList=new ArrayList<>();
            }
            else{
                throw new QueryExceptions("Error: Selected database does not exist");
            }
        } else{
            throw new QueryExceptions("Error: Select database");
        }
    }

    public void transactionRollback(String query) throws QueryExceptions {
        if(isDatabaseSelected()) {
            String dbPath = Constants.DATABASE_PATH + DB_IN_USE;
            File database = new File(dbPath);

            if (database.exists()) {
                if(!this.transactionStartFlag) {
                    throw new QueryExceptions("Error: Transaction not started");
                }
                else{
                    this.transactionStartFlag=false;
                    this.transactionQueryList=new ArrayList<>();
                }

            }
            else{
                throw new QueryExceptions("Error: Selected database does not exist");
            }
        } else{
            throw new QueryExceptions("Error: Select database");
        }
    }

    public void userQueryCounter(){
        if(isDatabaseSelected()) {
            try {
                String queryCountFile = Constants.METADATA_PATH + "/userCount.txt";
                Properties properties = new Properties();
                FileInputStream input = null;
                input = new FileInputStream(queryCountFile);

                properties.load(input);
                input.close();

                OutputStream output = new FileOutputStream(queryCountFile);
                String userCount = properties.getProperty(ProjectMenu.username+"."+this.DB_IN_USE);
                properties.setProperty(ProjectMenu.username+"."+this.DB_IN_USE, userCount != null ? String.valueOf(Integer.valueOf(userCount) + 1) : "1");
                properties.store(output, null);
            } catch (FileNotFoundException e) {
                System.out.println("Error : User Count File not found exception occurred");
            } catch (IOException e) {
                System.out.println("Error : I/O exception occurred");
            }
        }
    }


}

