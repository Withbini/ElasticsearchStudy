package com.example.demo.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebFilter
@Slf4j
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        chain.doFilter(httpServletRequest, response);
        log.info("[LOG_FILTER] IP : {}, URI : {}, REQUEST BODY : {}, REQUEST PARAM : {}",
                getRemoteAddr((HttpServletRequest) request), httpServletRequest.getRequestURI(),
                new String(httpServletRequest.getContentAsByteArray()), getRequestParamAsString(request.getParameterMap()));
    }

    private String getRequestParamAsString(Map<String, String[]> parameterMap) {
        StringBuilder body = new StringBuilder();
        parameterMap.forEach((key, values) -> {
            body.append(String.format("key:%s, value:%s\n", key, Arrays.toString(values)));
        });
        return body.toString();
    }

    private String getRemoteAddr(HttpServletRequest request) {
        return (null != request.getHeader("X-FORWARDED-FOR")) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
    }
}