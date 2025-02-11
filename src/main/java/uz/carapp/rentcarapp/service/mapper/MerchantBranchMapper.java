package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Merchant;
import uz.carapp.rentcarapp.domain.MerchantBranch;
import uz.carapp.rentcarapp.service.dto.MerchantBranchDTO;
import uz.carapp.rentcarapp.service.dto.MerchantDTO;

/**
 * Mapper for the entity {@link MerchantBranch} and its DTO {@link MerchantBranchDTO}.
 */
@Mapper(componentModel = "spring")
public interface MerchantBranchMapper extends EntityMapper<MerchantBranchDTO, MerchantBranch> {
    @Mapping(target = "merchant", source = "merchant", qualifiedByName = "merchantId")
    MerchantBranchDTO toDto(MerchantBranch s);

    @Named("merchantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MerchantDTO toDtoMerchantId(Merchant merchant);
}
