package com.qamet.book_store.security;

import com.qamet.book_store.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private final JwtParser jwtParser;

    private final Key key;

    private final UserRepository userRepository;

    @Value("${security.jwt.validity-in-seconds}")
    private long tokenValidityInSeconds;

    public TokenProvider(
            @Value("${security.jwt.base64-secret-key}") String base64Secret,
            UserRepository userRepository) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        this.userRepository = userRepository;
    }

    public String createToken(String username) {

        long now = (new Date()).getTime();
        Date validity = new Date(now + (this.tokenValidityInSeconds * 1000));

        return Jwts
                .builder()
                .setSubject(username)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        Optional<com.qamet.book_store.entity.User> optionalUser = userRepository.findByUsername(claims.getSubject());

        if (optionalUser.isEmpty()) {
            return null;
        }

        com.qamet.book_store.entity.User user = optionalUser.get();

        List<GrantedAuthority> grantedAuthorities = user
                .getRoles()
                .stream()
                .map(authority -> new SimpleGrantedAuthority("ROLE_".concat(authority.getName())))
                .collect(Collectors.toList());

        grantedAuthorities.forEach(System.out::println);

        User principal = new User(user.getUsername(), "", grantedAuthorities);

        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    public boolean validateToken(String authToken) {
        try {
            jwtParser.parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ignored) {
        }
        return false;
    }
}
