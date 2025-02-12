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
import uz.carapp.rentcarapp.repository.ParamValueRepository;
import uz.carapp.rentcarapp.service.ParamValueService;
import uz.carapp.rentcarapp.service.dto.ParamValueDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.ParamValue}.
 */
@RestController
@RequestMapping("/api/param-values")
public class ParamValueResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParamValueResource.class);

    private static final String ENTITY_NAME = "paramValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParamValueService paramValueService;

    private final ParamValueRepository paramValueRepository;

    public ParamValueResource(ParamValueService paramValueService, ParamValueRepository paramValueRepository) {
        this.paramValueService = paramValueService;
        this.paramValueRepository = paramValueRepository;
    }

    /**
     * {@code POST  /param-values} : Create a new paramValue.
     *
     * @param paramValueDTO the paramValueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paramValueDTO, or with status {@code 400 (Bad Request)} if the paramValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParamValueDTO> createParamValue(@RequestBody ParamValueDTO paramValueDTO) throws URISyntaxException {
        LOG.debug("REST request to save ParamValue : {}", paramValueDTO);
        if (paramValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new paramValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paramValueDTO = paramValueService.save(paramValueDTO);
        return ResponseEntity.created(new URI("/api/param-values/" + paramValueDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paramValueDTO.getId().toString()))
            .body(paramValueDTO);
    }

    /**
     * {@code PUT  /param-values/:id} : Updates an existing paramValue.
     *
     * @param id the id of the paramValueDTO to save.
     * @param paramValueDTO the paramValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paramValueDTO,
     * or with status {@code 400 (Bad Request)} if the paramValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paramValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParamValueDTO> updateParamValue(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParamValueDTO paramValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ParamValue : {}, {}", id, paramValueDTO);
        if (paramValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paramValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paramValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paramValueDTO = paramValueService.update(paramValueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paramValueDTO.getId().toString()))
            .body(paramValueDTO);
    }

    /**
     * {@code PATCH  /param-values/:id} : Partial updates given fields of an existing paramValue, field will ignore if it is null
     *
     * @param id the id of the paramValueDTO to save.
     * @param paramValueDTO the paramValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paramValueDTO,
     * or with status {@code 400 (Bad Request)} if the paramValueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paramValueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paramValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParamValueDTO> partialUpdateParamValue(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParamValueDTO paramValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ParamValue partially : {}, {}", id, paramValueDTO);
        if (paramValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paramValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paramValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParamValueDTO> result = paramValueService.partialUpdate(paramValueDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paramValueDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /param-values} : get all the paramValues.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paramValues in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ParamValueDTO>> getAllParamValues(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ParamValues");
        Page<ParamValueDTO> page = paramValueService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /param-values/:id} : get the "id" paramValue.
     *
     * @param id the id of the paramValueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paramValueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParamValueDTO> getParamValue(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ParamValue : {}", id);
        Optional<ParamValueDTO> paramValueDTO = paramValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paramValueDTO);
    }

    /**
     * {@code DELETE  /param-values/:id} : delete the "id" paramValue.
     *
     * @param id the id of the paramValueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParamValue(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ParamValue : {}", id);
        paramValueService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /param-values/_search?query=:query} : search for the paramValue corresponding
     * to the query.
     *
     * @param query the query of the paramValue search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ParamValueDTO>> searchParamValues(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ParamValues for query {}", query);
        try {
            Page<ParamValueDTO> page = paramValueService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
