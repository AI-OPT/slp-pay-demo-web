package com.ai.runner.center.pay.web.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ai.runner.center.pay.web.demo.util.ConfigUtil;
import com.ai.runner.center.pay.web.demo.util.DateUtil;
import com.ai.runner.center.pay.web.demo.util.HTTPUtil;
import com.ai.runner.center.pay.web.demo.util.PaymentUtil;
import com.ai.runner.center.pay.web.demo.util.VerifyUtil;

/**
 * demo Date: 2015年12月2日 <br>
 * Copyright (c) 2015 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
@Controller
@RequestMapping(value = "/demo")
public class DemoController {

    private static final Logger LOG = Logger.getLogger(DemoController.class);

    /**
     * 首页
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author LiangMeng
     * @ApiDocMethod
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("首页...");
        /* 1.组织参数 */
        String basePath = request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort() + request.getContextPath();
        
        String tenantId = ConfigUtil.getProperty("TENANT_ID");
        String orderId = DateUtil.getFormatDate(new Date(), "yyyyMMddhhmmss");
        String returnUrl = basePath+"/demo/returnUrl";
        String notifyUrl = basePath+"/demo/notifyUrl";
        String orderAmount = "0.01";
        String subject ="苹果 6 plus plus";
        request.setAttribute("tenantId", tenantId);
        request.setAttribute("orderId", orderId);
        request.setAttribute("returnUrl", returnUrl);
        request.setAttribute("notifyUrl", notifyUrl);
        request.setAttribute("orderAmount", orderAmount);
        request.setAttribute("subject", subject);
        return "/demo/index";
    }

    @RequestMapping(value = "/gotoPay")
    public void gotoPay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tenantId= request.getParameter("tenantId" );
        String orderId= request.getParameter("orderId");
        String returnUrl= request.getParameter("returnUrl");
        String notifyUrl= request.getParameter("notifyUrl");
        String orderAmount= request.getParameter("orderAmount");
        String subject= request.getParameter("subject");
        String requestSource= request.getParameter("requestSource");
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("tenantId", tenantId);
        map.put("orderId", orderId);
        map.put("returnUrl", returnUrl);
        map.put("notifyUrl", notifyUrl);
        map.put("requestSource", requestSource);
        map.put("orderAmount", String.valueOf(orderAmount));
        map.put("subject", subject);
        // 加密
        String infoStr = orderId+ VerifyUtil.SEPARATOR
                + orderAmount + VerifyUtil.SEPARATOR
                + notifyUrl + VerifyUtil.SEPARATOR
                + tenantId;
        String infoMd5 = VerifyUtil.encodeParam(infoStr, ConfigUtil.getProperty("REQUEST_KEY"));
        map.put("infoMd5", infoMd5);
        LOG.info("开始前台通知:" + map);
        String htmlStr = PaymentUtil.generateAutoSubmitForm(ConfigUtil.getProperty("ACTION_URL"), map);
        LOG.info("发起支付申请:" + htmlStr);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(htmlStr); 
    }

    
    @RequestMapping(value = "/appPay")
    public void appPay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tenantId= request.getParameter("tenantId" );
        String orderId= request.getParameter("orderId");
        String returnUrl= request.getParameter("returnUrl");
        String notifyUrl= request.getParameter("notifyUrl");
        String payOrgCode= request.getParameter("payOrgCode");
        String orderAmount= request.getParameter("orderAmount");
        String subject= request.getParameter("subject");
        String requestSource= request.getParameter("requestSource");
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("tenantId", tenantId);
        map.put("orderId", orderId);
        map.put("returnUrl", returnUrl);
        map.put("notifyUrl", notifyUrl);
        map.put("requestSource", requestSource);
        map.put("payOrgCode", payOrgCode);
        map.put("orderAmount", String.valueOf(orderAmount));
        map.put("subject", subject);
        // 加密
        String infoStr = orderId+ VerifyUtil.SEPARATOR
                + orderAmount + VerifyUtil.SEPARATOR
                + notifyUrl + VerifyUtil.SEPARATOR
                + tenantId;
        String infoMd5 = VerifyUtil.encodeParam(infoStr, ConfigUtil.getProperty("REQUEST_KEY"));
        map.put("infoMd5", infoMd5);
        LOG.info("APP支付:" + map);
        String htmlStr = HTTPUtil.post(ConfigUtil.getProperty("APP_ACTION_URL"), map);
        LOG.info("APP支付返回:" + htmlStr);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(htmlStr); 
    }
    /**
     * 前台返回页面demo
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author LiangMeng
     * @ApiDocMethod
     */
    @RequestMapping(value = "/returnUrl")
    public String returnUrl(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        LOG.info("前台回调...");
        return "/demo/returnUrl";
    }

    /**
     * 后台通知demo
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author LiangMeng
     * @ApiDocMethod
     */
    @RequestMapping(value = "/notifyUrl")
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        LOG.info("后台回调...");

    }
}
