package csv.tool;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ReadByApachCsv {

    public static List<String> getStockList(String csvPath) throws IOException {
        //Reader in = new FileReader("E:\\Run\\DataProcessor\\src\\stocklistA.csv");
        Reader in = new FileReader(csvPath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        List<String> stockList = new ArrayList<>(4000);

        for (CSVRecord record : records) {
            String stockName = record.get(0);
            //stockName = stockName.substring(1,stockName.length()-1);

            stockName = stockName.replaceAll("'","");
            stockList.add(stockName);
            //System.out.println(stockName);
        }

        return stockList;
    }


}
