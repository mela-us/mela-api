package com.hcmus.mela.statistic.service;

import com.hcmus.mela.statistic.dto.dto.ActivityHistoryDto;
import com.hcmus.mela.statistic.dto.dto.ActivityType;
import com.hcmus.mela.statistic.dto.response.GetStatisticsResponse;
import com.hcmus.mela.statistic.strategy.StatisticFilterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticQueryServiceImpl implements StatisticQueryService {

    private final StatisticInfoService statisticInfoService;

    public GetStatisticsResponse getStatisticByUserIdAndLevelIdAndType(UUID userId, UUID levelId, ActivityType activityType) {
        List<ActivityHistoryDto> activityHistoryDtoList = statisticInfoService
                .getActivitiesByUserIdAndLevelIdAndType(userId, levelId, activityType);
        return new GetStatisticsResponse(
                "Get statistic successfully for user " + userId + " and level " + levelId + "!",
                activityHistoryDtoList.size(),
                activityHistoryDtoList);
    }

    @Override
    public GetStatisticsResponse getStatisticByUserIdAndLevelIdAndType(StatisticFilterStrategy strategy, UUID ownId, UUID userIdToGet, UUID levelId, ActivityType activityType) {
        List<ActivityHistoryDto> activityHistoryDtoList = strategy.getActivities(ownId, userIdToGet, levelId, activityType);
        return new GetStatisticsResponse(
                "Get statistic successfully for user " + userIdToGet + " and level " + levelId + "!",
                activityHistoryDtoList.size(),
                activityHistoryDtoList);
    }
}