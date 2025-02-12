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
import uz.carapp.rentcarapp.repository.ModelRepository;
import uz.carapp.rentcarapp.service.ModelService;
import uz.carapp.rentcarapp.service.dto.ModelDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.Model}.
 */
@RestController
@RequestMapping("/api/models")
public class ModelResource {

    private static final Logger LOG = LoggerFactory.getLogger(ModelResource.class);

    private static final String ENTITY_NAME = "model";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ModelService modelService;

    private final ModelRepository modelRepository;

    public ModelResource(ModelService modelService, ModelRepository modelRepository) {
        this.modelService = modelService;
        this.modelRepository = modelRepository;
    }

    /**
     * {@code POST  /models} : Create a new model.
     *
     * @param modelDTO the modelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new modelDTO, or with status {@code 400 (Bad Request)} if the model has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ModelDTO> createModel(@RequestBody ModelDTO modelDTO) throws URISyntaxException {
        LOG.debug("REST request to save Model : {}", modelDTO);
        if (modelDTO.getId() != null) {
            throw new BadRequestAlertException("A new model cannot already have an ID", ENTITY_NAME, "idexists");
        }
        modelDTO = modelService.save(modelDTO);
        return ResponseEntity.created(new URI("/api/models/" + modelDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, modelDTO.getId().toString()))
            .body(modelDTO);
    }

    /**
     * {@code PUT  /models/:id} : Updates an existing model.
     *
     * @param id the id of the modelDTO to save.
     * @param modelDTO the modelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modelDTO,
     * or with status {@code 400 (Bad Request)} if the modelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the modelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ModelDTO> updateModel(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ModelDTO modelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Model : {}, {}", id, modelDTO);
        if (modelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!modelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        modelDTO = modelService.update(modelDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, modelDTO.getId().toString()))
            .body(modelDTO);
    }

    /**
     * {@code PATCH  /models/:id} : Partial updates given fields of an existing model, field will ignore if it is null
     *
     * @param id the id of the modelDTO to save.
     * @param modelDTO the modelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modelDTO,
     * or with status {@code 400 (Bad Request)} if the modelDTO is not valid,
     * or with status {@code 404 (Not Found)} if the modelDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the modelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ModelDTO> partialUpdateModel(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ModelDTO modelDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Model partially : {}, {}", id, modelDTO);
        if (modelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!modelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ModelDTO> result = modelService.partialUpdate(modelDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, modelDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /models} : get all the models.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of models in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ModelDTO>> getAllModels(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Models");
        Page<ModelDTO> page = modelService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /models/:id} : get the "id" model.
     *
     * @param id the id of the modelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the modelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModelDTO> getModel(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Model : {}", id);
        Optional<ModelDTO> modelDTO = modelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(modelDTO);
    }

    /**
     * {@code DELETE  /models/:id} : delete the "id" model.
     *
     * @param id the id of the modelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Model : {}", id);
        modelService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /models/_search?query=:query} : search for the model corresponding
     * to the query.
     *
     * @param query the query of the model search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ModelDTO>> searchModels(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Models for query {}", query);
        try {
            Page<ModelDTO> page = modelService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
