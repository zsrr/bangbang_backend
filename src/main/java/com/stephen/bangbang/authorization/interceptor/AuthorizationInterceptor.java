package com.stephen.bangbang.authorization.interceptor;

import com.stephen.bangbang.Constants;
import com.stephen.bangbang.authorization.Authorization;
import com.stephen.bangbang.authorization.TokenManager;
import com.stephen.bangbang.authorization.TokenModel;
import com.stephen.bangbang.exception.user.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private TokenManager tokenManager;

    @Autowired
    public AuthorizationInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if (method.getAnnotation(Authorization.class) == null && handlerMethod.getBeanType().getAnnotation(Authorization.class) == null)
            return true;

        String token = request.getHeader(Constants.AUTHORIZATION_HEADER);
        TokenModel model = tokenManager.getToken(token);
        if (tokenManager.checkToken(model)) {
            request.setAttribute(Constants.CURRENT_USER_ID, model.getUserId());
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new UnAuthorizedException();
        }
    }
}
