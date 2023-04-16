package com.xc.utils.stock.sina;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.xc.common.ServerResponse;
import com.xc.pojo.Stock;
import com.xc.pojo.StockFutures;
import com.xc.pojo.StockIndex;
import com.xc.utils.HttpClientRequest;
import com.xc.utils.PropertiesUtil;
import com.xc.utils.redis.JsonUtil;
import com.xc.utils.redis.RedisShardedPoolUtils;
import com.xc.utils.stock.sina.vo.SinaStockMinData;
import com.xc.vo.stock.StockListVO;
import com.xc.vo.stock.StockVO;
import com.xc.vo.stock.k.MinDataVO;
import com.xc.vo.stock.k.echarts.EchartsDataVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;


public class SinaStockApi {
    public static final String sina_url = PropertiesUtil.getProperty("sina.single.stock.url");
    private static final Logger log = LoggerFactory.getLogger(SinaStockApi.class);

    public static String getSinaStock(String stockGid) {
        String sina_result = "";
        try {
            String url = sina_url + stockGid;
            if(stockGid.contains("us")){
                String novelUrl = stockGid.substring(2,stockGid.length());
                url = sina_url + String.format("gb_%s",novelUrl.toLowerCase());
            }
            sina_result = HttpClientRequest.doGet(url);
        } catch (Exception e) {
            log.error("获取股票行情出错，错误信息 = {}", e);
        }
        return sina_result.substring(sina_result.indexOf("=") + 2);
    }

    /**
     * 处理美股数据
     * @param sinaResult
     * @return
     */
    public static StockListVO assembleUsStockListVO(String sinaResult) {
        StockListVO stockListVO = new StockListVO();

        String[] hqarr = sinaResult.split(",");

        if (hqarr.length > 1) {

            stockListVO.setName(hqarr[0]);

            stockListVO.setNowPrice(hqarr[1]);

            BigDecimal chang_rate = new BigDecimal("0");
            if ((new BigDecimal(hqarr[26])).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(hqarr[1]).compareTo(new BigDecimal("0")) != 0) {

                chang_rate = (new BigDecimal(hqarr[1])).subtract(new BigDecimal(hqarr[26]));

                chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(hqarr[26]), 2, RoundingMode.HALF_UP);
            }
            stockListVO.setHcrate(chang_rate);

            stockListVO.setToday_max(hqarr[6]);

            stockListVO.setToday_min(hqarr[7]);

            stockListVO.setBusiness_amount(hqarr[10]);

            stockListVO.setBusiness_balance(hqarr[11]);

            stockListVO.setPreclose_px(hqarr[26]);

            stockListVO.setOpen_px(hqarr[5]);
        }

        return stockListVO;
    }

    /**
     * 处理香港股票数据
     * @param sinaResult
     * @return
     */
    public static StockListVO assembleHkStockListVO(String sinaResult) {
        StockListVO stockListVO = new StockListVO();

        String[] hqarr = sinaResult.split(",");

        if (hqarr.length > 1) {

            stockListVO.setName(hqarr[1]);

            stockListVO.setNowPrice(hqarr[6]);

            BigDecimal chang_rate = new BigDecimal("0");
            if ((new BigDecimal(hqarr[3])).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(hqarr[6]).compareTo(new BigDecimal("0")) != 0) {

                chang_rate = (new BigDecimal(hqarr[6])).subtract(new BigDecimal(hqarr[3]));

                chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(hqarr[3]), 2, RoundingMode.HALF_UP);
            }
            stockListVO.setHcrate(chang_rate);

            stockListVO.setToday_max(hqarr[4]);

            stockListVO.setToday_min(hqarr[5]);

            stockListVO.setBusiness_amount(hqarr[11]);

            stockListVO.setBusiness_balance(hqarr[12]);

            stockListVO.setPreclose_px(hqarr[3]);

            stockListVO.setOpen_px(hqarr[2]);
        }

        return stockListVO;
    }

    public static StockListVO assembleStockListVO(String sinaResult) {
        StockListVO stockListVO = new StockListVO();

        String[] hqarr = sinaResult.split(",");

        if (hqarr.length > 1) {

            stockListVO.setName(hqarr[0]);

            stockListVO.setNowPrice(hqarr[3]);

            BigDecimal chang_rate = new BigDecimal("0");
            if ((new BigDecimal(hqarr[2])).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(hqarr[3]).compareTo(new BigDecimal("0")) != 0) {

                chang_rate = (new BigDecimal(hqarr[3])).subtract(new BigDecimal(hqarr[2]));

                chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(hqarr[2]), 2, RoundingMode.HALF_UP);
            }
            stockListVO.setHcrate(chang_rate);

            stockListVO.setToday_max(hqarr[4]);

            stockListVO.setToday_min(hqarr[5]);

            stockListVO.setBusiness_amount(hqarr[8]);

            stockListVO.setBusiness_balance(hqarr[9]);

            stockListVO.setPreclose_px(hqarr[2]);

            stockListVO.setOpen_px(hqarr[1]);
        }

        return stockListVO;
    }

    public static StockVO assembleStockVO(String sinaResult) {
        StockVO stockVO = new StockVO();

        String[] hqarr = sinaResult.split(",");

        stockVO.setName(hqarr[0]);

        stockVO.setNowPrice(hqarr[3]);

        BigDecimal chang_rate = new BigDecimal("0");
        if ((new BigDecimal(hqarr[26])).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(hqarr[1]).compareTo(new BigDecimal("0")) != 0) {

            chang_rate = (new BigDecimal(hqarr[1])).subtract(new BigDecimal(hqarr[26]));

            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(hqarr[26]), 2, RoundingMode.HALF_UP);
        }
        stockVO.setHcrate(chang_rate);

        stockVO.setToday_max(hqarr[4]);

        stockVO.setToday_min(hqarr[5]);

        stockVO.setBusiness_amount(hqarr[8]);

        stockVO.setBusiness_balance(hqarr[9]);

        stockVO.setPreclose_px(hqarr[2]);

        stockVO.setOpen_px(hqarr[1]);

        stockVO.setBuy1(hqarr[6]);
        stockVO.setBuy2(hqarr[13]);
        stockVO.setBuy3(hqarr[15]);
        stockVO.setBuy4(hqarr[17]);
        stockVO.setBuy5(hqarr[19]);

        stockVO.setSell1(hqarr[7]);
        stockVO.setSell2(hqarr[23]);
        stockVO.setSell3(hqarr[25]);
        stockVO.setSell4(hqarr[27]);
        stockVO.setSell5(hqarr[29]);

        stockVO.setBuy1_num(hqarr[10]);
        stockVO.setBuy2_num(hqarr[12]);
        stockVO.setBuy3_num(hqarr[14]);
        stockVO.setBuy4_num(hqarr[16]);
        stockVO.setBuy5_num(hqarr[18]);

        stockVO.setSell1_num(hqarr[20]);
        stockVO.setSell2_num(hqarr[22]);
        stockVO.setSell3_num(hqarr[24]);
        stockVO.setSell4_num(hqarr[26]);
        stockVO.setSell5_num(hqarr[28]);

        return stockVO;
    }

    public static StockVO assembleHkStockVO(String sinaResult) {
        StockVO stockVO = new StockVO();

        String[] hqarr = sinaResult.split(",");

        stockVO.setName(hqarr[1]);

        stockVO.setNowPrice(hqarr[6]);

        BigDecimal chang_rate = new BigDecimal("0");
        if ((new BigDecimal(hqarr[3])).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(hqarr[6]).compareTo(new BigDecimal("0")) != 0) {

            chang_rate = (new BigDecimal(hqarr[6])).subtract(new BigDecimal(hqarr[3]));

            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(hqarr[3]), 2, RoundingMode.HALF_UP);
        }


        stockVO.setHcrate(chang_rate);

        stockVO.setToday_max(hqarr[4]);

        stockVO.setToday_min(hqarr[5]);

        stockVO.setBusiness_amount(hqarr[11]);

        stockVO.setBusiness_balance(hqarr[12]);

        stockVO.setPreclose_px(hqarr[3]);

        stockVO.setOpen_px(hqarr[2]);

        String price = hqarr[2];
        stockVO.setBuy1(randomPrice(price));
        stockVO.setBuy2(randomPrice(price));
        stockVO.setBuy3(randomPrice(price));
        stockVO.setBuy4(randomPrice(price));
        stockVO.setBuy5(randomPrice(price));

        stockVO.setSell1(randomPrice(price));
        stockVO.setSell2(randomPrice(price));
        stockVO.setSell3(randomPrice(price));
        stockVO.setSell4(randomPrice(price));
        stockVO.setSell5(randomPrice(price));

        stockVO.setBuy1_num(randomVol());
        stockVO.setBuy2_num(randomVol());
        stockVO.setBuy3_num(randomVol());
        stockVO.setBuy4_num(randomVol());
        stockVO.setBuy5_num(randomVol());

        stockVO.setSell1_num(randomVol());
        stockVO.setSell2_num(randomVol());
        stockVO.setSell3_num(randomVol());
        stockVO.setSell4_num(randomVol());
        stockVO.setSell5_num(randomVol());

        return stockVO;
    }

    public static StockVO assembleUsStockVO(String sinaResult) {
        StockVO stockVO = new StockVO();

        String[] hqarr = sinaResult.split(",");

        stockVO.setName(hqarr[0]);

        stockVO.setNowPrice(hqarr[1]);

        BigDecimal chang_rate = new BigDecimal("0");
        if ((new BigDecimal(hqarr[26])).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(hqarr[1]).compareTo(new BigDecimal("0")) != 0) {

            chang_rate = (new BigDecimal(hqarr[1])).subtract(new BigDecimal(hqarr[26]));

            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(hqarr[1]), 2, RoundingMode.HALF_UP);
        }


        stockVO.setHcrate(chang_rate);

        stockVO.setToday_max(hqarr[6]);

        stockVO.setToday_min(hqarr[7]);

        stockVO.setBusiness_amount(hqarr[10]);

        stockVO.setBusiness_balance(hqarr[11]);

        stockVO.setPreclose_px(hqarr[26]);

        stockVO.setOpen_px(hqarr[5]);

        String price = hqarr[5];
        stockVO.setBuy1(randomPrice(price));
        stockVO.setBuy2(randomPrice(price));
        stockVO.setBuy3(randomPrice(price));
        stockVO.setBuy4(randomPrice(price));
        stockVO.setBuy5(randomPrice(price));

        stockVO.setSell1(randomPrice(price));
        stockVO.setSell2(randomPrice(price));
        stockVO.setSell3(randomPrice(price));
        stockVO.setSell4(randomPrice(price));
        stockVO.setSell5(randomPrice(price));

        stockVO.setBuy1_num(randomVol());
        stockVO.setBuy2_num(randomVol());
        stockVO.setBuy3_num(randomVol());
        stockVO.setBuy4_num(randomVol());
        stockVO.setBuy5_num(randomVol());

        stockVO.setSell1_num(randomVol());
        stockVO.setSell2_num(randomVol());
        stockVO.setSell3_num(randomVol());
        stockVO.setSell4_num(randomVol());
        stockVO.setSell5_num(randomVol());

        return stockVO;
    }

    /**
     * 随机买入 卖出
     * @param price
     * @return
     */
    private static String randomPrice(String price){
        BigDecimal selldec = RandomUtil.randomBigDecimal(new BigDecimal("0.15"));
        BigDecimal sellround = NumberUtil.round(selldec, 2);
        Double pricesell = Double.valueOf(price) - sellround.doubleValue();
        return pricesell.toString();
    }

    /**
     * 随机量
     * @return
     */
    private static String randomVol(){
        return String.valueOf(RandomUtil.randomInt(0,2000));
    }

    /*期货详情转换*/
    public static StockVO assembleStockFuturesVO(String sinaResult) {
        StockVO stockVO = new StockVO();

        String[] hqarr = sinaResult.split(",");
        //伦敦金格式不正确，特殊处理
        if(hqarr.length<=14){
            String sinaResulttemp = sinaResult.replace("\"",",1\"");
            hqarr = sinaResulttemp.split(",");
        }
        stockVO.setName(hqarr[13]);

        stockVO.setNowPrice(hqarr[0]);

        BigDecimal rates = new BigDecimal("0");
        BigDecimal b1 = new BigDecimal(hqarr[3].toString());
        BigDecimal b2 = new BigDecimal(hqarr[2].toString());
        BigDecimal b3 = b1.subtract(b2);
        String s = hqarr[14].toString();
        int index = s.indexOf("\"");
        String substring = s.substring(0, index);
        rates = b3.multiply(new BigDecimal("100")).divide(b1,2,BigDecimal.ROUND_HALF_UP);
        stockVO.setHcrate(rates);

        stockVO.setToday_max(hqarr[7]);

        stockVO.setToday_min(hqarr[8]);

        stockVO.setBusiness_amount(substring);

        stockVO.setBusiness_balance(hqarr[9]);

        stockVO.setPreclose_px(hqarr[11]);

        stockVO.setOpen_px(hqarr[10]);

        stockVO.setBuy1(hqarr[2]);
        stockVO.setBuy2("0");
        stockVO.setBuy3("0");
        stockVO.setBuy4("0");
        stockVO.setBuy5("0");

        stockVO.setSell1(hqarr[3]);
        stockVO.setSell2("0");
        stockVO.setSell3("0");
        stockVO.setSell4("0");
        stockVO.setSell5("0");

        stockVO.setBuy1_num(hqarr[10]);
        stockVO.setBuy2_num("0");
        stockVO.setBuy3_num("0");
        stockVO.setBuy4_num("0");
        stockVO.setBuy5_num("0");

        stockVO.setSell1_num(hqarr[11]);
        stockVO.setSell2_num("0");
        stockVO.setSell3_num("0");
        stockVO.setSell4_num("0");
        stockVO.setSell5_num("0");

        return stockVO;
    }


    public static ServerResponse<MinDataVO> getStockMinK(Stock stock, int time, int ma, int size) {
        int maxSize = Integer.parseInt(PropertiesUtil.getProperty("sina.k.min.max.size"));
        if (size > maxSize) {
            size = maxSize;
        }

        String minUrl = PropertiesUtil.getProperty("sina.k.min.url");
        minUrl = minUrl + "?symbol=" + stock.getStockGid() + "&scale=" + time + "&ma=" + ma + "&datalen=" + size;

        String hqstr = "";
        try {
            hqstr = HttpClientRequest.doGet(minUrl);
        } catch (Exception e) {
            log.error("获取股票K线分时图出错，错误信息 = {}", e);
        }

        log.info(" time = {} ma = {} size = {}", new Object[]{Integer.valueOf(time), Integer.valueOf(ma), Integer.valueOf(size)});


        hqstr = hqstr.replace("day", "\"day\"").replace("open", "\"open\"").replace("high", "\"high\"").replace("low", "\"low\"").replace("close", "\"close\"");

        if (ma == 5) {

            hqstr = hqstr.replace("ma_volume5", "\"ma_volume\"").replace(",volume", ",\"volume\"").replace("ma_price5", "\"ma_price\"");
        } else if (ma == 10) {


            hqstr = hqstr.replace("ma_volume10", "\"ma_volume\"").replace(",volume", ",\"volume\"").replace("ma_price10", "\"ma_price\"");
        } else if (ma == 15) {

            hqstr = hqstr.replace("ma_volume15", "\"ma_volume\"").replace(",volume", ",\"volume\"").replace("ma_price15", "\"ma_price\"");
        } else {
            return ServerResponse.createByErrorMsg("ma 取值 5，10，15");
        }


        if (StringUtils.isBlank(hqstr)) {
            return ServerResponse.createByErrorMsg("没有查询到行情数据");
        }

        MinDataVO minDataVO = new MinDataVO();
        minDataVO.setStockName(stock.getStockName());
        minDataVO.setStockCode(stock.getStockCode());
        minDataVO.setGid(stock.getStockGid());

        hqstr = hqstr.replaceAll("\"\"", "\"");

        List<SinaStockMinData> list = (List<SinaStockMinData>)JsonUtil.string2Obj(hqstr, new TypeReference<List<SinaStockMinData>>(){});
        log.info("需要查询的行情size为： {}", Integer.valueOf(size));

        minDataVO.setData(list);

        return ServerResponse.createBySuccess(minDataVO);
    }

    /*期货分时-k线
     * stock：期货代码
     * time：5、15、30、60，单位分钟
     * */
    public static ServerResponse<MinDataVO> getFuturesMinK(StockFutures stock, int time, int size) {
        String minUrl = PropertiesUtil.getProperty("sina.futures.k.min.url").replace("{code}",stock.getFuturesCode()).replace("{time}",String.valueOf(time));
        String stamp = String.valueOf(new Date().getTime());// new Date()为获取当前系统时间
        minUrl = minUrl.replace("{stamp}",stamp);

        String hqstr = "";
        try {
            hqstr = HttpClientRequest.doGet(minUrl);
        } catch (Exception e) {
            log.error("获取股票K线分时图出错，错误信息 = {}", e);
        }

        log.info("期货分时 - time = {} ", time);

        hqstr = hqstr.split("\\[")[1].replace("]);","");
        hqstr = hqstr.replace("d","\"day\"");
        hqstr = hqstr.replace("o","\"open\"");
        hqstr = hqstr.replace("h","\"high\"");
        hqstr = hqstr.replace("l","\"low\"");
        hqstr = hqstr.replace("c","\"close\"");
        hqstr = hqstr.replace("v","\"volume\"");

        if (StringUtils.isBlank(hqstr)) {
            return ServerResponse.createByErrorMsg("没有查询到行情数据");
        }

        MinDataVO minDataVO = new MinDataVO();
        minDataVO.setStockName(stock.getFuturesName());
        minDataVO.setStockCode(stock.getFuturesCode());
        minDataVO.setGid(stock.getFuturesGid());

        hqstr = hqstr.replaceAll("\"\"", "\"");
        hqstr = "[" + hqstr + "]";

        List<SinaStockMinData> list = (List<SinaStockMinData>)JsonUtil.string2Obj(hqstr, new TypeReference<List<SinaStockMinData>>(){});
        //int size = Integer.valueOf(PropertiesUtil.getProperty("sina.futures.k.min.max.size"));
        if(list.size()>size){
            list = list.subList((list.size()-size-1),list.size());
        }
        minDataVO.setData(list);
        return ServerResponse.createBySuccess(minDataVO);
    }

    public static EchartsDataVO assembleEchartsDataVO(MinDataVO minDataVO) {
        EchartsDataVO echartsDataVO = new EchartsDataVO();
        echartsDataVO.setStockName(minDataVO.getStockName());
        echartsDataVO.setStockCode(minDataVO.getStockCode());

        List<SinaStockMinData> minDataList = minDataVO.getData();


        double[][] values = (double[][]) null;
        Object[][] volumes = (Object[][]) null;
        String[] date = null;

        if (minDataList.size() > 0) {

            values = new double[minDataList.size()][5];

            volumes = new Object[minDataList.size()][3];

            date = new String[minDataList.size()];

            for (int i = 0; i < minDataList.size(); i++) {
                SinaStockMinData sinaStockMinData = (SinaStockMinData) minDataList.get(i);

                for (int j = 0; j < values[i].length; j++) {
                    values[i][0] = Double.valueOf(sinaStockMinData.getOpen()).doubleValue();
                    values[i][1] = Double.valueOf(sinaStockMinData.getClose()).doubleValue();
                    values[i][2] = Double.valueOf(sinaStockMinData.getLow()).doubleValue();
                    values[i][3] = Double.valueOf(sinaStockMinData.getHigh()).doubleValue();
                    values[i][4] = Double.valueOf(sinaStockMinData.getVolume()).doubleValue();
                }
                for (int k = 0; k < volumes[i].length; k++) {
                    volumes[i][0] = Integer.valueOf(i);
                    volumes[i][1] = Double.valueOf(sinaStockMinData.getVolume());

                    if ((new BigDecimal(sinaStockMinData.getClose()))
                            .compareTo(new BigDecimal(sinaStockMinData.getOpen())) == 1) {
                        volumes[i][2] = Integer.valueOf(1);
                    } else {
                        volumes[i][2] = Integer.valueOf(-1);
                    }
                }

                date[i] = sinaStockMinData.getDay();
            }
        }

        echartsDataVO.setValues(values);
        echartsDataVO.setVolumes(volumes);
        echartsDataVO.setDate(date);

        return echartsDataVO;
    }

    /*股票日线*/
    public static ServerResponse<MinDataVO> getStockDayK(Stock stock, int time, int ma, int size) {
        int maxSize = Integer.parseInt(PropertiesUtil.getProperty("sina.k.min.max.size"));
        if (size > maxSize) {
            size = maxSize;
        }

        String minUrl = PropertiesUtil.getProperty("sina.k.min.url");
        minUrl = minUrl + "?symbol=" + stock.getStockGid() + "&scale=" + time + "&ma=" + ma + "&datalen=" + size;

        String hqstr = "";
        try {
            hqstr = HttpClientRequest.doGet(minUrl);
        } catch (Exception e) {
            log.error("获取股票K线分时图出错，错误信息 = {}", e);
        }

        log.info(" time = {} ma = {} size = {}", new Object[]{Integer.valueOf(time), Integer.valueOf(ma), Integer.valueOf(size)});


        hqstr = hqstr.replace("day", "\"day\"").replace("open", "\"open\"").replace("high", "\"high\"").replace("low", "\"low\"").replace("close", "\"close\"");

        if (ma == 5) {

            hqstr = hqstr.replace("ma_volume5", "\"ma_volume\"").replace(",volume", ",\"volume\"").replace("ma_price5", "\"ma_price\"");
        } else if (ma == 10) {


            hqstr = hqstr.replace("ma_volume10", "\"ma_volume\"").replace(",volume", ",\"volume\"").replace("ma_price10", "\"ma_price\"");
        } else if (ma == 15) {

            hqstr = hqstr.replace("ma_volume15", "\"ma_volume\"").replace(",volume", ",\"volume\"").replace("ma_price15", "\"ma_price\"");
        } else {
            return ServerResponse.createByErrorMsg("ma 取值 5，10，15");
        }


        if (StringUtils.isBlank(hqstr)) {
            return ServerResponse.createByErrorMsg("没有查询到行情数据");
        }

        MinDataVO minDataVO = new MinDataVO();
        minDataVO.setStockName(stock.getStockName());
        minDataVO.setStockCode(stock.getStockCode());
        minDataVO.setGid(stock.getStockGid());

        hqstr = hqstr.replaceAll("\"\"", "\"");

        List<SinaStockMinData> list = (List<SinaStockMinData>)JsonUtil.string2Obj(hqstr, new TypeReference<List<SinaStockMinData>>(){});
        log.info("需要查询的行情size为： {}", Integer.valueOf(size));

        minDataVO.setData(list);

        return ServerResponse.createBySuccess(minDataVO);
    }

    /*指数分时-k线
     * stock：指数代码
     * time：5、15、30、60，单位分钟
     * */
    public static ServerResponse<MinDataVO> getIndexMinK(StockIndex stock, int time, int size) {
        String minUrl = PropertiesUtil.getProperty("sina.index.k.min.url").replace("{code}",stock.getIndexGid()).replace("{time}",String.valueOf(time));
        String stamp = String.valueOf(new Date().getTime());// new Date()为获取当前系统时间
        minUrl = minUrl.replace("{stamp}",stamp);

        String hqstr = "";
        try {
            hqstr = HttpClientRequest.doGet(minUrl);
        } catch (Exception e) {
            log.error("获取股票K线分时图出错，错误信息 = {}", e);
        }

        log.info("期货分时 - time = {} ", time);

        hqstr = hqstr.split("\\[")[1].replace("]);","");
        /*hqstr = hqstr.replace("d","\"day\"");
        hqstr = hqstr.replace("o","\"open\"");
        hqstr = hqstr.replace("h","\"high\"");
        hqstr = hqstr.replace("l","\"low\"");
        hqstr = hqstr.replace("c","\"close\"");
        hqstr = hqstr.replace("v","\"volume\"");*/

        if (StringUtils.isBlank(hqstr)) {
            return ServerResponse.createByErrorMsg("没有查询到行情数据");
        }

        MinDataVO minDataVO = new MinDataVO();
        minDataVO.setStockName(stock.getIndexName());
        minDataVO.setStockCode(stock.getIndexCode());
        minDataVO.setGid(stock.getIndexGid());

        hqstr = hqstr.replaceAll("\"\"", "\"");
        hqstr = "[" + hqstr + "]";

        List<SinaStockMinData> list = (List<SinaStockMinData>)JsonUtil.string2Obj(hqstr, new TypeReference<List<SinaStockMinData>>(){});
        //int size = Integer.valueOf(PropertiesUtil.getProperty("sina.index.k.min.max.size"));
        if(list.size()>size){
            list = list.subList((list.size()-size-1),list.size());
        }
        minDataVO.setData(list);
        return ServerResponse.createBySuccess(minDataVO);
    }


    public static void main(String[] args) {
//        List list = Lists.newArrayList();
//        list.add(Integer.valueOf(1));
//        list.add(Integer.valueOf(2));
//        list.add(Integer.valueOf(3));
//        System.out.println(list.size());
//
//        String[][] values = new String[list.size()][5];
//
//        System.out.println("[]" + values.length);
//        System.out.println("[][]" + values[1].length);
//
//        System.out.println(getSinaStock("sh601318"));
//
//        String sss = "[\n{\n\"day\": \"2019-03-05 14:50:00\",\n\"open\": \"13.020\",\n\"high\": \"13.040\",\n\"low\": \"13.000\",\n\"close\": \"13.040\",\n\"volume\": \"2611513\",\n\"ma_price5\": 13.01,\n\"ma_volume5\": 3216535\n},\n{\n\"day\": \"2019-03-05 14:55:00\",\n\"open\": \"13.040\",\n\"high\": \"13.040\",\n\"low\": \"13.010\",\n\"close\": \"13.030\",\n\"volume\": \"2296000\",\n\"ma_price5\": 13.016,\n\"ma_volume5\": 3044839\n}\n]";
//
//        sss = sss.substring(1, sss.length() - 1);
//
//        sss = "{" + sss + "}";
        getSinaStock("s_sh600090");
    }

    public static StockListVO getVietNamData(String type, String stockGid) {
        String result = "";
        StockListVO data = new StockListVO();
        try {
//            String url = "https://ezir.fpts.com.vn/ThongTinDoanhNghiep/PriceRealTime?stockCode="+stockGid;
//            result = HttpClientRequest.doGet(url);
//
            result = RedisShardedPoolUtils.get("data-cache-"+stockGid);
            if(null == result){
                return data;
            }
            JSONObject data2 = JSONObject.fromObject(result);
            data.setName(data2.getString("code"));
            data.setNowPrice(data2.getString("matchPrice"));
            data.setHcrate(BigDecimal.valueOf(data2.getDouble("changePrice")));
            data.setToday_max(data2.getString("ceiling"));
            data.setToday_min(data2.getString("floor"));
            data.setBusiness_amount(data2.getString("totalQtty").replaceAll(",",""));
            data.setOpen_px(data2.getString("refPrice"));
            data.setPreclose_px(data2.getString("lowestPrice"));
        } catch (Exception e) {
            log.error("鑾峰彇鑲＄エ琛屾儏鍑洪敊锛岄敊璇¯淇℃伅 = {}", e);
        }
        return data;
    }
}

