package com.hcmus.mela.statistic.service;

import com.hcmus.mela.statistic.dto.dto.ActivityHistoryDto;
import com.hcmus.mela.statistic.dto.dto.ActivityType;

import java.util.List;
import java.util.UUID;

public interface StatisticInfoService {

    List<ActivityHistoryDto> getActivitiesByUserIdAndLevelIdAndType(UUID userId, UUID levelId, ActivityType activityType);
}