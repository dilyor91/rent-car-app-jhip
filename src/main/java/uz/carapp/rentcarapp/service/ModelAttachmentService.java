package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.ModelAttachmentDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.ModelAttachment}.
 */
public interface ModelAttachmentService {
    /**
     * Save a modelAttachment.
     *
     * @param modelAttachmentDTO the entity to save.
     * @return the persisted entity.
     */
    ModelAttachmentDTO save(ModelAttachmentDTO modelAttachmentDTO);

    /**
     * Updates a modelAttachment.
     *
     * @param modelAttachmentDTO the entity to update.
     * @return the persisted entity.
     */
    ModelAttachmentDTO update(ModelAttachmentDTO modelAttachmentDTO);

    /**
     * Partially updates a modelAttachment.
     *
     * @param modelAttachmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ModelAttachmentDTO> partialUpdate(ModelAttachmentDTO modelAttachmentDTO);

    /**
     * Get all the modelAttachments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ModelAttachmentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" modelAttachment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ModelAttachmentDTO> findOne(Long id);

    /**
     * Delete the "id" modelAttachment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the modelAttachment corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ModelAttachmentDTO> search(String query, Pageable pageable);
}
