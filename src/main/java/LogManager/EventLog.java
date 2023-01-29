package LogManager;

import java.io.*;

public class EventLog {
    public static void writeLog(String dbName, String keyword, String tableName, String tablePath, Long executionTime, String errorMessage) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(tablePath));
            String header = br.readLine();
            long lines = 0;
            while (br.readLine() != null) lines++;
            File file = new File(Constants.EVENT_LOG_FILE);

            BufferedWriter wr = new BufferedWriter(new FileWriter(file, true));
            String content = "Query => " + keyword + ", Database Status: Database => " + dbName +
                    ", Table Changed => " + tableName + ", Total Records present => " + lines +
                    ", Error handled => " + errorMessage;
            wr.append(content);
            wr.newLine();
            wr.close();
        }
        catch (Exception e) {
            File file = new File(Constants.EVENT_LOG_FILE);
            BufferedWriter wr = new BufferedWriter(new FileWriter(file, true));
            String content = "Query => " + keyword + ", Database Status: Database => " + dbName +
                    ", Table Changed => " + tableName + ", Total Records present => " + "No records" +
                    ", Error handled => " + errorMessage;
            wr.append(content);
            wr.newLine();
            wr.close();
        }
    }
}
