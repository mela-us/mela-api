package com.hcmus.mela.statistic.service;

import com.hcmus.mela.statistic.dto.dto.ActivityType;
import com.hcmus.mela.statistic.dto.response.GetStatisticsResponse;
import com.hcmus.mela.statistic.strategy.StatisticFilterStrategy;

import java.util.UUID;

public interface StatisticQueryService {

    GetStatisticsResponse getStatisticByUserIdAndLevelIdAndType(UUID userId, UUID levelId, ActivityType activityType);

    GetStatisticsResponse getStatisticByUserIdAndLevelIdAndType(StatisticFilterStrategy strategy, UUID ownId, UUID userIdToGet, UUID levelId, ActivityType activityType);
}