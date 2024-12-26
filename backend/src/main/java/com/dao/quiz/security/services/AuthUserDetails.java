package com.dao.quiz.security.services;

import com.dao.quiz.models.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Collection;

public class AuthUserDetails implements UserDetails, Serializable {
    private final String username;
    private final long userId;
    public AuthUserDetails(User user) {
        this.username = user.getUsername();
        this.userId = user.getId();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @Transient
    public String getPassword() {
        return null;
    }

    public Long getUserId() {
        return userId;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
