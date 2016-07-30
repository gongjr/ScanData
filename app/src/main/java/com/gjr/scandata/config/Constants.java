package com.gjr.scandata.config;

/**
 * Created by gjr on 2015/11/30.
 */
public class Constants {
    //登录保存密码关键字
    public static final String Preferences_Login="LOGIN";
    public static final String Preferences_Login_UserInfo="USERINFO";
    public static final String Preferences_Login_PassWord="PASSWORD";
    public static final String Preferences_Login_IsCheck="ISCHECK";
    //请求码
    public static final int RequestCode_DishesMenuToMakeorder=1;
    public static final int ResultCode_MakeorderToDishesMenu_Back=1;
    public static final int ResultCode_MakeorderToDishesMenu_Clear=2;

    public static final int Handler_Dialog_Delay = 1;
    public static final int Handler_elay = 2;



    //订单支付类型类型:如果是现金结账传0，微信支付传4，支付宝支付传5
    public static final int Order_PayType_weixin = 4;
    public static final int Order_PayType_zhifubao = 5;
    public static final int Order_PayType_xianjin = 0;

    //volley框架最大请求尝试次数
    public static final int VOLLEY_MAX_RETRY_TIMES = 0;
}
