package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.ParamValueDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.ParamValue}.
 */
public interface ParamValueService {
    /**
     * Save a paramValue.
     *
     * @param paramValueDTO the entity to save.
     * @return the persisted entity.
     */
    ParamValueDTO save(ParamValueDTO paramValueDTO);

    /**
     * Updates a paramValue.
     *
     * @param paramValueDTO the entity to update.
     * @return the persisted entity.
     */
    ParamValueDTO update(ParamValueDTO paramValueDTO);

    /**
     * Partially updates a paramValue.
     *
     * @param paramValueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParamValueDTO> partialUpdate(ParamValueDTO paramValueDTO);

    /**
     * Get all the paramValues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParamValueDTO> findAll(Pageable pageable);

    /**
     * Get the "id" paramValue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParamValueDTO> findOne(Long id);

    /**
     * Delete the "id" paramValue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the paramValue corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParamValueDTO> search(String query, Pageable pageable);
}
