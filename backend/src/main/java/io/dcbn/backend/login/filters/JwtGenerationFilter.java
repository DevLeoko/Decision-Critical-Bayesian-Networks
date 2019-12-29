package io.dcbn.backend.login.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.dcbn.backend.login.models.LoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtGenerationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final String secret;

  public JwtGenerationFilter(AuthenticationManager authenticationManager, String secret) {
    this.authenticationManager = authenticationManager;
    this.secret = secret;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    ObjectMapper mapper = new JsonMapper();
    try {
      LoginRequest loginRequest = mapper.readValue(request.getReader(), LoginRequest.class);
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          loginRequest.getUsername(), loginRequest.getPassword());
      return authenticationManager.authenticate(authenticationToken);
    } catch (IOException ex) {
      logger.warn("Couldn't parse login request!", ex);
      throw new AuthenticationServiceException("Couldn't parse login request!", ex);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException {
    User user = (User) authResult.getPrincipal();
    List<String> roles = user.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    byte[] key = secret.getBytes();
    String accessToken = Jwts.builder().setSubject(user.getUsername())
        .setHeaderParam("typ", "jwt")
        .signWith(Keys.hmacShaKeyFor(key), SignatureAlgorithm.HS512)
        .claim("rol", roles)
        .compact();

    response.setContentType("text/plain");
    response.getWriter().write(accessToken);
  }
}
