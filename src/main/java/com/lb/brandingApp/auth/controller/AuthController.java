package com.lb.brandingApp.auth.controller;

import com.lb.brandingApp.auth.data.models.request.PermissionRequestDto;
import com.lb.brandingApp.auth.data.models.request.RegistrationRequestDto;
import com.lb.brandingApp.auth.data.models.request.TeamRequestDto;
import com.lb.brandingApp.auth.data.models.request.UserRequestDto;
import com.lb.brandingApp.auth.data.models.response.TeamResponseDto;
import com.lb.brandingApp.auth.data.models.response.UserResponseDto;
import com.lb.brandingApp.auth.service.AuthService;
import com.lb.brandingApp.auth.service.TeamService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TeamService teamService;

    @PostMapping("/app/login")
    public ResponseEntity<UserResponseDto> authenticate(@RequestBody UserRequestDto request) {
        log.info("Login requested for {}", request.username());
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PutMapping("/app/password")
    public ResponseEntity<Void> changePassword(@RequestBody UserRequestDto request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers(
            @RequestParam("active") boolean fetchActive,
            @RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(authService.getAllUsers(fetchActive, name));
    }

    @GetMapping("/app/user/{username}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("username") @NonNull String username) {
        return ResponseEntity.ok(authService.getUser(username));
    }

    @PutMapping("/app/user/password")
    public ResponseEntity<Void> resetUserPassword(@RequestBody UserRequestDto request) {
        authService.resetUserPassword(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/user/activate")
    public ResponseEntity<Void> toggleUserActivation(@RequestBody UserRequestDto request, @RequestParam boolean activate) {
        authService.toggleUserActivation(request, activate);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/app/user/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegistrationRequestDto request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PutMapping("/app/user/update")
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
