package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Car;
import uz.carapp.rentcarapp.domain.Merchant;
import uz.carapp.rentcarapp.domain.MerchantBranch;
import uz.carapp.rentcarapp.domain.Model;
import uz.carapp.rentcarapp.service.dto.CarDTO;
import uz.carapp.rentcarapp.service.dto.MerchantBranchDTO;
import uz.carapp.rentcarapp.service.dto.MerchantDTO;
import uz.carapp.rentcarapp.service.dto.ModelDTO;

/**
 * Mapper for the entity {@link Car} and its DTO {@link CarDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarMapper extends EntityMapper<CarDTO, Car> {
    @Mapping(target = "model", source = "model", qualifiedByName = "modelId")
    @Mapping(target = "merchant", source = "merchant", qualifiedByName = "merchantId")
    @Mapping(target = "merchantBranch", source = "merchantBranch", qualifiedByName = "merchantBranchId")
    CarDTO toDto(Car s);

    @Named("modelId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ModelDTO toDtoModelId(Model model);

    @Named("merchantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MerchantDTO toDtoMerchantId(Merchant merchant);

    @Named("merchantBranchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MerchantBranchDTO toDtoMerchantBranchId(MerchantBranch merchantBranch);
}
