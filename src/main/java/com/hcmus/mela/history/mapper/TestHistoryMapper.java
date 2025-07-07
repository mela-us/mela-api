package com.hcmus.mela.history.mapper;

import com.hcmus.mela.history.dto.dto.TestHistoryDto;
import com.hcmus.mela.history.model.TestHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TestAnswerMapper.class)
public interface TestHistoryMapper {
    TestHistoryMapper INSTANCE = Mappers.getMapper(TestHistoryMapper.class);

    TestHistoryDto convertToTestHistoryDto(TestHistory testHistory);
}
