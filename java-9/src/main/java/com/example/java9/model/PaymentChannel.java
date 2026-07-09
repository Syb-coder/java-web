package com.example.java9.model;  // 当前枚举所在的包路径，归属于 model 实体/枚举层

/**
 * 支付渠道枚举
 * <p>
 * 底层支付业务对接的支付通道，模拟真实第三方支付：
 * - ALIPAY：支付宝通道；
 * - WECHAT：微信支付通道；
 * - BANK：银行卡直连通道。
 * </p>
 */
public enum PaymentChannel {  // 支付渠道枚举，决定路由策略、手续费率与对账文件来源
    /** 支付宝 */
    ALIPAY,  // 支付宝通道：对接支付宝开放平台，支持 App/H5/扫码
    /** 微信支付 */
    WECHAT,  // 微信支付通道：对接微信商户平台，支持 JSAPI/App/扫码
    /** 银行卡 */
    BANK     // 银行卡直连通道：走银联或直连银行，处理大额支付场景
}
