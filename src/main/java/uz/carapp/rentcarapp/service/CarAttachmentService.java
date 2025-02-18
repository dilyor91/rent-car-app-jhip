package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.CarAttachmentDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.CarAttachment}.
 */
public interface CarAttachmentService {
    /**
     * Save a carAttachment.
     *
     * @param carAttachmentDTO the entity to save.
     * @return the persisted entity.
     */
    CarAttachmentDTO save(CarAttachmentDTO carAttachmentDTO);

    /**
     * Updates a carAttachment.
     *
     * @param carAttachmentDTO the entity to update.
     * @return the persisted entity.
     */
    CarAttachmentDTO update(CarAttachmentDTO carAttachmentDTO);

    /**
     * Partially updates a carAttachment.
     *
     * @param carAttachmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarAttachmentDTO> partialUpdate(CarAttachmentDTO carAttachmentDTO);

    /**
     * Get all the carAttachments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarAttachmentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" carAttachment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarAttachmentDTO> findOne(Long id);

    /**
     * Delete the "id" carAttachment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the carAttachment corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarAttachmentDTO> search(String query, Pageable pageable);
}
