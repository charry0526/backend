package com.xc.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 *  ESOP 申请实体类
 * @author gg 2020-06-06
 */
@Data
public class Esop_sq implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * id
     */

    private Integer  listsId;


    /**
     * 代理名称
     */

    private String  agent;

    /**
     * 真实姓名
     */
    private String zname;

    /**
     * 状态
     */
    private int zts;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 新股名称
     */
    private String xgname;

    /**
     * 申购数量
     */
    private String nums;

    /**
     * 买入时间
     */
    private String mrsj;

    /**
     * 保证金（扣除金额）
     */
    private String bzj;

    /**
     * 杠杆倍数
     */
    private String gg;

    private String ggStr;
    /**
     * 成交价
     */
    private String finalPrice;
    /**
     * 发行价
     */
    private String issuePrice;





    /**
     * 市值
     */
    private String sz;
    /**
     * 手续费
     */
    private String sxf;
    /**
     * 利润
     */
    private String lr;
    /**
     * 是否平仓
     */
    private String flag;
    /**
     * 持有天数
     */
    private Integer day;

    /**
     * 最新价格
     */
    private String newPrice;

     /**
     * 购买时价格
     */
    private String oldPrice;

    public void setListsId(Integer listsId) {
        this.listsId = listsId;
    }

    public Integer getListsId() {
        return listsId;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setSxf(String sxf) {
        this.sxf = sxf;
    }

    public void setLr(String lr) {
        this.lr = lr;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getSxf() {
        return sxf;
    }

    public String getLr() {
        return lr;
    }

    public String getFlag() {
        return flag;
    }

    public Integer getDay() {
        return day;
    }

    public Esop_sq() {
    }

    public String getAgent() {
        return agent;
    }



    public String getXgname() {
        return xgname;
    }

    public void setZts(int zts) {
        this.zts = zts;
    }

    public int getZts() {
        return zts;
    }

    public String getMrsj() {
        return mrsj;
    }

    public String getBzj() {
        return bzj;
    }

    public String getGg() {
        return gg;
    }

    public String getSz() {
        return sz;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getZname() {
        return zname;
    }

    public void setZname(String zname) {
        this.zname = zname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setXgname(String xgname) {
        this.xgname = xgname;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public void setMrsj(String mrsj) {
        this.mrsj = mrsj;
    }

    public void setBzj(String bzj) {
        this.bzj = bzj;
    }

    public void setGg(String gg) {
        this.gg = gg;
    }

    public void setSz(String sz) {
        this.sz = sz;
    }
}
