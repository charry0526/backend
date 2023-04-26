package com.xc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.dao.SiteBannerMapper;
import com.xc.pojo.SiteBanner;
import com.xc.service.ISiteBannerService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("iSiteBannerService")
public class SiteBannerServiceImpl
        implements ISiteBannerService {
    private static final Logger log = LoggerFactory.getLogger(SiteBannerServiceImpl.class);


    @Autowired
    SiteBannerMapper siteBannerMapper;


    public ServerResponse add(SiteBanner siteBanner) {
        if (StringUtils.isBlank(siteBanner.getBannerUrl()) || siteBanner
                .getIsOrder() == null || siteBanner
                .getIsPc() == null || siteBanner
                .getIsM() == null) {
            return ServerResponse.createByErrorMsg("Tham số không thể để trống");
        }

        siteBanner.setAddTime(new Date());
        int insertCount = this.siteBannerMapper.insert(siteBanner);
        if (insertCount > 0) {
            return ServerResponse.createBySuccessMsg("Thêm thành công");
        }
        return ServerResponse.createByErrorMsg("Thêm không thành công");
    }


    public ServerResponse listByAdmin(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SiteBanner> siteBanners = this.siteBannerMapper.listByAdmin();

        PageInfo pageInfo = new PageInfo(siteBanners);
        return ServerResponse.createBySuccess(pageInfo);
    }


    public ServerResponse update(SiteBanner siteBanner) {
        if (siteBanner == null) {
            return ServerResponse.createByErrorMsg("id không thể để trống");
        }

        int updateCount = this.siteBannerMapper.updateByPrimaryKeySelective(siteBanner);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("Sửa đổi thành công");
        }
        return ServerResponse.createByErrorMsg("Không thể chỉnh sửa");
    }


    public ServerResponse delete(Integer id) {
        if (id == null) {
            return ServerResponse.createByErrorMsg("id không thể để trống");
        }
        int deleteCount = this.siteBannerMapper.deleteByPrimaryKey(id);
        if (deleteCount > 0) {
            return ServerResponse.createBySuccessMsg("Xóa thành công");
        }
        return ServerResponse.createByErrorMsg("Không thể xóa");
    }


    public ServerResponse getBannerByPlat(String platType) {
        if ("m".equals(platType)) {
            List bannerList = this.siteBannerMapper.getBannerByMobile();
            return ServerResponse.createBySuccess(bannerList);
        }
        if ("pc".equals(platType)) {
            List bannerList = this.siteBannerMapper.getBannerByPC();
            return ServerResponse.createBySuccess(bannerList);
        }
        return ServerResponse.createByErrorMsg("Loại nền tảng không tồn tại");
    }
}

