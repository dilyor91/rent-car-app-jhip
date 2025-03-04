package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Document;
import uz.carapp.rentcarapp.domain.Merchant;
import uz.carapp.rentcarapp.domain.MerchantDocument;
import uz.carapp.rentcarapp.service.dto.DocumentDTO;
import uz.carapp.rentcarapp.service.dto.MerchantDTO;
import uz.carapp.rentcarapp.service.dto.MerchantDocumentDTO;

/**
 * Mapper for the entity {@link MerchantDocument} and its DTO {@link MerchantDocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface MerchantDocumentMapper extends EntityMapper<MerchantDocumentDTO, MerchantDocument> {
    @Mapping(target = "merchant", source = "merchant", qualifiedByName = "merchantId")
    @Mapping(target = "document", source = "document", qualifiedByName = "documentId")
    MerchantDocumentDTO toDto(MerchantDocument s);

    @Named("merchantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MerchantDTO toDtoMerchantId(Merchant merchant);

    @Named("documentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DocumentDTO toDtoDocumentId(Document document);
}
