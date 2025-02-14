package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.CarParamDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.CarParam}.
 */
public interface CarParamService {
    /**
     * Save a carParam.
     *
     * @param carParamDTO the entity to save.
     * @return the persisted entity.
     */
    CarParamDTO save(CarParamDTO carParamDTO);

    /**
     * Updates a carParam.
     *
     * @param carParamDTO the entity to update.
     * @return the persisted entity.
     */
    CarParamDTO update(CarParamDTO carParamDTO);

    /**
     * Partially updates a carParam.
     *
     * @param carParamDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarParamDTO> partialUpdate(CarParamDTO carParamDTO);

    /**
     * Get all the carParams.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarParamDTO> findAll(Pageable pageable);

    /**
     * Get the "id" carParam.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarParamDTO> findOne(Long id);

    /**
     * Delete the "id" carParam.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the carParam corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarParamDTO> search(String query, Pageable pageable);
}
