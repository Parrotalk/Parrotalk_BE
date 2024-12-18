package com.be.parrotalk.login.controller;

import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.login.dto.UserInfoDTO;
import com.be.parrotalk.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/info")
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserInfoDTO userInfo = userService.getUserInfo(user.getId());
        return ResponseEntity.ok(userInfo);
    }
}
