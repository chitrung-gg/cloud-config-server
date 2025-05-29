package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.viettel.spring.cloud.server.dto.application.ApplicationDto;
import com.viettel.spring.cloud.server.dto.application.CreateApplicationDto;
import com.viettel.spring.cloud.server.dto.application.UpdateApplicationDto;
import com.viettel.spring.cloud.server.entity.ApplicationEntity;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
    ApplicationDto convertEntityToDto(ApplicationEntity applicationEntity);

    CreateApplicationDto convertEntityToCreateDto(ApplicationEntity applicationEntity);

    UpdateApplicationDto convertEntityToUpdateDto(ApplicationEntity applicationEntity);

    ApplicationEntity convertDtoToEntity(ApplicationDto applicationDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ApplicationEntity convertCreateDtoToEntity(CreateApplicationDto createApplicationDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ApplicationEntity convertUpdateDtoToEntity(UpdateApplicationDto updateApplicationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateApplicationDto updateApplicationDto, @MappingTarget ApplicationEntity applicationEntity);
}
