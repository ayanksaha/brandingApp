package com.lb.brandingApp.auth.filter;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.auth.data.models.common.PermissionDto;
import com.lb.brandingApp.auth.data.models.common.UserExtension;
import com.lb.brandingApp.auth.repository.UserRepository;
import com.lb.brandingApp.auth.service.JwtUtilsService;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static com.lb.brandingApp.app.constants.ApplicationConstants.AUTHORIZATION_HEADER;
import static com.lb.brandingApp.app.constants.ApplicationConstants.USER_NOT_FOUND;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtilsService jwtUtilsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.isNotBlank(authHeader) &&
            (Objects.isNull(SecurityContextHolder.getContext().getAuthentication()) ||
                Objects.isNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal()))) {
            String jws = jwtUtilsService.getJws(authHeader);
            Claims claims = jwtUtilsService.parseIdToken(jws);
            Date date = claims.getExpiration();
            String username = jwtUtilsService.getUsernameFromIdClaims(claims);
            User userInDb = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
            Team team = userInDb.getTeam();

            Set<PermissionDto> permissions = jwtUtilsService.getPermissionsFromIdClaims(claims);

            UserExtension user = new UserExtension(userInDb.getUsername(), userInDb.getPassword(),
                    List.of(new SimpleGrantedAuthority(team.getDescription().description())),
                    userInDb.getName(),
                    userInDb.getEmail(),
                    userInDb.getPhoneNumber(),
                    permissions,
                    team.getHomePageUri(),
                    userInDb.isActive(),
                    userInDb.isDefaultPass());

            if(permissions.stream().anyMatch(
                    permission -> permission.isHttpResource()
                            && Pattern.compile(permission.getResourceUri()).matcher(request.getServletPath()).matches()
                        && request.getMethod().equalsIgnoreCase(permission.getHttpMethod()))
                        && date.after(Date.from(Instant.now()))) {
                Authentication authentication =
                        UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

}
