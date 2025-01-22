package uz.carapp.rentcarapp.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import uz.carapp.rentcarapp.repository.VehicleRepository;
import uz.carapp.rentcarapp.service.VehicleService;
import uz.carapp.rentcarapp.service.dto.VehicleDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.Vehicle}.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleResource {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleResource.class);

    private static final String ENTITY_NAME = "vehicle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VehicleService vehicleService;

    private final VehicleRepository vehicleRepository;

    public VehicleResource(VehicleService vehicleService, VehicleRepository vehicleRepository) {
        this.vehicleService = vehicleService;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * {@code POST  /vehicles} : Create a new vehicle.
     *
     * @param vehicleDTO the vehicleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehicleDTO, or with status {@code 400 (Bad Request)} if the vehicle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) throws URISyntaxException {
        LOG.debug("REST request to save Vehicle : {}", vehicleDTO);
        if (vehicleDTO.getId() != null) {
            throw new BadRequestAlertException("A new vehicle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        vehicleDTO = vehicleService.save(vehicleDTO);
        return ResponseEntity.created(new URI("/api/vehicles/" + vehicleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, vehicleDTO.getId().toString()))
            .body(vehicleDTO);
    }

    /**
     * {@code PUT  /vehicles/:id} : Updates an existing vehicle.
     *
     * @param id the id of the vehicleDTO to save.
     * @param vehicleDTO the vehicleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicleDTO,
     * or with status {@code 400 (Bad Request)} if the vehicleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vehicleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VehicleDTO vehicleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Vehicle : {}, {}", id, vehicleDTO);
        if (vehicleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vehicleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vehicleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        vehicleDTO = vehicleService.update(vehicleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehicleDTO.getId().toString()))
            .body(vehicleDTO);
    }

    /**
     * {@code PATCH  /vehicles/:id} : Partial updates given fields of an existing vehicle, field will ignore if it is null
     *
     * @param id the id of the vehicleDTO to save.
     * @param vehicleDTO the vehicleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicleDTO,
     * or with status {@code 400 (Bad Request)} if the vehicleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the vehicleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the vehicleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VehicleDTO> partialUpdateVehicle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VehicleDTO vehicleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Vehicle partially : {}, {}", id, vehicleDTO);
        if (vehicleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vehicleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vehicleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VehicleDTO> result = vehicleService.partialUpdate(vehicleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehicleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /vehicles} : get all the vehicles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehicles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VehicleDTO>> getAllVehicles(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Vehicles");
        Page<VehicleDTO> page = vehicleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vehicles/:id} : get the "id" vehicle.
     *
     * @param id the id of the vehicleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehicleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Vehicle : {}", id);
        Optional<VehicleDTO> vehicleDTO = vehicleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vehicleDTO);
    }

    /**
     * {@code DELETE  /vehicles/:id} : delete the "id" vehicle.
     *
     * @param id the id of the vehicleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Vehicle : {}", id);
        vehicleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /vehicles/_search?query=:query} : search for the vehicle corresponding
     * to the query.
     *
     * @param query the query of the vehicle search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<VehicleDTO>> searchVehicles(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Vehicles for query {}", query);
        try {
            Page<VehicleDTO> page = vehicleService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
