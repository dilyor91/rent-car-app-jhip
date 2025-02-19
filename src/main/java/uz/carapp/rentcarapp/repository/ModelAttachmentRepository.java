package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.ModelAttachment;

/**
 * Spring Data JPA repository for the ModelAttachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ModelAttachmentRepository extends JpaRepository<ModelAttachment, Long> {}
