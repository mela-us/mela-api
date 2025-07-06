package com.hcmus.mela.streak.service;

import com.hcmus.mela.streak.dto.response.GetStreakResponse;
import com.hcmus.mela.streak.dto.response.UpdateStreakResponse;
import com.hcmus.mela.streak.model.Streak;
import com.hcmus.mela.streak.repository.StreakRepository;
import com.hcmus.mela.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreakServiceImpl implements StreakService {

    private final StreakRepository streakRepository;
    private final TokenService tokenService;

    @Override
    public GetStreakResponse getStreak(UUID userId) {
        Instant nowInstant = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        Date now = Date.from(nowInstant);
        Streak streak = streakRepository.findByUserId(userId).orElse(null);

        if (streak == null) {
            streak = new Streak(userId, 0, now, now, 0);
            streakRepository.save(streak);
            return new GetStreakResponse(
                    streak.getStreakDays(),
                    streak.getUpdatedAt(),
                    streak.getLongestStreak(),
                    "Get streak successful, but no streak found. A new streak has been created.");
        }

        if (ChronoUnit.DAYS.between(
                streak.getUpdatedAt().toInstant().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate(),
                Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate()) > 1) {
            streak.setStreakDays(0);
            streakRepository.updateStreak(streak);
        }
        return new GetStreakResponse(
                streak.getStreakDays(),
                streak.getUpdatedAt(),
                streak.getLongestStreak(),
                "Get streak successful, streak found.");
    }

    @Override
    public UpdateStreakResponse updateStreak(UUID userId) {
        Instant nowInstant = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        Date now = Date.from(nowInstant);
        Streak streak = streakRepository.findByUserId(userId).orElse(null);

        if (streak == null) {
            streak = new Streak(userId, 1, now, now, 1);
            streakRepository.save(streak);
            return new UpdateStreakResponse("New streak created successfully.");
        }

        if (ChronoUnit.DAYS.between(
                streak.getUpdatedAt().toInstant().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate(),
                Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate()) == 0
                && streak.getStreakDays() != 0) {
            return new UpdateStreakResponse("Streak already updated today. No changes made.");
        }

        streak.setStreakDays(streak.getStreakDays() + 1);
        streak.setUpdatedAt(now);
        if (streak.getStreakDays() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getStreakDays());
        }
        streakRepository.updateStreak(streak);
        tokenService.increaseUserToken(userId, streak.getStreakDays() / 3);

        return new UpdateStreakResponse("Streak updated successfully. Current streak: " + streak.getStreakDays() + " days.");
    }

    @Override
    public void deleteStreak(UUID userId) {
        streakRepository.findByUserId(userId).ifPresent(streakRepository::delete);
    }
}
