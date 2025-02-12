package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Param;
import uz.carapp.rentcarapp.domain.ParamValue;
import uz.carapp.rentcarapp.service.dto.ParamDTO;
import uz.carapp.rentcarapp.service.dto.ParamValueDTO;

/**
 * Mapper for the entity {@link ParamValue} and its DTO {@link ParamValueDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParamValueMapper extends EntityMapper<ParamValueDTO, ParamValue> {
    @Mapping(target = "param", source = "param", qualifiedByName = "paramId")
    ParamValueDTO toDto(ParamValue s);

    @Named("paramId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParamDTO toDtoParamId(Param param);
}
