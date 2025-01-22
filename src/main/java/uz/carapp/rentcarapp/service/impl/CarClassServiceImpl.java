package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.CarClass;
import uz.carapp.rentcarapp.repository.CarClassRepository;
import uz.carapp.rentcarapp.repository.search.CarClassSearchRepository;
import uz.carapp.rentcarapp.service.CarClassService;
import uz.carapp.rentcarapp.service.dto.CarClassDTO;
import uz.carapp.rentcarapp.service.mapper.CarClassMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.CarClass}.
 */
@Service
@Transactional
public class CarClassServiceImpl implements CarClassService {

    private static final Logger LOG = LoggerFactory.getLogger(CarClassServiceImpl.class);

    private final CarClassRepository carClassRepository;

    private final CarClassMapper carClassMapper;

    private final CarClassSearchRepository carClassSearchRepository;

    public CarClassServiceImpl(
        CarClassRepository carClassRepository,
        CarClassMapper carClassMapper,
        CarClassSearchRepository carClassSearchRepository
    ) {
        this.carClassRepository = carClassRepository;
        this.carClassMapper = carClassMapper;
        this.carClassSearchRepository = carClassSearchRepository;
    }

    @Override
    public CarClassDTO save(CarClassDTO carClassDTO) {
        LOG.debug("Request to save CarClass : {}", carClassDTO);
        CarClass carClass = carClassMapper.toEntity(carClassDTO);
        carClass = carClassRepository.save(carClass);
        carClassSearchRepository.index(carClass);
        return carClassMapper.toDto(carClass);
    }

    @Override
    public CarClassDTO update(CarClassDTO carClassDTO) {
        LOG.debug("Request to update CarClass : {}", carClassDTO);
        CarClass carClass = carClassMapper.toEntity(carClassDTO);
        carClass = carClassRepository.save(carClass);
        carClassSearchRepository.index(carClass);
        return carClassMapper.toDto(carClass);
    }

    @Override
    public Optional<CarClassDTO> partialUpdate(CarClassDTO carClassDTO) {
        LOG.debug("Request to partially update CarClass : {}", carClassDTO);

        return carClassRepository
            .findById(carClassDTO.getId())
            .map(existingCarClass -> {
                carClassMapper.partialUpdate(existingCarClass, carClassDTO);

                return existingCarClass;
            })
            .map(carClassRepository::save)
            .map(savedCarClass -> {
                carClassSearchRepository.index(savedCarClass);
                return savedCarClass;
            })
            .map(carClassMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarClassDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CarClasses");
        return carClassRepository.findAll(pageable).map(carClassMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarClassDTO> findOne(Long id) {
        LOG.debug("Request to get CarClass : {}", id);
        return carClassRepository.findById(id).map(carClassMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarClass : {}", id);
        carClassRepository.deleteById(id);
        carClassSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarClassDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of CarClasses for query {}", query);
        return carClassSearchRepository.search(query, pageable).map(carClassMapper::toDto);
    }
}
