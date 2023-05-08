package com.xc.service.impl;


import com.xc.pojo.*;
import com.xc.service.*;
import com.github.pagehelper.PageHelper;

import com.github.pagehelper.PageInfo;

import com.xc.common.ServerResponse;

import com.xc.dao.AgentUserMapper;

import com.xc.dao.UserMapper;

import com.xc.dao.UserWithdrawMapper;

import com.xc.service.*;
import com.xc.utils.stock.WithDrawUtils;

import java.math.BigDecimal;

import java.util.Date;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


@Service("iUserWithdrawService")
public class UserWithdrawServiceImpl implements IUserWithdrawService {

    private static final Logger log = LoggerFactory.getLogger(UserWithdrawServiceImpl.class);


    @Autowired
    UserWithdrawMapper userWithdrawMapper;


    @Autowired
    IUserService iUserService;


    @Autowired
    UserMapper userMapper;


    @Autowired
    IAgentUserService iAgentUserService;

    @Autowired
    AgentUserMapper agentUserMapper;

    @Autowired
    IUserPositionService iUserPositionService;

    @Autowired
    IUserBankService iUserBankService;

    @Autowired
    ISiteSettingService iSiteSettingService;

    @Autowired
    ISiteProductService iSiteProductService;


    @Transactional
    public ServerResponse outMoney(String amt, String with_Pwd, HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(amt)) {
            return ServerResponse.createByErrorMsg("Sửa đổi thất Tham số không được bỏ trống");
        }
        User user = this.iUserService.getCurrentRefreshUser(request);
        String w = user.getWithPwd();
        if (w == null) {
            w = "";
        }
        if (with_Pwd == null) {
            with_Pwd = "";
        }
        if (w.equals(with_Pwd)) {
            if (user.getIsLogin().intValue() == 1) {
                return ServerResponse.createByErrorMsg("Người dùng bị khóa");
            }


           /* List<UserPosition> userPositions = this.iUserPositionService.findPositionByUserIdAndSellIdIsNull(user.getId());

            if (userPositions.size() > 0) {
                // 有持仓单不能出金
                return ServerResponse.createByErrorMsg("Cổ phiếu còn trong danh mục không thể rút tiền được");

            }*/


            if (StringUtils.isBlank(user.getRealName()) || StringUtils.isBlank(user.getIdCard())) {
                // 未实名认证
                return ServerResponse.createByErrorMsg("Chưa được xác thực");

            }

            UserBank userBank = this.iUserBankService.findUserBankByUserId(user.getId());

            if (userBank == null) {
                // 未添加银行卡
                return ServerResponse.createByErrorMsg("Không có thẻ nào được thêm vào");

            }


            if (user.getAccountType().intValue() == 1) {
                // 模拟用户不能出金
                return ServerResponse.createByErrorMsg("Người dùng mô phỏng không thể rút tiền");

            }


            SiteSetting siteSetting = this.iSiteSettingService.getSiteSetting();

            if ((new BigDecimal(amt)).compareTo(new BigDecimal(siteSetting.getWithMinAmt().intValue())) == -1) {
                // 提现金额不能低于 设定值
                return ServerResponse.createByErrorMsg("Số tiền rút không được nhỏ hơn" + siteSetting.getWithMinAmt() + "VND");

            }

            int with_time_begin = siteSetting.getWithTimeBegin().intValue();

            int with_time_end = siteSetting.getWithTimeEnd().intValue();

            SiteProduct siteProduct = iSiteProductService.getProductSetting();
            if(siteProduct.getHolidayDisplay()){
                // 周末节假日不能提现
                return ServerResponse.createByErrorMsg("Không rút tiền vào cuối tuần hoặc ngày lễ！");
            }

            if (!WithDrawUtils.checkIsWithTime(with_time_begin, with_time_end)) {
                // 规定时间内
                return ServerResponse.createByErrorMsg("Rút tiền không thành công, thời gian rút tiền từ" + with_time_begin + "đến" + with_time_end);

            }


            BigDecimal index_user_amt = user.getUserIndexAmt();

            if (index_user_amt.compareTo(new BigDecimal("0")) == -1) {

                return ServerResponse.createByErrorMsg("Quỹ chỉ số không thể nhỏ hơn 0");

            }


            BigDecimal futures_user_amt = user.getUserFutAmt();

            if (futures_user_amt.compareTo(new BigDecimal("0")) == -1) {

                return ServerResponse.createByErrorMsg("Các quỹ tương lai không thể nhỏ hơn 0");

            }


            BigDecimal enable_amt = user.getEnableAmt();

            int compareAmt = enable_amt.compareTo(new BigDecimal(amt));

            if (compareAmt == -1) {

                return ServerResponse.createByErrorMsg("Rút tiền không thành công，Không đủ tiền cho người dùng");

            }


            BigDecimal user_all_amt = user.getUserAmt();

            BigDecimal reckon_all_amt = user_all_amt.subtract(new BigDecimal(amt));

            BigDecimal reckon_enable_amt = enable_amt.subtract(new BigDecimal(amt));

            user.setUserAmt(reckon_all_amt);

            user.setEnableAmt(reckon_enable_amt);

            log.info("用户提现{}，金额 = {},总资金 = {},可用资金 = {}", new Object[]{user.getId(), amt, user_all_amt, enable_amt});


            log.info("提现后，总金额={},可用资金={}", reckon_all_amt, reckon_enable_amt);

            int updateUserCount = this.userMapper.updateByPrimaryKeySelective(user);

            if (updateUserCount > 0) {

                log.info("修改用户资金成功");

            } else {

                log.error("修改用户资金失败");

                throw new Exception("Tài khoản rút tiền, sửa đổi tiền tài khoản thất bại");

            }


            UserWithdraw userWithdraw = new UserWithdraw();

            userWithdraw.setUserId(user.getId());

            userWithdraw.setNickName(user.getRealName());

            userWithdraw.setAgentId(user.getAgentId());

            userWithdraw.setWithAmt(new BigDecimal(amt));

            userWithdraw.setApplyTime(new Date());

            userWithdraw.setWithName(user.getRealName());

            userWithdraw.setBankNo(userBank.getBankNo());

            userWithdraw.setBankName(userBank.getBankName());

            userWithdraw.setBankAddress(userBank.getBankAddress());

            userWithdraw.setWithStatus(Integer.valueOf(0));


            BigDecimal withfee = siteSetting.getWithFeePercent().multiply(new BigDecimal(amt)).add(new BigDecimal(siteSetting.getWithFeeSingle().intValue()));

            userWithdraw.setWithFee(withfee);


            int insertCount = this.userWithdrawMapper.insert(userWithdraw);

            if (insertCount > 0) {

                return ServerResponse.createBySuccessMsg("Rút tiền thành công");

            }

            log.error("保存提现记录失败");

            throw new Exception("Tài khoản rút tiền, lưu sao kê rút tiền thất bại");
        } else {
            return ServerResponse.createByErrorMsg("Mật khẩu rút tiền không chính xác！！");
        }

    }


    public ServerResponse<PageInfo> findUserWithList(String withStatus, HttpServletRequest request, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);


        User user = this.iUserService.getCurrentUser(request);


        List<UserWithdraw> userWithdraws = this.userWithdrawMapper.findUserWithList(user.getId(), withStatus);


        PageInfo pageInfo = new PageInfo(userWithdraws);


        return ServerResponse.createBySuccess(pageInfo);

    }


    public ServerResponse userCancel(Integer withId) {

        if (withId == null) {

            return ServerResponse.createByErrorMsg("id không thể để trống");

        }


        UserWithdraw userWithdraw = this.userWithdrawMapper.selectByPrimaryKey(withId);

        if (userWithdraw == null) {

            return ServerResponse.createByErrorMsg("Đặt lệnh không tồn tại\n");

        }


        if (0 != userWithdraw.getWithStatus().intValue()) {

            return ServerResponse.createByErrorMsg("Đặt lệnh hiện tại không thể hủy\n");

        }


        userWithdraw.setWithStatus(Integer.valueOf(3));

        userWithdraw.setWithMsg("Tài khoản từ chối rút tiền");

        int updateCount = this.userWithdrawMapper.updateByPrimaryKeySelective(userWithdraw);

        if (updateCount > 0) {

            log.info("修改用户提现订单 {} 状态成功", withId);


            User user = this.userMapper.selectByPrimaryKey(userWithdraw.getUserId());

            user.setUserAmt(user.getUserAmt().add(userWithdraw.getWithAmt()));

            user.setEnableAmt(user.getEnableAmt().add(userWithdraw.getWithAmt()));

            int updateUserCount = this.userMapper.updateByPrimaryKeySelective(user);

            if (updateUserCount > 0) {

                log.info("反还用户资金，总 {} 可用 {}", user.getUserAmt(), user.getEnableAmt());

                return ServerResponse.createBySuccessMsg("Hủy thành công");

            }

            return ServerResponse.createByErrorMsg("Hủy thất bại");

        }


        log.info("修改用户提现订单 {} 状态失败", withId);

        return ServerResponse.createByErrorMsg("Hủy thất bại");

    }


    public ServerResponse listByAgent(Integer agentId, String realName, Integer state, HttpServletRequest request, int pageNum, int pageSize) {

        AgentUser currentAgent = this.iAgentUserService.getCurrentAgent(request);


        if (agentId != null) {

            AgentUser agentUser = this.agentUserMapper.selectByPrimaryKey(agentId);

            if (agentUser.getParentId() != currentAgent.getId()) {

                return ServerResponse.createByErrorMsg("Không thể truy vấn các bản ghi proxy không cấp dưới");

            }

        }

        Integer searchId = null;

        if (agentId == null) {

            searchId = currentAgent.getId();

        } else {

            searchId = agentId;

        }


        PageHelper.startPage(pageNum, pageSize);


        List<UserWithdraw> userWithdraws = this.userWithdrawMapper.listByAgent(searchId, realName, state);


        PageInfo pageInfo = new PageInfo(userWithdraws);


        return ServerResponse.createBySuccess(pageInfo);

    }


    public ServerResponse<PageInfo> listByAdmin(Integer agentId, Integer userId, String realName, Integer state, String beginTime, String endTime, HttpServletRequest request, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);


        List<UserWithdraw> userWithdraws = this.userWithdrawMapper.listByAdmin(agentId, userId, realName, state, beginTime, endTime);


        PageInfo pageInfo = new PageInfo(userWithdraws);


        return ServerResponse.createBySuccess(pageInfo);

    }


    public ServerResponse updateState(Integer withId, Integer state, String authMsg) throws Exception {

        UserWithdraw userWithdraw = this.userWithdrawMapper.selectByPrimaryKey(withId);

        if (userWithdraw == null) {

            return ServerResponse.createByErrorMsg("Lệnh rút tiền không tồn tại");

        }


        if (userWithdraw.getWithStatus().intValue() != 0) {

            return ServerResponse.createByErrorMsg("Lệnh rút tiền đã được xử lý, không thực hiện lại thao tác");

        }


        if (state.intValue() == 2 &&

                StringUtils.isBlank(authMsg)) {

            return ServerResponse.createByErrorMsg("Thông tin lỗi là bắt buộc");

        }


        if (state.intValue() == 2) {


            User user = this.userMapper.selectByPrimaryKey(userWithdraw.getUserId());

            if (user == null) {

                return ServerResponse.createByErrorMsg("Người dùng không tồn tại");

            }

            BigDecimal user_amt = user.getUserAmt().add(userWithdraw.getWithAmt());

            log.info("管理员确认提现订单失败，返还用户 {} 总资金，原金额 = {} , 返还后 = {}", new Object[]{user.getId(), user.getUserAmt(), user_amt});

            user.setUserAmt(user_amt);

            BigDecimal user_enable_amt = user.getEnableAmt().add(userWithdraw.getWithAmt());

            log.info("管理员确认提现订单失败，返还用户 {} 可用资金，原金额 = {} , 返还后 = {}", new Object[]{user.getId(), user.getEnableAmt(), user_enable_amt});

            user.setEnableAmt(user_enable_amt);


            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);

            if (updateCount > 0) {

                log.info("提现失败，返还用户资金成功！");

            } else {

                log.error("返还用户资金出错，抛出异常");

                throw new Exception("Xảy ra lỗi sửa đổi tiền người dùng, đưa ra bất thường");

            }


            userWithdraw.setWithMsg(authMsg);

        }


        userWithdraw.setWithStatus(Integer.valueOf((state.intValue() == 1) ? 1 : 2));


        userWithdraw.setTransTime(new Date());


        int updateCount = this.userWithdrawMapper.updateByPrimaryKeySelective(userWithdraw);

        if (updateCount > 0) {

            return ServerResponse.createBySuccessMsg("Thao tác thành công！");

        }

        return ServerResponse.createByErrorMsg("Thao tác thất bại！");

    }


    public int deleteByUserId(Integer userId) {
        return this.userWithdrawMapper.deleteByUserId(userId);
    }


    public BigDecimal CountSpWithSumAmtByState(Integer withState) {
        return this.userWithdrawMapper.CountSpWithSumAmtByState(withState);
    }

    public BigDecimal CountSpWithSumAmTodaytByState(Integer withState) {
        return this.userWithdrawMapper.CountSpWithSumAmTodaytByState(withState);
    }

    public ServerResponse deleteWithdraw(Integer withdrawId) {
        if (withdrawId == null) {
            return ServerResponse.createByErrorMsg("Xóa bỏ id không thể để trống");
        }
        int updateCount = this.userWithdrawMapper.deleteByPrimaryKey(withdrawId);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("Hủy thành công");
        }
        return ServerResponse.createByErrorMsg("Không thể xóa");
    }

}

