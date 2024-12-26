package com.dao.quiz.mappers.converters;

import com.dao.quiz.dto.users.UserDTO;
import com.dao.quiz.mappers.DTOBuilder;
import com.dao.quiz.models.domain.User;
import org.modelmapper.AbstractConverter;

public class UserToDTOConverter extends AbstractConverter<User, UserDTO> {
    @Override
    protected UserDTO convert(User source) {
        return DTOBuilder.toDTO(source, UserDTO.class);
    }
}
