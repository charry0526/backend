package com.xc.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.xc.common.ServerResponse;
import com.xc.service.IStockService;
import com.xc.vo.stock.HistoryVO;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Controller
@RequestMapping({"/"})
public class StockHistoryApiController {
    private static final Logger log = LoggerFactory.getLogger(StockHistoryApiController.class);

    @Autowired
    IStockService iStockService;


    @RequestMapping({"chart3api/history.do"})
    @ResponseBody
    public HistoryVO getHistory(@RequestParam("symbol") String code) {
        return this.iStockService.getHistory(code);
    }


}