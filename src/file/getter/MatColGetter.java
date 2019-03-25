package file.getter;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MatColGetter {

  /*  public static void main(String[] args) throws IOException {
        String path = "/home/young/Run/stocklistA.mat";
        List<String> stockList = getStockList(path);
    }
*/
/*    public static List<String> getStockList(String stockListFilePath) throws IOException {
        List<File> stockNameList = new ArrayList<>();
        MatFileReader read = new MatFileReader(stockListFilePath);
        Map<String,MLArray> allInOne = read.getContent(); //获取原始内容
        Set<String> keySet = allInOne.keySet(); //获取文件内容中主键集合
        MLArray allInOneMLA =  allInOne.get("stocklistA"); //拿到每一个主键对应的 结构体
        MLStructure tmpStruct0 = (MLStructure)allInOneMLA;

        return null;
    }*/

    public static void matReader(File aimStock,File resultFile) throws IOException, ParseException {
        String aimStockPath = aimStock.getAbsolutePath();
        MatFileReader read = new MatFileReader(aimStockPath);

        if(!resultFile.exists()){
            resultFile.createNewFile();
            System.out.println("create an empty result file ");
        }
        //输出流  重复打开输出结果文件 可优化
        FileOutputStream fos = new FileOutputStream(resultFile);
        BufferedOutputStream buf = new BufferedOutputStream(fos);

        Map<String,MLArray> allInOne = read.getContent(); //获取原始内容
        Set<String> keySet = allInOne.keySet(); //获取文件内容中主键集合

        MLArray allInOneMLA =  allInOne.get("StockTickAB"); //拿到每一个主键对应的 结构体
        MLStructure tmpStruct0 = (MLStructure)allInOneMLA;

        //单个文件数据处理逻辑
        // 0. 根据股票名称mat文件是按照一个股票一个文件存的,获取到股票名称
        // 1. 根据时间（时间范围 两个区间） 计算出行索引
        // 2. 讲需要的列原始数据数据还原成数组
        // 3. 按照索引依次索引，拼凑一行，写入buffer

        //需要截取的时间范围内部数据 9:30-11:30 , 13：00-15:00
        //需要根据时间计算索引 然后拿出对应的行 有些行可以理解为包含一定的列 比如要取5列的值
        //Date + Time --> timeStamp

        //Date
        MLArray dateMLArray = tmpStruct0.getField("Date");

        MLInt32 dateMLInt32 = (MLInt32) dateMLArray;
        int[][] date = dateMLInt32.getArray();
        for(int i=0;i<5;i++){
            int d = date[i][0];
            System.out.println("==date =="+i +" value "+ d );
        }

        //Time
        MLArray timeMLArray = tmpStruct0.getField("Time");
        MLInt32 timeMLInt32 = (MLInt32) timeMLArray;
        int[][] time = timeMLInt32.getArray();
        for(int i=0;i<5;i++){
            int d = time[i][0];
            System.out.println("==time =="+i +" value "+ d );
        }


        //Price
        MLArray priceMLArray = tmpStruct0.getField("Price");
        MLDouble priceMLDouble = (MLDouble) priceMLArray;
        double[][] price = priceMLDouble.getArray();
        for(int i=0;i<5;i++){
            double d = price[i][0];
            System.out.println("==price =="+i +" value "+ d );
        }

        //Volume
        MLArray MLArray1 = tmpStruct0.getField("Volume");
        MLInt64 VolumeMLInt64 = (MLInt64) MLArray1;
        long[][] volume = VolumeMLInt64.getArray();
        for(int i=0;i<5;i++){
            long d = volume[i][0];
            System.out.println("==volume =="+i +" value "+ d );
        }

        //BidPrice10 （col1-5）
        MLArray bidPriceMLArray = tmpStruct0.getField("BidPrice10");
        MLDouble bidPriceMLDouble = (MLDouble) bidPriceMLArray;
        double[][] bidPrice = bidPriceMLDouble.getArray();
        for(int i=0;i<5;i++){
            double d = bidPrice[i][0];
            System.out.println("==bidPrice =="+i +" value "+ d );
        }

        //BidVolume10 （col1-5）
        MLArray bidVolumeMLArray = tmpStruct0.getField("BidVolume10");
        MLInt32 bidVolumeMLInt32 = (MLInt32) bidVolumeMLArray;
        int[][] bidVolume = bidVolumeMLInt32.getArray();
        for(int i=0;i<5;i++){
            int d = bidVolume[i][0];
            System.out.println("==bidVolume =="+i +" value "+ d );
        }


        //AskPrice10（col1-5）
        MLArray askPriceMLArray = tmpStruct0.getField("AskPrice10");
        MLDouble askPriceMLDouble = (MLDouble) askPriceMLArray;
        double[][] askPrice = askPriceMLDouble.getArray();
        for(int i=0;i<5;i++){
            double d = askPrice[i][0];
            System.out.println("==askPrice =="+i +" value "+ d );
        }

        //AskVolume10（col1-5）
        MLArray askVolumeMLArray = tmpStruct0.getField("AskVolume10");
        MLInt32 askVolumeMLInt32 = (MLInt32) askVolumeMLArray;
        int[][] askVolume = askVolumeMLInt32.getArray();
        for(int i=0;i<5;i++){
            long d = askVolume[i][0];
            System.out.println("==askVolume =="+i +" value "+ d );
        }


        System.out.println("M x axis index"+dateMLArray.getM());
        for(int i=0;i<dateMLArray.getM();i++){
        //for(int i=0;i<5;i++){ //测试前5

            StringBuilder sb = new StringBuilder();
            String dateStr = date[i][0]+"";
            String timeStr = time[i][0]+"";
            if(timeStr.length()==8){
                timeStr = "0"+timeStr; //Time 宽度对齐
            }

            //日期 20110922
            //时间：
            //H  m  s  ms
            // 9 59 57 530 注意此宽度
            //10 00 00 460
            //时间宽度需要处理
            String dateTime = dateStr+timeStr;
            //9:30-11:30 , 13：00-15:00
            String time0930 = dateStr+"093000000";
            String time1130 = dateStr+"113000000";
            String time1300 = dateStr+"130000000";
            String time1500 = dateStr+"150000000";
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

            long long930 = sf.parse(time0930).getTime();
            long long1130 = sf.parse(time1130).getTime();
            long long1300 = sf.parse(time1300).getTime();
            long long1500 = sf.parse(time1500).getTime();

            Date dateparse = sf.parse(dateTime);
            long thisTime = dateparse.getTime();
            boolean flag = (thisTime>=long930&thisTime<=long1130)||(thisTime>=long1300 & thisTime<=long1500);
            if(!flag){
                continue; //不在时间范围则丢弃本条记录
            }

            String priceStr = price[i][0]+"";
            String volumeString = volume[i][0]+"";

            //sb.append(dateTime).append("=").append(dateparse.getTime()).append(",").append(priceStr).append(",").append(volumeString);
            sb.append(dateparse.getTime()).append(",").append(priceStr).append(",").append(volumeString);
            //bidPrice 取前五列
            for(int j=0;j<5;j++){
               //String bidPriceStr = bidPrice[i][j]+"";
               sb.append(",").append(bidPrice[i][j]);

            }
            for(int j=0;j<5;j++){
                sb.append(",").append(bidVolume[i][j]);
            }
            for(int j=0;j<5;j++){
                sb.append(",").append(askPrice[i][j]);
            }
            for(int j=0;j<5;j++){
                sb.append(",").append(askVolume[i][j]);
            }

            buf.write((sb.toString()+"\n").getBytes());
        }

        System.out.println(("Processor Complete one Mat file：-----------> ").getBytes());
        buf.flush();
        buf.close();
        }
    }







