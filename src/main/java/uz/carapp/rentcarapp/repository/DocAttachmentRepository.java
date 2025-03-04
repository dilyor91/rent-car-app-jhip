package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.DocAttachment;

/**
 * Spring Data JPA repository for the DocAttachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocAttachmentRepository extends JpaRepository<DocAttachment, Long> {}
