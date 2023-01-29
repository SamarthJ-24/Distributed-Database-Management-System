package LogManager;

import ProjectMain.ProjectMenu;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class LogManager {

    public static void log(String dbName, String tablePath,String query,Instant startTime, Instant endTime, String error_Message) throws IOException {

        String username = ProjectMenu.username;
        String keyword =  error_Message.equals("No Errors") ? getQueryKeyword(query) :"null";
        String tableName;

        if(tablePath != "null"){
            String[] pathArray = tablePath.split("/");
            tableName = (pathArray[pathArray.length - 1].split("\\."))[0];
        }
        else{
            tableName = "null";
        }

        Long executionTime = Duration.between(startTime, endTime).toMillis();

        writeQueryLog(username,dbName,tableName,keyword,query,startTime);
        writeGeneralLog(dbName, keyword, tableName, tablePath,executionTime);
        writeEventLog(dbName, keyword, tableName, tablePath,executionTime,error_Message);
    }

    private static String getQueryKeyword(String query) {
        return query.split(" ")[0];
    }

    private static void writeGeneralLog(String dbName, String keyword,
                                        String tableName, String tablePath,
                                        Long executionTime) {

        try {
            checkLogFileExist(Constants.GENERAL_LOG_FILE);
            GeneralLog.writeLog(dbName, keyword, tableName, tablePath,executionTime);

        } catch (IOException e) {
        }
    }

    private static void writeEventLog(String dbName, String keyword,
                                      String tableName, String tablePath,
                                      Long executionTime, String Error_Message) throws IOException {

//            checkLogFileExist(Constants.EVENT_LOG_FILE);
            EventLog.writeLog( dbName, keyword,
                   tableName, tablePath,
                    executionTime,Error_Message);


    }

    private static void writeQueryLog(String username,String dbName,
                                      String tableName,String keyword,
                                      String query, Instant timestamp)
    {

        try {
            if(checkLogFileExist(Constants.QUERY_LOG_FILE)){
                QueryLog.writeLog(username,dbName,tableName,keyword,query,timestamp);
            }
        } catch (IOException e) {
            System.out.printf("Error : Couldn't create query logs");
        }
    }

    private static boolean checkLogDirExist() {

        File dir = new File(Constants.LOG_PATH);

        if(!dir.exists()){
            return dir.mkdir();
        }

        return true;
    }

    private static boolean checkLogFileExist(String filePath) throws IOException {

        if(checkLogDirExist()) {

            boolean fileCreated = true;
            File file = new File(filePath);

            if (!file.exists()) {
                if (file.createNewFile()) {
                    fileCreated = true;
                } else {
                    fileCreated = false;
                }
            }

            return fileCreated;
        }

        return false;
    }
}
