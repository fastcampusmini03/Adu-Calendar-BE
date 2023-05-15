package com.fastcampus03.calendarbe.core.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fastcampus03.calendarbe.core.auth.jwt.MyJwtProvider;
import com.fastcampus03.calendarbe.core.auth.session.MyUserDetails;
import com.fastcampus03.calendarbe.core.exception.Exception401;
import com.fastcampus03.calendarbe.dto.ResponseDTO;
import com.fastcampus03.calendarbe.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class JwtVerifyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("디버그 : JwtVerigyFilter 동작함");

        // 1. 다운캐스팅
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 2. 헤더 검증
        String prefixJwt = req.getHeader(MyJwtProvider.HEADER);
        if (prefixJwt == null) {
            chain.doFilter(req, resp);
            return;
        }

        // 3. Bearer 제거
        String jwt = prefixJwt.replace(MyJwtProvider.TOKEN_PREFIX, "");

        try {
            // 4. 검증
            DecodedJWT decodedJWT = MyJwtProvider.verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            String role = decodedJWT.getClaim("role").asString();

            // 5. Authentication user 정보 담기
            User user = User.builder().id(id).role(role).build();
            MyUserDetails myUserDetails = new MyUserDetails(user);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            myUserDetails,
                            myUserDetails.getPassword(),
                            myUserDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("디버그 : 인증 객체 만들어짐");

            // 6. 다음 필터로 가!! - 없으면 DS로!!
            chain.doFilter(req, resp);
        } catch (SignatureVerificationException sve){
            error(resp, sve);
        }catch (TokenExpiredException tee){
            error(resp, tee);
        }
    }

    private void error(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json; charset=utf-8");
        ResponseDTO<?> responseDTO = new ResponseDTO<>(HttpStatus.UNAUTHORIZED, "인증 안됨", e.getMessage());
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(responseDTO);
        resp.getWriter().println(responseBody);
    }
}
