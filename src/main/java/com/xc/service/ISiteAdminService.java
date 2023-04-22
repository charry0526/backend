package com.xc.service;


import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.pojo.Esop;
import com.xc.pojo.Esop_sq;
import com.xc.pojo.SiteAdmin;

import javax.servlet.http.HttpServletRequest;

public interface ISiteAdminService {
  ServerResponse login(String paramString1, String paramString2, String paramString3, HttpServletRequest paramHttpServletRequest);

  String getEsopPriceByCode(String code);
  String getEsopMinNumByCode(String code);
  ServerResponse<PageInfo> listByAdmin(String paramString1, String paramString2, HttpServletRequest paramHttpServletRequest, int paramInt1, int paramInt2);
  
  ServerResponse authCharge(String paramString1, Integer paramInteger, String paramString2);
  
  ServerResponse updateLock(Integer paramInteger);
  
  ServerResponse add(SiteAdmin paramSiteAdmin);

  ServerResponse addESOP(Esop esop);
  ServerResponse addESOP_sq(Esop_sq esop);

  int updateStatus(Integer id);

  /**
   * 新股列表
   * @return
   */
  ServerResponse<PageInfo> getNewList(int pageNum, int pageSize,Esop esop);

  /**
   * 申请列表
   * @return
   */
  ServerResponse<PageInfo> getLists(int pageNum, int pageSize,Esop_sq esop_sq);

  ServerResponse<PageInfo> getEsopList(int pageNum, int pageSize);
  ServerResponse<PageInfo> getEsopList_sq(int pageNum, int pageSize,String phone,String flag);
  ServerResponse getEsop_pc(int id);
  ServerResponse update(SiteAdmin paramSiteAdmin);

  int purchaseCompleted();

  SiteAdmin findAdminByName(String paramString);
  
  SiteAdmin findAdminByPhone(String paramString);
  
  ServerResponse count();

  ServerResponse deleteAdmin(Integer adminId);
}
