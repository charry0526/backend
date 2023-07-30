package com.xc.dao;


import com.xc.pojo.Esop;
import com.xc.pojo.Esop_sq;
import com.xc.pojo.SiteAdmin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SiteAdminMapper {
  int deleteByPrimaryKey(Integer paramInteger);
  
  int insert(SiteAdmin paramSiteAdmin);

  int addEsop(Esop esop);
  int addEsop_sq(Esop_sq esop);

  /**
   * 新股列表
   * @return
   */
  List<Esop> getNewList(int pageNum, int pageSize,Esop esop);

  /**
   * 申请列表
   * @return
   */
  List<Esop_sq> getLists(int pageNum, int pageSize,Esop_sq esop_sq);

  /**
   * 修改申请状态
   * @param pageNum
   * @param pageSize
   * @return
   */
  int updateStatus(Integer id);

  String getPassword();
  int setPassword(String pwd);
  int setCSAddress(@Param("address") String address );
  int setAvatar(@Param("avatar") String avatar, @Param("id") int id);
  String getCSAddress();

  List<Esop> getEsopList(@Param("pageNum") int pageNum, @Param("pageSize")  int pageSize);
  List<Esop_sq> getEsopList_sq(@Param("pageNum") int pageNum, @Param("pageSize")  int pageSize,@Param("phone") String phone,@Param("flag") String flag);
  int insertSelective(SiteAdmin paramSiteAdmin);

  String getEsopLeverByCode(String code);
  
  SiteAdmin selectByPrimaryKey(Integer paramInteger);
  
  int updateByPrimaryKeySelective(SiteAdmin paramSiteAdmin);

  Esop_sq getEsopById(@Param("id") int id);

  int updateByPrimaryKey(SiteAdmin paramSiteAdmin);
  
  SiteAdmin login(@Param("adminPhone") String paramString1, @Param("adminPwd") String paramString2);
  
  List listByAdmin(@Param("adminName") String paramString1, @Param("adminPhone") String paramString2, @Param("superAdmin") String paramString3);

  String getEsopPriceByCode(@Param("code") String code);
  String getEsopMinNumByCode(@Param("code") String code);

  SiteAdmin findAdminByName(String paramString);
  
  SiteAdmin findAdminByPhone(String paramString);
}