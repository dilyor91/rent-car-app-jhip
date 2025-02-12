package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Param;
import uz.carapp.rentcarapp.service.dto.ParamDTO;

/**
 * Mapper for the entity {@link Param} and its DTO {@link ParamDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParamMapper extends EntityMapper<ParamDTO, Param> {}
