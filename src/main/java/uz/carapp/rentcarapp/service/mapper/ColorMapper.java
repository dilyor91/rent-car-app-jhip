package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Color;
import uz.carapp.rentcarapp.service.dto.ColorDTO;

/**
 * Mapper for the entity {@link Color} and its DTO {@link ColorDTO}.
 */
@Mapper(componentModel = "spring")
public interface ColorMapper extends EntityMapper<ColorDTO, Color> {}
