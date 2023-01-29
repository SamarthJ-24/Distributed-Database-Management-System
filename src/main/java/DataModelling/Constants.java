package DataModelling;

import ProjectMain.Main;

public class Constants {

    public static final String VM_1_HOSTNAME = "data-project-group5-vm1";
    public static final String VM_2_HOSTNAME = "data-project-group5-vm2";
    public static final String DATABASE_PATH = "./src/main/resources/database/";
    public static final String ERD_PATH = "./src/main/resources/erd";
    public static final String METADATA_PATH= Main.hostname.equals(VM_1_HOSTNAME)?"./src/main/resources/metadata/":Main.hostname.equals(VM_2_HOSTNAME)?"./src/main/resources/metadata2/":"./src/main/resources/metadata/";
}
