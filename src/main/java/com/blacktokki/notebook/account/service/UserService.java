package com.blacktokki.notebook.account.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.blacktokki.notebook.account.dto.UserDto;
import com.blacktokki.notebook.account.dto.UserQueryParam;
import com.blacktokki.notebook.account.entity.User;
import com.blacktokki.notebook.core.dto.AuthenticateDto;
import com.blacktokki.notebook.core.dto.BaseUserDto;
import com.blacktokki.notebook.core.service.restful.RestfulService;

@Service
public class UserService extends RestfulService<UserDto, User, Long> implements UserDetailsService{
    @Override
    public AuthenticateDto loadUserByUsername(String username) throws UsernameNotFoundException {
        UserQueryParam userSpecification = new UserQueryParam(null, username, null);
        User user = getExecutor().findOne(toSpecification(userSpecification)).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new AuthenticateDto(user.getId(), user.getUsername(), user.getName());
    }

    @Override
    public Predicate toPredicate(String key, Object value, Root<User> root, CriteriaBuilder builder){
        if (value == null){
            return null;
        }
        if (key.equals("self") && (Boolean)value){
            String username = ((BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).username();
            return builder.equal(root.get("username"), username);
        }
        return builder.equal(root.get(key), value);
    }

    @Override
    public UserDto toDto(User e) {
        throw new UnsupportedOperationException("Unimplemented method 'toDto'");
    }

    @Override
    public User toEntity(UserDto b) {
        throw new UnsupportedOperationException("Unimplemented method 'toEntity'");
    }
}