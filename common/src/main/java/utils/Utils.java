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

    public static byte[] readFile(String filePath, int cnt, int buferSize) throws IOException {
        System.out.println(filePath);
        File file = new File( filePath );
        InputStream fis = new FileInputStream( file );
        if (file.length() < buferSize) buferSize = (int) file.length();
        byte[] bytes = new byte[buferSize];
        int readingByte = 0;
        fis.skip( (long) cnt*buferSize );
        readingByte = fis.read(bytes);

        System.out.println("readFile cnt="+cnt+" buferSize = "+buferSize+" readingByte = "+readingByte);
        System.out.println(bytes.toString());
        //fis.read(bytes, i, buferSize);
        fis.close();
        System.out.println("read from file "+bytes.length+" bytes.");
        return bytes;
    }

    public static void writeFile(String filePath, byte[] bytes,  int path) throws IOException {
        File file = Paths.get(filePath ).toFile();
        boolean isAppend = false;
        if (path != 0) isAppend = true;
        if (!file.exists()) file.createNewFile();

        OutputStream fos = new FileOutputStream( file,  isAppend);
        fos.write(bytes);
        fos.close();
    }
}
