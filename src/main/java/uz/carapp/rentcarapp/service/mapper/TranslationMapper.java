package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Translation;
import uz.carapp.rentcarapp.service.dto.TranslationDTO;

/**
 * Mapper for the entity {@link Translation} and its DTO {@link TranslationDTO}.
 */
@Mapper(componentModel = "spring")
public interface TranslationMapper extends EntityMapper<TranslationDTO, Translation> {}
