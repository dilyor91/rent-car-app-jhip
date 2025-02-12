package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Brand;
import uz.carapp.rentcarapp.domain.Model;
import uz.carapp.rentcarapp.service.dto.BrandDTO;
import uz.carapp.rentcarapp.service.dto.ModelDTO;

/**
 * Mapper for the entity {@link Model} and its DTO {@link ModelDTO}.
 */
@Mapper(componentModel = "spring")
public interface ModelMapper extends EntityMapper<ModelDTO, Model> {
    @Mapping(target = "brand", source = "brand", qualifiedByName = "brandId")
    ModelDTO toDto(Model s);

    @Named("brandId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BrandDTO toDtoBrandId(Brand brand);
}
