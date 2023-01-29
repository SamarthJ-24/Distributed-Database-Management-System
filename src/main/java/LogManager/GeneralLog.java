package LogManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class GeneralLog {
    public static void writeLog(String dbName, String keyword, String tableName, String tablePath, Long executionTime) throws IOException {

        File file=new File(Constants.DATABASE_PATH+dbName);
        long tableCount=0;
        long lines=0;
        if(file.exists()){
            for(File temp:file.listFiles()){
                tableCount++;
                lines+= Files.lines(Path.of(temp.getPath())).count()-1;
            }
        }
        File general_log = new File(Constants.GENERAL_LOG_FILE);
        FileWriter fw = new FileWriter(general_log, true);
        String content = "Execution time: "+executionTime+" ms, Query => "+ keyword+ ", Database Status: Database => "+dbName+ ", Number of Tables => "+tableCount+", Total Records present => "+ lines ;
        fw.append(content+"\n");
        fw.flush();
        fw.close();
    }
}
