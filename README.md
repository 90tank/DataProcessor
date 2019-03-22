# DataProcessor
+ Analys matlab data and csv data

####analysis of the dir

   



####Matlab文件2016年9月之前
+ 1.从mat文件中提取date和time，转换成时间戳，
+ 2.StockTickAB.Price (col 1 只有一列)
+ 3.StockTickAB.Volume (col 1 只有一列)
+ 4-8.StockTickAB.BidPrice10 （col1-5）
+ 9-13.StockTickAB.BidVolume10 （col1-5）
+ 14-18.StockTickAB.AskPrice10（col1-5）
+ 19-23．StockTickAB.AskVolume10（col1-5）


####2016年九月之后 
+ 第一列：csv文件中提取date和time，转换成时间戳，

```
2016年九月之后的时间按照2016/2017文件夹里取
Mat文件到20160930结束,之后的股票时间信息从 2016/2017获取
```
+ 第二列：成交价，stock文件夹里的StockTickAB.Price，2016/2017文件夹里的成交价，Stk_Tick_2016/2017文件夹里的最新价
+ 第三列：成交量，stock文件夹里的StockTickAB.Volume，2016/2017文件夹里的成交量*100，Stk_Tick_2016/2017文件夹里的成交量*100
+ 第四列到八列：买一到五价，stock文件夹里的StockTickAB.BidPrice10前五列，2016/2017文件夹里的b1价，b2价~b5价，Stk_Tick_2016/2017文件夹里的买一价，买二价~买五价
+ 第九列到十三列：买一到五量，stock文件夹里的StockTickAB.BidVolume10前五列，2016/2017文件夹里的量，b1量~b5量，Stk_Tick_2016/2017文件夹里的买一量，买二量~买五量
+ 第十四列到十八列：麦一到五价，stock文件夹里的StockTickAB.AskPrice10前五列，2016/2017文件夹里的s1价，s2价~s5价，Stk_Tick_2016/2017文件夹里的卖一价，卖二价~卖五价
+ 第十九列到二十三列：卖一到五量，stock文件夹里的StockTickAB.AskVolume10前五列，2016/2017文件夹里的量，s2量~s5量，Stk_Tick_2016/2017文件夹里的卖一量，卖二量~卖五量

####时间区间要求
+ 只获取此区间之内9:30~11.30，13：00~15:00
