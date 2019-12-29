package io.dcbn.backend.login.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final String secret;

  public JwtAuthorizationFilter(String secret) {
    this.secret = secret;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {
    String header = httpServletRequest.getHeader("Authorization");
    if (header == null || header.isEmpty() || !header.startsWith("Bearer")) {
      httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String token = header.substring(7);

    try {
      Claims claims = Jwts.parser()
          .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
          .parseClaimsJws(token)
          .getBody();

      String username = claims.getSubject();
      List<GrantedAuthority> authorities = ((List<?>) claims.get("rol")).stream()
          .map(authority -> new SimpleGrantedAuthority((String) authority))
          .collect(Collectors.toList());
      Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
          authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (ExpiredJwtException exception) {
      logger.warn("Request to parse expired JWT", exception);
    } catch (UnsupportedJwtException exception) {
      logger.warn("Request to parse unsupported JWT", exception);
    } catch (MalformedJwtException exception) {
      logger.warn("Request to parse invalid JWT", exception);
    } catch (IllegalArgumentException exception) {
      logger.warn("Request to parse empty or null JWT", exception);
    }

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

}
