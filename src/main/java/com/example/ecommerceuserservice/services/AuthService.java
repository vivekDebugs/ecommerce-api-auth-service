package com.example.ecommerceuserservice.services;

import com.example.ecommerceuserservice.exceptions.InvalidCredentialsException;
import com.example.ecommerceuserservice.exceptions.InvalidJwtTokenException;
import com.example.ecommerceuserservice.exceptions.UserAlreadyExistsException;
import com.example.ecommerceuserservice.models.Role;
import com.example.ecommerceuserservice.models.Session;
import com.example.ecommerceuserservice.models.SessionStatus;
import com.example.ecommerceuserservice.models.User;
import com.example.ecommerceuserservice.repositories.SessionRepository;
import com.example.ecommerceuserservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AuthService {
    UserRepository userRepository;
    SessionRepository sessionRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private String key = "thisisasupersecretkeydontyoudaretouchithaha";
    private SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public Boolean signUp(String email, String password) throws UserAlreadyExistsException {
        if (this.userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with email '" + email + "' already exists.");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        this.userRepository.save(user);
        return true;
    }

    public String logIn(String email, String password) throws InvalidCredentialsException {
        Optional<User> userOptional = this.userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }
        if (!bCryptPasswordEncoder.matches(password, userOptional.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        User user = userOptional.get();
        List<Role> roles = new ArrayList<>();

        Jwt jwt = this.createJwtToken(user, roles);
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setUser(user);
        session.setToken(jwt.token);
        session.setExpiringAt(jwt.expiration);
        sessionRepository.save(session);

        return jwt.token;
    }

    public void logOut(String jwtToken) {
        this.sessionRepository.deleteByToken(jwtToken);
    }

    public Boolean validateJwtToken(String jwtToken) throws InvalidJwtTokenException {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(jwtToken);
            return true;
        } catch (JwtException e) {
            throw new InvalidJwtTokenException("JWT token is not valid.");
        }
    }

    private Jwt createJwtToken(User user, List<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", roles);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date expiration = calendar.getTime();

        String token = Jwts.builder()
                        .claims(claims)
                        .expiration(expiration)
                        .issuedAt(new Date())
                        .issuer("ecom")
                        .signWith(this.secretKey)
                        .compact();

        return new Jwt(token, expiration);
    }

    private class Jwt {
        public String token;
        public Date expiration;

        Jwt(String token, Date expiration) {
            this.token = token;
            this.expiration = expiration;
        }
    }

}
