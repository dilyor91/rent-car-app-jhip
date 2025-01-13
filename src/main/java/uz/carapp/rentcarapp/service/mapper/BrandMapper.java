package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Attachment;
import uz.carapp.rentcarapp.domain.Brand;
import uz.carapp.rentcarapp.service.dto.AttachmentDTO;
import uz.carapp.rentcarapp.service.dto.BrandDTO;

/**
 * Mapper for the entity {@link Brand} and its DTO {@link BrandDTO}.
 */
@Mapper(componentModel = "spring")
public interface BrandMapper extends EntityMapper<BrandDTO, Brand> {
    @Mapping(target = "attachment", source = "attachment", qualifiedByName = "attachmentId")
    BrandDTO toDto(Brand s);

    @Named("attachmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttachmentDTO toDtoAttachmentId(Attachment attachment);
}
