package com.hcmus.mela.history.dto.response;

import com.hcmus.mela.history.dto.dto.AnswerResultDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestResultResponse {

    private String message;

    private List<AnswerResultDto> answers;
}
