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

@Mapper(componentModel = "spring", uses = {ApplicationMetadataMapper.class})
public interface ApplicationMapper {
    ApplicationDto convertEntityToDto(ApplicationEntity applicationEntity);

    CreateApplicationDto convertEntityToCreateDto(ApplicationEntity applicationEntity);

    UpdateApplicationDto convertEntityToUpdateDto(ApplicationEntity applicationEntity);

    @Mapping(target = "metadata.application", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    ApplicationEntity convertDtoToEntity(ApplicationDto applicationDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    ApplicationEntity convertCreateDtoToEntity(CreateApplicationDto createApplicationDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    ApplicationEntity convertUpdateDtoToEntity(UpdateApplicationDto updateApplicationDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    void updateEntityFromDto(UpdateApplicationDto updateApplicationDto, @MappingTarget ApplicationEntity applicationEntity);
}
