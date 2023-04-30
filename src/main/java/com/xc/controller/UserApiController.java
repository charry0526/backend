package com.xc.controller;


import com.xc.common.ServerResponse;

import com.xc.pojo.SiteSpread;
import com.xc.service.ISiteSpreadService;
import com.xc.service.IUserService;

import com.xc.utils.PropertiesUtil;

import com.xc.utils.redis.CookieUtils;

import com.xc.utils.redis.JsonUtil;

import com.xc.utils.redis.RedisConst;

import com.xc.utils.redis.RedisShardedPoolUtils;

import com.xc.vo.user.UserLoginResultVO;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;


@Controller
@RequestMapping({"/api/user/"})
public class UserApiController {
    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    IUserService iUserService;

    @Autowired
    ISiteSpreadService iSiteSpreadService;

    //注册
    @RequestMapping(value = {"reg.do"}, method = {RequestMethod.POST})
    @ResponseBody
    public ServerResponse reg(@RequestParam("agentCode") String agentCode, @RequestParam("phone") String phone, @RequestParam(value = "yzmCode", defaultValue = "") String yzmCode, @RequestParam("userPwd") String userPwd, HttpServletRequest httpServletRequest) {
        return this.iUserService.reg(yzmCode, agentCode, phone, userPwd, httpServletRequest);
}

    //登录
    @RequestMapping(value = {"login.do"}, method = {RequestMethod.POST})
    @ResponseBody
    public ServerResponse login(@RequestParam("phone") String phone, @RequestParam("userPwd") String userPwd, HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {
        String pc_cookie_name = PropertiesUtil.getProperty("user.cookie.name");
        String token = RedisConst.getUserRedisKey(httpSession.getId());
        ServerResponse serverResponse = this.iUserService.login(phone, userPwd, request);
        if (serverResponse.isSuccess()) {
            CookieUtils.writeLoginToken(response, token, pc_cookie_name);
            String redisSetExResult = RedisShardedPoolUtils.setEx(token, JsonUtil.obj2String(serverResponse.getData()), 5400);
            log.info("redis setex user result : {}", redisSetExResult);
            UserLoginResultVO resultVO = new UserLoginResultVO();
            resultVO.setKey(pc_cookie_name);
            resultVO.setToken(token);
            return ServerResponse.createBySuccess("Đăng nhập thành công", resultVO);
        }
        return serverResponse;
    }

    //注销
    @RequestMapping({"logout.do"})
    @ResponseBody
    public ServerResponse logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String cookie_name = PropertiesUtil.getProperty("user.cookie.name");
        String logintoken = CookieUtils.readLoginToken(httpServletRequest, cookie_name);
        log.info("用户 token = {} ,退出登陆", logintoken);
        RedisShardedPoolUtils.del(logintoken);
        CookieUtils.delLoginToken(httpServletRequest, httpServletResponse, cookie_name);
        return ServerResponse.createBySuccess();
    }

    //查询手机号是否存在
    @RequestMapping({"checkPhone.do"})
    @ResponseBody
    public ServerResponse checkPhone(String phoneNum) {
        return this.iUserService.checkPhone(phoneNum);
    }

    //找回密码
    @RequestMapping({"updatePwd.do"})
    @ResponseBody
    public ServerResponse updatePwd(String phoneNum, String code, String newPwd) {
        return this.iUserService.updatePwd(phoneNum, code, newPwd);
    }

    /**
     * 查询点差费率
     * @author lr
     * @date 2020/07/01
     * applies：涨跌幅
     * turnover：成交额
     * code:股票代码
     * unitprice：股票单价
     **/
    @RequestMapping({"findSpreadRateOne.do"})
    @ResponseBody
    public ServerResponse findSpreadRateOne(BigDecimal applies, BigDecimal turnover, String code, BigDecimal unitprice) {
        SiteSpread siteSpread = this.iSiteSpreadService.findSpreadRateOne(applies,turnover,code,unitprice);
        return ServerResponse.createBySuccess("Nhận được thành công", siteSpread);
    }


}

