package com.xc.controller;

import com.xc.common.ServerResponse;
import com.xc.pojo.Esop;
import com.xc.pojo.Esop_sq;
import com.xc.pojo.SiteSpread;
import com.xc.service.*;
import com.xc.utils.PropertiesUtil;
import com.xc.utils.redis.CookieUtils;
import com.xc.utils.redis.JsonUtil;
import com.xc.utils.redis.RedisConst;
import com.xc.utils.redis.RedisShardedPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Controller
@RequestMapping({"/api/admin/"})
public class AdminApiController {
    private static final Logger log = LoggerFactory.getLogger(AdminApiController.class);

    @Autowired
    ISiteAdminService iSiteAdminService;

    @Autowired
    ISiteSettingService iSiteSettingService;

    @Autowired
    ISiteIndexSettingService iSiteIndexSettingService;

    @Autowired
    ISiteFuturesSettingService iSiteFuturesSettingService;

    @Autowired
    ISiteProductService iSiteProductService;

    @Autowired
    ISiteSpreadService iSiteSpreadService;

    //管理系统登录
    @RequestMapping({"login.do"})
    @ResponseBody
    public ServerResponse login(@RequestParam("adminPhone") String adminPhone, @RequestParam("adminPwd") String adminPwd, @RequestParam("verifyCode") String verifyCode, HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {
        ServerResponse serverResponse = this.iSiteAdminService.login(adminPhone, adminPwd, verifyCode, request);
        String admin_cookie_name = PropertiesUtil.getProperty("admin.cookie.name");
        if (serverResponse.isSuccess()) {
            CookieUtils.writeLoginToken(response, RedisConst.getAdminRedisKey(httpSession.getId()), admin_cookie_name);
           String str = RedisShardedPoolUtils.setEx(RedisConst.getAdminRedisKey(httpSession.getId()),
                    JsonUtil.obj2String(serverResponse.getData()), 5400);
        }
        return serverResponse;
    }

    //管理系统注销
    @RequestMapping({"logout.do"})
    @ResponseBody
    public ServerResponse logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String cookie_name = PropertiesUtil.getProperty("admin.cookie.name");
        String logintoken = CookieUtils.readLoginToken(httpServletRequest, cookie_name);
        log.info("管理员 token = {} ,退出登陆", logintoken);
        RedisShardedPoolUtils.del(logintoken);
        CookieUtils.delLoginToken(httpServletRequest, httpServletResponse, cookie_name);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping({"authCharge.do"})
    @ResponseBody
    public ServerResponse authCharge(@RequestParam("token") String token, @RequestParam("state") Integer state, @RequestParam("orderSn") String orderSn) {
        return this.iSiteAdminService.authCharge(token, state, orderSn);
    }

    //查询风控设置 股票分控信息
    @RequestMapping({"getSetting.do"})
    @ResponseBody
    public ServerResponse getSetting() {
        return ServerResponse.createBySuccess(this.iSiteSettingService.getSiteSetting());
    }

    //查询风控设置 指数风控信息
    @RequestMapping({"getIndexSetting.do"})
    @ResponseBody
    public ServerResponse getIndexSetting() {
        return ServerResponse.createBySuccess(this.iSiteIndexSettingService.getSiteIndexSetting());
    }

    //查询风控设置 期货风控信息
    @RequestMapping({"getFuturesSetting.do"})
    @ResponseBody
    public ServerResponse getFuturesSetting() {
        return ServerResponse.createBySuccess(this.iSiteFuturesSettingService.getSetting());
    }

    //风控设置 显示产品配置信息
    @RequestMapping({"getProductSetting.do"})
    @ResponseBody
    public ServerResponse getProductSetting() {
        return ServerResponse.createBySuccess(this.iSiteProductService.getProductSetting());
    }

    //查询点差设置列表
    @RequestMapping({"getSiteSpreadList.do"})
    @ResponseBody
    public ServerResponse getSiteSpreadList(int pageNum, int pageSize, String typeName) {
        return ServerResponse.createBySuccess(this.iSiteSpreadService.pageList(pageNum, pageSize, typeName));
    }

    //添加点差设置
    @RequestMapping({"addSiteSpread.do"})
    @ResponseBody
    public ServerResponse addSiteSpread(SiteSpread siteSpread) {
        return ServerResponse.createBySuccess(this.iSiteSpreadService.insert(siteSpread));
    }

    @RequestMapping({"addESOP.do"})
    @ResponseBody
    public ServerResponse addESOP(Esop esop) {
        return this.iSiteAdminService.addESOP(esop);
    }
    @RequestMapping({"addESOP_sq.do"})
    @ResponseBody
    public ServerResponse addESOP(Esop_sq esop) {
        return this.iSiteAdminService.addESOP_sq(esop);
    }

    @RequestMapping({"getEsopList.do"})
    @ResponseBody
    public ServerResponse getEsopList(int pageNum, int pageSize) {
        return this.iSiteAdminService.getEsopList(pageNum, pageSize);
    }

    @RequestMapping(value = {"test.do"})
    public String test(String name) {
        log.info("接受到的值："+ name);
        return name ;
    }
    @RequestMapping({"auth.do"})
    @ResponseBody
    public ServerResponse auth(String realName, HttpServletResponse response) {
        log.info("接受到的值："+ realName);
        try {
            String name = URLDecoder.decode(realName, StandardCharsets.UTF_8.name());
            log.info("接受到的值："+ name);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ServerResponse.createBySuccess(realName);
    }

    public static void main(String[] args) {
        String aaa = "ă,Ă,â,Â,đ,Đ,ê,Ê,ô,Ô,ơ,Ơ,ư,Ư";
        System.out.println(aaa);
    }

    public static String printVietnamese(String vietnameseText) {
        try {
            byte[] utf8Bytes = vietnameseText.getBytes("UTF-8");
            System.out.println("UTF-8编码字节数组：" + Arrays.toString(utf8Bytes));
            String encodedText = new String(utf8Bytes, "UTF-8");
            System.out.println("UTF-8解码后的字符串：" + encodedText);
            return encodedText;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return vietnameseText;
        }
    }

    @RequestMapping({"getEsopList_sq.do"})
    @ResponseBody
    public ServerResponse getEsopList_sq(int pageNum, int pageSize,String phone,String flag) {
        return this.iSiteAdminService.getEsopList_sq(pageNum, pageSize,phone,flag);
    }
    @RequestMapping({"getEsop_pc.do"})
    @ResponseBody
    public ServerResponse getEsop_pc(int id) {
        return this.iSiteAdminService.getEsop_pc(id);
    }
    @RequestMapping({"getNewList.do"})
    @ResponseBody
    public ServerResponse getNewList(int pageNum, int pageSize,Esop esop) {
        return this.iSiteAdminService.getNewList(pageNum,pageSize,esop);
    }
    @RequestMapping({"getLists.do"})
    @ResponseBody
    public ServerResponse getLists(int pageNum, int pageSize,Esop_sq esop_sq) {
        return this.iSiteAdminService.getLists(pageNum,pageSize,esop_sq);
    }

    //添加点差设置
    @RequestMapping({"updateSiteSpread.do"})
    @ResponseBody
    public ServerResponse updateSiteSpread(SiteSpread siteSpread) {
        return ServerResponse.createBySuccess(this.iSiteSpreadService.update(siteSpread));
    }

}
