package QueryProcessor;

import ProjectMain.Main;

public class Constants {

    public static final String VM_1_HOSTNAME = "data-project-group5-vm1";
    public static final String VM_2_HOSTNAME = "data-project-group5-vm2";
    public static final String KEYWORDS_CREATE_DATABASE = "create database";
    public static final String REGEX_CREATE_DATABASE = "^(?i)(CREATE\\sDATABASE\\s[a-zA-Z\\d]+;)$";
    public static final String KEYWORDS_USE_DATABASE = "use";
    public static final String REGEX_USE_DATABASE = "^(?i)(USE\\s[a-zA-Z\\d]+;)$";
    public static final String KEYWORD_CREATE_TABLE = "create table";
    public static final String REGEX_CREATE_TABLE = "^(?i)(CREATE\\sTABLE\\s[a-zA-Z\\d]+\\s\\(([a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?(,\\s[a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?)*)\\);)$";
    public static final String REGEX_CREATE_TABLE_COLUMN = "(?i)([a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?(,\\s[a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?)*)";
    public static final String FILE_EXT = ".txt";
    public static final String DATABASE_PATH = "./src/main/resources/database/";
    public static final String METADATA_PATH= Main.hostname.equals(VM_1_HOSTNAME)?"./src/main/resources/metadata/":Main.hostname.equals(VM_2_HOSTNAME)?"./src/main/resources/metadata2/":"./src/main/resources/metadata/";
    public static final String COLUMN_SEPARATOR = "|";
    public static final String COLUMN_META_SEPARATOR = "@";
    public static final String KEYWORD_UPDATE_TABLE = "update";
    public static final String REGEX_UPDATE_TABLE ="^(?i)(UPDATE\\s[a-zA-Z\\d]+\\sSET\\s[a-zA-Z\\d]+\\s=\\s[a-zA-Z\\d\"]+(\\sWHERE\\s[a-zA-Z\\d]+\\s(\\!)*(\\>)*(\\<)*(\\=)*\\s[a-zA-Z2\\d\"]+)*;)$";
    public static final String KEYWORD_DELETE_TABLE = "delete from";
    public static final String REGEX_DELETE_TABLE = "^(?i)(DELETE\\sFROM\\s[a-zA-Z\\d]+(\\sWHERE\\s[a-zA-Z\\d]+\\s(\\!)*(\\>)*(\\<)*(\\=)*\\s[a-zA-Z2\\d\"]+)*;)$";
    public static final String KEYWORD_INSERT_TABLE = "insert";
    public static final String REGEX_INSERT_TABLE = "^(?i)(INSERT\\sINTO\\s[a-zA-Z\\d]+\\sVALUES\\s[a-zA-Z\\d,\\(\\)\"]+;)$";
    public static final String KEYWORD_SELECT_TABLE = "select";
    public static final String REGEX_SELECT_TABLE ="^(?i)(SELECT\\s[a-zA-Z\\d\\*\\,]+\\sFROM\\s[a-zA-Z\\d]+(\\sWHERE\\s[a-zA-Z\\d]+\\s(\\!)*(\\>)*(\\<)*(\\=)*\\s[a-zA-Z2\\d\"]+)*;)$";
    public static final String KEYWORD_TRANSACTION_START = "start transaction";
    public static final String REGEX_TRANSACTION_START = "^(?i)(START\\sTRANSACTION;)$";
    public static final String KEYWORD_TRANSACTION_COMMIT = "commit";
    public static final String REGEX_TRANSACTION_COMMIT = "^(?i)(COMMIT;)$";
    public static final String KEYWORD_TRANSACTION_ROLLBACK = "rollback";
    public static final String REGEX_TRANSACTION_ROLLBACK = "^(?i)(ROLLBACK;)$";
}
