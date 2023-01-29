package MetadataTransfer;

import ProjectMain.Main;
import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class GCPTransfer {
    public void transferMetadata() {
        JSch jSch=new JSch();
        String user = null;
        String host = null;
        int port= Constants.PORT;
        String sourcePath = null;
        String destinationPath = null;
        String pvtKey = null;
        if(Main.hostname.equals(Constants.VM_1_HOSTNAME)){
            user= Constants.VM_2_USER;
            host= Constants.VM_2_IP;
            sourcePath= Constants.VM_1_METADATA_SRC;
            destinationPath= Constants.VM_1_METADATA_DEST;
            pvtKey= Constants.VM_2_PVT_KEY;
        }
        else if(Main.hostname.equals(Constants.VM_2_HOSTNAME)){
            user= Constants.VM_1_USER;
            host= Constants.VM_1_IP;
            sourcePath = Constants.VM_2_METADATA_SRC;
            destinationPath= Constants.VM_2_METADATA_DEST;
            pvtKey= Constants.VM_1_PVT_KEY;
        }
        try{
            if(Main.hostname.equals(Constants.VM_1_HOSTNAME) || Main.hostname.equals(Constants.VM_2_HOSTNAME)) {
                jSch.addIdentity(pvtKey);
                Session session = jSch.getSession(user, host, port);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.setInputStream(System.in);
                channel.setOutputStream(System.out);
                channel.connect();
                ChannelSftp sftpChannel = (ChannelSftp) channel;
                try {
                    copyMetadataToGCP(sourcePath, destinationPath, sftpChannel);
                } catch (SftpException e) {
                    System.out.println("Error : Error occurred while sending metadata");
                } catch (FileNotFoundException e) {
                    System.out.println("Error : Error occurred while sending metadata");
                } finally {
                    sftpChannel.exit();
                    channel.disconnect();
                    session.disconnect();
                }
            }
        } catch (JSchException e) {
            System.out.println("Error : Error occurred while sending metadata");
        }
    }

    private static void copyMetadataToGCP(String metadataSourcePath, String gcpPath, ChannelSftp sftpChannel)
            throws SftpException, FileNotFoundException {
        File localFile = new File(metadataSourcePath);
        if (localFile.isFile()) {
            sftpChannel.cd(gcpPath);
            sftpChannel.put(new FileInputStream(localFile), localFile.getName(), ChannelSftp.OVERWRITE);
        } else {
            File[] files = localFile.listFiles();

            if (files != null && files.length > 0) {
                sftpChannel.cd(gcpPath);
                SftpATTRS stat = null;

                try {
                    stat = sftpChannel.stat(gcpPath + "/" + localFile.getName());
                } catch (Exception e) {
                }

                if (stat == null){
                    sftpChannel.mkdir(localFile.getName());
                }

                for (File file:files) {
                    copyMetadataToGCP(file.getPath(), gcpPath + "/" + localFile.getName(), sftpChannel);
                }
            }
        }
    }

    public void createDatabase(String dbName){
        try{
            if(Main.hostname.equals(Constants.VM_1_HOSTNAME) || Main.hostname.equals(Constants.VM_2_HOSTNAME)) {
                JSch jSch = new JSch();
                String user = null;
                String host = null;
                int port = Constants.PORT;
                String pvtKey = null;
                String dbPath = null;
                if (Main.hostname.equals(Constants.VM_1_HOSTNAME)) {
                    user = Constants.VM_2_USER;
                    host = Constants.VM_2_IP;
                    pvtKey = Constants.VM_2_PVT_KEY;
                    dbPath = Constants.VM_2_DATABASE;
                } else if (Main.hostname.equals(Constants.VM_2_HOSTNAME)) {
                    user = Constants.VM_1_USER;
                    host = Constants.VM_1_IP;
                    pvtKey = Constants.VM_1_PVT_KEY;
                    dbPath = Constants.VM_1_DATABASE;
                }
                jSch.addIdentity(pvtKey);
                Session session = jSch.getSession(user, host, port);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.setInputStream(System.in);
                channel.setOutputStream(System.out);
                channel.connect();
                ChannelSftp sftpChannel = (ChannelSftp) channel;

                try {
                    sftpChannel.cd(dbPath);
                    sftpChannel.mkdir(dbName);
                } catch (SftpException e) {

                } finally {
                    sftpChannel.exit();
                    channel.disconnect();
                    session.disconnect();
                }
            }
        } catch (JSchException e) {
            System.out.println("Error : Creating database on other VM");
        }
    }
}
