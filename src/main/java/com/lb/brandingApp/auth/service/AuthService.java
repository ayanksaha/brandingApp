package com.lb.brandingApp.auth.service;

import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.auth.data.models.common.PermissionDto;
import com.lb.brandingApp.auth.data.models.common.UserExtension;
import com.lb.brandingApp.auth.data.models.request.RegistrationRequestDto;
import com.lb.brandingApp.auth.data.models.request.UserRequestDto;
import com.lb.brandingApp.auth.data.models.response.PermissionResponseDto;
import com.lb.brandingApp.auth.data.models.response.UserResponseDto;
import com.lb.brandingApp.auth.repository.UserRepository;
import com.lb.brandingApp.auth.repository.TeamRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lb.brandingApp.app.constants.ApplicationConstants.*;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private JwtUtilsService jwtUtilsService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Value("${user.default.password}")
    private String defaultPassword;

    public UserResponseDto authenticate(UserRequestDto request) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        UserExtension userDetails = (UserExtension) authenticationResponse.getPrincipal();

        List<String> teams = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String username = userDetails.getUsername();
        String teamName = teams.get(0);
        Set<PermissionDto> permissions = userDetails.getPermissions();

        return UserResponseDto.builder()
                .username(username)
                .idToken(jwtUtilsService.generateIdToken(username, teamName, permissions))
                .team(teamName)
                .permissions(userDetails.getPermissions().stream()
                        .filter(permission -> !permission.isHttpResource())
                        .map(permissionDto -> PermissionResponseDto.builder()
                                .permissionName(permissionDto.getPermissionName())
                                .httpResource(permissionDto.isHttpResource())
                                .resourceUri(permissionDto.getResourceUri())
                                .httpMethod(permissionDto.getHttpMethod())
                                .build()
                        ).collect(Collectors.toSet()))
                .name(userDetails.getName())
                .email(userDetails.getEmail())
                .phoneNumber(userDetails.getPhoneNumber())
                .landingPage(userDetails.getHomePage())
                .active(userDetails.isActive())
                .defaultPasswordSet(userDetails.isDefaultPass())
                .build();
    }

    public void changePassword(UserRequestDto request) {
        UserExtension userDetails = (UserExtension) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User userInDb = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        userInDb.setPassword(encoder.encode(request.newPassword()));
        userInDb.setDefaultPass(false);
        userRepository.save(userInDb);
    }

    public List<UserResponseDto> getAllUsers(boolean fetchActive, String name) {
        UserExtension userDetails = (UserExtension) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        List<User> result;
        //TODO Pagination & Generalise
        if(Objects.nonNull(name)) {
            result = userRepository.findAllByNameContaining(name);
        } else {
            result = userRepository.findAll();
        }
        return result.stream().filter(user -> !user.getUsername().equals(username))
                .filter(user -> user.isActive() == fetchActive)
                .map(user -> UserResponseDto.builder()
                        .username(user.getUsername())
                        .team(user.getTeam().getDescription().description())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .active(user.isActive())
                        .defaultPasswordSet(user.isDefaultPass())
                        .build())
                .toList();
    }


    public UserResponseDto getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        return UserResponseDto.builder()
                .username(user.getUsername())
                .team(user.getTeam().getDescription().description())
                .name(user.getName())
                .email(user.getEmail())
                .active(user.isActive())
                .defaultPasswordSet(user.isDefaultPass())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void resetUserPassword(UserRequestDto request) {
        String username = request.username();
        User userInDb = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        userInDb.setPassword(encoder.encode(defaultPassword));
        userInDb.setDefaultPass(true);
        userRepository.save(userInDb);
    }

    public void toggleUserActivation(UserRequestDto request, boolean activate) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        if(user.isActive() == activate) return;
        user.setActive(activate);
        if(activate) {
            user.setPassword(encoder.encode(defaultPassword));
            user.setDefaultPass(true);
        }
        userRepository.save(user);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public UserResponseDto registerUser(RegistrationRequestDto request) {
        String encryptedPass = encoder.encode(defaultPassword);
        User user = new User();
        user.setUsername(request.username());
        user.setName(request.name());
        user.setPassword(encryptedPass);
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setTeam(teamRepository.findById(request.teamId())
                .orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND)));
        user.setActive(true);
        user.setDefaultPass(true);
        User savedUser = userRepository.save(user);
        return UserResponseDto.builder()
                .username(savedUser.getUsername())
                .active(user.isActive())
                .defaultPasswordSet(user.isDefaultPass())
                .message(REGISTRATION_SUCCESSFUL)
                .build();
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public UserResponseDto updateUser(RegistrationRequestDto request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setTeam(teamRepository.findById(request.teamId())
                .orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND)));
        User savedUser = userRepository.save(user);
        return UserResponseDto.builder()
                .username(savedUser.getUsername())
                .message(UPDATE_USER_SUCCESSFUL)
                .build();
    }
}
