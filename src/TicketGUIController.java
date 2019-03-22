import com.jmatio.io.MatFileReader;
import com.jmatio.types.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
public class TicketGUIController {

    public static void main(String[] args) throws IOException {
        MatFileReader read = new MatFileReader("/home/young/Run/000004.SZ.mat");
        File file = new File("/home/young/ProjectYoung/MatFileReader/out/MatDataInfo.txt");
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream buf = new BufferedOutputStream(fos);



        Map<String,MLArray> allInOne = read.getContent(); //获取原始内容
        Set<String> keySet = allInOne.keySet(); //获取文件内容中主键集合

        buf.write(("源文件主键个数 ： "+keySet.size()+"\n").getBytes());

        for(String key :keySet){ //按照主键遍历
            buf.write(("开始遍历字段：-----------> "+key+"\n").getBytes());
            MLArray tmpAry =  allInOne.get(key); //拿到每一个主键对应的 结构体
            MLStructure tmpStruct = (MLStructure)tmpAry;
            for (String tmpName: tmpStruct.getFieldNames()) {
                MLArray tmpFieldMlAry = tmpStruct.getField(tmpName); //获取当前字段结构体
                int typeInt = tmpFieldMlAry.getType();
                String typeStr = MLArray.typeToString(typeInt);
                buf.write((String.format("字段名： %-20s 类型： %-10s",tmpName ,typeStr)+"\n").getBytes());

                if(typeStr.equalsIgnoreCase("char")){
                    MLChar mlCHar = (MLChar)tmpFieldMlAry;
                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i<tmpFieldMlAry.getM();i++){
                          sb.append(mlCHar.getString(i)).append(",");
                          break; //字符太多,后面的不打印
                    }
                    sb.append("\n");
                    buf.write(sb.toString().getBytes()); //windcode 打印较多 不便于观察
                }
                if(typeStr.equalsIgnoreCase("uint32")){
                    MLUInt32 mLUInt32 = (MLUInt32)tmpFieldMlAry;
                    int x = mLUInt32.getM(); //横向维度
                    int y = mLUInt32.getN(); //纵向唯独
                    buf.write((String.format("m %s n %s",x,y)+"\n").getBytes());
                    int[][] int32Array = mLUInt32.getArray();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i<y;i++){
                        for(int j=0;j<y;j++){
                            sb.append(int32Array[i][j]).append(",");
//                            System.out.println("uint32--> "+int32Array[i][j]);
                        }
                    }
                    sb.append("\n");
                    buf.write(sb.toString().getBytes());
                }


                if(typeStr.equalsIgnoreCase("uint64")){
                    MLUInt64 mLUInt64 = (MLUInt64)tmpFieldMlAry;
                    int x = mLUInt64.getM(); //横向维度
                    int y = mLUInt64.getN(); //纵向唯独
                    buf.write((String.format("m %s n %s",x,y)+"\n").getBytes());
                    long[][] int64Array = mLUInt64.getArray();

                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i<y;i++){
                        for(int j=0;j<y;j++){
//                            System.out.println("uint64--> "+int64Array[i][j]);
                            sb.append(int64Array[i][j]).append(",");
                        }
                    }
                    sb.append("\n");
                    buf.write(sb.toString().getBytes());
                }


                if(typeStr.equalsIgnoreCase("double")){
                    MLDouble mLDouble = (MLDouble)tmpFieldMlAry;
                    int x = mLDouble.getM(); //横向维度
                    int y = mLDouble.getN(); //纵向唯独
                    buf.write((String.format("m %s n %s",x,y)+"\n").getBytes());
                    double[][] doubleArray = mLDouble.getArray();

                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i<y;i++){
                        for(int j=0;j<y;j++){
                            sb.append(doubleArray[i][j]).append(",");
//                            System.out.println("uint64--> "+int64Array[i][j]);
                        }
                    }
                    sb.append("\n");
                    buf.write(sb.toString().getBytes());
                }
            }
            buf.write(("结束遍历字段：-----------> "+key+"\n").getBytes());
            buf.flush();
            buf.close();
        }




    }

}





