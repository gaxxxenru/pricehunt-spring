package ru.pricehunt.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pricehunt.auth.model.Person;
import ru.pricehunt.auth.model.Role;
import ru.pricehunt.auth.model.Token;
import ru.pricehunt.auth.model.TokenType;
import ru.pricehunt.auth.repository.TokenRepository;
import ru.pricehunt.auth.repository.UserRepository;
import ru.pricehunt.auth.web.*;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с email " + request.getEmail() + " уже существует");
        }
        var user = Person.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of(Role.USER, Role.ADMIN))
            .build();
        var savedUser = repository.save(user);
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, accessToken, TokenType.ACCESS);
        saveUserToken(savedUser, refreshToken, TokenType.REFRESH);
        return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        var user = repository.findByEmail(request.getEmail())
            .orElseThrow();
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken, TokenType.ACCESS);
        saveUserToken(user, refreshToken, TokenType.REFRESH);
        return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    private void saveUserToken(Person person, String jwtToken, TokenType tokenType) {
        var token = Token.builder()
            .person(person)
            .token(jwtToken)
            .tokenType(tokenType)
            .expired(false)
            .revoked(false)
            .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Person person) {
        var validUserTokens = tokenRepository.findAllValidTokenByPerson(person.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        final String refreshToken = request.getRefreshToken();
        final String userEmail;
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new IllegalArgumentException("Пользователя для данного токена авторизации не существует");
        }
        var user = this.repository.findByEmail(userEmail)
            .orElseThrow();
        var isTokenValid = tokenRepository.findByToken(refreshToken)
            .map(t -> !t.isExpired() && !t.isRevoked())
            .orElse(false);
        if (!jwtService.isTokenValid(refreshToken, user) || !isTokenValid) {
            throw new IllegalArgumentException("Токен авторизации не валиден");
        }
        var accessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken, TokenType.ACCESS);
        saveUserToken(user, newRefreshToken, TokenType.REFRESH);
        return RefreshTokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(newRefreshToken)
            .build();
    }

    public PersonResponse getPerson(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Токен авторизации отсутствует");
        }
        final String token = header.substring(7);
        final String userEmail;
        userEmail = jwtService.extractUsername(token);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                .orElseThrow();
            var isTokenValid = tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
            if (jwtService.isTokenValid(token, user) && isTokenValid) {
                return PersonResponse.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .authorities(user.getAuthorities())
                    .build();
            } else {
                throw new IllegalArgumentException("Токен авторизации не валиден");
            }
        }
        return null;
    }

    public Boolean validateToken(ValidateTokenRequest request) {
        final String token = request.getToken();
        if (token == null) {
            throw new IllegalArgumentException("Токен авторизации отсутствует");
        }
        final String userEmail;
        userEmail = jwtService.extractUsername(token);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                .orElseThrow();
            var isTokenValid = tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
            if (jwtService.isTokenValid(token, user) && isTokenValid) {
                return true;
            } else {
                throw new IllegalArgumentException("Токен авторизации не валиден");
            }
        }
        return false;
    }

    public Boolean logout(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
        throw new IllegalArgumentException("Токен авторизации отсутствует");
        }
        final String token = header.substring(7);
        final String userEmail = jwtService.extractUsername(token);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                .orElseThrow();
            revokeAllUserTokens(user);
        }
        return false;
    }
}
