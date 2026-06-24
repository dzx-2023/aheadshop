package com.aheadshop.common.core.exception;

public final class BusinessExceptionCode {

    private BusinessExceptionCode() {}

    // 通用
    public static final int PARAM_ERROR = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;
    public static final int SYSTEM_ERROR = 500;

    // 用户模块 10xxx
    public static final int USER_EXISTS = 10001;
    public static final int USER_NOT_FOUND = 10002;
    public static final int PASSWORD_ERROR = 10003;
    public static final int ADDRESS_LIMIT = 10004;

    // 商品模块 20xxx
    public static final int PRODUCT_NOT_FOUND = 20001;
    public static final int STOCK_NOT_ENOUGH = 20002;
    public static final int PRODUCT_OFF_SHELF = 20003;

    // 订单模块 30xxx
    public static final int ORDER_NOT_FOUND = 30001;
    public static final int ORDER_STATUS_ERROR = 30002;
    public static final int CART_EMPTY = 30003;

    // 支付模块 40xxx
    public static final int PAY_CREATE_FAIL = 40001;
    public static final int PAY_ORDER_EXPIRED = 40002;

    // AI客服模块 50xxx
    public static final int CHAT_SESSION_NOT_FOUND = 50001;
    public static final int LLM_CALL_FAILED = 50002;
    public static final int LLM_TIMEOUT = 50003;
    public static final int LLM_INVALID_KEY = 50004;
    public static final int CHAT_RATE_LIMIT = 50005;
    public static final int CHAT_DAILY_LIMIT = 50006;

    // 签到积分模块 60xxx
    public static final int SIGN_ALREADY_DONE = 60001;
    public static final int SIGN_RETRO_LIMIT = 60002;
    public static final int SIGN_RETRO_DATE_INVALID = 60003;
    public static final int SIGN_POINTS_NOT_ENOUGH = 60004;

    // 分销模块 70xxx
    public static final int DISTRIBUTOR_NOT_FOUND   = 70001;
    public static final int INVITE_CODE_INVALID     = 70002;
    public static final int SELF_INVITE_NOT_ALLOWED = 70004;
    public static final int ALREADY_BINDDED         = 70005;
    public static final int COMMISSION_NOT_ENOUGH   = 70006;
    public static final int WITHDRAW_MIN_AMOUNT     = 70007;
    public static final int DISTRIBUTOR_DISABLED    = 70008;
    public static final int NO_REFERRER_FOUND       = 70009;
    public static final int UNBIND_NOT_ALLOWED      = 70010;

}