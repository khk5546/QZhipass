package org.microsoft.qintelipass.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.microsoft.qintelipass.CredentialManager;
import org.microsoft.qintelipass.ILoginStrategy;
import org.microsoft.qintelipass.IRegisterable;
import org.microsoft.qintelipass.LoginStrategyFactory;
import org.microsoft.qintelipass.models.User;
import org.microsoft.qintelipass.request.LoginRequest;
import org.microsoft.qintelipass.request.RegisterRequest;
import org.microsoft.qintelipass.response.ResponseBody;
import org.microsoft.qintelipass.services.SmsServiceImpl;
import org.microsoft.qintelipass.services.StringRedisService;
import org.microsoft.qintelipass.services.UserDetailsServiceImpl;
import org.microsoft.qintelipass.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/auth/portal")
public class AuthController {
    private final LoginStrategyFactory factory;
    private final SmsServiceImpl smsService;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final CredentialManager credentialManager;
    private static final String COOKIE_ROOT = "/";
    private static final Integer EXPIRATION = 7 * 24 * 60 * 60;
    private static final String HEADER = "access_token";
    @Autowired
    private IRegisterable registerService;
    @Autowired
    private StringRedisService redisService;
    @Autowired
    public AuthController(LoginStrategyFactory factory, SmsServiceImpl smsService, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, CredentialManager credentialManager) {
        this.factory = factory;
        this.smsService = smsService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.credentialManager = credentialManager;
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest formData, HttpServletResponse httpResponse){
        redisService.setValue("name", "alec");
        Object name = redisService.getValue("name");
        System.out.println("name = " + name);
        try {
            String loginType = formData.getLoginType();
            Map<String, Object> params = formData.getCredential();
            ILoginStrategy strategy = factory.getStrategy(loginType);
            log.info("User response: {}", formData);
            ResponseBody<User> response = strategy.authenticate(params);
            log.info("Authenticator response: {}", response);
            User user = response.getPayload();
            if (response.isSuccess() && user != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getName());
                String token = jwtUtil.generateToken(userDetails);

                Cookie userIdCookie = new Cookie("user_id", String.valueOf(user.getId()));
                Cookie auth = new Cookie(HEADER, token);
                userIdCookie.setPath(COOKIE_ROOT);
                userIdCookie.setMaxAge(EXPIRATION);
                auth.setPath(COOKIE_ROOT);
                auth.setMaxAge(EXPIRATION);
                httpResponse.addCookie(userIdCookie);
                httpResponse.addCookie(auth);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/sendcode")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> payload){
        if (payload.get("phone") != null){
            String code = smsService.sendSmsCode(payload.get("phone"));
            log.info("Sent sms code: {}", code);
        }
        return ResponseEntity
                .badRequest()
                .body(ResponseBody
                        .builder()
                        .success(false)
                        .message("phone number should not be null")
                );
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse httpResponse, @RequestHeader("Authorization") String token){
        if (!credentialManager.checkIfLogin(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Logged in.");
        }
        Cookie userIdCookie = new Cookie("user_id", "");
        userIdCookie.setPath("/");
        userIdCookie.setMaxAge(0);
        httpResponse.addCookie(userIdCookie);

        return ResponseEntity.ok(Map.of("success",true,"message", "OK"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest payload){
        User registered = registerService.register(payload, payload.getPassword());
        Map<String, Object> responseBody = new HashMap<>();

        UserDetails userDetails = userDetailsService.loadUserByUsername(registered.getName());
        String token = jwtUtil.generateToken(userDetails);
        responseBody.put("success", true);
        responseBody.put("data", registered);
        responseBody.put("token", token);

        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(registered.getId())
                .toUri())
        .body(responseBody);
    }
}
