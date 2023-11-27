package com.github.dadogk.config;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Component
@Order(1)
@Log4j2
public class InLogFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String ip = ((HttpServletRequest) request).getHeader("X-Real-IP");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        String requestType = ((HttpServletRequest) request).getMethod();
        String contentType = ((HttpServletRequest) request).getContentType();
        String url = ((HttpServletRequest) request).getRequestURL().toString();
        String query = ((HttpServletRequest) request).getQueryString();
        log.info("doFilter: ip={}, method={}, contentType={}, url={}, query={}",
                ip, requestType, contentType, url, query);

        chain.doFilter(request, response);
    }
}
