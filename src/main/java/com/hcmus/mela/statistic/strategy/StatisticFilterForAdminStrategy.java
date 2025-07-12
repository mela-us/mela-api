package com.hcmus.mela.statistic.strategy;

import com.hcmus.mela.level.service.LevelStatusService;
import com.hcmus.mela.shared.type.ContentStatus;
import com.hcmus.mela.statistic.dto.dto.ActivityHistoryDto;
import com.hcmus.mela.statistic.dto.dto.ActivityType;
import com.hcmus.mela.statistic.exception.StatisticException;
import com.hcmus.mela.statistic.service.StatisticInfoService;
import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.model.UserRole;
import com.hcmus.mela.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("STATISTIC_ADMIN")
@RequiredArgsConstructor
public class StatisticFilterForAdminStrategy implements StatisticFilterStrategy {

    private final StatisticInfoService statisticInfoService;
    private final UserInfoService userInfoService;
    private final LevelStatusService levelStatusService;

    @Override
    public List<ActivityHistoryDto> getActivities(UUID ownId, UUID userIdToGet, UUID levelId, ActivityType activityType) {
        if (!levelStatusService.isLevelInStatus(levelId, ContentStatus.VERIFIED)) {
            throw new StatisticException("Level is not in VERIFIED status, cannot get statistics");
        }
        User user = userInfoService.getUserByUserId(userIdToGet);
        if (user == null || user.getUserRole() != UserRole.USER) {
            throw new StatisticException("User not found or user role is not USER");
        }
        return statisticInfoService.getActivitiesByUserIdAndLevelIdAndType(
                userIdToGet, levelId, activityType);
    }
}