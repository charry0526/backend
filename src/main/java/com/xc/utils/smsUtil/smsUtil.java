package com.xc.utils.smsUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xc.common.ServerResponse;
import com.xc.controller.SmsApiController;
import com.xc.dao.SiteSmsLogMapper;
import com.xc.pojo.SiteSmsLog;
import com.xc.service.ISiteSmsLogService;
import com.xc.service.impl.SiteSmsLogServiceImpl;
import com.xc.utils.DateTimeUtil;
import com.xc.utils.PropertiesUtil;
import com.xc.utils.pay.CmcPayOuterRequestUtil;
import com.xc.utils.redis.RedisShardedPoolUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class smsUtil {
    private static final Logger log = LoggerFactory.getLogger(SmsApiController.class);
    // 接口请求地址
    private static final String url = "http://sms.51yunchu.com:7862/sms";

    // 账号 国内
    private static final String accountCh = "122441";
    // 账号 国外
    private static final String accountWa = "122442";

    // 密码 国内
    private static final String passwordCh = "MEZH6D";
    // 密码 国外
    private static final String passwordWa = "PDxjtB";

    // 接入号 国内
    private static final String extnoCh = "106902441";

    // 接入号 国外
    private static final String extnoWa = "106902442";


    public String sendSMS(String telephone) {
        String code = RandomStringUtils.randomNumeric(4);


        Map<String, String> paramMap = null;
        String body = "";
        String quhao=telephone.substring(0,3);
        System.out.println(quhao);
        if ("85".equals(telephone.substring(0,3))){
            //国内短信
            log.info("--------------国内短信发送-----------");
            String smscontent = "您的验证码是：" + code + "，请妥善保存您的注册码，不要泄露其他人。 如有任何疑问，请及时联系在线客服。谢谢！";
            paramMap = new HashMap<String, String>();
            paramMap.put("action", "send");
            paramMap.put("account", accountCh);
            String pwd = passwordCh + extnoCh + smscontent + telephone.substring(3,14);
            paramMap.put("password", md5Encrypt(pwd));
            paramMap.put("extno", extnoCh);
            paramMap.put("mobile", telephone.substring(3,14));
            paramMap.put("content", smscontent);
            paramMap.put("rt", "json");
            body = sendPost(url, paramMap, "utf-8");

        }else {
            log.info("--------------国际短信发送-----------");
            //国际短信
            String smscontent = "Your verification code is" + code + "，Please keep your registration code properly and do not disclose it to others. If you have any questions, please contact our online customer service immediately. Thank you very much!";
            paramMap = new HashMap<String, String>();
            paramMap.put("action", "send");
            paramMap.put("account", accountWa);
            String pwd = passwordWa + extnoWa + smscontent + telephone.trim();
            paramMap.put("password", md5Encrypt(pwd));
            paramMap.put("extno", extnoWa);
            paramMap.put("mobile", telephone.trim());
            paramMap.put("content", smscontent);
            paramMap.put("rt", "json");
            body = sendPost(url, paramMap, "utf-8");
        }
        log.info("smsresult=" + body + "==code=" + code);
        JSONObject jsonObject= (JSONObject) JSON.parse(body);
        if (!jsonObject.get("status").equals("0")) {
            return "";
        } else {
            String keys = "AliyunSmsCode:" + telephone;
            RedisShardedPoolUtils.setEx(keys, code, 5400);
            return code;
        }
    }

    public static void main(String[] args) {
        String sss="8518511023670";
        System.out.println(sss.substring(2,13));
    }

    /**
     * MD5加密
     *
     * @param plainText
     * @return
     */
    private static String md5Encrypt(String plainText) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try {
                md.update(plainText.getBytes("GB2312"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 打印生成的MD5加密信息摘要
            //System.out.println(buf.toString().substring(8, 24));
            // 32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送HTTP请求
     * @param url
     * @param map
     * @param encoding
     * @return
     */
    private static String sendPost(String url, Map<String, String> map, String encoding) {

        String body = "";
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);

        String parm = "?";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry
                        .getValue()));
                parm = parm + entry.getKey() + "=" + entry.getValue() + "&";
            }
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            System.out.println("URL = " + httpPost.getURI() + parm);
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {

                body = EntityUtils.toString(entity, encoding);
            }
            EntityUtils.consume(entity);
            response.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return body;
    }
}
