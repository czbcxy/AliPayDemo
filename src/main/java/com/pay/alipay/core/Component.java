package com.pay.alipay.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pay.alipay.common.security.MD5;
import com.pay.alipay.common.util.StringUtils;
import com.pay.alipay.enums.AlipayField;
import com.pay.alipay.enums.SignType;
import com.pay.alipay.vo.base.AliPay;

/**
 * Author:czb
 */
public abstract class Component {

    protected AliPay alipay;

    protected Component(AliPay alipay) {
        this.alipay = alipay;
    }

    /**
     * 构建MD5签名参数
     * @param payParams 支付参数
     */
    void buildMd5SignParams(Map<String, String> payParams) {
        String payString = buildSignString(payParams);
        String sign = MD5.generate(payString + alipay.secret, false).toLowerCase();
        payParams.put(AlipayField.SIGN_TYPE.field(), SignType.MD5.value());
        payParams.put(AlipayField.SIGN.field(), sign);
    }

    /**
     * 对签名参数过滤处理，出去值为"", null, sign, signType
     * @param signingParams 签名参数
     * @return 过滤后的签名参数
     */
    Map<String, String> filterSigningParams(Map<String, String> signingParams) {

        Map<String, String> validParams = new HashMap<>();

        for(Map.Entry<String, String> kv : signingParams.entrySet()){
            if (StringUtils.isNullOrEmpty(kv.getValue())
                    || AlipayField.SIGN.field().equals(kv.getKey())
                    || AlipayField.SIGN_TYPE.field().equals(kv.getKey())){
                continue;
            }
            validParams.put(kv.getKey(), kv.getValue());
        }

        return validParams;
    }

    /**
     * 把请求参数中的key/value组装成用与号连接的请求字符串，按key的字母升序排序
     * @param params 支付参数
     * @return key/value组装成用与号连接的请求字符串，按key的字母升序排序
     */
    public String buildSignString(Map<String, String> params) {
        return buildSignString(params, "");
    }

    /**
     * 把请求参数中的key/value组装成用与号连接的请求字符串，按key的字母升序排序
     * @param params 支付参数
     * @param wrapChar 值的包装字符，如APP支付需要加"
     * @return key/value组装成用与号连接的请求字符串，按key的字母升序排序
     */
    public String buildSignString(Map<String, String> params, String wrapChar) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder payString = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {
                //拼接时，不包括最后一个&字符
                payString.append(key).append("=").append(wrapChar).append(value).append(wrapChar);
            } else {
                payString.append(key).append("=").append(wrapChar).append(value).append(wrapChar).append("&");
            }
        }

        return payString.toString();
    }

    protected void putIfNotEmpty(Map<String, String> map, AlipayField field, String paramValue) {
        if (!StringUtils.isNullOrEmpty(paramValue)){
            map.put(field.field(), paramValue);
        }
    }
}
