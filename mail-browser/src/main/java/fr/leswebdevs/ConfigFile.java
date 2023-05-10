package fr.leswebdevs;

import java.io.*;

public class ConfigFile {

    public static final String FILE_NAME = "config.mbc";


    public static boolean isExist() {
        return new File(FILE_NAME).isFile();
    }

    public static boolean writeFile(MailConnectionCredentials mailConnectionCredentials) {
        try {
            FileOutputStream fileOut = new FileOutputStream(FILE_NAME);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(mailConnectionCredentials);
            objectOut.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static MailConnectionCredentials readFile() {
        if (!isExist()) {
            return null;
        }
        try {
            FileInputStream fileInput = new FileInputStream(FILE_NAME);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            MailConnectionCredentials mailConnectionCredentials = (MailConnectionCredentials) objectInput.readObject();
            objectInput.close();
            return mailConnectionCredentials;
        } catch (Exception e) {
            return null;
        }
    }


}
