package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.CarTemplate;
import uz.carapp.rentcarapp.domain.Model;
import uz.carapp.rentcarapp.service.dto.CarTemplateDTO;
import uz.carapp.rentcarapp.service.dto.ModelDTO;

/**
 * Mapper for the entity {@link CarTemplate} and its DTO {@link CarTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarTemplateMapper extends EntityMapper<CarTemplateDTO, CarTemplate> {
    @Mapping(target = "model", source = "model", qualifiedByName = "modelId")
    CarTemplateDTO toDto(CarTemplate s);

    @Named("modelId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ModelDTO toDtoModelId(Model model);
}
