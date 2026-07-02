package org.microsoft.qintelipass.controllers;

import lombok.extern.slf4j.Slf4j;
import org.microsoft.qintelipass.dtos.TokenUsageRankDTO;
import org.microsoft.qintelipass.dtos.UserTokenUsageDTO;
import org.microsoft.qintelipass.response.ResponseBody;
import org.microsoft.qintelipass.security.SecurityUtil;
import org.microsoft.qintelipass.services.TokenUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/token")
public class TokenUsageController {
    @Autowired
    private TokenUsageService tokenUsageService;

    @GetMapping("/usage")
    public ResponseEntity<ResponseBody<UserTokenUsageDTO>> getUserUsage() {
//        SecurityUtil.requireAuthentication();
        Long userId = 1L;//SecurityUtil.getCurrentUserId();
        log.info("Getting token usage for authenticated user: {}", userId);

        UserTokenUsageDTO usage = tokenUsageService.getUserTokenUsage(userId);
        return ResponseEntity.ok(ResponseBody.<UserTokenUsageDTO>builder()
                .success(true)
                .message("Token usage retrieved successfully")
                .payload(usage)
                .build());
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseBody<Map<String, Object>>> checkLimit() {
//        SecurityUtil.requireAuthentication();
        Long userId = 1L;//SecurityUtil.getCurrentUserId();
        log.info("Checking token limit for authenticated user: {}", userId);

        boolean canProceed = tokenUsageService.checkTokenLimit(userId);
        UserTokenUsageDTO usage = tokenUsageService.getUserTokenUsage(userId);

        return ResponseEntity.ok(ResponseBody.<Map<String, Object>>builder()
                .success(canProceed)
                .message(canProceed ? "Under token limit" : "Token limit exceeded")
                .payload(Map.of(
                        "canProceed", canProceed,
                        "usage", usage
                ))
                .build());
    }

    @PostMapping("/usage")
    public ResponseEntity<ResponseBody<Map<String, Object>>> recordUsage() {
//        SecurityUtil.requireAuthentication();
        Long userId = 1L;//SecurityUtil.getCurrentUserId();
        log.info("Recording token usage for authenticated user: {}", userId);

        boolean canProceed = tokenUsageService.checkTokenLimit(userId);
        if (!canProceed) {
            return ResponseEntity.badRequest().body(
                    ResponseBody.<Map<String, Object>>builder()
                            .success(false)
                            .message("Token limit exceeded, cannot record additional usage")
                            .build()
            );
        }

        int mockToken = 10343;
        boolean recorded = tokenUsageService.recordTokenUsage(userId, mockToken);
        UserTokenUsageDTO usage = tokenUsageService.getUserTokenUsage(userId);

        return ResponseEntity.ok(ResponseBody.<Map<String, Object>>builder()
                .success(recorded)
                .message("Token usage recorded successfully")
                .payload(Map.of("usage", usage))
                .build());
    }

    @PutMapping("/limit")
    public ResponseEntity<ResponseBody<Void>> setUserLimit(
            @RequestBody Map<String, Long> requestBody) {
//        SecurityUtil.requireAuthentication();
        Long userId = 1L;//SecurityUtil.getCurrentUserId();
        log.info("Setting token limit for authenticated user: {}", userId);

        Long limit = requestBody.get("limit");
        if (limit == null || limit < 0) {
            return ResponseEntity.badRequest().body(
                    ResponseBody.<Void>builder()
                            .success(false)
                            .message("Invalid token limit")
                            .build()
            );
        }

        tokenUsageService.setUserTokenLimit(userId, limit);
        return ResponseEntity.ok(ResponseBody.<Void>builder()
                .success(true)
                .message("Token limit updated successfully")
                .build());
    }
}

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/token")
class TokenUsageAdminController {
    @Autowired
    private TokenUsageService tokenUsageService;

    @GetMapping("/rank")
    public ResponseEntity<ResponseBody<List<TokenUsageRankDTO>>> getDailyRank(
            @RequestParam(value = "topN", defaultValue = "10") int topN) {
        SecurityUtil.requireAuthentication();
        log.info("Getting daily token rank requested by admin user: {}", SecurityUtil.getCurrentUserId());

        if (topN <= 0 || topN > 100) {
            topN = 10;
        }

        List<TokenUsageRankDTO> rank = tokenUsageService.getDailyTokenRank(topN);
        return ResponseEntity.ok(ResponseBody.<List<TokenUsageRankDTO>>builder()
                .success(true)
                .message("Daily token usage rank retrieved successfully")
                .payload(rank)
                .build());
    }
}
