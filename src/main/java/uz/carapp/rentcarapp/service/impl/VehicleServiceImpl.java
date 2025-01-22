package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.Vehicle;
import uz.carapp.rentcarapp.repository.VehicleRepository;
import uz.carapp.rentcarapp.repository.search.VehicleSearchRepository;
import uz.carapp.rentcarapp.service.VehicleService;
import uz.carapp.rentcarapp.service.dto.VehicleDTO;
import uz.carapp.rentcarapp.service.mapper.VehicleMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Vehicle}.
 */
@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;

    private final VehicleMapper vehicleMapper;

    private final VehicleSearchRepository vehicleSearchRepository;

    public VehicleServiceImpl(
        VehicleRepository vehicleRepository,
        VehicleMapper vehicleMapper,
        VehicleSearchRepository vehicleSearchRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
        this.vehicleSearchRepository = vehicleSearchRepository;
    }

    @Override
    public VehicleDTO save(VehicleDTO vehicleDTO) {
        LOG.debug("Request to save Vehicle : {}", vehicleDTO);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        vehicle = vehicleRepository.save(vehicle);
        vehicleSearchRepository.index(vehicle);
        return vehicleMapper.toDto(vehicle);
    }

    @Override
    public VehicleDTO update(VehicleDTO vehicleDTO) {
        LOG.debug("Request to update Vehicle : {}", vehicleDTO);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        vehicle = vehicleRepository.save(vehicle);
        vehicleSearchRepository.index(vehicle);
        return vehicleMapper.toDto(vehicle);
    }

    @Override
    public Optional<VehicleDTO> partialUpdate(VehicleDTO vehicleDTO) {
        LOG.debug("Request to partially update Vehicle : {}", vehicleDTO);

        return vehicleRepository
            .findById(vehicleDTO.getId())
            .map(existingVehicle -> {
                vehicleMapper.partialUpdate(existingVehicle, vehicleDTO);

                return existingVehicle;
            })
            .map(vehicleRepository::save)
            .map(savedVehicle -> {
                vehicleSearchRepository.index(savedVehicle);
                return savedVehicle;
            })
            .map(vehicleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Vehicles");
        return vehicleRepository.findAll(pageable).map(vehicleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleDTO> findOne(Long id) {
        LOG.debug("Request to get Vehicle : {}", id);
        return vehicleRepository.findById(id).map(vehicleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Vehicle : {}", id);
        vehicleRepository.deleteById(id);
        vehicleSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Vehicles for query {}", query);
        return vehicleSearchRepository.search(query, pageable).map(vehicleMapper::toDto);
    }
}
