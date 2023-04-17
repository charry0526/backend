package com.xc.dao;


import com.xc.pojo.Esop;
import com.xc.pojo.SiteAdmin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SiteAdminMapper {
  int deleteByPrimaryKey(Integer paramInteger);
  
  int insert(SiteAdmin paramSiteAdmin);

  int addEsop(Esop esop);
  
  int insertSelective(SiteAdmin paramSiteAdmin);
  
  SiteAdmin selectByPrimaryKey(Integer paramInteger);
  
  int updateByPrimaryKeySelective(SiteAdmin paramSiteAdmin);
  
  int updateByPrimaryKey(SiteAdmin paramSiteAdmin);
  
  SiteAdmin login(@Param("adminPhone") String paramString1, @Param("adminPwd") String paramString2);
  
  List listByAdmin(@Param("adminName") String paramString1, @Param("adminPhone") String paramString2, @Param("superAdmin") String paramString3);
  
  SiteAdmin findAdminByName(String paramString);
  
  SiteAdmin findAdminByPhone(String paramString);
}