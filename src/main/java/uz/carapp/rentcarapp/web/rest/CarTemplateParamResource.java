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
import uz.carapp.rentcarapp.repository.CarTemplateParamRepository;
import uz.carapp.rentcarapp.service.CarTemplateParamService;
import uz.carapp.rentcarapp.service.dto.CarTemplateParamDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.CarTemplateParam}.
 */
@RestController
@RequestMapping("/api/car-template-params")
public class CarTemplateParamResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarTemplateParamResource.class);

    private static final String ENTITY_NAME = "carTemplateParam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarTemplateParamService carTemplateParamService;

    private final CarTemplateParamRepository carTemplateParamRepository;

    public CarTemplateParamResource(
        CarTemplateParamService carTemplateParamService,
        CarTemplateParamRepository carTemplateParamRepository
    ) {
        this.carTemplateParamService = carTemplateParamService;
        this.carTemplateParamRepository = carTemplateParamRepository;
    }

    /**
     * {@code POST  /car-template-params} : Create a new carTemplateParam.
     *
     * @param carTemplateParamDTO the carTemplateParamDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carTemplateParamDTO, or with status {@code 400 (Bad Request)} if the carTemplateParam has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarTemplateParamDTO> createCarTemplateParam(@RequestBody CarTemplateParamDTO carTemplateParamDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CarTemplateParam : {}", carTemplateParamDTO);
        if (carTemplateParamDTO.getId() != null) {
            throw new BadRequestAlertException("A new carTemplateParam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carTemplateParamDTO = carTemplateParamService.save(carTemplateParamDTO);
        return ResponseEntity.created(new URI("/api/car-template-params/" + carTemplateParamDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carTemplateParamDTO.getId().toString()))
            .body(carTemplateParamDTO);
    }

    /**
     * {@code PUT  /car-template-params/:id} : Updates an existing carTemplateParam.
     *
     * @param id the id of the carTemplateParamDTO to save.
     * @param carTemplateParamDTO the carTemplateParamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carTemplateParamDTO,
     * or with status {@code 400 (Bad Request)} if the carTemplateParamDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carTemplateParamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarTemplateParamDTO> updateCarTemplateParam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarTemplateParamDTO carTemplateParamDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarTemplateParam : {}, {}", id, carTemplateParamDTO);
        if (carTemplateParamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carTemplateParamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carTemplateParamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carTemplateParamDTO = carTemplateParamService.update(carTemplateParamDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carTemplateParamDTO.getId().toString()))
            .body(carTemplateParamDTO);
    }

    /**
     * {@code PATCH  /car-template-params/:id} : Partial updates given fields of an existing carTemplateParam, field will ignore if it is null
     *
     * @param id the id of the carTemplateParamDTO to save.
     * @param carTemplateParamDTO the carTemplateParamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carTemplateParamDTO,
     * or with status {@code 400 (Bad Request)} if the carTemplateParamDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carTemplateParamDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carTemplateParamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarTemplateParamDTO> partialUpdateCarTemplateParam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarTemplateParamDTO carTemplateParamDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarTemplateParam partially : {}, {}", id, carTemplateParamDTO);
        if (carTemplateParamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carTemplateParamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carTemplateParamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarTemplateParamDTO> result = carTemplateParamService.partialUpdate(carTemplateParamDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carTemplateParamDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /car-template-params} : get all the carTemplateParams.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carTemplateParams in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CarTemplateParamDTO>> getAllCarTemplateParams(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of CarTemplateParams");
        Page<CarTemplateParamDTO> page = carTemplateParamService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /car-template-params/:id} : get the "id" carTemplateParam.
     *
     * @param id the id of the carTemplateParamDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carTemplateParamDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarTemplateParamDTO> getCarTemplateParam(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarTemplateParam : {}", id);
        Optional<CarTemplateParamDTO> carTemplateParamDTO = carTemplateParamService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carTemplateParamDTO);
    }

    /**
     * {@code DELETE  /car-template-params/:id} : delete the "id" carTemplateParam.
     *
     * @param id the id of the carTemplateParamDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarTemplateParam(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarTemplateParam : {}", id);
        carTemplateParamService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /car-template-params/_search?query=:query} : search for the carTemplateParam corresponding
     * to the query.
     *
     * @param query the query of the carTemplateParam search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CarTemplateParamDTO>> searchCarTemplateParams(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of CarTemplateParams for query {}", query);
        try {
            Page<CarTemplateParamDTO> page = carTemplateParamService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
