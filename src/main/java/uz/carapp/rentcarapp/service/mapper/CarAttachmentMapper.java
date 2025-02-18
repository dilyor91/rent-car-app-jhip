package uz.carapp.rentcarapp.service.mapper;

import org.mapstruct.*;
import uz.carapp.rentcarapp.domain.Attachment;
import uz.carapp.rentcarapp.domain.Car;
import uz.carapp.rentcarapp.domain.CarAttachment;
import uz.carapp.rentcarapp.service.dto.AttachmentDTO;
import uz.carapp.rentcarapp.service.dto.CarAttachmentDTO;
import uz.carapp.rentcarapp.service.dto.CarDTO;

/**
 * Mapper for the entity {@link CarAttachment} and its DTO {@link CarAttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarAttachmentMapper extends EntityMapper<CarAttachmentDTO, CarAttachment> {
    @Mapping(target = "car", source = "car", qualifiedByName = "carId")
    @Mapping(target = "attachment", source = "attachment", qualifiedByName = "attachmentId")
    CarAttachmentDTO toDto(CarAttachment s);

    @Named("carId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CarDTO toDtoCarId(Car car);

    @Named("attachmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttachmentDTO toDtoAttachmentId(Attachment attachment);
}
