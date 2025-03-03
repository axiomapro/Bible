package ru.ampstudy.bible.component.immutable.box;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileManager {

    public boolean checkExist(String path) {
        return new File(path).exists();
    }

    public void createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) folder.mkdirs();
    }

    public void createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeFile(String path,String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(path));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(String path) {
        String result = "";
        try {
            InputStream stream = new FileInputStream(path);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            result = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void copyFile(String input, String output) {
        try (InputStream in = new FileInputStream(input)){
            try (OutputStream out = new FileOutputStream(output)) {
                byte[] buf = new byte[1024];
                int len;
                while((len = in.read(buf)) > 0) {
                    out.write(buf,0,len);
                }

                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String path) {
        File file = new File(path);
        if (file.exists()) file.delete();
    }

}