package solis3d.projectvaultbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.exceptions.UnauthorizedException;
import solis3d.projectvaultbackend.services.AppUsersService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenTools tokenTools;
    private final AppUsersService appUsersService;

    public TokenFilter(TokenTools tokenTools, AppUsersService appUsersService) {
        this.tokenTools = tokenTools;
        this.appUsersService = appUsersService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Inserire il token nell'Authorization header nel formato corretto");
            return;
        }

        String accessToken = authHeader.replace("Bearer ", "");

        tokenTools.verifyToken(accessToken);

        UUID userId = tokenTools.extractIdFromToken(accessToken);

        AppUser authenticatedUser = appUsersService.findById(userId);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + authenticatedUser.getRole().name()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String path = request.getServletPath();
        String method = request.getMethod();

        return method.equals("OPTIONS") ||
                pathMatcher.match("/auth/**", path) ||
                (
                        method.equalsIgnoreCase("GET") &&
                                (
                                        pathMatcher.match("/categories", path)
                                                || pathMatcher.match("/categories/**", path)
                                                || pathMatcher.match("/softwares", path)
                                                || pathMatcher.match("/softwares/**", path)
                                                || pathMatcher.match("/projects", path)
                                                || pathMatcher.match("/projects/**", path)
                                )
                );
    }
}
