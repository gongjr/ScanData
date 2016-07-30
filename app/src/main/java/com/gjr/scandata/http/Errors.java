package com.gjr.scandata.http;

/**
 * ClassName: Errors <br/>
 * Function:  TODO ADD FUNCTION. <br/>
 * Reason:    TODO ADD REASON(可选). <br/>
 * date:      2013年12月21日 上午11:36:18 <br/>
 *
 * @author ChangYuan
 */
public enum Errors {
    /*----------公共错误----------*/
    exception_handler("2", "异常处理类捕捉到异常"), //ExceptionHandler使用的错误编码
    param_error("30", "参数错误"),
    param_missing("31", "相关参数为空"),
    param_format_error("32", "参数格式错误"),
    param_value_error("33", "参数值错误"),
    data_not_found("4", "没有符合条件的数据"),
    date_is_used("5", "已使用"),
    data_update_failed_all("6", "数据更新失败"),
    data_update_failed("60", "部分数据更新失败"),
    has_sensitive_words("7", "内容含有敏感词"),
    exception("9", "程序抛出异常"),
    md5_check_error("10", "签名校验失败"),
    error_404("404", "Not found"),
    error_400("400", "Bad Request"),
    error_500("500", "Internal Server Error"),
    missing_apikey("10001", "Missing apikey"),
    invalid_apikey("10002", "Invalid apikey"),
    missing_sign("10003", "Missing sign"),
    missing_timestamp("10004", "Missing timestamp"),
    request_timeout("10005", "Request timeout"),
    invalid_sign("10006", "Invalid sign"),
    out_of_service("10007", "班次已满"),
    user_or_password_invalid("101", "用户或密码不存在"),
    user_exist("102", "用户已存在"),
    iden_fail("103", "验证码错误"),
    register_fail("104", "注册失败"),
    push_fail("105", "发送失败"),
    add_fail("106", "添加失败"),
    checkin_fail("107", "签到失败"),
    not_found_nearbyM("108", "没有找到附近商家"),
    not_found_memberM("109", "没有找到会员商家"),
    this_msg_sended("111", "此消息已经发送了"),
    not_third_activity("112", "没有第三方的优惠活动"),
    user_had_sign("113", "用户已经签到"),
    sign_failure("114", "用户签到失败"),
    user_not_sign("115", "用户还未签到"),
    merchant_not_coupon("116", "没有商家优惠活动"),
    wishid_is_exist("117", "不能重复添加"),
    /*----------账户相关错误----------*/
    invalid_username_or_password("20001", "Invalid username or password"),//无效的用户名或密码
    rsa_decryption_failed("20002", "RSA decryption failed"), //解密失败
    cust_update_failed("20003", "用户信息更新失败"),
    head_path_not_set("20004", "未设置头像文件路径"),
    pic_format_error("20005", "图片格式错误"),
    pic_exceed_maximum_size("20006", "图片超过最大尺寸"),
    old_password_not_match("20007", "旧密码错误"),
    username_exists("20008", "用户名已存在"),
    username_not_exists("20009", "用户名不存在"),
    invalid_verify_code("22001", "无效的验证码"),
    verify_code_expired("22002", "验证码已过期"),
    verify_code_used("22003", "验证码已验证"),
    signup_bindsmstemp_error("22004", "注册用户后绑定短信模板失败"),

    /*----------邀请码相关错误----------*/
    invite_code_expired("21001", "邀请码已过期"),
    invite_code_used("21002", "邀请码已被使用"),
    invite_code_bind_failed("21004", "邀请码绑定失败"),
    invalid_invite_code("21005", "无效的邀请码"),
    cannot_invite_others("21006", "不能邀请其他人"),
    already_invited("21007", "被邀请人已经被邀请或已经注册"),

    /*----------短信相关错误----------*/
    sms_not_enough("30000", "短信余额不足"),
    sms_account_not_exist("30001", "账户不存在"),
    sms_template_already_binded("30002", "短信模板已经与用户被绑定"),
    exceed_max_sms_drafts("30003", "短信草稿箱已满");

    private String code;
    private String msg;

    // 枚举对象构造函数
    private Errors(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String toJson() {
        return "{\"errcode\":\"" + code + "\",\"msg\":\"" + msg + "\"}";
    }
}

