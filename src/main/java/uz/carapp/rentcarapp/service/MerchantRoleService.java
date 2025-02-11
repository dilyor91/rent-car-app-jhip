package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.MerchantRoleDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.MerchantRole}.
 */
public interface MerchantRoleService {
    /**
     * Save a merchantRole.
     *
     * @param merchantRoleDTO the entity to save.
     * @return the persisted entity.
     */
    MerchantRoleDTO save(MerchantRoleDTO merchantRoleDTO);

    /**
     * Updates a merchantRole.
     *
     * @param merchantRoleDTO the entity to update.
     * @return the persisted entity.
     */
    MerchantRoleDTO update(MerchantRoleDTO merchantRoleDTO);

    /**
     * Partially updates a merchantRole.
     *
     * @param merchantRoleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MerchantRoleDTO> partialUpdate(MerchantRoleDTO merchantRoleDTO);

    /**
     * Get all the merchantRoles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MerchantRoleDTO> findAll(Pageable pageable);

    /**
     * Get the "id" merchantRole.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MerchantRoleDTO> findOne(Long id);

    /**
     * Delete the "id" merchantRole.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the merchantRole corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MerchantRoleDTO> search(String query, Pageable pageable);
}
