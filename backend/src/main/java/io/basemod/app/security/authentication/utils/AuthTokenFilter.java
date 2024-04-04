package io.basemod.app.security.authentication.utils;


import io.basemod.app.security.authentication.domain.BaseUser;
import io.basemod.app.security.authentication.domain.BaseUserController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BaseUserController baseUserController;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            logger.debug("AuthtokenFilter tries to extract jwt from request and set authentication!");
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                logger.debug("\t...extracted valid jwt with length {} from request to {}", jwt.length(), request.getRequestURI());
                String uniqueUserId = jwtUtils.getUniqueUserIdFromJwtToken(jwt);

                logger.debug("\t\t..extracted unique user ID {}", uniqueUserId);
                BaseUser userDetails = baseUserController.getBaseUserUsingUniqueId(uniqueUserId);
                if(userDetails!=null) {
                    logger.debug("\t\t..got user details for unique user ID {}, e.g. having {} roles"
                            , uniqueUserId, userDetails.getRolesForSecurityContext().size());
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getRolesForSecurityContext());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.debug("\t\t..set authentication context");
                } else {
                    logger.error("user jwt token is invalid!, can not set security context!");
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
