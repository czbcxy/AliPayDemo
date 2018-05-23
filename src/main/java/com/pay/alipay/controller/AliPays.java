package com.pay.alipay.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pay.alipay.server.AliPayService;
import com.pay.alipay.vo.WebPayDetail;

@Controller
@RequestMapping("/alipay")
public class AliPays {

	private static final Logger logger = LoggerFactory.getLogger(AliPays.class);

	@Autowired
	private AliPayService alipayService;

	/**
	 * web pay
	 */
	@RequestMapping("/webpay")
	public void webpay(@RequestParam("orderNum") String orderNum, HttpServletResponse hsp) {
		WebPayDetail detail = new WebPayDetail(orderNum, "訂單為-" + orderNum, "0.01");
		String fields = alipayService.jointFields(detail);
		
		logger.info("web pay form: {}", fields);
		hsp.setContentType("text/html,charset=utf-8");
		hsp.setCharacterEncoding("UTF-8");
		try {
			hsp.getWriter().write(fields);
			hsp.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			logger.info("web resp form: {}","请求错误");
			e.printStackTrace();
		}
	}

}
