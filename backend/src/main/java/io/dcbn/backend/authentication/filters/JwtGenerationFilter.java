package io.dcbn.backend.authentication.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.dcbn.backend.authentication.models.LoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.util.Calendar;
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
  private final int tokenDurationInMinutes;

  public JwtGenerationFilter(AuthenticationManager authenticationManager, String secret, int tokenDurationInMinutes) {
    this.authenticationManager = authenticationManager;
    this.secret = secret;
    this.tokenDurationInMinutes = tokenDurationInMinutes;
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

    Calendar expireTime = Calendar.getInstance();
    expireTime.add(Calendar.MINUTE, tokenDurationInMinutes);

    String accessToken = Jwts.builder().setSubject(user.getUsername())
        .setHeaderParam("typ", "jwt")
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
        .setExpiration(expireTime.getTime())
        .claim("rol", roles)
        .compact();

    response.setContentType("text/plain");
    response.getWriter().write(accessToken);
  }
}
