package LogManager;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public class QueryLog {

    public static void writeLog(String username,String dbName,
                                String tableName,String keyword,
                                String query, Instant timestamp) {

        try {
            FileWriter queryLogWriter = new FileWriter(Constants.QUERY_LOG_FILE, true);
            queryLogWriter.append(username);
            queryLogWriter.append(Constants.SEPARATOR+dbName);
            queryLogWriter.append(Constants.SEPARATOR+tableName);
            queryLogWriter.append(Constants.SEPARATOR+keyword);
            queryLogWriter.append(Constants.SEPARATOR+query);
            queryLogWriter.append(Constants.SEPARATOR+timestamp);
            queryLogWriter.append(System.getProperty( "line.separator" ));
            queryLogWriter.flush();
            queryLogWriter.close();
        } catch (IOException e) {
            System.out.println("Some error occurred in query logger");
        }

    }
}
