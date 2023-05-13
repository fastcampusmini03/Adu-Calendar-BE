package com.fastcampus03.calendarbe.core.filter;

import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class MyTempFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("디버그 : MyTempFilter 동작");
        chain.doFilter(request, response);
    }
}
