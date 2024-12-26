package com.dao.quiz.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DTOBuilder {
    public static final String DATE_PATTERN_DEF = "dd-MM-yyyy";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_PATTERN_DEF)
            .withLocale(Locale.US)
            .withZone(ZoneId.of("UTC"));
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();
    static {
        registerDefaultConverters(MODEL_MAPPER);
    }
    public static void registerDefaultConverters(ModelMapper modelMapper) {
        modelMapper.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source == null ? null : ZonedDateTime.ofInstant(source, ZoneOffset.UTC).toString();
            }
        });
        modelMapper.addConverter(new AbstractConverter<LocalDate, String>() {
            @Override
            protected String convert(LocalDate source) {
                return source == null ? null : DATE_FORMAT.format(source);
            }
        });
    }
    public static <S, T> S toDTO(T entity, Class<S> targetClass) {
        if (entity == null) {
            return null;
        }
        return MODEL_MAPPER.map(entity, targetClass);
    }

    public static <S, T> List<S> toDTO(Iterable<T> entities, Class<S> targetClass) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(entity -> toDTO(entity, targetClass))
                .collect(Collectors.toList());
    }

}
