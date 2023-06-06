package com.xc.utils.sms.ali;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xc.common.ServerResponse;
import com.xc.utils.PropertiesUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.xc.utils.redis.JsonUtil;
import com.xc.utils.redis.RedisShardedPoolUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BukaSms {

    static final String baseUrl = PropertiesUtil.getProperty("message.url");
    static final String apiKey = PropertiesUtil.getProperty("message.apiKey");
    static final String apiPwd = PropertiesUtil.getProperty("message.apiPwd");
    static final String smsAppId = PropertiesUtil.getProperty("message.sms.appId_sms");
    static final String codeAppId = PropertiesUtil.getProperty("message.sms.appId_code");
    static final String redisKey = PropertiesUtil.getProperty("message.code");



    /**
     * 发送验证码
     * @param phone
     * @return
     */
    public static ServerResponse send(String phone) {
        /**
         * step1
         * 生成6位随机数验证码
         */
        String code =  generateCode(6);
        /**
         * step2
         * 发送短信验证码
         */
        HttpResponse result = sendSms(phone, code, 2);
        /**
         * step3
         * 写入redis
         */
        if(result.isOk()){
            JSONObject json = JSONObject.parseObject(result.body());
            if("0".equals(json.get("status"))){
                JSONArray array = json.getJSONArray("array");
                String msgId = "";
                for (int i = 0; i < array.size(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    msgId = item.getString("msgId");
                }
                String key = redisKey + msgId;
                RedisShardedPoolUtils.setEx(key, code, 300);
                return ServerResponse.createBySuccess("Gửi thành công",msgId);
            }
        }
        return ServerResponse.createByErrorMsg("Gửi thất bại");
    }

    /**
     * 重新发送验证码
     * @param msgId
     * @return
     */
    public static ServerResponse resend(String msgId,String phone) {
        String key = redisKey + msgId;
        RedisShardedPoolUtils.del(key);
        /**
         * step1
         * 生成6位随机数验证码
         */
        String code =  generateCode(6);
        /**
         * step2
         * 发送短信验证码
         */
        HttpResponse result = sendSms(phone, code, 2);
        /**
         * step3
         * 写入redis
         */
        if(result.isOk()){
            JSONObject json = JSONObject.parseObject(result.body());
            if("0".equals(json.get("status"))){
                JSONArray array = json.getJSONArray("array");
                String msgid = "";
                for (int i = 0; i < array.size(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    msgid = item.getString("msgId");
                }
                String newKey = redisKey + msgid;
                RedisShardedPoolUtils.setEx(newKey, code, 300);
                return ServerResponse.createBySuccess("Gửi thành công",msgid);
            }
        }
        return ServerResponse.createByErrorMsg("Gửi thất bại");
    }

    /**
     * 校验验证码
     * @param msgId
     * @param number
     * @return
     */
    public static ServerResponse verify(String msgId,String number) {
        String newKey = redisKey + msgId;
        String redis = RedisShardedPoolUtils.get(newKey);
        if(!StringUtils.isEmpty(redis)){
            if(number.equals(redis)){
                return ServerResponse.createBySuccess("Xác minh thành công");
            }
        }
        return ServerResponse.createByErrorMsg("Lỗi mã xác minh");
    }


    /**
     * 发送短信
     * @param numbers 号码
     * @param content 内容
     * @param type 1:营销短信 2:验证码
     * @return
     */
    public static HttpResponse sendSms(String numbers,String content,int type) {
        String appId = null;
        if(type == 1){
            appId = smsAppId;
        }
        if(type == 2){
            appId = codeAppId;
        }
        String senderId = "";
        String url = baseUrl.concat("/sendSms");
        HttpRequest request = HttpRequest.post(url);
        //generate md5 key
        final String datetime = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        final String sign = SecureUtil.md5(apiKey.concat(apiPwd).concat(datetime));
        request.header(Header.CONNECTION, "Keep-Alive")
                .header(Header.CONTENT_TYPE, "application/json;charset=UTF-8")
                .header("Sign", sign)
                .header("Timestamp", datetime)
                .header("Api-Key", apiKey);
        final String params = JSONUtil.createObj()
                .set("appId", appId)
                .set("numbers", numbers)
                .set("content", content)
                .set("senderId", senderId)
                .toString();
        HttpResponse response = request.body(params).execute();
        return response;
    }

    public static void main(String[] args) {
        //BukaSms bukaSms = new BukaSms();
        //bukaSms.sendSms();
        String result = "{\"status\":\"0\",\"reason\":\"success\",\"success\":\"1\",\"fail\":\"0\",\"array\":[{\"msgId\":\"2306012308091125889\",\"number\":\"84988005516\",\"orderId\":null}]}";
        JSONObject json = JSONObject.parseObject(result);
        if("0".equals(json.get("status"))){
            JSONArray array = json.getJSONArray("array");
            for (int i = 0; i < array.size(); i++) {
                JSONObject item = array.getJSONObject(i);
                String msgId = item.getString("msgId");
                System.out.println(msgId);
            }
        }


    }

    void sendSms() {
        final String baseUrl = "https://api.onbuka.com/v3";
        final String apiKey = "ffSD8uMx";
        final String apiPwd = "ZouiW8B9";
        //final String appId = "b4MaLqVj"; // 包含4位数字
        final String appId = "jA97oHCT"; // 任意内容
        final String numbers = "84988005516";
        final String content = "Chúc mừng quý khách đã mở tài khoản và đăng ký các sản phẩm dịch vụ thành công tại E*TRADE !";
        final String senderId = "";
        final String url = baseUrl.concat("/sendSms");
        HttpRequest request = HttpRequest.post(url);
        //generate md5 key
        final String datetime = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        final String sign = SecureUtil.md5(apiKey.concat(apiPwd).concat(datetime));
        request.header(Header.CONNECTION, "Keep-Alive")
                .header(Header.CONTENT_TYPE, "application/json;charset=UTF-8")
                .header("Sign", sign)
                .header("Timestamp", datetime)
                .header("Api-Key", apiKey);
        final String params = JSONUtil.createObj()
                .set("appId", appId)
                .set("numbers", numbers)
                .set("content", content)
                .set("senderId", senderId)
                .toString();
        HttpResponse response = request.body(params).execute();
        if (response.isOk()) {
            String result = response.body();
            System.out.println(result);
        }
    }

    /**
     * 生成指定长度的随机数字串
     * @param length 长度
     * @return
     */
    private static String generateCode(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }

    /**
     * 添加请求头内容
     * @param request
     */
    private static void createHeader(HttpRequest request) {
        // currentTime
        final String datetime = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        // generate md5 key
        final String sign = SecureUtil.md5(apiKey.concat(apiPwd).concat(datetime));
        request.header(Header.CONNECTION, "Keep-Alive")
                .header(Header.CONTENT_TYPE, "application/json;charset=UTF-8")
                .header("Sign", sign)
                .header("Timestamp", datetime)
                .header("Api-Key", apiKey);
    }

}
