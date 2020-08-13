package utils;

import property.Property;

import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Utils {
    public static List<String> getFileList(String dir){
        List<String> fileList = new LinkedList<String>();
        fileList.add( ".." );
        File file = new File( dir );
        if (file.exists()){
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (File f: files) {
                    if (!f.isHidden()) {
                        fileList.add(f.getName());
                    }
                }
            }
        }

        return fileList;
    }

    public static byte[] readFile(String filePath, int i, int buferSize) throws IOException {
        System.out.println(filePath);
        File file = new File( filePath );
        byte[] bytes = new byte[(int) file.length()];
        InputStream fis = new FileInputStream( file );
        //fis.read(bytes);
        fis.read(bytes, i*buferSize, buferSize);
        fis.close();
        System.out.println("read from file "+bytes.length+" bytes.");
        return bytes;
    }

    public static void writeFile(String filePath, byte[] bytes,  int path) throws IOException {
        File file = Paths.get(filePath ).toFile();
        OutputStream fos = new FileOutputStream( file );
        if (!file.exists()) file.createNewFile();
        System.out.println("write to file:"+filePath);
        System.out.println("write to file "+bytes.length+" bytes.");
        fos.write(bytes, path * Property.getBuferSize(), bytes.length);
        fos.close();
    }
}
