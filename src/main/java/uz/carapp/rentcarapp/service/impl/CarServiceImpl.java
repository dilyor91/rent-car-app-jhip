package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.Car;
import uz.carapp.rentcarapp.repository.CarRepository;
import uz.carapp.rentcarapp.repository.search.CarSearchRepository;
import uz.carapp.rentcarapp.service.CarService;
import uz.carapp.rentcarapp.service.dto.CarDTO;
import uz.carapp.rentcarapp.service.mapper.CarMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Car}.
 */
@Service
@Transactional
public class CarServiceImpl implements CarService {

    private static final Logger LOG = LoggerFactory.getLogger(CarServiceImpl.class);

    private final CarRepository carRepository;

    private final CarMapper carMapper;

    private final CarSearchRepository carSearchRepository;

    public CarServiceImpl(CarRepository carRepository, CarMapper carMapper, CarSearchRepository carSearchRepository) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
        this.carSearchRepository = carSearchRepository;
    }

    @Override
    public CarDTO save(CarDTO carDTO) {
        LOG.debug("Request to save Car : {}", carDTO);
        Car car = carMapper.toEntity(carDTO);
        car = carRepository.save(car);
        carSearchRepository.index(car);
        return carMapper.toDto(car);
    }

    @Override
    public CarDTO update(CarDTO carDTO) {
        LOG.debug("Request to update Car : {}", carDTO);
        Car car = carMapper.toEntity(carDTO);
        car = carRepository.save(car);
        carSearchRepository.index(car);
        return carMapper.toDto(car);
    }

    @Override
    public Optional<CarDTO> partialUpdate(CarDTO carDTO) {
        LOG.debug("Request to partially update Car : {}", carDTO);

        return carRepository
            .findById(carDTO.getId())
            .map(existingCar -> {
                carMapper.partialUpdate(existingCar, carDTO);

                return existingCar;
            })
            .map(carRepository::save)
            .map(savedCar -> {
                carSearchRepository.index(savedCar);
                return savedCar;
            })
            .map(carMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Cars");
        return carRepository.findAll(pageable).map(carMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarDTO> findOne(Long id) {
        LOG.debug("Request to get Car : {}", id);
        return carRepository.findById(id).map(carMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Car : {}", id);
        carRepository.deleteById(id);
        carSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Cars for query {}", query);
        return carSearchRepository.search(query, pageable).map(carMapper::toDto);
    }
}
