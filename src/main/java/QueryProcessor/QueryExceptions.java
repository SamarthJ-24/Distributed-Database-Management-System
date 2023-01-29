package QueryProcessor;
import LogManager.*;
public class QueryExceptions extends Exception{
    LogManager lm = new LogManager();
    public QueryExceptions(String errorMessage) {
        super(errorMessage);
      //  lm.passErrorMessage(errorMessage);

    }

}
