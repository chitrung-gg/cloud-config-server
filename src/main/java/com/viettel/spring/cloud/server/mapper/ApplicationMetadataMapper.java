package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.viettel.spring.cloud.server.dto.applicationmetadata.CreateApplicationMetadataDto;
import com.viettel.spring.cloud.server.dto.applicationmetadata.UpdateApplicationMetadataDto;
import com.viettel.spring.cloud.server.dto.applicationmetadata.ApplicationMetadataDto;
import com.viettel.spring.cloud.server.entity.ApplicationMetadataEntity;

@Mapper(componentModel = "spring")
public interface ApplicationMetadataMapper {

    @Mapping(source = "application.id", target = "applicationId")
    ApplicationMetadataDto convertEntityToDto(ApplicationMetadataEntity applicationMetadataEntity);
    
    @Mapping(source = "application.id", target = "applicationId")
    CreateApplicationMetadataDto convertEntityToCreateDto(ApplicationMetadataEntity applicationMetadataEntity);
    
    @Mapping(source = "application.id", target = "applicationId")
    UpdateApplicationMetadataDto convertEntityToUpdateDto(ApplicationMetadataEntity applicationMetadataEntity);
    
    @Mapping(target = "application", ignore = true)
    ApplicationMetadataEntity convertDtoToEntity(ApplicationMetadataDto applicationMetadataDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ApplicationMetadataEntity convertCreateDtoToEntity(CreateApplicationMetadataDto createApplicationMetadataDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ApplicationMetadataEntity convertUpdateDtoToEntity(UpdateApplicationMetadataDto updateApplicationMetadataDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromUpdateDto(UpdateApplicationMetadataDto updateApplicationMetadataDto, @MappingTarget ApplicationMetadataEntity applicationMetadataEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ApplicationMetadataDto applicationMetadataDto, @MappingTarget ApplicationMetadataEntity applicationMetadataEntity);

    
}
