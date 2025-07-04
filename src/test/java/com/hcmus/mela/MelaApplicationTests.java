package com.hcmus.mela;

import com.hcmus.mela.exercise.model.Question;
import com.hcmus.mela.exercise.model.QuestionType;
import com.hcmus.mela.exercise.service.ExerciseGradeService;
import com.hcmus.mela.history.dto.dto.ExerciseAnswerDto;
import com.hcmus.mela.history.model.ExerciseAnswer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class MelaApplicationTests {

    @Autowired
    private ExerciseGradeService exerciseGradeService;

    @Test
    void contextLoads() {
    }

    @Test
    void testGradeQuestion() {
        System.out.println("Checking question answer...");
        Map<String, Object> result = exerciseGradeService.evaluateQuestion(
                new ExerciseAnswerDto(
                        UUID.randomUUID(),
                        "",
                        null,
                        List.of("https://stmelauat001.blob.core.windows.net/users/d28c6bab-d5a4-48a7-981b-a970106e6efd/exercises/images/BaiGiai.JPG"),
                        false
                ),
                new Question(
                        UUID.randomUUID(),
                        1,
                        "Yêu cầu: <img src='https://stmelauat001.blob.core.windows.net/users/d28c6bab-d5a4-48a7-981b-a970106e6efd/exercises/images/Debai01.JPG'> .Giá trị biểu thức là: <img src='https://stmelauat001.blob.core.windows.net/users/d28c6bab-d5a4-48a7-981b-a970106e6efd/exercises/images/Debai02.JPG'>.",
                        QuestionType.ESSAY,
                        List.of(),
                        null,
                        "Lời giải cho câu hỏi:<img src='https://stmelauat001.blob.core.windows.net/users/d28c6bab-d5a4-48a7-981b-a970106e6efd/exercises/images/S01.JPG'><br><img src='https://stmelauat001.blob.core.windows.net/users/d28c6bab-d5a4-48a7-981b-a970106e6efd/exercises/images/S02.JPG'>.",
                        null,
                        null)
        );
        System.out.println("Result: " + result.get("isCorrect") + ", Feedback: " + result.get("feedback"));
    }

    @Test
    void testGradeExercise() {
        System.out.println("Grading exercise...");
        List<ExerciseAnswerDto> answers = List.of(
                // Câu 1: MULTIPLE_CHOICE, đúng là option 2 ("Tiền Giang")
                new ExerciseAnswerDto(
                        UUID.fromString("107a0433-854f-4087-a4f2-8d2671a317ed"),
                        "",
                        2,
                        List.of(),
                        false
                ),

                // Câu 2: MULTIPLE_CHOICE, đúng là option 1 ("Việt Nam")
                new ExerciseAnswerDto(
                        UUID.fromString("bc1ba38f-98eb-4b65-9f16-cfee98dd2348"),
                        "",
                        1,
                        List.of(),
                        false
                ),

                // Câu 3: FILL_IN_THE_BLANK, blankAnswer = "5"
                new ExerciseAnswerDto(
                        UUID.fromString("61977b10-30d9-4475-a01a-bc34b5928003"),
                        "5",
                        null,
                        List.of(),
                        false
                ),

                // Câu 4 (ordinal_number = 5): MULTIPLE_CHOICE, đúng là option 3 ("Điện")
                new ExerciseAnswerDto(
                        UUID.fromString("44199b7b-ee02-4040-b1ef-9e909ff53d9f"),
                        "",
                        3,
                        List.of(),
                        false
                ),

                // Câu 5 (ordinal_number = 6): FILL_IN_THE_BLANK, blankAnswer = "10"
                new ExerciseAnswerDto(
                        UUID.fromString("473a92e3-0c5b-4db3-bea2-5b5250a7fadd"),
                        "10",
                        null,
                        List.of(),
                        false
                ),

                // Câu 6 (ordinal_number = 6): ESSAY
                new ExerciseAnswerDto(
                        UUID.fromString("4dd0fd7c-7199-4ac4-9c4b-073758c72874"),
                        "",
                        null,
                        List.of("https://stmelauat001.blob.core.windows.net/users/d28c6bab-d5a4-48a7-981b-a970106e6efd/exercises/images/BaiGiai.JPG"),
                        false
                ),

                // Câu 7 (ordinal_number = 7): ESSAY
                new ExerciseAnswerDto(
                        UUID.fromString("c2daf5f8-e5c2-4648-bc42-f81a8b1104ed"),
                        "Sử dụng phương pháp tập hợp và sơ đồ Venn để suy luận.",
                        null,
                        List.of(),
                        false
                ),

                // Câu 8 (ordinal_number = 8): ESSAY
                new ExerciseAnswerDto(
                        UUID.fromString("bc6effb0-d1e0-405d-b73c-5a2ec2606589"),
                        "3",
                        null,
                        List.of(),
                        false
                )
        );

        List<ExerciseAnswer> gradedAnswers = exerciseGradeService.gradeExercise(
                UUID.fromString("749ea0b3-35c4-47eb-a34e-f59cccf3f832"),
                answers);
        gradedAnswers.forEach(answer -> System.out.println(answer.toString()));
    }
}
