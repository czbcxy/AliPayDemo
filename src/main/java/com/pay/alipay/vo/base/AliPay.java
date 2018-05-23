package com.pay.alipay.vo.base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;

import com.pay.alipay.core.Component;
import com.pay.alipay.core.Pays;
import com.pay.alipay.core.Refunds;
import com.pay.alipay.core.Verifies;
import com.pay.alipay.enums.AlipayField;
import com.pay.alipay.enums.PayMethod;
import com.pay.alipay.enums.PaymentType;


/**
 * 支付宝支付参数拼装
 * Author: czb
 */
public final class AliPay {
	public AliPay(String merchantId, String secret) {
		this.merchantId = merchantId;
		this.secret = secret;
	}

	public AliPay start() {
		initConfig();
		initComponents();
		return this;
	}

	/**
	 * 支付配置参数，不需每次请求都生成
	 */
	public Map<String, String> payConfig;

	/**
	 * 退款配置参数，不需每次请求都生成
	 */
	public Map<String, String> refundConfig;

	Map<String, Component> components = new HashMap<>();

	public Pays pay() {
		return (Pays) components.get(Pays.class.getSimpleName());
	}

	 public Refunds refund() {
		 return (Refunds) components.get(Refunds.class.getSimpleName());
	 }
	
	 public Verifies verify() {
		 return (Verifies) components.get(Verifies.class.getSimpleName());
	 }
	@Bean
	private void initComponents() {
		components.put(Pays.class.getSimpleName(), new Pays(this));
		components.put(Refunds.class.getSimpleName(), new Refunds(this));
		components.put(Verifies.class.getSimpleName(), new Verifies(this));
	}

	private void initConfig() {
		payConfig = new HashMap<>();
		payConfig.put(AlipayField.PARTNER.field(), merchantId);
		payConfig.put(AlipayField.SELLER_ID.field(), merchantId);
		payConfig.put(AlipayField.PAYMENT_TYPE.field(), paymentType);
		payConfig.put(AlipayField.IT_B_PAY.field(), expired);
		payConfig.put(AlipayField.INPUT_CHARSET.field(), inputCharset);

		refundConfig = new HashMap<>();
		refundConfig.put(AlipayField.PARTNER.field(), merchantId);
		refundConfig.put(AlipayField.INPUT_CHARSET.field(), inputCharset);
		refundConfig.put(AlipayField.SELLER_USER_ID.field(), merchantId);
	}

	/**
	 * 支付宝网关
	 */
	public static final String GATEWAY = "https://mapi.alipay.com/gateway.do?";

	/**
	 * 签约的支付宝账号对应的支付宝唯一用户号，以2088开头的16位纯数字组成。
	 */
	public String merchantId;

	/**
	 * 商户密钥
	 */
	public String secret;

	/**
	 * 商户邮箱帐号
	 */
	public String email;

	/**
	 * 商户网站使用的编码格式，如utf-8、gbk、gb2312等
	 */
	public String inputCharset = "UTF-8";

	/**
	 * 支付类型
	 */
	public String paymentType = PaymentType.BUY.value();

	/**
	 * 默认支付方式
	 */
	public String payMethod = PayMethod.DIRECT_PAY.value();

	/**
	 * 支付超时时间
	 */
	public String expired = "1h";

	/**
	 * APP RSA私钥
	 */
	public String appPriKey;

	/**
	 * APP RSA公钥
	 */
	public String appPubKey;

	/**
	 * 二维码 RSA私钥
	 */
	public String qrPriKey;

	/**
	 * 二维码 RSA公钥
	 */
	public String qrPubKey;

	@Override
	public String toString() {
		return "Alipay{" + "merchantId='" + merchantId + '\'' + ", secret='" + secret + '\'' + ", inputCharset='"
				+ inputCharset + '\'' + ", paymentType='" + paymentType + '\'' + ", payMethod='" + payMethod + '\''
				+ ", expired='" + expired + '\'' + ", appPriKey='" + appPriKey + '\'' + ", appPubKey='" + appPubKey
				+ '\'' + ", qrPriKey='" + qrPriKey + '\'' + ", qrPubKey='" + qrPubKey + '\'' + '}';
	}
}
