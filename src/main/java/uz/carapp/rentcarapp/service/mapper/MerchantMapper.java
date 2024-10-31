package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Merchant;
import uz.carapp.rentcarapp.service.dto.MerchantDTO;

/**
 * Mapper for the entity {@link Merchant} and its DTO {@link MerchantDTO}.
 */
@Mapper(componentModel = "spring")
public interface MerchantMapper extends EntityMapper<MerchantDTO, Merchant> {}
