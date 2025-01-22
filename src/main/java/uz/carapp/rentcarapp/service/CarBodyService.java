package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.CarBodyDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.CarBody}.
 */
public interface CarBodyService {
    /**
     * Save a carBody.
     *
     * @param carBodyDTO the entity to save.
     * @return the persisted entity.
     */
    CarBodyDTO save(CarBodyDTO carBodyDTO);

    /**
     * Updates a carBody.
     *
     * @param carBodyDTO the entity to update.
     * @return the persisted entity.
     */
    CarBodyDTO update(CarBodyDTO carBodyDTO);

    /**
     * Partially updates a carBody.
     *
     * @param carBodyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarBodyDTO> partialUpdate(CarBodyDTO carBodyDTO);

    /**
     * Get all the carBodies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarBodyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" carBody.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarBodyDTO> findOne(Long id);

    /**
     * Delete the "id" carBody.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the carBody corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarBodyDTO> search(String query, Pageable pageable);
}
