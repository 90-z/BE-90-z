package com.be90z.domain.user.controller;

import com.be90z.domain.user.dto.request.UserMypageUpdateReqDTO;
import com.be90z.domain.user.dto.response.UserMypageResDTO;
import com.be90z.domain.user.service.UserService;
import com.be90z.global.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;
    private final AuthUtil authUtil;

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 정보 조회", description = "현재 사용자의 마이페이지 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<UserMypageResDTO> getMypage(
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = authUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            UserMypageResDTO response = userService.getMypage(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/mypage")
    @Operation(summary = "마이페이지 정보 수정", description = "현재 사용자의 마이페이지 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<UserMypageResDTO> updateMypage(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody UserMypageUpdateReqDTO request) {
        
        if (token == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = authUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            UserMypageResDTO response = userService.updateMypage(userId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}