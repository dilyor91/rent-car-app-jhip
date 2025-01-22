package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.CarBody;
import uz.carapp.rentcarapp.repository.CarBodyRepository;
import uz.carapp.rentcarapp.repository.search.CarBodySearchRepository;
import uz.carapp.rentcarapp.service.CarBodyService;
import uz.carapp.rentcarapp.service.dto.CarBodyDTO;
import uz.carapp.rentcarapp.service.mapper.CarBodyMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.CarBody}.
 */
@Service
@Transactional
public class CarBodyServiceImpl implements CarBodyService {

    private static final Logger LOG = LoggerFactory.getLogger(CarBodyServiceImpl.class);

    private final CarBodyRepository carBodyRepository;

    private final CarBodyMapper carBodyMapper;

    private final CarBodySearchRepository carBodySearchRepository;

    public CarBodyServiceImpl(
        CarBodyRepository carBodyRepository,
        CarBodyMapper carBodyMapper,
        CarBodySearchRepository carBodySearchRepository
    ) {
        this.carBodyRepository = carBodyRepository;
        this.carBodyMapper = carBodyMapper;
        this.carBodySearchRepository = carBodySearchRepository;
    }

    @Override
    public CarBodyDTO save(CarBodyDTO carBodyDTO) {
        LOG.debug("Request to save CarBody : {}", carBodyDTO);
        CarBody carBody = carBodyMapper.toEntity(carBodyDTO);
        carBody = carBodyRepository.save(carBody);
        carBodySearchRepository.index(carBody);
        return carBodyMapper.toDto(carBody);
    }

    @Override
    public CarBodyDTO update(CarBodyDTO carBodyDTO) {
        LOG.debug("Request to update CarBody : {}", carBodyDTO);
        CarBody carBody = carBodyMapper.toEntity(carBodyDTO);
        carBody = carBodyRepository.save(carBody);
        carBodySearchRepository.index(carBody);
        return carBodyMapper.toDto(carBody);
    }

    @Override
    public Optional<CarBodyDTO> partialUpdate(CarBodyDTO carBodyDTO) {
        LOG.debug("Request to partially update CarBody : {}", carBodyDTO);

        return carBodyRepository
            .findById(carBodyDTO.getId())
            .map(existingCarBody -> {
                carBodyMapper.partialUpdate(existingCarBody, carBodyDTO);

                return existingCarBody;
            })
            .map(carBodyRepository::save)
            .map(savedCarBody -> {
                carBodySearchRepository.index(savedCarBody);
                return savedCarBody;
            })
            .map(carBodyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarBodyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CarBodies");
        return carBodyRepository.findAll(pageable).map(carBodyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarBodyDTO> findOne(Long id) {
        LOG.debug("Request to get CarBody : {}", id);
        return carBodyRepository.findById(id).map(carBodyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarBody : {}", id);
        carBodyRepository.deleteById(id);
        carBodySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarBodyDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of CarBodies for query {}", query);
        return carBodySearchRepository.search(query, pageable).map(carBodyMapper::toDto);
    }
}
