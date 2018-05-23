package com.pay.alipay.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext;

import com.pay.alipay.common.security.RSA;
import com.pay.alipay.common.util.Preconditions;
import com.pay.alipay.enums.AlipayField;
import com.pay.alipay.enums.Service;
import com.pay.alipay.enums.SignType;
import com.pay.alipay.exception.AliPayException;
import com.pay.alipay.vo.WebPayDetail;
import com.pay.alipay.vo.base.AliPay;
import com.pay.alipay.vo.base.PayDetail;

/**
 * Pay Component Author: {@link ContainerElementNodeBuilderCustomizableContext}
 */
public class Pays extends Component {

	public Pays(AliPay alipay) {
		super(alipay);
	}

	/**
	 * WEB支付
	 * 
	 * @param webPayDetail
	 *            支付字段信息
	 * @return 自动提交表单(可直接输出到浏览器)
	 */
	public String webPay(WebPayDetail webPayDetail) {
		Map<String, String> payParams = buildWebPayParams(webPayDetail);
		return buildPayForm(payParams);
	}

	/**
	 * 构建PC支付参数
	 * 
	 * @param webPayDetail
	 *            字段集合
	 * @return WEB支付参数
	 */
	private Map<String, String> buildWebPayParams(WebPayDetail webPayDetail) {

		// 公共参数
		Map<String, String> webPayParams = buildPayParams(webPayDetail, Service.WEB_PAY);

		// PC特有参数
		putIfNotEmpty(webPayParams, AlipayField.EXTER_INVOKE_IP, webPayDetail.getExterInvokeIp());
		putIfNotEmpty(webPayParams, AlipayField.ANTI_PHISHING_KEY, webPayDetail.getAntiPhishingKey());
		putIfNotEmpty(webPayParams, AlipayField.ERROR_NOTIFY_URL, webPayDetail.getErrorNotifyUrl());
		putIfNotEmpty(webPayParams, AlipayField.EXTRA_COMMON_PARAM, webPayDetail.getExtraCommonParam());

		// md5签名参数
		buildMd5SignParams(webPayParams);

		return webPayParams;
	}

	/**
	 * 构建支付表单
	 * 
	 * @param payParams
	 *            支付参数
	 * @return 支付表单
	 */
	private String buildPayForm(Map<String, String> payParams) {
		StringBuilder form = new StringBuilder();

		form.append("<form id=\"pay_form\" name=\"pay_form\"").append(" action=\"" + AliPay.GATEWAY)
				.append(AlipayField.INPUT_CHARSET + "=").append(alipay.inputCharset).append("\" method=\"POST\">");
		for (Map.Entry<String, String> param : payParams.entrySet()) {
			form.append("<input type=\"hidden\" name=\"").append(param.getKey()).append("\" value=\"")
					.append(param.getValue()).append("\" />");
		}
		form.append("<input type=\"submit\" value=\"去支付\" style=\"display:none;\" />");
		form.append("</form>");
		form.append("<script>document.forms['pay_form'].submit();</script>");

		return form.toString();
	}

	/**
	 * 构建RSA签名参数
	 * 
	 * @param payParams
	 *            支付参数
	 * @return RSA签名后的支付字符串
	 */
	@SuppressWarnings("unused")
	private String buildRsaPayString(Map<String, String> payParams) {
		String payString = buildSignString(payParams, "\"");
		String sign = RSA.sign(payString, alipay.appPriKey, alipay.inputCharset);
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AliPayException("sign encode failed", e);
		}
		payString += ("&sign_type=\"" + SignType.RSA.value() + "\"&sign=\"" + sign + "\"");
		return payString;
	}

	/**
	 * 构建支付公共参数
	 * 
	 * @param payDetail
	 *            字段
	 * @param service
	 *            服务接口
	 * @return PC和WAP公共支付参数
	 */
	private Map<String, String> buildPayParams(PayDetail payDetail, Service service) {

		Map<String, String> payParams = new HashMap<>();

		// 配置参数
		payParams.putAll(alipay.payConfig);

		// 业务参数
		payParams.put(AlipayField.SERVICE.field(), service.value());

		Preconditions.checkNotNullAndEmpty(payDetail.getOutTradeNo(), "outTradeNo");
		payParams.put(AlipayField.OUT_TRADE_NO.field(), payDetail.getOutTradeNo());

		Preconditions.checkNotNullAndEmpty(payDetail.getOrderName(), "orderName");
		payParams.put(AlipayField.SUBJECT.field(), payDetail.getOrderName());

		Preconditions.checkNotNullAndEmpty(payDetail.getTotalFee(), "totalFee");
		payParams.put(AlipayField.TOTAL_FEE.field(), payDetail.getTotalFee());

		putIfNotEmpty(payParams, AlipayField.NOTIFY_URL, payDetail.getNotifyUrl());
		putIfNotEmpty(payParams, AlipayField.RETURN_URL, payDetail.getReturnUrl());

		return payParams;
	}

}
