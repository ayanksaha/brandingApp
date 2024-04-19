package com.lb.brandingApp.auth.service;

import com.lb.brandingApp.auth.data.entities.Permission;
import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.data.models.request.PermissionRequestDto;
import com.lb.brandingApp.auth.data.models.request.TeamRequestDto;
import com.lb.brandingApp.auth.data.models.response.PermissionResponseDto;
import com.lb.brandingApp.auth.data.models.response.TeamResponseDto;
import com.lb.brandingApp.auth.repository.PermissionRepository;
import com.lb.brandingApp.auth.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.lb.brandingApp.app.constants.ApplicationConstants.TEAM_NOT_FOUND;

@Service
@Transactional
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public List<TeamResponseDto> getAllTeams() {
        return teamRepository.findAll().stream().map(team ->
                TeamResponseDto.builder().teamId(team.getId())
                        .teamName(team.getDescription().name())
                        .teamDescription(team.getDescription().description())
                        .build()
        ).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addTeam(TeamRequestDto request) {
        Team team = new Team();
        team.setDescription(request.description());
        team.setHomePageUri(request.homePage());
        team.setPermissions(request.permissions().stream().map(
                permissionRequestDto -> {
                    String permissionName = permissionRequestDto.permissionName();
                    Permission permission = new Permission();
                    permission.setPermissionName(permissionName);
                    permission.setHttpMethod(permissionRequestDto.httpMethod());
                    permission.setHttpResource(permissionRequestDto.httpResource());
                    permission.setResourceUri(permissionRequestDto.resourceUri());
                    permission = permissionRepository.findByPermissionName(permissionName).orElse(permission);
                    return permission;
                }
        ).collect(Collectors.toSet()));
        teamRepository.save(team);
    }

    public TeamResponseDto getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));
        return TeamResponseDto.builder().teamId(team.getId())
                .teamName(team.getDescription().name())
                .teamDescription(team.getDescription().description())
                .permissions(team.getPermissions().stream().map(
                        permission -> PermissionResponseDto.builder()
                                .permissionName(permission.getPermissionName())
                                .httpResource(permission.isHttpResource())
                                .resourceUri(permission.getResourceUri())
                                .httpMethod(permission.getHttpMethod())
                                .build()
                ).collect(Collectors.toSet()))
                .build();
    }

    public void addPermissionsToTeam(Long teamId, TeamRequestDto request) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));
        Set<Permission> permissions = new HashSet<>();
        request.permissions().forEach(
            permissionRequestDto -> {
                String permissionName = permissionRequestDto.permissionName();
                boolean httpResource = Optional.ofNullable(permissionRequestDto.httpResource()).orElse(false);
                Optional<Permission> permissionInDb = permissionRepository.findByPermissionName(permissionName);
                Permission permission = null;
                if(permissionInDb.isEmpty()) {
                    permission = new Permission();
                    permission.setPermissionName(permissionName);
                    permission.setHttpMethod(permissionRequestDto.httpMethod());
                    permission.setHttpResource(httpResource);
                    permission.setResourceUri(permissionRequestDto.resourceUri());
                    permissionRepository.save(permission);
                } else{
                    permission = permissionInDb.get();
                }
                permissions.add(permission);
            });
        team.setPermissions(permissions);
        teamRepository.save(team);
    }

    public void addPermissionToTeam(Long teamId, PermissionRequestDto request) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));

        String permissionName = request.permissionName();
        boolean httpResource = Optional.ofNullable(request.httpResource()).orElse(false);

        Permission permission = new Permission();
        permission.setPermissionName(permissionName);
        permission.setHttpMethod(request.httpMethod());
        permission.setHttpResource(httpResource);
        permission.setResourceUri(request.resourceUri());

        permission = permissionRepository.findByPermissionName(permissionName).orElse(permission);

        Set<Team> teams = permission.getTeams();
        teams.add(team);
        permission.setTeams(teams);

        permissionRepository.save(permission);
    }
}
