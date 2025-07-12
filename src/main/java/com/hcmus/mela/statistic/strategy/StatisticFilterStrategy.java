package com.hcmus.mela.statistic.strategy;

import com.hcmus.mela.statistic.dto.dto.ActivityHistoryDto;
import com.hcmus.mela.statistic.dto.dto.ActivityType;

import java.util.List;
import java.util.UUID;

public interface StatisticFilterStrategy {

    List<ActivityHistoryDto> getActivities(UUID ownId, UUID userIdToGet, UUID levelId, ActivityType activityType);
}