package com.xc.controller;

import cn.hutool.http.HttpResponse;
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
import com.xc.utils.sms.ali.BukaSms;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    @RequestMapping({"verifyPassWord.do"})
    @ResponseBody
    public ServerResponse verifyPassWord(String password) {
        return ServerResponse.createBySuccess(this.iSiteAdminService.verifyPassword(password));
    }
    @RequestMapping({"setPassWord.do"})
    @ResponseBody
    public ServerResponse setPassWord(String password) {
        return ServerResponse.createBySuccess(this.iSiteAdminService.setPassword(password));
    }

    @RequestMapping({"getCSAddress.do"})
    @ResponseBody
    public ServerResponse getaddress() {
        return this.iSiteAdminService.getCSAddress();
    }

    @RequestMapping({"setCSAddress.do"})
    @ResponseBody
    public ServerResponse setaddress(String address) {
        return this.iSiteAdminService.setCSAddress(address);
    }

    @RequestMapping({"setAvatar.do"})
    @ResponseBody
    public ServerResponse setAvatar(String avatar,Integer id) {
        return this.iSiteAdminService.setAvatar(avatar,id);
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

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @RequestMapping({"sendMessages.do"})
    @ResponseBody
    public ServerResponse sendMessages(String phone) {
        ServerResponse result = BukaSms.send(phone);
        return result;
    }

    /**
     * 重新发送短信验证码
     * @param msgId
     * @return
     */
    @RequestMapping({"resendMessages.do"})
    @ResponseBody
    public ServerResponse resendMessages(String msgId,String phone) {
        ServerResponse result = BukaSms.resend(msgId,phone);
        return result;
    }

    /**
     * 验证短信验证码
     * @param msgId
     * @param code
     * @return
     */
    @RequestMapping({"verifyMessages.do"})
    @ResponseBody
    public ServerResponse verifyMessages(String msgId,String code) {
        ServerResponse result = BukaSms.verify(msgId,code);
        return result;
    }

    /**
     * 发送短信
     * @param phone
     * @param content
     * @return
     */
    @RequestMapping({"sendMsg.do"})
    @ResponseBody
    public ServerResponse sendMsg(String phone,String content) {
        HttpResponse result = BukaSms.sendSms(phone,content,1,1);
        log.info("发送短信接口返回："+result.body());
        if(result.isOk()){
            return ServerResponse.createBySuccess(result.body());
        }
        return ServerResponse.createByErrorMsg("Failure");
    }







}
