package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Car;
import uz.carapp.rentcarapp.domain.CarParam;
import uz.carapp.rentcarapp.domain.Param;
import uz.carapp.rentcarapp.service.dto.CarDTO;
import uz.carapp.rentcarapp.service.dto.CarParamDTO;
import uz.carapp.rentcarapp.service.dto.ParamDTO;

/**
 * Mapper for the entity {@link CarParam} and its DTO {@link CarParamDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarParamMapper extends EntityMapper<CarParamDTO, CarParam> {
    @Mapping(target = "car", source = "car", qualifiedByName = "carId")
    @Mapping(target = "param", source = "param", qualifiedByName = "paramId")
    CarParamDTO toDto(CarParam s);

    @Named("carId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CarDTO toDtoCarId(Car car);

    @Named("paramId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParamDTO toDtoParamId(Param param);
}
