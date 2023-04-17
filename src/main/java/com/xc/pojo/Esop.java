package com.xc.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *  ESOP 实体类
 * @author gg 2020-06-06
 */
@Data
public class Esop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 新股名称
     */

    private String  names;

    /**
     * 申购代码
     */
    private String code;

    /**
     * 金额
     */
    private Integer price;

    /**
     * 状态 0停用 1启用
     */
    private Integer zt;

    /**
     * 最低购买数量
     */
    private Integer num;

    /**
     * 发行时间
     */
    private String fxtime;

    /**
     * 杠杆倍数
     */
    private String lever;

    /**
     * 市场价格
     */
    private Integer scprice;


    public Esop() {
    }

    public void setNames(String names) {
        this.names = names;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setZt(Integer zt) {
        this.zt = zt;
    }

    public void setNum(Integer num) {
        this.num = num;
    }



    public void setLever(String lever) {
        this.lever = lever;
    }

    public void setScprice(Integer scprice) {
        this.scprice = scprice;
    }

    public String getNames() {
        return names;
    }

    public String getCode() {
        return code;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getZt() {
        return zt;
    }

    public Integer getNum() {
        return num;
    }

    public void setFxtime(String fxtime) {
        this.fxtime = fxtime;
    }

    public String getFxtime() {
        return fxtime;
    }

    public String getLever() {
        return lever;
    }

    public Integer getScprice() {
        return scprice;
    }
}
