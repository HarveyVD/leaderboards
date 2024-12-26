package com.dao.quiz.controllers.base;

import com.dao.quiz.exceptions.UnauthorizedException;
import com.dao.quiz.models.domain.User;
import com.dao.quiz.security.services.AuthUserDetails;
import com.dao.quiz.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public abstract class BaseController {
    @Autowired
    private UserManagementService userManagementService;
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void logoutUser() {
        SecurityContextHolder.clearContext();
    }

    public static AuthUserDetails getUserDetails() {
        return (AuthUserDetails) Optional.ofNullable(getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(AuthUserDetails.class::isInstance)
                .orElse(null);
    }

    protected User getLoggedInUser() {
        return Optional.ofNullable(getUserDetails())
                .flatMap(user -> userManagementService.findUserById(user.getUserId()))
                .orElseThrow(() -> {
                    logoutUser();
                    return new UnauthorizedException();
                });
    }

}
