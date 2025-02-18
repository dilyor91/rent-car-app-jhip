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
import uz.carapp.rentcarapp.repository.CarTemplateRepository;
import uz.carapp.rentcarapp.service.CarTemplateService;
import uz.carapp.rentcarapp.service.dto.CarTemplateDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.CarTemplate}.
 */
@RestController
@RequestMapping("/api/car-templates")
public class CarTemplateResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarTemplateResource.class);

    private static final String ENTITY_NAME = "carTemplate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarTemplateService carTemplateService;

    private final CarTemplateRepository carTemplateRepository;

    public CarTemplateResource(CarTemplateService carTemplateService, CarTemplateRepository carTemplateRepository) {
        this.carTemplateService = carTemplateService;
        this.carTemplateRepository = carTemplateRepository;
    }

    /**
     * {@code POST  /car-templates} : Create a new carTemplate.
     *
     * @param carTemplateDTO the carTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carTemplateDTO, or with status {@code 400 (Bad Request)} if the carTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarTemplateDTO> createCarTemplate(@RequestBody CarTemplateDTO carTemplateDTO) throws URISyntaxException {
        LOG.debug("REST request to save CarTemplate : {}", carTemplateDTO);
        if (carTemplateDTO.getId() != null) {
            throw new BadRequestAlertException("A new carTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carTemplateDTO = carTemplateService.save(carTemplateDTO);
        return ResponseEntity.created(new URI("/api/car-templates/" + carTemplateDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carTemplateDTO.getId().toString()))
            .body(carTemplateDTO);
    }

    /**
     * {@code PUT  /car-templates/:id} : Updates an existing carTemplate.
     *
     * @param id the id of the carTemplateDTO to save.
     * @param carTemplateDTO the carTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the carTemplateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarTemplateDTO> updateCarTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarTemplateDTO carTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarTemplate : {}, {}", id, carTemplateDTO);
        if (carTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carTemplateDTO = carTemplateService.update(carTemplateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carTemplateDTO.getId().toString()))
            .body(carTemplateDTO);
    }

    /**
     * {@code PATCH  /car-templates/:id} : Partial updates given fields of an existing carTemplate, field will ignore if it is null
     *
     * @param id the id of the carTemplateDTO to save.
     * @param carTemplateDTO the carTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the carTemplateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carTemplateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarTemplateDTO> partialUpdateCarTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarTemplateDTO carTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarTemplate partially : {}, {}", id, carTemplateDTO);
        if (carTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarTemplateDTO> result = carTemplateService.partialUpdate(carTemplateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carTemplateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /car-templates} : get all the carTemplates.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carTemplates in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CarTemplateDTO>> getAllCarTemplates(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CarTemplates");
        Page<CarTemplateDTO> page = carTemplateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /car-templates/:id} : get the "id" carTemplate.
     *
     * @param id the id of the carTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarTemplateDTO> getCarTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarTemplate : {}", id);
        Optional<CarTemplateDTO> carTemplateDTO = carTemplateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carTemplateDTO);
    }

    /**
     * {@code DELETE  /car-templates/:id} : delete the "id" carTemplate.
     *
     * @param id the id of the carTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarTemplate : {}", id);
        carTemplateService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /car-templates/_search?query=:query} : search for the carTemplate corresponding
     * to the query.
     *
     * @param query the query of the carTemplate search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CarTemplateDTO>> searchCarTemplates(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of CarTemplates for query {}", query);
        try {
            Page<CarTemplateDTO> page = carTemplateService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
