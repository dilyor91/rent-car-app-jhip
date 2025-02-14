package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.CarParam;
import uz.carapp.rentcarapp.repository.CarParamRepository;
import uz.carapp.rentcarapp.repository.search.CarParamSearchRepository;
import uz.carapp.rentcarapp.service.CarParamService;
import uz.carapp.rentcarapp.service.dto.CarParamDTO;
import uz.carapp.rentcarapp.service.mapper.CarParamMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.CarParam}.
 */
@Service
@Transactional
public class CarParamServiceImpl implements CarParamService {

    private static final Logger LOG = LoggerFactory.getLogger(CarParamServiceImpl.class);

    private final CarParamRepository carParamRepository;

    private final CarParamMapper carParamMapper;

    private final CarParamSearchRepository carParamSearchRepository;

    public CarParamServiceImpl(
        CarParamRepository carParamRepository,
        CarParamMapper carParamMapper,
        CarParamSearchRepository carParamSearchRepository
    ) {
        this.carParamRepository = carParamRepository;
        this.carParamMapper = carParamMapper;
        this.carParamSearchRepository = carParamSearchRepository;
    }

    @Override
    public CarParamDTO save(CarParamDTO carParamDTO) {
        LOG.debug("Request to save CarParam : {}", carParamDTO);
        CarParam carParam = carParamMapper.toEntity(carParamDTO);
        carParam = carParamRepository.save(carParam);
        carParamSearchRepository.index(carParam);
        return carParamMapper.toDto(carParam);
    }

    @Override
    public CarParamDTO update(CarParamDTO carParamDTO) {
        LOG.debug("Request to update CarParam : {}", carParamDTO);
        CarParam carParam = carParamMapper.toEntity(carParamDTO);
        carParam = carParamRepository.save(carParam);
        carParamSearchRepository.index(carParam);
        return carParamMapper.toDto(carParam);
    }

    @Override
    public Optional<CarParamDTO> partialUpdate(CarParamDTO carParamDTO) {
        LOG.debug("Request to partially update CarParam : {}", carParamDTO);

        return carParamRepository
            .findById(carParamDTO.getId())
            .map(existingCarParam -> {
                carParamMapper.partialUpdate(existingCarParam, carParamDTO);

                return existingCarParam;
            })
            .map(carParamRepository::save)
            .map(savedCarParam -> {
                carParamSearchRepository.index(savedCarParam);
                return savedCarParam;
            })
            .map(carParamMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarParamDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CarParams");
        return carParamRepository.findAll(pageable).map(carParamMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarParamDTO> findOne(Long id) {
        LOG.debug("Request to get CarParam : {}", id);
        return carParamRepository.findById(id).map(carParamMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarParam : {}", id);
        carParamRepository.deleteById(id);
        carParamSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarParamDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of CarParams for query {}", query);
        return carParamSearchRepository.search(query, pageable).map(carParamMapper::toDto);
    }
}
