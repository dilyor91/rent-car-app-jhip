package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.ParamDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.Param}.
 */
public interface ParamService {
    /**
     * Save a param.
     *
     * @param paramDTO the entity to save.
     * @return the persisted entity.
     */
    ParamDTO save(ParamDTO paramDTO);

    /**
     * Updates a param.
     *
     * @param paramDTO the entity to update.
     * @return the persisted entity.
     */
    ParamDTO update(ParamDTO paramDTO);

    /**
     * Partially updates a param.
     *
     * @param paramDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParamDTO> partialUpdate(ParamDTO paramDTO);

    /**
     * Get all the params.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParamDTO> findAll(Pageable pageable);

    /**
     * Get the "id" param.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParamDTO> findOne(Long id);

    /**
     * Delete the "id" param.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the param corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParamDTO> search(String query, Pageable pageable);
}
