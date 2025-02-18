package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.CarTemplateParamDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.CarTemplateParam}.
 */
public interface CarTemplateParamService {
    /**
     * Save a carTemplateParam.
     *
     * @param carTemplateParamDTO the entity to save.
     * @return the persisted entity.
     */
    CarTemplateParamDTO save(CarTemplateParamDTO carTemplateParamDTO);

    /**
     * Updates a carTemplateParam.
     *
     * @param carTemplateParamDTO the entity to update.
     * @return the persisted entity.
     */
    CarTemplateParamDTO update(CarTemplateParamDTO carTemplateParamDTO);

    /**
     * Partially updates a carTemplateParam.
     *
     * @param carTemplateParamDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarTemplateParamDTO> partialUpdate(CarTemplateParamDTO carTemplateParamDTO);

    /**
     * Get all the carTemplateParams.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarTemplateParamDTO> findAll(Pageable pageable);

    /**
     * Get the "id" carTemplateParam.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarTemplateParamDTO> findOne(Long id);

    /**
     * Delete the "id" carTemplateParam.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the carTemplateParam corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarTemplateParamDTO> search(String query, Pageable pageable);
}
