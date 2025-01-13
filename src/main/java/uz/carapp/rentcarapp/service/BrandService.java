package uz.carapp.rentcarapp.service;

import java.util.List;
import java.util.Optional;
import uz.carapp.rentcarapp.service.dto.BrandDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.Brand}.
 */
public interface BrandService {
    /**
     * Save a brand.
     *
     * @param brandDTO the entity to save.
     * @return the persisted entity.
     */
    BrandDTO save(BrandDTO brandDTO);

    /**
     * Updates a brand.
     *
     * @param brandDTO the entity to update.
     * @return the persisted entity.
     */
    BrandDTO update(BrandDTO brandDTO);

    /**
     * Partially updates a brand.
     *
     * @param brandDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BrandDTO> partialUpdate(BrandDTO brandDTO);

    /**
     * Get all the brands.
     *
     * @return the list of entities.
     */
    List<BrandDTO> findAll();

    /**
     * Get the "id" brand.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BrandDTO> findOne(Long id);

    /**
     * Delete the "id" brand.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the brand corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<BrandDTO> search(String query);
}
