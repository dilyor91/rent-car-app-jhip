package uz.carapp.rentcarapp.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.CarMileage;
import uz.carapp.rentcarapp.repository.CarMileageRepository;
import uz.carapp.rentcarapp.repository.search.CarMileageSearchRepository;
import uz.carapp.rentcarapp.service.CarMileageService;
import uz.carapp.rentcarapp.service.dto.CarMileageDTO;
import uz.carapp.rentcarapp.service.mapper.CarMileageMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.CarMileage}.
 */
@Service
@Transactional
public class CarMileageServiceImpl implements CarMileageService {

    private static final Logger LOG = LoggerFactory.getLogger(CarMileageServiceImpl.class);

    private final CarMileageRepository carMileageRepository;

    private final CarMileageMapper carMileageMapper;

    private final CarMileageSearchRepository carMileageSearchRepository;

    public CarMileageServiceImpl(
        CarMileageRepository carMileageRepository,
        CarMileageMapper carMileageMapper,
        CarMileageSearchRepository carMileageSearchRepository
    ) {
        this.carMileageRepository = carMileageRepository;
        this.carMileageMapper = carMileageMapper;
        this.carMileageSearchRepository = carMileageSearchRepository;
    }

    @Override
    public CarMileageDTO save(CarMileageDTO carMileageDTO) {
        LOG.debug("Request to save CarMileage : {}", carMileageDTO);
        CarMileage carMileage = carMileageMapper.toEntity(carMileageDTO);
        carMileage = carMileageRepository.save(carMileage);
        carMileageSearchRepository.index(carMileage);
        return carMileageMapper.toDto(carMileage);
    }

    @Override
    public CarMileageDTO update(CarMileageDTO carMileageDTO) {
        LOG.debug("Request to update CarMileage : {}", carMileageDTO);
        CarMileage carMileage = carMileageMapper.toEntity(carMileageDTO);
        carMileage = carMileageRepository.save(carMileage);
        carMileageSearchRepository.index(carMileage);
        return carMileageMapper.toDto(carMileage);
    }

    @Override
    public Optional<CarMileageDTO> partialUpdate(CarMileageDTO carMileageDTO) {
        LOG.debug("Request to partially update CarMileage : {}", carMileageDTO);

        return carMileageRepository
            .findById(carMileageDTO.getId())
            .map(existingCarMileage -> {
                carMileageMapper.partialUpdate(existingCarMileage, carMileageDTO);

                return existingCarMileage;
            })
            .map(carMileageRepository::save)
            .map(savedCarMileage -> {
                carMileageSearchRepository.index(savedCarMileage);
                return savedCarMileage;
            })
            .map(carMileageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarMileageDTO> findAll() {
        LOG.debug("Request to get all CarMileages");
        return carMileageRepository.findAll().stream().map(carMileageMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarMileageDTO> findOne(Long id) {
        LOG.debug("Request to get CarMileage : {}", id);
        return carMileageRepository.findById(id).map(carMileageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarMileage : {}", id);
        carMileageRepository.deleteById(id);
        carMileageSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarMileageDTO> search(String query) {
        LOG.debug("Request to search CarMileages for query {}", query);
        try {
            return StreamSupport.stream(carMileageSearchRepository.search(query).spliterator(), false)
                .map(carMileageMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
