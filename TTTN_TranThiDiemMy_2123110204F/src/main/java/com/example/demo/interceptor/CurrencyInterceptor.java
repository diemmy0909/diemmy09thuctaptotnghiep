package com.example.demo.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CurrencyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String currencyParam = request.getParameter("currency");
        HttpSession session = request.getSession();

        if (currencyParam != null && (currencyParam.equalsIgnoreCase("VND") || currencyParam.equalsIgnoreCase("USD"))) {
            session.setAttribute("currency", currencyParam.toUpperCase());
        } else if (session.getAttribute("currency") == null) {
            // Default currency
            session.setAttribute("currency", "VND");
        }
        return true;
    }
}
