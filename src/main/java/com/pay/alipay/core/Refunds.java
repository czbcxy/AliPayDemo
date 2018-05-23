package com.pay.alipay.core;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pay.alipay.common.date.Dates;
import com.pay.alipay.common.exception.XmlException;
import com.pay.alipay.common.http.Http;
import com.pay.alipay.common.util.Preconditions;
import com.pay.alipay.common.util.StringUtils;
import com.pay.alipay.common.xml.XmlReaders;
import com.pay.alipay.enums.AlipayField;
import com.pay.alipay.enums.Service;
import com.pay.alipay.exception.AliPayException;
import com.pay.alipay.vo.RefundDetail;
import com.pay.alipay.vo.RefundDetailData;
import com.pay.alipay.vo.base.AliPay;

/**
 * 退款组件
 * Author: haolin
 * Email: haolin.h0@gmail.com
 * Date: 22/11/15
 */
public class Refunds extends Component {

    public Refunds(AliPay alipay){
        super(alipay);
    }

    /**
     * 发起退款请求
     * @param refundDetail 退款明细
     * @return 退款是否提交成功(不表示实际退款是否, 需从支付宝退款通知中来确认)
     */
    public Boolean refund(RefundDetail refundDetail){
        try {
            String url = AliPay.GATEWAY + "_input_charset=" + alipay.inputCharset;
            Map<String, String> refundParams = buildRefundParams(refundDetail);
            String resp = Http.get(url).params(refundParams).request();
            XmlReaders reader = XmlReaders.create(resp);
            String isSuccess = reader.getNodeStr("is_success");
            if ("T".equals(isSuccess)){
                return Boolean.TRUE;
            }
        } catch (XmlException e){
            // ignore xml parse error
        } catch (Exception e){
            throw new AliPayException(e);
        }
        return Boolean.FALSE;
    }

    private Map<String, String> buildRefundParams(RefundDetail refundDetail) {

        Map<String, String> refundParams = new HashMap<>();

        // 配置参数
        refundParams.putAll(alipay.refundConfig);

        // 接口名
        refundParams.put(AlipayField.SERVICE.field(), Service.REFUND_NO_PWD.value());

        if (!StringUtils.isNullOrEmpty(alipay.email)){
            refundParams.put(AlipayField.SELLER_EMAIL.field(), alipay.email);
        }

        // 通知URL
        if (!StringUtils.isNullOrEmpty(refundDetail.getNotifyUrl())){
            refundParams.put(AlipayField.NOTIFY_URL.field(), refundDetail.getNotifyUrl());
        }

        // 卖家ID
        refundParams.put(AlipayField.SELLER_USER_ID.field(), alipay.merchantId);

        // 退款日期
        refundParams.put(AlipayField.REFUND_DATE.field(), Dates.now("yyyy-MM-dd HH:mm:ss"));

        // 退款批次号
        Preconditions.checkNotNullAndEmpty(refundDetail.getBatchNo(), "batchNo");
        refundParams.put(AlipayField.BATCH_NO.field(), refundDetail.getBatchNo());

        // 退款明细
        List<RefundDetailData> detailDatas = refundDetail.getDetailDatas();
        Preconditions.checkNotNullAndEmpty(detailDatas, "detail datas");
        refundParams.put(AlipayField.BATCH_NUM.field(), Integer.toString(detailDatas.size()));
        refundParams.put(AlipayField.DETAIL_DATA.field(), refundDetail.formatDetailDatas());

        // md5签名参数
        buildMd5SignParams(refundParams);

        return refundParams;
    }
}
