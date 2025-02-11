package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Merchant;
import uz.carapp.rentcarapp.domain.MerchantBranch;
import uz.carapp.rentcarapp.domain.MerchantRole;
import uz.carapp.rentcarapp.domain.User;
import uz.carapp.rentcarapp.service.dto.MerchantBranchDTO;
import uz.carapp.rentcarapp.service.dto.MerchantDTO;
import uz.carapp.rentcarapp.service.dto.MerchantRoleDTO;
import uz.carapp.rentcarapp.service.dto.UserDTO;

/**
 * Mapper for the entity {@link MerchantRole} and its DTO {@link MerchantRoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface MerchantRoleMapper extends EntityMapper<MerchantRoleDTO, MerchantRole> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "merchant", source = "merchant", qualifiedByName = "merchantId")
    @Mapping(target = "merchantBranch", source = "merchantBranch", qualifiedByName = "merchantBranchId")
    MerchantRoleDTO toDto(MerchantRole s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("merchantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MerchantDTO toDtoMerchantId(Merchant merchant);

    @Named("merchantBranchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MerchantBranchDTO toDtoMerchantBranchId(MerchantBranch merchantBranch);
}
