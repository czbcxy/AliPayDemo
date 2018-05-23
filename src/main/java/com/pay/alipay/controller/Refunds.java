package com.pay.alipay.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.alipay.common.date.Dates;
import com.pay.alipay.server.AliPayService;
import com.pay.alipay.vo.RefundDetail;
import com.pay.alipay.vo.RefundDetailData;

/**
 * 退款
 * Author: czb
 */
@Controller
@RequestMapping("/refunds")
public class Refunds {

    private static final Logger logger = LoggerFactory.getLogger(Refunds.class);

    @Autowired
    private AliPayService alipayService;

    /**
     * 提交退款请求
     * @param batchNo 批次号(前面会加日期)，如0001
     *        {@link me.hao0.alipay.model.enums.AlipayField#BATCH_NO}
     * @param tradeNo 支付宝交易号，如2015112321001004010248104122
     * @return 是否提交成功
     */
    @RequestMapping("/submit")
    @ResponseBody
    public Boolean submit(
            @RequestParam("batchNo") String batchNo,
            @RequestParam("tradeNo") String tradeNo){
        RefundDetail detail = new RefundDetail();
        detail.setBatchNo(Dates.now("yyyyMMdd") + batchNo);
        RefundDetailData data = new RefundDetailData();
        data.setTradeNo(tradeNo);
        data.setFee("0.01");
        data.setReason("订单取消");
        detail.setDetailDatas(Arrays.asList(data));
        logger.info("refund submit: {}", detail);
        return alipayService.refund(detail);
    }
}
