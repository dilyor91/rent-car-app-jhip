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
import uz.carapp.rentcarapp.repository.CarParamRepository;
import uz.carapp.rentcarapp.service.CarParamService;
import uz.carapp.rentcarapp.service.dto.CarParamDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.CarParam}.
 */
@RestController
@RequestMapping("/api/car-params")
public class CarParamResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarParamResource.class);

    private static final String ENTITY_NAME = "carParam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarParamService carParamService;

    private final CarParamRepository carParamRepository;

    public CarParamResource(CarParamService carParamService, CarParamRepository carParamRepository) {
        this.carParamService = carParamService;
        this.carParamRepository = carParamRepository;
    }

    /**
     * {@code POST  /car-params} : Create a new carParam.
     *
     * @param carParamDTO the carParamDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carParamDTO, or with status {@code 400 (Bad Request)} if the carParam has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarParamDTO> createCarParam(@RequestBody CarParamDTO carParamDTO) throws URISyntaxException {
        LOG.debug("REST request to save CarParam : {}", carParamDTO);
        if (carParamDTO.getId() != null) {
            throw new BadRequestAlertException("A new carParam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carParamDTO = carParamService.save(carParamDTO);
        return ResponseEntity.created(new URI("/api/car-params/" + carParamDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carParamDTO.getId().toString()))
            .body(carParamDTO);
    }

    /**
     * {@code PUT  /car-params/:id} : Updates an existing carParam.
     *
     * @param id the id of the carParamDTO to save.
     * @param carParamDTO the carParamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carParamDTO,
     * or with status {@code 400 (Bad Request)} if the carParamDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carParamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarParamDTO> updateCarParam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarParamDTO carParamDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarParam : {}, {}", id, carParamDTO);
        if (carParamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carParamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carParamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carParamDTO = carParamService.update(carParamDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carParamDTO.getId().toString()))
            .body(carParamDTO);
    }

    /**
     * {@code PATCH  /car-params/:id} : Partial updates given fields of an existing carParam, field will ignore if it is null
     *
     * @param id the id of the carParamDTO to save.
     * @param carParamDTO the carParamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carParamDTO,
     * or with status {@code 400 (Bad Request)} if the carParamDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carParamDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carParamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarParamDTO> partialUpdateCarParam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarParamDTO carParamDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarParam partially : {}, {}", id, carParamDTO);
        if (carParamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carParamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carParamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarParamDTO> result = carParamService.partialUpdate(carParamDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carParamDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /car-params} : get all the carParams.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carParams in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CarParamDTO>> getAllCarParams(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CarParams");
        Page<CarParamDTO> page = carParamService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /car-params/:id} : get the "id" carParam.
     *
     * @param id the id of the carParamDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carParamDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarParamDTO> getCarParam(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarParam : {}", id);
        Optional<CarParamDTO> carParamDTO = carParamService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carParamDTO);
    }

    /**
     * {@code DELETE  /car-params/:id} : delete the "id" carParam.
     *
     * @param id the id of the carParamDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarParam(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarParam : {}", id);
        carParamService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /car-params/_search?query=:query} : search for the carParam corresponding
     * to the query.
     *
     * @param query the query of the carParam search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CarParamDTO>> searchCarParams(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of CarParams for query {}", query);
        try {
            Page<CarParamDTO> page = carParamService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
