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

@DisplayName("MissionUpdateReqDTO 테스트 (명세서 준수)")
class MissionUpdateReqDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("정상적인 미션 수정 요청 DTO 생성 성공")
    void createValidMissionUpdateReqDTO_Success() {
        // given & when
        MissionUpdateReqDTO dto = MissionUpdateReqDTO.builder()
                .missionName("수정된 미션 제목")
                .missionContent("수정된 미션 내용")
                .build();

        Set<ConstraintViolation<MissionUpdateReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
        assertThat(dto.getMissionName()).isEqualTo("수정된 미션 제목");
        assertThat(dto.getMissionContent()).isEqualTo("수정된 미션 내용");
    }

    @Test
    @DisplayName("미션명이 null인 경우 유효성 검증 실패")
    void createMissionUpdateReqDTO_WithNullMissionName_ValidationFails() {
        // given & when
        MissionUpdateReqDTO dto = MissionUpdateReqDTO.builder()
                .missionName(null)
                .missionContent("수정된 미션 내용")
                .build();

        Set<ConstraintViolation<MissionUpdateReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("미션명은 필수입니다");
    }

    @Test
    @DisplayName("미션명이 빈 문자열인 경우 유효성 검증 실패")
    void createMissionUpdateReqDTO_WithBlankMissionName_ValidationFails() {
        // given & when
        MissionUpdateReqDTO dto = MissionUpdateReqDTO.builder()
                .missionName("")
                .missionContent("수정된 미션 내용")
                .build();

        Set<ConstraintViolation<MissionUpdateReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("미션명은 필수입니다");
    }

    @Test
    @DisplayName("미션 내용이 null인 경우 유효성 검증 실패")
    void createMissionUpdateReqDTO_WithNullMissionContent_ValidationFails() {
        // given & when
        MissionUpdateReqDTO dto = MissionUpdateReqDTO.builder()
                .missionName("수정된 미션 제목")
                .missionContent(null)
                .build();

        Set<ConstraintViolation<MissionUpdateReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("미션 내용은 필수입니다");
    }

    @Test
    @DisplayName("미션 내용이 빈 문자열인 경우 유효성 검증 실패")
    void createMissionUpdateReqDTO_WithBlankMissionContent_ValidationFails() {
        // given & when
        MissionUpdateReqDTO dto = MissionUpdateReqDTO.builder()
                .missionName("수정된 미션 제목")
                .missionContent("")
                .build();

        Set<ConstraintViolation<MissionUpdateReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("미션 내용은 필수입니다");
    }
}