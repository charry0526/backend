package com.xc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xc.common.ServerResponse;
import com.xc.config.StockPoll;
import com.xc.dao.RealTimeMapper;
import com.xc.dao.StockFuturesMapper;
import com.xc.dao.StockIndexMapper;
import com.xc.dao.StockMapper;
import com.xc.pojo.Stock;
import com.xc.pojo.StockFutures;
import com.xc.pojo.StockIndex;
import com.xc.pojo.User;
import com.xc.service.*;
import com.xc.utils.HttpClientRequest;
import com.xc.utils.PropertiesUtil;
import com.xc.utils.stock.pinyin.GetPyByChinese;
import com.xc.utils.stock.qq.QqStockApi;
import com.xc.utils.stock.sina.SinaStockApi;
import com.xc.vo.stock.*;
import com.xc.vo.stock.k.MinDataVO;
import com.xc.vo.stock.k.echarts.EchartsDataVO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("iStockService")
public class StockServiceImpl implements IStockService {
  private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

  @Autowired
  StockMapper stockMapper;

  @Autowired
  ISiteAdminService iSiteAdminService;

  @Autowired
  RealTimeMapper realTimeMapper;

  @Autowired
  IStockMarketsDayService iStockMarketsDayService;

  @Autowired
  StockPoll stockPoll;

  @Autowired
  StockFuturesMapper stockFuturesMapper;

  @Autowired
  StockIndexMapper stockIndexMapper;

  @Autowired
  IUserService iUserService;

  @Autowired
  IStockOptionService iStockOptionService;

  public ServerResponse getMarket() {
    String market_url = PropertiesUtil.getProperty("sina.market.url");
    String result = null;
    try {
      result = HttpClientRequest.doGet(market_url);
    } catch (Exception e) {
      log.error("e = {}", e);
    }
    String[] marketArray = result.split(";");
    List<MarketVO> marketVOS = Lists.newArrayList();
    for (int i = 0; i < marketArray.length; i++) {
      String hqstr = marketArray[i];
      try {
        if (StringUtils.isNotBlank(hqstr)) {
          hqstr = hqstr.substring(hqstr.indexOf("\"") + 1, hqstr.lastIndexOf("\""));
          MarketVO marketVO = new MarketVO();
          String[] sh01_arr = hqstr.split(",");
          marketVO.setName(sh01_arr[0]);
          marketVO.setNowPrice(sh01_arr[1]);
          marketVO.setIncrease(sh01_arr[2]);
          marketVO.setIncreaseRate(sh01_arr[3]);
          marketVOS.add(marketVO);
        }
      } catch (Exception e) {
        log.error("str = {} ,  e = {}", hqstr, e);
      }
    }
    MarketVOResult marketVOResult = new MarketVOResult();
    marketVOResult.setMarket(marketVOS);
    return ServerResponse.createBySuccess(marketVOResult);
  }

  public ServerResponse getStock(int pageNum, int pageSize, String keyWords, String stockPlate, String stockType, HttpServletRequest request) {
    PageHelper.startPage(pageNum, pageSize);
    User user = iUserService.getCurrentUser(request);
    List<Stock> stockList = this.stockMapper.findStockListByKeyWords(keyWords, stockPlate, stockType, Integer.valueOf(0));
    List<StockListVO> stockListVOS = Lists.newArrayList();
    if (stockList.size() > 0)
      for (Stock stock : stockList) {

        String stype = stock.getStockType();
        String stockGid = stock.getStockGid();
        StockListVO stockListVO = findStockList(stype,stockGid);
        //过滤空数据
        if(Objects.isNull(stockListVO.getName())){
          continue;
        }

        stockListVO.setCode(stock.getStockCode());
        stockListVO.setSpell(stock.getStockSpell());
        stockListVO.setGid(stock.getStockGid());
//        BigDecimal day3Rate = (BigDecimal)selectRateByDaysAndStockCode(stock.getStockCode(), 3).getData();
//        stockListVO.setDay3Rate(day3Rate);
        stockListVO.setStock_plate(stock.getStockPlate());
        stockListVO.setStock_type(stock.getStockType());
        //是否添加自选
        if(user == null){
          stockListVO.setIsOption("0");
        } else {
          stockListVO.setIsOption(iStockOptionService.isMyOption(user.getId(), stock.getStockCode()));
        }
        stockListVOS.add(stockListVO);
      }
    PageInfo pageInfo = new PageInfo(stockList);
    pageInfo.setList(stockListVOS);
    return ServerResponse.createBySuccess(pageInfo);
  }

  public void z1() {
    this.stockPoll.z1();
  }
  public void z11() {
    this.stockPoll.z11();
  }
  public void z12() {
    this.stockPoll.z12();
  }

  public void z2() {
    this.stockPoll.z2();
  }
  public void z21() {
    this.stockPoll.z21();
  }
  public void z22() {
    this.stockPoll.z22();
  }

  public void z3() {
    this.stockPoll.z3();
  }
  public void z31() {
    this.stockPoll.z31();
  }
  public void z32() {
    this.stockPoll.z32();
  }

  public void z4() {
    this.stockPoll.z4();
  }
  public void z41() {
    this.stockPoll.z41();
  }
  public void z42() {
    this.stockPoll.z42();
  }

  public void h1() {
    this.stockPoll.h1();
  }
  public void h11() {
    this.stockPoll.h11();
  }
  public void h12() {
    this.stockPoll.h12();
  }

  public void h2() {
    this.stockPoll.h2();
  }
  public void h21() {
    this.stockPoll.h21();
  }
  public void h22() {
    this.stockPoll.h22();
  }

  public void h3() {
    this.stockPoll.h3();
  }
  public void h31() {
    this.stockPoll.h31();
  }
  public void h32() {
    this.stockPoll.h32();
  }

  public ServerResponse getDateline(HttpServletResponse response, String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.findStockByCode(code,0);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    response.setHeader("Access-Control-Allow-Origin", "*");
    Date time = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String end = sdf.format(time);
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(2, -3);
    Date m = c.getTime();
    String mon = sdf.format(m);
    String methodUrl = "http://q.stock.sohu.com/hisHq?code=cn_" + code + "+&start=" + mon + "&end=" + end + "&stat=1&order=D";
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    String line = null;
    EchartsDataVO echartsDataVO = new EchartsDataVO();
    try {
      URL url = new URL(methodUrl);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      if (connection.getResponseCode() == 200) {
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null)
          result.append(line).append(System.getProperty("line.separator"));
        JSONArray jsonArray = JSONArray.fromObject(result.toString());
        JSONObject json = jsonArray.getJSONObject(0);
        JSONArray jsonArray1 = JSONArray.fromObject(json.get("hq"));
        Collections.reverse((List<?>)jsonArray1);
        double[][] values = (double[][])null;
        Object[][] volumes = (Object[][])null;
        String[] date = null;
        values = new double[jsonArray1.size()][5];
        volumes = new Object[jsonArray1.size()][3];
        date = new String[jsonArray1.size()];
        for (int i = 0; i < jsonArray1.size(); i++) {
          JSONArray js = JSONArray.fromObject(jsonArray1.get(i));
          date[i] = js.get(0).toString();
          values[i][0] = Double.valueOf(js.get(1).toString()).doubleValue();
          values[i][1] = Double.valueOf(js.get(2).toString()).doubleValue();
          values[i][2] = Double.valueOf(js.get(5).toString()).doubleValue();
          values[i][3] = Double.valueOf(js.get(6).toString()).doubleValue();
          values[i][4] = Double.valueOf(js.get(7).toString()).doubleValue();
          volumes[i][0] = Integer.valueOf(i);
          volumes[i][1] = Double.valueOf(js.get(7).toString());
          volumes[i][2] = Integer.valueOf((Double.valueOf(js.get(3).toString()).doubleValue() > 0.0D) ? 1 : -1);
        }
        echartsDataVO.setDate(date);
        echartsDataVO.setValues(values);
        echartsDataVO.setVolumes(volumes);
        echartsDataVO.setStockCode(stock.getStockCode());
        echartsDataVO.setStockName(stock.getStockName());
        log.info(String.valueOf(echartsDataVO));
        ServerResponse.createBySuccess(echartsDataVO);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      connection.disconnect();
    }
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  public ServerResponse getSingleStock(String code,String isNew) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    Stock stock = new Stock();
    Integer depositAmt = 0;
    //期货
    if(code.contains("hf")){
      StockFutures futmodel = stockFuturesMapper.selectFuturesByCode(code.replace("hf_",""));
      stock.setStockGid(futmodel.getFuturesGid());
      stock.setStockCode(futmodel.getFuturesCode());
      stock.setStockName(futmodel.getFuturesName());
      stock.setAddTime(futmodel.getAddTime());
      stock.setId(futmodel.getId());
      stock.setStockSpell("0");
      depositAmt = futmodel.getDepositAmt();
    } else if(code.contains("sh") || code.contains("sz")){ //指数
      StockIndex model = stockIndexMapper.selectIndexByCode(code.replace("sh","").replace("sz",""));
      stock.setStockGid(model.getIndexGid());
      stock.setStockCode(model.getIndexCode());
      stock.setStockName(model.getIndexName());
      stock.setAddTime(model.getAddTime());
      stock.setId(model.getId());
      stock.setStockSpell("0");
      depositAmt = model.getDepositAmt();
    } else {//股票
      int flag = 0;
      if(!org.springframework.util.StringUtils.isEmpty(isNew)){
        flag = 1;
      }
      stock = this.stockMapper.findStockByCode(code,flag);
    }
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    String gid = stock.getStockGid();
//    String sinaResult = SinaStockApi.getSinaStock(gid);
    StockVO stockVO = new StockVO();
//    if(code.contains("hf")){
//      stockVO = SinaStockApi.assembleStockFuturesVO(sinaResult);
//    } else if("hk".equals(stock.getStockType())){
//      stockVO = SinaStockApi.assembleHkStockVO(sinaResult);
//    } else if("us".equals(stock.getStockType())){
//      stockVO = SinaStockApi.assembleUsStockVO(sinaResult);
//    } else {
//      stockVO = SinaStockApi.assembleStockVO(sinaResult);
//    }
    StockListVO cacheData = SinaStockApi.getVietNamData(stock.getStockType(), code);
    stockVO.setToday_max(cacheData.getToday_max());
    stockVO.setToday_min(cacheData.getToday_min());
    stockVO.setHcrate(cacheData.getHcrate());

    String price = "";
    if(!org.springframework.util.StringUtils.isEmpty(isNew)){
      String esopPrice = this.iSiteAdminService.getEsopPriceByCode(code);
      price = esopPrice;
    }else{
      price = cacheData.getNowPrice();
    }
    stockVO.setNowPrice(price);
    stockVO.setBusiness_amount(cacheData.getBusiness_amount());
    stockVO.setPreclose_px(cacheData.getPreclose_px());
    stockVO.setOpen_px(cacheData.getOpen_px());

    stockVO.setDepositAmt(depositAmt);
    stockVO.setId(stock.getId().intValue());
    stockVO.setCode(code);
    stockVO.setSpell(stock.getStockSpell());
    stockVO.setGid(stock.getStockGid());
    stockVO.setMinImg(PropertiesUtil.getProperty("sina.single.stock.min.url") + stock.getStockGid() + ".jpg");
    stockVO.setDayImg(PropertiesUtil.getProperty("sina.single.stock.day.url") + stock.getStockGid() + ".jpg");
    stockVO.setWeekImg(PropertiesUtil.getProperty("sina.single.stock.week.url") + stock.getStockGid() + ".jpg");
    stockVO.setMonthImg(PropertiesUtil.getProperty("sina.single.stock.month.url") + stock.getStockGid() + ".jpg");
    //StockListVO cacheData1 = SinaStockApi.getVietNamData(code.toUpperCase(), code.toUpperCase());
    //if(!org.springframework.util.StringUtils.isEmpty(cacheData1)){
    //  double d = Double.parseDouble(cacheData1.getNowPrice());
    //  double r = d * 1000;
    //  int rr = (int) r;
    //  cacheData1.setNowPrice(String.valueOf(rr));
    //}
    return ServerResponse.createBySuccess(stockVO);
  }


  public ServerResponse getMinK(String code, Integer time, Integer ma, Integer size) {
    if (StringUtils.isBlank(code) || time == null || ma == null || size == null)
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.findStockByCode(code,0);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    return SinaStockApi.getStockMinK(stock, time.intValue(), ma.intValue(), size.intValue());
  }

  public ServerResponse getMinK_Echarts(String code, Integer time, Integer ma, Integer size) {
    if (StringUtils.isBlank(code) || time == null || ma == null || size == null)
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.findStockByCode(code,0);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = SinaStockApi.getStockMinK(stock, time.intValue(), ma.intValue(), size.intValue());
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*期货分时-k线*/
  public ServerResponse getFuturesMinK_Echarts(String code, Integer time, Integer size) {
    if (StringUtils.isBlank(code) || time == null)
      return ServerResponse.createByErrorMsg("");
    StockFutures stock = this.stockFuturesMapper.selectFuturesByCode(code.split("_")[1]);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = SinaStockApi.getFuturesMinK(stock, time.intValue(), size.intValue());
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*指数分时-k线*/
  public ServerResponse getIndexMinK_Echarts(String code, Integer time, Integer size) {
    if (StringUtils.isBlank(code) || time == null)
      return ServerResponse.createByErrorMsg("");
    StockIndex stock = this.stockIndexMapper.selectIndexByCode(code.replace("sh","").replace("sz",""));
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = SinaStockApi.getIndexMinK(stock, time.intValue(), size.intValue());
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*股票日线-K线*/
  public ServerResponse getDayK_Echarts(String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.findStockByCode(code,0);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = QqStockApi.getGpStockMonthK(stock,"day");
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*期货日线-K线*/
  public ServerResponse getFuturesDayK(String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    StockFutures stock = this.stockFuturesMapper.selectFuturesByCode(code.split("_")[1]);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = QqStockApi.getQqStockDayK(stock);
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*指数日线-K线*/
  public ServerResponse getIndexDayK(String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    StockIndex stock = this.stockIndexMapper.selectIndexByCode(code.replace("sh","").replace("sz",""));
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = QqStockApi.getQqIndexDayK(stock);
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  public ServerResponse<Stock> findStockByName(String name) {
    return ServerResponse.createBySuccess(this.stockMapper.findStockByName(name));
  }

  public ServerResponse<Stock> findStockByCode(String code) {
    return ServerResponse.createBySuccess(this.stockMapper.findStockByCode(code,0));
  }

  public ServerResponse<Stock> findStockById(Integer stockId) {
    return ServerResponse.createBySuccess(this.stockMapper.selectByPrimaryKey(stockId));
  }

  public ServerResponse<PageInfo> listByAdmin(Integer showState, Integer lockState, String code, String name, String stockPlate, String stockType, int pageNum, int pageSize, HttpServletRequest request) {
    PageHelper.startPage(pageNum, pageSize);
    List<Stock> stockList = this.stockMapper.listByAdmin(showState, lockState, code, name, stockPlate, stockType);
    List<StockAdminListVO> stockAdminListVOS = Lists.newArrayList();
    for (Stock stock : stockList) {
      StockAdminListVO stockAdminListVO = assembleStockAdminListVO(stock);
      stockAdminListVOS.add(stockAdminListVO);
    }
    PageInfo pageInfo = new PageInfo(stockList);
    pageInfo.setList(stockAdminListVOS);
    return ServerResponse.createBySuccess(pageInfo);
  }

  private StockAdminListVO assembleStockAdminListVO(Stock stock) {
    StockAdminListVO stockAdminListVO = new StockAdminListVO();
    stockAdminListVO.setId(stock.getId());
    stockAdminListVO.setStockName(stock.getStockName());
    stockAdminListVO.setStockCode(stock.getStockCode());
    stockAdminListVO.setStockSpell(stock.getStockSpell());
    stockAdminListVO.setStockType(stock.getStockType());
    stockAdminListVO.setStockGid(stock.getStockGid());
    stockAdminListVO.setStockPlate(stock.getStockPlate());
    stockAdminListVO.setIsLock(stock.getIsLock());
    stockAdminListVO.setIsShow(stock.getIsShow());
    stockAdminListVO.setAddTime(stock.getAddTime());

    String stype = stock.getStockType();
    String stockGid = stock.getStockGid();
    StockListVO stockListVO = findStockList(stype,stockGid);

    stockAdminListVO.setNowPrice(stockListVO.getNowPrice());
    stockAdminListVO.setHcrate(stockListVO.getHcrate());
    stockAdminListVO.setSpreadRate(stock.getSpreadRate());
    ServerResponse serverResponse = selectRateByDaysAndStockCode(stock.getStockCode(), 3);
    BigDecimal day3Rate = new BigDecimal("0");
    if (serverResponse.isSuccess())
      day3Rate = (BigDecimal)serverResponse.getData();
    stockAdminListVO.setDay3Rate(day3Rate);
    return stockAdminListVO;
  }

  public ServerResponse updateLock(Integer stockId) {
    Stock stock = this.stockMapper.selectByPrimaryKey(stockId);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    if (stock.getIsLock().intValue() == 1) {
      stock.setIsLock(Integer.valueOf(0));
    } else {
      stock.setIsLock(Integer.valueOf(1));
    }
    int updateCount = this.stockMapper.updateByPrimaryKeySelective(stock);
    if (updateCount > 0)
      return ServerResponse.createBySuccessMsg("");
    return ServerResponse.createByErrorMsg("");
  }

  public ServerResponse updateShow(Integer stockId) {
    Stock stock = this.stockMapper.selectByPrimaryKey(stockId);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    if (stock.getIsShow().intValue() == 0) {
      stock.setIsShow(Integer.valueOf(1));
    } else {
      stock.setIsShow(Integer.valueOf(0));
    }
    int updateCount = this.stockMapper.updateByPrimaryKeySelective(stock);
    if (updateCount > 0)
      return ServerResponse.createBySuccessMsg("");
    return ServerResponse.createByErrorMsg("");
  }

  public ServerResponse addStock(String stockName, String stockCode, String stockType, String stockPlate, Integer isLock, Integer isShow) {
    if (StringUtils.isBlank(stockName) || StringUtils.isBlank(stockCode) || StringUtils.isBlank(stockType) || isLock == null || isShow == null)
      return ServerResponse.createByErrorMsg("");
    Stock cstock = (Stock)findStockByCode(stockCode).getData();
    if (cstock != null)
      return ServerResponse.createByErrorMsg("");
    Stock nstock = (Stock)findStockByName(stockName).getData();
    if (nstock != null)
      return ServerResponse.createByErrorMsg("");
    Stock stock = new Stock();
    stock.setStockName(stockName);
    stock.setStockCode(stockCode);
    stock.setStockSpell(GetPyByChinese.converterToFirstSpell(stockName));
    stock.setStockType(stockType);
    stock.setStockGid(stockType + stockCode);
    stock.setIsLock(isLock);
    stock.setIsShow(isShow);
    stock.setAddTime(new Date());
    if (stockPlate != null)
      stock.setStockPlate(stockPlate);

//    if(stockPlate != null && StringUtils.isNotEmpty(stockPlate) && stockCode.startsWith("300"))
//      stock.setStockPlate("创业");
//    else if(stockPlate != null && StringUtils.isNotEmpty(stockPlate) &&stockCode.startsWith("688"))
//      stock.setStockPlate("科创");
//    else
//      stock.setStockPlate(null);

    int insertCount = this.stockMapper.insert(stock);
    if (insertCount > 0)
      return ServerResponse.createBySuccessMsg("");
    return ServerResponse.createByErrorMsg("");
  }

  public int CountStockNum() {
    return this.stockMapper.CountStockNum();
  }

  public int CountShowNum(Integer showState) {
    return this.stockMapper.CountShowNum(showState);
  }

  public int CountUnLockNum(Integer lockState) {
    return this.stockMapper.CountUnLockNum(lockState);
  }

  public List findStockList() {
    return this.stockMapper.findStockList();
  }

  public ServerResponse selectRateByDaysAndStockCode(String stockCode, int days) {
    Stock stock = this.stockMapper.findStockByCode(stockCode,0);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    BigDecimal daysRate = this.iStockMarketsDayService.selectRateByDaysAndStockCode(stock.getId(), days);
    return ServerResponse.createBySuccess(daysRate);
  }

  public ServerResponse updateStock(Stock model) {
    if (StringUtils.isBlank(model.getId().toString()) || StringUtils.isBlank(model.getStockName()))
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.selectByPrimaryKey(model.getId());
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    stock.setStockName(model.getStockName());
    if (model.getSpreadRate() != null)
      stock.setSpreadRate(model.getSpreadRate());
    int updateCount = this.stockMapper.updateByPrimaryKeySelective(stock);
    if (updateCount > 0)
      return ServerResponse.createBySuccessMsg("");
    return ServerResponse.createByErrorMsg("");
  }

  public ServerResponse deleteByPrimaryKey(Integer id) {
    int updateCount = this.stockMapper.deleteByPrimaryKey(id);
    if (updateCount > 0) {
      return ServerResponse.createBySuccessMsg("操作成功");
    }
    return ServerResponse.createByErrorMsg("操作失败");
  }

  @Override
  public HistoryVO getHistory(String code) {

    LocalDate date = LocalDate.now();
    DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
    String nowYear = date.format(year);

    DateTimeFormatter md = DateTimeFormatter.ofPattern("-MM-dd");
    String nowMd = date.format(md);


    HistoryVO historyVO = new HistoryVO();

    String result = HttpClientRequest.doGet("https://wifeed.vn/api/du-lieu-gia-eod/full?apikey=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mjk4LCJlbWFpbCI6ImxvbmdtcjEyMDQxOUBnbWFpbC5jb20iLCJpYXQiOjE2ODE1NzMzNzd9.JAIAHp7qIWtQIVRLPCMUEI4oyVVJMyK4YaMSW1Jj1oo&code="+code+"&from-date="+String.valueOf(Integer.valueOf(nowYear)-1)+nowMd+"&to-date="+nowYear+nowMd);
    JSONArray data2 = JSONArray.fromObject(result);

    ArrayList<String> t = new ArrayList<>();
    ArrayList<String> o = new ArrayList<>();
    ArrayList<String> h = new ArrayList<>();
    ArrayList<String> l = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();
    ArrayList<String> v = new ArrayList<>();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    for (int i = data2.size()-1; i >=0 ; i--) {
      try {
        t.add(
                String.valueOf(
                        simpleDateFormat.parse(
                                ((JSONObject) data2.get(i)).get("ngay").toString()
                                        .replace(".000Z","")
                                        .replace("T"," "))
                                .getTime()/1000)
        );

        h.add(String.valueOf(Float.valueOf(((JSONObject) data2.get(i)).get("high_root").toString())/1000));
        o.add(String.valueOf(Float.valueOf(((JSONObject) data2.get(i)).get("open_root").toString())/1000));
        l.add(String.valueOf(Float.valueOf(((JSONObject) data2.get(i)).get("low_root").toString())/1000));
        c.add(String.valueOf(Float.valueOf(((JSONObject) data2.get(i)).get("close_adjust").toString())/1000));
        v.add(String.valueOf(Float.valueOf(((JSONObject) data2.get(i)).get("volume_adjust").toString())/1));
      } catch (ParseException e) {
        e.printStackTrace();
      }


    }
    historyVO.setT(t);
    historyVO.setH(h);
    historyVO.setO(o);
    historyVO.setL(l);
    historyVO.setC(c);
    historyVO.setV(v);
    return historyVO;
  }

  /**
   * 获取每个股票的信息
   * @param type
   * @param stockGid
   * @return
   */
  private StockListVO findStockList(String type, String stockGid){

    if(type.equals("HOSE") || type.equals("HNX") || type.equals("UPCOM")){
        return SinaStockApi.getVietNamData(type,stockGid);
    }


    String sinaStock = SinaStockApi.getSinaStock(stockGid);
    if(type.equals("hk")){
      return SinaStockApi.assembleHkStockListVO(sinaStock);
    } else if(type.equals("us")){
      return SinaStockApi.assembleUsStockListVO(sinaStock);
    } else {
      return SinaStockApi.assembleStockListVO(sinaStock);
    }
  }

}
