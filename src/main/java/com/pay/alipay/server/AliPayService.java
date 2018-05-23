package com.pay.alipay.server;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pay.alipay.core.AlipayBuilder;
import com.pay.alipay.vo.RefundDetail;
import com.pay.alipay.vo.WebPayDetail;
import com.pay.alipay.vo.base.AliPay;

@Service
public class AliPayService {
	static  Logger l = LoggerFactory.getLogger(AliPayService.class);
	@Value("${merchantId}")
    private String merchantId;

    @Value("${secret}")
    private String secret;

    @Value("${payNotifyUrl}")
    private String payNotifyUrl;

    @Value("${refundNotifyUrl}")
    private String refundNotifyUrl;

    @Value("${webReturnUrl}")
    private String webReturnUrl;

    private AliPay alipay;
    @PostConstruct
    public void initAlipay(){
        alipay = AlipayBuilder
                    .newBuilder(merchantId, secret)
                    .build();
        l.info("alipay: {}",alipay);
    }
	
	public String jointFields(WebPayDetail detail) {
		detail.setNotifyUrl(payNotifyUrl);
		detail.setReturnUrl(webReturnUrl);
		return alipay.pay().webPay(detail);
	}
	
	 /**
     * 退款申请
     */
    public Boolean refund(RefundDetail detail){
        detail.setNotifyUrl(refundNotifyUrl);
        return alipay.refund().refund(detail);
    }

    
    /**
     * MD5验证
     */
    public Boolean notifyVerifyMd5(Map<String, String> params){
        return alipay.verify().md5(params);
    }
}
