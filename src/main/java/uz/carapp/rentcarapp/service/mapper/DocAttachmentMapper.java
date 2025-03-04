package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Attachment;
import uz.carapp.rentcarapp.domain.DocAttachment;
import uz.carapp.rentcarapp.domain.Document;
import uz.carapp.rentcarapp.service.dto.AttachmentDTO;
import uz.carapp.rentcarapp.service.dto.DocAttachmentDTO;
import uz.carapp.rentcarapp.service.dto.DocumentDTO;

/**
 * Mapper for the entity {@link DocAttachment} and its DTO {@link DocAttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DocAttachmentMapper extends EntityMapper<DocAttachmentDTO, DocAttachment> {
    @Mapping(target = "document", source = "document", qualifiedByName = "documentId")
    @Mapping(target = "attachment", source = "attachment", qualifiedByName = "attachmentId")
    DocAttachmentDTO toDto(DocAttachment s);

    @Named("documentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DocumentDTO toDtoDocumentId(Document document);

    @Named("attachmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttachmentDTO toDtoAttachmentId(Attachment attachment);
}
