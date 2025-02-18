package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.CarTemplate;
import uz.carapp.rentcarapp.domain.CarTemplateParam;
import uz.carapp.rentcarapp.domain.Param;
import uz.carapp.rentcarapp.domain.ParamValue;
import uz.carapp.rentcarapp.service.dto.CarTemplateDTO;
import uz.carapp.rentcarapp.service.dto.CarTemplateParamDTO;
import uz.carapp.rentcarapp.service.dto.ParamDTO;
import uz.carapp.rentcarapp.service.dto.ParamValueDTO;

/**
 * Mapper for the entity {@link CarTemplateParam} and its DTO {@link CarTemplateParamDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarTemplateParamMapper extends EntityMapper<CarTemplateParamDTO, CarTemplateParam> {
    @Mapping(target = "carTemplate", source = "carTemplate", qualifiedByName = "carTemplateId")
    @Mapping(target = "param", source = "param", qualifiedByName = "paramId")
    @Mapping(target = "paramValue", source = "paramValue", qualifiedByName = "paramValueId")
    CarTemplateParamDTO toDto(CarTemplateParam s);

    @Named("carTemplateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CarTemplateDTO toDtoCarTemplateId(CarTemplate carTemplate);

    @Named("paramId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParamDTO toDtoParamId(Param param);

    @Named("paramValueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParamValueDTO toDtoParamValueId(ParamValue paramValue);
}
