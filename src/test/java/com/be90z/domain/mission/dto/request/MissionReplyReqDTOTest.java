package com.be90z.domain.mission.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MissionReplyReqDTO 테스트")
class MissionReplyReqDTOTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("유효한 미션 답글 요청 DTO 생성 테스트")
    void createValidMissionReplyReqDTO() {
        // given
        String replyContent = "이 미션에 참여하고 싶습니다!";
        Long userId = 1L;

        // when
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .replyContent(replyContent)
                .userId(userId)
                .build();

        // then
        assertThat(dto.getReplyContent()).isEqualTo(replyContent);
        assertThat(dto.getUserId()).isEqualTo(userId);

        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("미션 답글 내용이 null일 때 검증 실패")
    void validateReplyContentNotNull() {
        // given
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .replyContent(null)
                .userId(1L)
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("답글 내용은 필수입니다");
    }

    @Test
    @DisplayName("미션 답글 내용이 빈 문자열일 때 검증 실패")
    void validateReplyContentNotBlank() {
        // given
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .replyContent("")
                .userId(1L)
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("답글 내용은 필수입니다");
    }

    @Test
    @DisplayName("사용자 ID가 null일 때 검증 실패")
    void validateUserIdNotNull() {
        // given
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .replyContent("답글 내용")
                .userId(null)
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("사용자 ID는 필수입니다");
    }
}