package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.TranslationDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.Translation}.
 */
public interface TranslationService {
    /**
     * Save a translation.
     *
     * @param translationDTO the entity to save.
     * @return the persisted entity.
     */
    TranslationDTO save(TranslationDTO translationDTO);

    /**
     * Updates a translation.
     *
     * @param translationDTO the entity to update.
     * @return the persisted entity.
     */
    TranslationDTO update(TranslationDTO translationDTO);

    /**
     * Partially updates a translation.
     *
     * @param translationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TranslationDTO> partialUpdate(TranslationDTO translationDTO);

    /**
     * Get all the translations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TranslationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" translation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TranslationDTO> findOne(Long id);

    /**
     * Delete the "id" translation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the translation corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TranslationDTO> search(String query, Pageable pageable);
}
