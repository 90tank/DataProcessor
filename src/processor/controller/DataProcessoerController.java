package processor.controller;

import csv.tool.ReadByApachCsv;
import file.getter.MatColGetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import select.DirFinder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class DataProcessoerController {
    private static Logger LOGGER = LogManager.getLogger(DataProcessoerController.class.getName());

    public static void main(String[] args) throws IOException, ParseException {

        LOGGER.info("DataProcessor  开始运行");

        String stockPath = "D:\\原始数据\\stock";
        String outputPath = "D:\\原始数据\\OutPut";
//        String cspPath = "E:\\Run\\DataProcessor\\src\\stocklistA.csv";
        String cspPath = "E:\\Run\\DataProcessor\\src\\list.csv";

        List<String> stockNameList  = ReadByApachCsv.getStockList(cspPath);

        for(int i=0;i<stockNameList.size();i++){
            String aimStock = stockNameList.get(i)+".mat";
            aimstockProcessor(stockPath,aimStock,outputPath);
        }


        LOGGER.info("DataProcessor 运行结束");
    }

    public static void aimstockProcessor(String stockPath,String aimStock,String outputPath) throws IOException, ParseException {
        //输出文件
        //000001.SZ.mat
        String [] sp = aimStock.split("\\.");

        String outfileName = sp[0]+"."+sp[1]+".csv";//合成文件名 两个点

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
            LOGGER.info("\ntmpStock "+tmpStock);
            MatColGetter.matReader(tmpStock,outfile);
        }
    }
}
