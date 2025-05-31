package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.viettel.spring.cloud.server.dto.configproperty.ConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configproperty.CreateConfigPropertyDto;
import com.viettel.spring.cloud.server.dto.configproperty.UpdateConfigPropertyDto;
import com.viettel.spring.cloud.server.entity.ConfigPropertyEntity;

@Mapper(componentModel = "spring")
public interface ConfigPropertyMapper {
    ConfigPropertyDto convertEntityToDto(ConfigPropertyEntity ConfigPropertyEntity);
    
    @Mapping(source = "applicationProfile.id", target = "applicationProfileId")
    CreateConfigPropertyDto convertEntityToCreateDto(ConfigPropertyEntity ConfigPropertyEntity);

    @Mapping(source = "applicationProfile.id", target = "applicationProfileId")
    UpdateConfigPropertyDto convertEntityToUpdateDto(ConfigPropertyEntity ConfigPropertyEntity);
    
    @Mapping(target = "applicationProfile", ignore = true)
    ConfigPropertyEntity convertDtoToEntity(ConfigPropertyDto ConfigPropertyDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    ConfigPropertyEntity convertCreateDtoToEntity(CreateConfigPropertyDto createConfigPropertyDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    ConfigPropertyEntity convertUpdateDtoToEntity(UpdateConfigPropertyDto updateConfigPropertyDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    void updateEntityFromDto(UpdateConfigPropertyDto updateConfigPropertyDto, @MappingTarget ConfigPropertyEntity ConfigPropertyEntity);
}
