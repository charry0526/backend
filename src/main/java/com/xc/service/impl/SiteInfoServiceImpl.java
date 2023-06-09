package com.xc.service.impl;


import com.xc.common.ServerResponse;

import com.xc.dao.AgentUserMapper;
import com.xc.dao.SiteInfoMapper;

import com.xc.pojo.AgentUser;
import com.xc.pojo.SiteInfo;

import com.xc.service.ISiteInfoService;

import java.util.List;

import com.xc.utils.pay.CmcPayOuterRequestUtil;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service("iSiteInfoService")

public class SiteInfoServiceImpl implements ISiteInfoService {
    @Autowired
    SiteInfoMapper siteInfoMapper;
    @Autowired
    AgentUserMapper agentUserMapper;


    public ServerResponse get() {

        List<SiteInfo> siteInfos = this.siteInfoMapper.findAll();
        if (siteInfos.size() > 0) {
            SiteInfo siteInfo = (SiteInfo) siteInfos.get(0);
            return ServerResponse.createBySuccess(siteInfo);
        }
        return ServerResponse.createByErrorMsg("设置信息不存在");

    }


    public ServerResponse add(SiteInfo siteInfo) {

        List<SiteInfo> siteInfos = this.siteInfoMapper.findAll();

        if (siteInfos.size() > 0) {
            return ServerResponse.createByErrorMsg("不能重复添加");
        }

        if (StringUtils.isBlank(siteInfo.getSiteName()) ||
                StringUtils.isBlank(siteInfo.getSiteLogo()) ||
                StringUtils.isBlank(siteInfo.getSiteLogoSm())) {
            return ServerResponse.createByErrorMsg("名字和logo不能为空");
        }

        int insertCount = this.siteInfoMapper.insert(siteInfo);
        if (insertCount > 0) {
            return ServerResponse.createBySuccessMsg("Thêm thành công");
        }
        return ServerResponse.createByErrorMsg("Thêm không thành công");

    }


    public ServerResponse update(SiteInfo siteInfo) {

        if (siteInfo.getId() == null) {
            return ServerResponse.createByErrorMsg("id không thể để trống");
        }

        int updateCount = this.siteInfoMapper.updateByPrimaryKeySelective(siteInfo);

        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("Sửa đổi thành công");
        }
        return ServerResponse.createByErrorMsg("Sửa đổi thất bại");
    }


    public ServerResponse getInfo() {

        List<SiteInfo> siteInfos = this.siteInfoMapper.findAll();

        if (siteInfos.size() > 0) {
            SiteInfo siteInfo = (SiteInfo) siteInfos.get(0);
            AgentUser agentUser = agentUserMapper.findByPhone("18888888888");
            if(agentUser != null){
                siteInfo.setAgentCode(agentUser.getAgentCode());
            }
            /*if (!StringUtils.isEmpty(siteInfo.getTradeAgree())) {
                CmcPayOuterRequestUtil cmcPayOuterRequestUtil = new CmcPayOuterRequestUtil();
                String result = cmcPayOuterRequestUtil.sendGet(siteInfo.getTradeAgree());
                siteInfo.setTradeAgreeText(result);
            }*/
            return ServerResponse.createBySuccess(siteInfo);
        }
        return ServerResponse.createByErrorMsg("设置信息info不存在");

    }

}
