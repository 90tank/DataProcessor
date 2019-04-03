package file.getter;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.*;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MatColGetter {
    private static Logger LOGGER = LogManager.getLogger(MatColGetter.class.getName());
    public static void matReader(File aimStock,File resultFile) throws IOException, ParseException {
        String aimStockPath = aimStock.getAbsolutePath();
        LOGGER.info("Current StockPath "+aimStockPath);

        MatFileReader read = null;
        try {
            read = new MatFileReader(aimStockPath);
        } catch (IOException e) {
            LOGGER.error("MatlabIOException FileName : "+aimStockPath+"\n " + e );
            return ;
        } catch (Throwable t){
            LOGGER.error("Throwable FileName : "+aimStockPath+"\n " + t.getMessage());
            LOGGER.error("Throwable FileName : "+aimStockPath+"\n " + t.getCause());
            return ;
        }

        if(!resultFile.exists()){
            resultFile.createNewFile();
            System.out.println("create an empty result file ");
        }
        //输出流  重复打开输出结果文件 可优化
        FileOutputStream fos = new FileOutputStream(resultFile,true); //追加模式
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

        //先将一个文件中对应的列拿出来 放到对应的列数组

        //Date
        MLArray dateMLArray = tmpStruct0.getField("Date");
        MLInt32 dateMLInt32 = (MLInt32) dateMLArray;
        int[][] date = dateMLInt32.getArray();
        //Time
        MLArray timeMLArray = tmpStruct0.getField("Time");
        MLInt32 timeMLInt32 = (MLInt32) timeMLArray;
        int[][] time = timeMLInt32.getArray();
        //Price
        MLArray priceMLArray = tmpStruct0.getField("Price");
        MLDouble priceMLDouble = (MLDouble) priceMLArray;
        double[][] price = priceMLDouble.getArray();
        //Volume
        MLArray MLArray1 = tmpStruct0.getField("Volume");
        MLInt64 VolumeMLInt64 = (MLInt64) MLArray1;
        long[][] volume = VolumeMLInt64.getArray();

        //BidPrice10 （col1-5）
        MLArray bidPriceMLArray = tmpStruct0.getField("BidPrice10");
        MLDouble bidPriceMLDouble = (MLDouble) bidPriceMLArray;
        double[][] bidPrice = bidPriceMLDouble.getArray();

        //BidVolume10 （col1-5）
        MLArray bidVolumeMLArray = tmpStruct0.getField("BidVolume10");
        MLInt32 bidVolumeMLInt32 = (MLInt32) bidVolumeMLArray;
        int[][] bidVolume = bidVolumeMLInt32.getArray();

        //AskPrice10（col1-5）
        MLArray askPriceMLArray = tmpStruct0.getField("AskPrice10");
        MLDouble askPriceMLDouble = (MLDouble) askPriceMLArray;
        double[][] askPrice = askPriceMLDouble.getArray();
        //AskVolume10（col1-5）
        MLArray askVolumeMLArray = tmpStruct0.getField("AskVolume10");
        MLInt32 askVolumeMLInt32 = (MLInt32) askVolumeMLArray;
        int[][] askVolume = askVolumeMLInt32.getArray();

        for(int i=0;i<dateMLArray.getM();i++){

            StringBuilder sb = new StringBuilder();
            String dateStr = date[i][0]+"";
            String originTimeStr = time[i][0]+"";
            String timeStr = "";
           // System.out.println("date: "+dateStr+" time: "+timeStr);
            if(originTimeStr.length()==8){
                timeStr = "0"+originTimeStr; //Time 宽度对齐
            }else{
                timeStr = originTimeStr ;
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
            //yyyy MM dd HH mmssSSS
            //2010 01 04 3 0 0 00
            long long930 = sf.parse(time0930).getTime();
            long long1130 = sf.parse(time1130).getTime();
            long long1300 = sf.parse(time1300).getTime();
            long long1500 = sf.parse(time1500).getTime();

            Date dateparse = null;
            try {
                dateparse = sf.parse(dateTime);
            } catch (ParseException e) {
                //如果日期转换异常则不要这一条数据
                LOGGER.error("data parse error  date :"+dateStr +" time :"+originTimeStr);
                continue;
            }



            long thisTime = dateparse.getTime(); //时间戳 这一行的时间
            boolean flag = (thisTime>=long930&thisTime<=long1130)||(thisTime>=long1300 & thisTime<=long1500);
            if(!flag){
                continue; //不在时间范围则丢弃本条记录
            }

            //时间 （变体时间）
            //Matlab 中 693963 是1900-01-01  对应 c++ 2.0
            //Matlab 中 1      是0000-01-01

            double VariantTime = SystemTimeToVarinantTime(thisTime);
            VariantTime +=  693960 ;

            //成交价
            double pri = price[i][0];
            //成交量 要求乘以100
            long vol = volume[i][0]*100 ;

            sb.append(VariantTime).append(",").append(pri).append(",").append(vol);

            // 买一到五价 bidPrice 取前五列
            for(int j=0;j<5;j++){
               sb.append(",").append(bidPrice[i][j]);

            }
            //买一到五量
            for(int j=0;j<5;j++){
                sb.append(",").append(bidVolume[i][j]);
            }
            //卖一到五价
            for(int j=0;j<5;j++){
                sb.append(",").append(askPrice[i][j]);
            }
            //卖一到五量
            for(int j=0;j<5;j++){
                sb.append(",").append(askVolume[i][j]);
            }
            //写入文件  换行
            buf.write((sb.toString()+"\n").getBytes());
        }

        LOGGER.info("Complete Current StockPath "+aimStockPath);
        buf.flush();
        buf.close();
        fos.close();
        }


    /**
     * 转换方法
     * @param timeStamp
     * @return
     */
    public static double SystemTimeToVarinantTime(long timeStamp){
        //实例化timeStamp 为一个日历
        //Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //东八区
        Calendar calendar= timeStampToCalendar(timeStamp);

        //时间结构体 c++方法入参
        CLibrary.SYSTEMTIME sysTime = new CLibrary.SYSTEMTIME();
        //返回值 变体时间
        DoubleByReference pvtime = new DoubleByReference();

        sysTime.wYear =(short) (calendar.get(Calendar.YEAR));
        sysTime.wMonth = (short)(calendar.get(Calendar.MONTH)+1); // month + 1
        sysTime.wDayOfWeek = (short)calendar.get(Calendar.DAY_OF_WEEK);
        sysTime.wDay =  (short)calendar.get(Calendar.DAY_OF_MONTH);
        sysTime.wHour =  (short)calendar.get(Calendar.HOUR_OF_DAY);
        sysTime.wMinute = (short)calendar.get(Calendar.MINUTE);
        sysTime.wSecond =  (short)calendar.get(Calendar.SECOND);
        sysTime.wMilliseconds =  (short)calendar.get(Calendar.MILLISECOND);

        //转换方法
        CLibrary.INSTANCE.SystemTimeToVariantTime(sysTime,pvtime);
        return pvtime.getValue();
    }


    public static Calendar timeStampToCalendar(long timeStamp) {
        Date date = new Date(timeStamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }




    public static interface CLibrary extends Library {

        @Structure.FieldOrder({ "wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds" })
        public static class SYSTEMTIME extends Structure {
            public short wYear;
            public short wMonth;
            public short wDayOfWeek;
            public short wDay;
            public short wHour;
            public short wMinute;
            public short wSecond;
            public short wMilliseconds;
        }
        CLibrary INSTANCE = (CLibrary)
                Native.loadLibrary("oleaut32", CLibrary.class);
        int SystemTimeToVariantTime(SYSTEMTIME systemtime, DoubleByReference pvtime);
        int VariantTimeToSystemTime(double vtime, SYSTEMTIME lpSystemTime);
    }

    }