package com.lb.brandingApp.auth.service;

import com.lb.brandingApp.auth.data.dto.request.PermissionRequestDto;
import com.lb.brandingApp.auth.data.dto.request.TeamRequestDto;
import com.lb.brandingApp.auth.data.dto.response.TeamResponseDto;

import java.util.List;

public interface TeamService {

    List<TeamResponseDto> getAllTeams();

    void addTeam(TeamRequestDto request);

    TeamResponseDto getTeam(Long teamId);

    void addPermissionsToTeam(Long teamId, TeamRequestDto request);

    void addPermissionToTeam(Long teamId, PermissionRequestDto request);
}
