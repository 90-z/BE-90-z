package com.be90z.domain.mission.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MissionReplyReqDTO 테스트")
class MissionReplyReqDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("미션 댓글 요청 DTO를 정상적으로 생성할 수 있다")
    void shouldCreateMissionReplyReqDTO() {
        // given
        String missionName = "테스트 미션";
        String missionContent = "테스트 미션 내용입니다.";

        // when
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .missionName(missionName)
                .missionContent(missionContent)
                .build();

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getMissionName()).isEqualTo(missionName);
        assertThat(dto.getMissionContent()).isEqualTo(missionContent);
    }

    @Test
    @DisplayName("미션명이 null이면 유효성 검증에 실패한다")
    void shouldFailValidationWhenMissionNameIsNull() {
        // given
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .missionName(null)
                .missionContent("테스트 미션 내용")
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("missionName"));
    }

    @Test
    @DisplayName("미션명이 빈 문자열이면 유효성 검증에 실패한다")
    void shouldFailValidationWhenMissionNameIsBlank() {
        // given
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .missionName("")
                .missionContent("테스트 미션 내용")
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("missionName"));
    }

    @Test
    @DisplayName("미션 내용이 null이면 유효성 검증에 실패한다")
    void shouldFailValidationWhenMissionContentIsNull() {
        // given
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent(null)
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("missionContent"));
    }

    @Test
    @DisplayName("미션 내용이 빈 문자열이면 유효성 검증에 실패한다")
    void shouldFailValidationWhenMissionContentIsBlank() {
        // given
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent("")
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("missionContent"));
    }

    @Test
    @DisplayName("미션명이 100자를 초과하면 유효성 검증에 실패한다")
    void shouldFailValidationWhenMissionNameExceedsMaxLength() {
        // given
        String longMissionName = "a".repeat(101);
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .missionName(longMissionName)
                .missionContent("테스트 미션 내용")
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("missionName"));
    }

    @Test
    @DisplayName("미션 내용이 500자를 초과하면 유효성 검증에 실패한다")
    void shouldFailValidationWhenMissionContentExceedsMaxLength() {
        // given
        String longMissionContent = "a".repeat(501);
        MissionReplyReqDTO dto = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent(longMissionContent)
                .build();

        // when
        Set<ConstraintViolation<MissionReplyReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("missionContent"));
    }
}