package test.technique.SMA.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;

@Service
public class RateLimitService {

    @Value("${rate.limit.max.attempts:5}")
    private int maxAttempts;

    @Value("${rate.limit.duration.minutes:1}")
    private int durationMinutes;

    private static class AttemptsInfo {
        int attempts;
        LocalDateTime resetTime;

        AttemptsInfo(int attempts, LocalDateTime resetTime) {
            this.attempts = attempts;
            this.resetTime = resetTime;
        }
    }

    private final ConcurrentHashMap<String, AttemptsInfo> loginAttempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        AttemptsInfo info = loginAttempts.get(key);
        if (info == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(info.resetTime)) {
            loginAttempts.remove(key);
            return false;
        }

        return info.attempts >= maxAttempts;
    }

    public void recordAttempt(String key) {
        loginAttempts.compute(key, (k, v) -> {
            if (v == null || LocalDateTime.now().isAfter(v.resetTime)) {
                return new AttemptsInfo(1, LocalDateTime.now().plusMinutes(durationMinutes));
            }
            return new AttemptsInfo(v.attempts + 1, v.resetTime);
        });
    }

    public void resetAttempts(String key) {
        loginAttempts.remove(key);
    }
}
