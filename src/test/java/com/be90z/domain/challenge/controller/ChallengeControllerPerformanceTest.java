//package com.be90z.domain.challenge.controller;
//
//import com.be90z.domain.user.entity.Gender;
//import com.be90z.domain.user.entity.User;
//import com.be90z.domain.user.entity.UserAuthority;
//import com.be90z.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//@DisplayName("ChallengeController 성능 테스트")
//class ChallengeControllerPerformanceTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        testUser = User.builder()
//                .provider("kakao")
//                .nickname("성능테스트유저")
//                .email("performance@example.com")
//                .auth(UserAuthority.USER)
//                .gender(Gender.MAN)
//                .birth(1990)
//                .createdAt(LocalDateTime.now())
//                .build();
//        testUser = userRepository.save(testUser);
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("API 응답 시간 - 200ms 이하 검증")
//    void getChallengeStatus_ResponseTime_ShouldBeLessThan200ms() throws Exception {
//        // given
//        int testIterations = 10;
//        List<Long> responseTimes = new ArrayList<>();
//
//        // when
//        for (int i = 0; i < testIterations; i++) {
//            long startTime = System.nanoTime();
//
//            MvcResult result = mockMvc.perform(get("/api/challenge/status")
//                            .param("userId", testUser.getUserId().toString())
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andReturn();
//
//            long endTime = System.nanoTime();
//            long responseTimeMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
//            responseTimes.add(responseTimeMs);
//        }
//
//        // then
//        double averageResponseTime = responseTimes.stream()
//                .mapToLong(Long::longValue)
//                .average()
//                .orElse(0.0);
//
//        long maxResponseTime = responseTimes.stream()
//                .mapToLong(Long::longValue)
//                .max()
//                .orElse(0L);
//
//        System.out.println("=== 성능 테스트 결과 ===");
//        System.out.println("평균 응답 시간: " + averageResponseTime + "ms");
//        System.out.println("최대 응답 시간: " + maxResponseTime + "ms");
//        System.out.println("모든 응답 시간: " + responseTimes);
//
//        // PRD 요구사항: 200ms 이하
//        assertThat(averageResponseTime).as("평균 응답 시간이 200ms 이하여야 합니다")
//                .isLessThan(200.0);
//        assertThat(maxResponseTime).as("최대 응답 시간이 300ms 이하여야 합니다")
//                .isLessThan(300L);
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("연속 요청 테스트 - 5개 요청 순차 처리")
//    void getChallengeStatus_SequentialRequests_ShouldHandleWell() throws Exception {
//        // given
//        int sequentialRequests = 5;
//        List<Long> responseTimes = new ArrayList<>();
//
//        // when
//        for (int i = 0; i < sequentialRequests; i++) {
//            long startTime = System.nanoTime();
//
//            mockMvc.perform(get("/api/challenge/status")
//                            .param("userId", testUser.getUserId().toString())
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk());
//
//            long endTime = System.nanoTime();
//            long responseTimeMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
//            responseTimes.add(responseTimeMs);
//        }
//
//        // then
//        double averageResponseTime = responseTimes.stream()
//                .mapToLong(Long::longValue)
//                .average()
//                .orElse(0.0);
//
//        System.out.println("=== 연속 요청 테스트 결과 ===");
//        System.out.println("연속 요청 수: " + sequentialRequests);
//        System.out.println("평균 응답 시간: " + averageResponseTime + "ms");
//        System.out.println("모든 응답 시간: " + responseTimes);
//
//        assertThat(averageResponseTime).as("연속 요청 시에도 평균 응답 시간이 200ms 이하여야 합니다")
//                .isLessThan(200.0);
//    }
//}