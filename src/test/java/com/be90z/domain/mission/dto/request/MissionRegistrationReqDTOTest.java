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

@DisplayName("MissionRegistrationReqDTO 테스트")
class MissionRegistrationReqDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("정상적인 챌린지 등록 요청 DTO 생성 성공")
    void createValidMissionRegistrationReqDTO_Success() {
        // given & when
        MissionRegistrationReqDTO dto = MissionRegistrationReqDTO.builder()
                .missionContent("매일 30분씩 운동하는 챌린지")
                .build();

        Set<ConstraintViolation<MissionRegistrationReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
        assertThat(dto.getMissionContent()).isEqualTo("매일 30분씩 운동하는 챌린지");
    }


    @Test
    @DisplayName("미션 내용이 null인 경우 유효성 검증 실패")
    void createMissionRegistrationReqDTO_WithNullMissionContent_ValidationFails() {
        // given & when
        MissionRegistrationReqDTO dto = MissionRegistrationReqDTO.builder()
                .missionContent(null)
                .build();

        Set<ConstraintViolation<MissionRegistrationReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("미션 내용은 필수입니다");
    }

    @Test
    @DisplayName("미션 내용이 빈 문자열인 경우 유효성 검증 실패")
    void createMissionRegistrationReqDTO_WithBlankMissionContent_ValidationFails() {
        // given & when
        MissionRegistrationReqDTO dto = MissionRegistrationReqDTO.builder()
                .missionContent("")
                .build();

        Set<ConstraintViolation<MissionRegistrationReqDTO>> violations = validator.validate(dto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("미션 내용은 필수입니다");
    }
}