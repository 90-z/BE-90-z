//package com.be90z.domain.challenge.controller;
//
//import com.be90z.domain.challenge.dto.response.ChallengeStatusResDTO;
//import com.be90z.domain.challenge.service.ChallengeService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/challenge")
//@RequiredArgsConstructor
//@Tag(name = "Challenge", description = "챌린지 관련 API")
//public class ChallengeController {
//
//    private final ChallengeService challengeService;
//
//    @GetMapping("/status")
//    @Operation(summary = "챌린지 현황 조회", description = "사용자의 챌린지 참여 현황을 조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "서버 에러")
//    })
//    public ResponseEntity<ChallengeStatusResDTO> getChallengeStatus(
//            @Parameter(description = "사용자 ID", required = true, example = "1")
//            @RequestParam Long userId) {
//
//        ChallengeStatusResDTO response = challengeService.getChallengeStatus(userId);
//        return ResponseEntity.ok(response);
//    }
//}