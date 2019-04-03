package test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class test {

    private static Logger LOGGER = LogManager.getLogger(test.class.getName());
    public static void main(String[] args) throws IOException {
        LOGGER.info("Logger is Logger");
        for(int i=0;i<3;i++){
            File file  = new File("123.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream("123.txt",true); //追加模式
            BufferedOutputStream buf = new BufferedOutputStream(fos);
            buf.write("123".getBytes());


            buf.flush();
            buf.close();
            fos.close();



        }

    }
}
