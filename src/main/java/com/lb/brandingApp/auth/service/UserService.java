package com.lb.brandingApp.auth.service;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.auth.data.models.common.UserExtension;
import com.lb.brandingApp.auth.repository.UserRepository;
import com.lb.brandingApp.common.mapper.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import static com.lb.brandingApp.app.constants.ApplicationConstants.USER_NOT_FOUND;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        Team team = user.getTeam();
        return new UserExtension(user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority(team.getDescription().description())),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                commonMapper.mapPermissions(user.getTeam().getPermissions()),
                team.getHomePageUri(),
                user.isActive(),
                user.isDefaultPass());
    }
}
