import com.jmatio.io.MatFileReader;
import file.getter.MatColGetter;
import select.DirFinder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DataProcessoerController {

    public static void main(String[] args) throws IOException, ParseException {

        System.out.println("Demo  开始运行");

        String stockPath = "/home/young/Run/stock";
        String outputPath = "/home/young/Run/OutPut";
        //要从list表中获取
        String aimStock1 = "000001.SZ.mat";
        String aimStock2 = "000002.SZ.mat";
        String aimStock3 = "000003.SZ.mat";
        String aimStock4 = "000004.SZ.mat";
        ArrayList<String> stockNameList = new ArrayList();
        stockNameList.add(aimStock1);
        stockNameList.add(aimStock2);
        stockNameList.add(aimStock3);
        stockNameList.add(aimStock4);

        for(int i=0;i<stockNameList.size();i++){
            String aimStock = stockNameList.get(i);
            aimstockProcessor(stockPath,aimStock,outputPath);

        }

    }

    public static void aimstockProcessor(String stockPath,String aimStock,String outputPath) throws IOException, ParseException {
        //输出文件
        String [] sp = aimStock.split("\\.");
        String outfileName = sp[0]+sp[1]+".csv";
        String outfilePath =  outputPath + File.separator + outfileName;
        File outfile = new File(outfilePath);
        if(!outfile.exists()){
            outfile.createNewFile(); //不存在则创建 存在则追加
        }

        System.out.println("outfile :"+outfilePath);
        //开始合并一个文件
        DirFinder finder = new DirFinder();
        List<File> stockList = finder.findOneStockInAllYearsDir(stockPath,aimStock);

        for(int i=0;i<stockList.size();i++){
            File tmpStock = stockList.get(i);
            MatColGetter.matReader(tmpStock,outfile);
        }


    }
}
