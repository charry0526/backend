package com.xc.dao;


import com.xc.pojo.Esop;
import com.xc.pojo.Esop_sq;
import com.xc.pojo.SiteAdmin;
import com.xc.pojo.SiteSpread;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SiteAdminMapper {
  int deleteByPrimaryKey(Integer paramInteger);
  
  int insert(SiteAdmin paramSiteAdmin);

  int addEsop(Esop esop);
  int addEsop_sq(Esop_sq esop);

  List<Esop> getEsopList(@Param("pageNum") int pageNum, @Param("pageSize")  int pageSize);
  List<Esop_sq> getEsopList_sq(@Param("pageNum") int pageNum, @Param("pageSize")  int pageSize,@Param("phone") String phone);
  int insertSelective(SiteAdmin paramSiteAdmin);
  
  SiteAdmin selectByPrimaryKey(Integer paramInteger);
  
  int updateByPrimaryKeySelective(SiteAdmin paramSiteAdmin);
  
  int updateByPrimaryKey(SiteAdmin paramSiteAdmin);
  
  SiteAdmin login(@Param("adminPhone") String paramString1, @Param("adminPwd") String paramString2);
  
  List listByAdmin(@Param("adminName") String paramString1, @Param("adminPhone") String paramString2, @Param("superAdmin") String paramString3);
  
  SiteAdmin findAdminByName(String paramString);
  
  SiteAdmin findAdminByPhone(String paramString);
}