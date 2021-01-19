package com.jzg.clue.api.auth;


import com.alibaba.fastjson.JSON;
import com.jzg.clue.api.system.AuthBiz;
import com.jzg.framework.core.vo.ResultVo;
import com.jzg.framework.core.vo.RetStatus;
import com.jzg.framework.web.auth.AuthService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description: 登录权限控制服务
 * @author: JZG
 * @date: 2016/12/1 18:50
 */
@Service("authService")
public class AuthServiceImpl implements AuthService {
    /**
     * logger
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    /**
     * 权限
     */
    @Resource
    private AuthBiz authBiz;

    @Override
    public boolean isLogin() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ResultVo res = authBiz.checkAuth(request);
        //LOGGER.info("[clue-api][AuthServiceImpl][isLogin] :权限验证,请求url={},Ip={},返回结果：ReturnInfo={}",  request.getRequestURI() + "?" + request.getQueryString(), request.getHeader("X-Real-IP"), JSON.toJSONString(res));
        return res.getStatus() == RetStatus.Ok.getValue();
    }

    @Override
    public boolean login() {
        return true;
    }

    @Override
    public boolean logout() {
        return false;
    }

    @Override
    public boolean isAuth() {
        return true;
    }

    @Override
    public String getLoginUrl() {
        return null;
    }

    @Override
    public String getHomeUrl() {
        return null;
    }


    @Override
    public String getNoAuthUrl() {
        return null;
    }

    @Override
    public String getRedirectUrl(String s) {
        String redirectUrl = "";
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        if (request.getRequestURI().equals(loginUrl)) {
//            redirectUrl = null;
//        }else {
//            redirectUrl = String.format("%s?backurl=%s", loginUrl, s);
//        }
        return redirectUrl;
    }

    @Override
    public boolean isAjax() {
        return false;
    }

    @Override
    public boolean isApi() {
        return false;
    }

    @Override
    public boolean isSign() {
        return false;
    }

}
