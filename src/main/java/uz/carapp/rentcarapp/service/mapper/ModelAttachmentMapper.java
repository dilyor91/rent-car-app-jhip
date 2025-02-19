package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Attachment;
import uz.carapp.rentcarapp.domain.Model;
import uz.carapp.rentcarapp.domain.ModelAttachment;
import uz.carapp.rentcarapp.service.dto.AttachmentDTO;
import uz.carapp.rentcarapp.service.dto.ModelAttachmentDTO;
import uz.carapp.rentcarapp.service.dto.ModelDTO;

/**
 * Mapper for the entity {@link ModelAttachment} and its DTO {@link ModelAttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ModelAttachmentMapper extends EntityMapper<ModelAttachmentDTO, ModelAttachment> {
    @Mapping(target = "model", source = "model", qualifiedByName = "modelId")
    @Mapping(target = "attachment", source = "attachment", qualifiedByName = "attachmentId")
    ModelAttachmentDTO toDto(ModelAttachment s);

    @Named("modelId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ModelDTO toDtoModelId(Model model);

    @Named("attachmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttachmentDTO toDtoAttachmentId(Attachment attachment);
}
