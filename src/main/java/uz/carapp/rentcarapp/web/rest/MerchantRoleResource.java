package uz.carapp.rentcarapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.carapp.rentcarapp.repository.MerchantRoleRepository;
import uz.carapp.rentcarapp.service.MerchantRoleService;
import uz.carapp.rentcarapp.service.dto.MerchantRoleDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.MerchantRole}.
 */
@RestController
@RequestMapping("/api/merchant-roles")
public class MerchantRoleResource {

    private static final Logger LOG = LoggerFactory.getLogger(MerchantRoleResource.class);

    private static final String ENTITY_NAME = "merchantRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MerchantRoleService merchantRoleService;

    private final MerchantRoleRepository merchantRoleRepository;

    public MerchantRoleResource(MerchantRoleService merchantRoleService, MerchantRoleRepository merchantRoleRepository) {
        this.merchantRoleService = merchantRoleService;
        this.merchantRoleRepository = merchantRoleRepository;
    }

    /**
     * {@code POST  /merchant-roles} : Create a new merchantRole.
     *
     * @param merchantRoleDTO the merchantRoleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new merchantRoleDTO, or with status {@code 400 (Bad Request)} if the merchantRole has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MerchantRoleDTO> createMerchantRole(@RequestBody MerchantRoleDTO merchantRoleDTO) throws URISyntaxException {
        LOG.debug("REST request to save MerchantRole : {}", merchantRoleDTO);
        if (merchantRoleDTO.getId() != null) {
            throw new BadRequestAlertException("A new merchantRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        merchantRoleDTO = merchantRoleService.save(merchantRoleDTO);
        return ResponseEntity.created(new URI("/api/merchant-roles/" + merchantRoleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, merchantRoleDTO.getId().toString()))
            .body(merchantRoleDTO);
    }

    /**
     * {@code PUT  /merchant-roles/:id} : Updates an existing merchantRole.
     *
     * @param id the id of the merchantRoleDTO to save.
     * @param merchantRoleDTO the merchantRoleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated merchantRoleDTO,
     * or with status {@code 400 (Bad Request)} if the merchantRoleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the merchantRoleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MerchantRoleDTO> updateMerchantRole(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MerchantRoleDTO merchantRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MerchantRole : {}, {}", id, merchantRoleDTO);
        if (merchantRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, merchantRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!merchantRoleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        merchantRoleDTO = merchantRoleService.update(merchantRoleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, merchantRoleDTO.getId().toString()))
            .body(merchantRoleDTO);
    }

    /**
     * {@code PATCH  /merchant-roles/:id} : Partial updates given fields of an existing merchantRole, field will ignore if it is null
     *
     * @param id the id of the merchantRoleDTO to save.
     * @param merchantRoleDTO the merchantRoleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated merchantRoleDTO,
     * or with status {@code 400 (Bad Request)} if the merchantRoleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the merchantRoleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the merchantRoleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MerchantRoleDTO> partialUpdateMerchantRole(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MerchantRoleDTO merchantRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MerchantRole partially : {}, {}", id, merchantRoleDTO);
        if (merchantRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, merchantRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!merchantRoleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MerchantRoleDTO> result = merchantRoleService.partialUpdate(merchantRoleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, merchantRoleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /merchant-roles} : get all the merchantRoles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of merchantRoles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MerchantRoleDTO>> getAllMerchantRoles(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of MerchantRoles");
        Page<MerchantRoleDTO> page = merchantRoleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /merchant-roles/:id} : get the "id" merchantRole.
     *
     * @param id the id of the merchantRoleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the merchantRoleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MerchantRoleDTO> getMerchantRole(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MerchantRole : {}", id);
        Optional<MerchantRoleDTO> merchantRoleDTO = merchantRoleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(merchantRoleDTO);
    }

    /**
     * {@code DELETE  /merchant-roles/:id} : delete the "id" merchantRole.
     *
     * @param id the id of the merchantRoleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchantRole(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MerchantRole : {}", id);
        merchantRoleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /merchant-roles/_search?query=:query} : search for the merchantRole corresponding
     * to the query.
     *
     * @param query the query of the merchantRole search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<MerchantRoleDTO>> searchMerchantRoles(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of MerchantRoles for query {}", query);
        try {
            Page<MerchantRoleDTO> page = merchantRoleService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
