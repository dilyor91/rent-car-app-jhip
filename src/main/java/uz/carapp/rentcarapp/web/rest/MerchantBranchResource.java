package uz.carapp.rentcarapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.carapp.rentcarapp.repository.MerchantBranchRepository;
import uz.carapp.rentcarapp.service.MerchantBranchService;
import uz.carapp.rentcarapp.service.dto.MerchantBranchDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.MerchantBranch}.
 */
@RestController
@RequestMapping("/api/merchant-branches")
public class MerchantBranchResource {

    private static final Logger LOG = LoggerFactory.getLogger(MerchantBranchResource.class);

    private static final String ENTITY_NAME = "merchantBranch";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MerchantBranchService merchantBranchService;

    private final MerchantBranchRepository merchantBranchRepository;

    public MerchantBranchResource(MerchantBranchService merchantBranchService, MerchantBranchRepository merchantBranchRepository) {
        this.merchantBranchService = merchantBranchService;
        this.merchantBranchRepository = merchantBranchRepository;
    }

    /**
     * {@code POST  /merchant-branches} : Create a new merchantBranch.
     *
     * @param merchantBranchDTO the merchantBranchDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new merchantBranchDTO, or with status {@code 400 (Bad Request)} if the merchantBranch has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MerchantBranchDTO> createMerchantBranch(@RequestBody MerchantBranchDTO merchantBranchDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MerchantBranch : {}", merchantBranchDTO);
        if (merchantBranchDTO.getId() != null) {
            throw new BadRequestAlertException("A new merchantBranch cannot already have an ID", ENTITY_NAME, "idexists");
        }
        merchantBranchDTO = merchantBranchService.save(merchantBranchDTO);
        return ResponseEntity.created(new URI("/api/merchant-branches/" + merchantBranchDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, merchantBranchDTO.getId().toString()))
            .body(merchantBranchDTO);
    }

    /**
     * {@code PUT  /merchant-branches/:id} : Updates an existing merchantBranch.
     *
     * @param id the id of the merchantBranchDTO to save.
     * @param merchantBranchDTO the merchantBranchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated merchantBranchDTO,
     * or with status {@code 400 (Bad Request)} if the merchantBranchDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the merchantBranchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MerchantBranchDTO> updateMerchantBranch(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MerchantBranchDTO merchantBranchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MerchantBranch : {}, {}", id, merchantBranchDTO);
        if (merchantBranchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, merchantBranchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!merchantBranchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        merchantBranchDTO = merchantBranchService.update(merchantBranchDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, merchantBranchDTO.getId().toString()))
            .body(merchantBranchDTO);
    }

    /**
     * {@code PATCH  /merchant-branches/:id} : Partial updates given fields of an existing merchantBranch, field will ignore if it is null
     *
     * @param id the id of the merchantBranchDTO to save.
     * @param merchantBranchDTO the merchantBranchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated merchantBranchDTO,
     * or with status {@code 400 (Bad Request)} if the merchantBranchDTO is not valid,
     * or with status {@code 404 (Not Found)} if the merchantBranchDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the merchantBranchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MerchantBranchDTO> partialUpdateMerchantBranch(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MerchantBranchDTO merchantBranchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MerchantBranch partially : {}, {}", id, merchantBranchDTO);
        if (merchantBranchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, merchantBranchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!merchantBranchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MerchantBranchDTO> result = merchantBranchService.partialUpdate(merchantBranchDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, merchantBranchDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /merchant-branches} : get all the merchantBranches.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of merchantBranches in body.
     */
    @GetMapping("")
    public List<MerchantBranchDTO> getAllMerchantBranches() {
        LOG.debug("REST request to get all MerchantBranches");
        return merchantBranchService.findAll();
    }

    /**
     * {@code GET  /merchant-branches/:id} : get the "id" merchantBranch.
     *
     * @param id the id of the merchantBranchDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the merchantBranchDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MerchantBranchDTO> getMerchantBranch(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MerchantBranch : {}", id);
        Optional<MerchantBranchDTO> merchantBranchDTO = merchantBranchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(merchantBranchDTO);
    }

    /**
     * {@code DELETE  /merchant-branches/:id} : delete the "id" merchantBranch.
     *
     * @param id the id of the merchantBranchDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchantBranch(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MerchantBranch : {}", id);
        merchantBranchService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /merchant-branches/_search?query=:query} : search for the merchantBranch corresponding
     * to the query.
     *
     * @param query the query of the merchantBranch search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<MerchantBranchDTO> searchMerchantBranches(@RequestParam("query") String query) {
        LOG.debug("REST request to search MerchantBranches for query {}", query);
        try {
            return merchantBranchService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
