package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.CarTemplateDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.CarTemplate}.
 */
public interface CarTemplateService {
    /**
     * Save a carTemplate.
     *
     * @param carTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    CarTemplateDTO save(CarTemplateDTO carTemplateDTO);

    /**
     * Updates a carTemplate.
     *
     * @param carTemplateDTO the entity to update.
     * @return the persisted entity.
     */
    CarTemplateDTO update(CarTemplateDTO carTemplateDTO);

    /**
     * Partially updates a carTemplate.
     *
     * @param carTemplateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarTemplateDTO> partialUpdate(CarTemplateDTO carTemplateDTO);

    /**
     * Get all the carTemplates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarTemplateDTO> findAll(Pageable pageable);

    /**
     * Get the "id" carTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" carTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the carTemplate corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarTemplateDTO> search(String query, Pageable pageable);
}
