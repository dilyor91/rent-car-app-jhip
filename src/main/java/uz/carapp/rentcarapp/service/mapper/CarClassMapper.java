package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.CarClass;
import uz.carapp.rentcarapp.service.dto.CarClassDTO;

/**
 * Mapper for the entity {@link CarClass} and its DTO {@link CarClassDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarClassMapper extends EntityMapper<CarClassDTO, CarClass> {}
