package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.CarBody;
import uz.carapp.rentcarapp.service.dto.CarBodyDTO;

/**
 * Mapper for the entity {@link CarBody} and its DTO {@link CarBodyDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarBodyMapper extends EntityMapper<CarBodyDTO, CarBody> {}
