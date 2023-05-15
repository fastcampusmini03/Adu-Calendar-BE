package com.fastcampus03.calendarbe.core.config;

import com.fastcampus03.calendarbe.core.filter.JwtVerifyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MyFilterRegisterConfig {
    @Bean
    public FilterRegistrationBean<?> filter1() {
        FilterRegistrationBean<JwtVerifyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtVerifyFilter()); // 서블릿 필터 객체 담기
        registration.addUrlPatterns("/annualDuty");
        registration.setOrder(1); // 순서
        return registration;
    }
}
