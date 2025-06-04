package com.viettel.spring.cloud.server.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.viettel.spring.cloud.server.dto.configversion.ConfigVersionDto;
import com.viettel.spring.cloud.server.dto.configversion.CreateConfigVersionDto;
import com.viettel.spring.cloud.server.dto.configversion.UpdateConfigVersionDto;
import com.viettel.spring.cloud.server.entity.ConfigVersionEntity;

@Mapper(componentModel = "spring")
public interface ConfigVersionMapper {
    ConfigVersionDto convertEntityToDto(ConfigVersionEntity configVersionEntity);
    
    @Mapping(source = "applicationProfile.id", target = "applicationProfileId")
    CreateConfigVersionDto convertEntityToCreateDto(ConfigVersionEntity configVersionEntity);

    @Mapping(source = "applicationProfile.id", target = "applicationProfileId")
    UpdateConfigVersionDto convertEntityToUpdateDto(ConfigVersionEntity configVersionEntity);
    
    @Mapping(target = "applicationProfile", ignore = true)
    ConfigVersionEntity convertDtoToEntity(ConfigVersionDto configVersionDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "snapshotHash", ignore = true)
    ConfigVersionEntity convertCreateDtoToEntity(CreateConfigVersionDto createConfigVersionDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "snapshotHash", ignore = true)
    ConfigVersionEntity convertUpdateDtoToEntity(UpdateConfigVersionDto updateConfigVersionDto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applicationProfile", ignore = true)
    @Mapping(target = "snapshotHash", ignore = true)
    void updateEntityFromDto(UpdateConfigVersionDto updateConfigVersionDto, @MappingTarget ConfigVersionEntity configVersionEntity);
    
}