package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.ParametrDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.Parametr}.
 */
public interface ParametrService {
    /**
     * Save a parametr.
     *
     * @param parametrDTO the entity to save.
     * @return the persisted entity.
     */
    ParametrDTO save(ParametrDTO parametrDTO);

    /**
     * Updates a parametr.
     *
     * @param parametrDTO the entity to update.
     * @return the persisted entity.
     */
    ParametrDTO update(ParametrDTO parametrDTO);

    /**
     * Partially updates a parametr.
     *
     * @param parametrDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParametrDTO> partialUpdate(ParametrDTO parametrDTO);

    /**
     * Get all the parametrs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParametrDTO> findAll(Pageable pageable);

    /**
     * Get the "id" parametr.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParametrDTO> findOne(Long id);

    /**
     * Delete the "id" parametr.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the parametr corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParametrDTO> search(String query, Pageable pageable);
}
