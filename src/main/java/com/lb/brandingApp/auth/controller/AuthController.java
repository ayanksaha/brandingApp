package com.lb.brandingApp.auth.controller;

import com.lb.brandingApp.auth.data.dto.request.PermissionRequestDto;
import com.lb.brandingApp.auth.data.dto.request.RegistrationRequestDto;
import com.lb.brandingApp.auth.data.dto.request.TeamRequestDto;
import com.lb.brandingApp.auth.data.dto.request.UserRequestDto;
import com.lb.brandingApp.auth.data.dto.response.TeamResponseDto;
import com.lb.brandingApp.auth.data.dto.response.UserResponseDto;
import com.lb.brandingApp.auth.service.AuthService;
import com.lb.brandingApp.auth.service.TeamService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TeamService teamService;

    @PostMapping("/app/login")
    public ResponseEntity<UserResponseDto> authenticate(@RequestBody UserRequestDto request) {
        UserResponseDto response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/app/password")
    public ResponseEntity<Void> changePassword(@RequestBody UserRequestDto request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestParam("active") @NonNull boolean fetchActive) {
        return ResponseEntity.ok(authService.getAllUsers(fetchActive));
    }

    @GetMapping("/app/user/{username}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("username") @NonNull String username) {
        UserResponseDto response = authService.getUser(username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/app/user/password")
    public ResponseEntity<Void> resetUserPassword(@RequestBody UserRequestDto request) {
        authService.resetUserPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/app/user/activate")
    public ResponseEntity<Void> toggleUserActivation(@RequestBody UserRequestDto request, @RequestParam boolean activate) {
        authService.toggleUserActivation(request, activate);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/app/user/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegistrationRequestDto request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/app/user/update")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody RegistrationRequestDto request) {
        return ResponseEntity.ok(authService.updateUser(request));
    }

    @GetMapping("/app/teams")
    public ResponseEntity<List<TeamResponseDto>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @PostMapping("/app/team")
    public ResponseEntity<Void> addTeam(@RequestBody TeamRequestDto request) {
        teamService.addTeam(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/team/{team_id}")
    public ResponseEntity<TeamResponseDto> getTeamById(@PathVariable("team_id") @NonNull Long teamId) {
        return ResponseEntity.ok(teamService.getTeam(teamId));
    }

    @PutMapping("/app/team/{team_id}/permissions")
    public ResponseEntity<Void> addPermissionsToTeam(
            @PathVariable("team_id") @NonNull Long teamId, @RequestBody @NonNull TeamRequestDto request) {
        teamService.addPermissionsToTeam(teamId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/team/{team_id}/permission")
    public ResponseEntity<Void> addPermissionToTeam(
            @PathVariable("team_id") @NonNull Long teamId, @RequestBody @NonNull PermissionRequestDto request) {
        teamService.addPermissionToTeam(teamId, request);
        return ResponseEntity.ok().build();
    }

}
