package com.xc.service.impl;


import com.xc.common.ServerResponse;
import com.xc.dao.SiteIndexSettingMapper;
import com.xc.pojo.SiteIndexSetting;
import com.xc.service.ISiteIndexSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("iSiteIndexSettingService")

public class SiteIndexSettingServiceImpl

        implements ISiteIndexSettingService {

    private static final Logger log = LoggerFactory.getLogger(SiteIndexSettingServiceImpl.class);


    @Autowired

    SiteIndexSettingMapper siteIndexSettingMapper;


    public SiteIndexSetting getSiteIndexSetting() {

        SiteIndexSetting siteIndexSetting = null;

        List list = this.siteIndexSettingMapper.selectAllSiteIndexSetting();

        if (list.size() > 0) {

            siteIndexSetting = (SiteIndexSetting) list.get(0);

        }

        return siteIndexSetting;

    }


    public ServerResponse update(SiteIndexSetting siteIndexSetting) {

        if (siteIndexSetting.getId() == null) {

            return ServerResponse.createByErrorMsg("Sửa đổi id không thể để trống");

        }


        SiteIndexSetting dbsetting = this.siteIndexSettingMapper.selectByPrimaryKey(siteIndexSetting.getId());

        if (dbsetting == null) {

            return ServerResponse.createByErrorMsg("Chỉ mục không tồn tại");

        }


        dbsetting.setBuyMaxPercent(siteIndexSetting.getBuyMaxPercent());

        dbsetting.setForceSellPercent(siteIndexSetting.getForceSellPercent());

        dbsetting.setTransAmBegin(siteIndexSetting.getTransAmBegin());

        dbsetting.setTransAmEnd(siteIndexSetting.getTransAmEnd());

        dbsetting.setTransPmBegin(siteIndexSetting.getTransPmBegin());

        dbsetting.setTransPmEnd(siteIndexSetting.getTransPmEnd());

        dbsetting.setDownLimit(siteIndexSetting.getDownLimit());

        dbsetting.setRiseLimit(siteIndexSetting.getRiseLimit());

        dbsetting.setForceStopRemindRatio(siteIndexSetting.getForceStopRemindRatio());


        int updateCount = this.siteIndexSettingMapper.updateByPrimaryKey(dbsetting);

        if (updateCount > 0) {

            return ServerResponse.createBySuccessMsg("Sửa đổi thành công");

        }

        return ServerResponse.createByErrorMsg("Không thể chỉnh sửa");

    }

}