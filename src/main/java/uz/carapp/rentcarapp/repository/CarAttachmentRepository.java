package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.CarAttachment;

/**
 * Spring Data JPA repository for the CarAttachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarAttachmentRepository extends JpaRepository<CarAttachment, Long> {}
