package com.lebedev.exchangeRate.mapper;

import com.lebedev.exchangeRate.dto.ChatDTO;
import com.lebedev.exchangeRate.entity.Chat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    ChatDTO toDto(Chat chat);
}
