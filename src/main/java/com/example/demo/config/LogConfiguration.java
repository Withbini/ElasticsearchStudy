package com.example.demo.config;

import com.example.demo.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfiguration {
    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<LogFilter> filterRegistrationBean =
                new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        //filterRegistrationBean.addUrlPatterns("/members", "");
        return filterRegistrationBean;
    }
}
