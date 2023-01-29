package Analytics;

import ProjectMain.Main;

public class Constants {

    public static final String VM_1_HOSTNAME = "data-project-group5-vm1";
    public static final String VM_2_HOSTNAME = "data-project-group5-vm2";
    public static final String DATABASE_PATH = "./src/main/resources/database/";
    public static final String ANALYTICS_PATH = "./src/main/resources/analytics/";
    public static final String KEYWORD_COUNT = "count";
    public static final String REGEX_COUNT = "^(?i)(COUNT\\s(INSERT|DELETE|UPDATE|SELECT)\\s[a-zA-Z\\d]+;)$";
    public static final String KEYWORD_COUNT_USER_QUERIES = "count queries";
    public static final String REGEX_COUNT_USER_QUERIES = "^(?i)(COUNT\\sQUERIES;)$";
    public static final String METADATA_PATH = "./src/main/resources/metadata/";
    public static final String METADATA2_PATH = "./src/main/resources/metadata2/";
    public static final String CURRENT_METADATA_PATH= Main.hostname.equals(VM_1_HOSTNAME)?"./src/main/resources/metadata/":Main.hostname.equals(VM_2_HOSTNAME)?"./src/main/resources/metadata2/":"./src/main/resources/metadata/";

}
