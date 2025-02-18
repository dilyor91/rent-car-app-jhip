package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.CarTemplateParam;
import uz.carapp.rentcarapp.repository.CarTemplateParamRepository;
import uz.carapp.rentcarapp.repository.search.CarTemplateParamSearchRepository;
import uz.carapp.rentcarapp.service.CarTemplateParamService;
import uz.carapp.rentcarapp.service.dto.CarTemplateParamDTO;
import uz.carapp.rentcarapp.service.mapper.CarTemplateParamMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.CarTemplateParam}.
 */
@Service
@Transactional
public class CarTemplateParamServiceImpl implements CarTemplateParamService {

    private static final Logger LOG = LoggerFactory.getLogger(CarTemplateParamServiceImpl.class);

    private final CarTemplateParamRepository carTemplateParamRepository;

    private final CarTemplateParamMapper carTemplateParamMapper;

    private final CarTemplateParamSearchRepository carTemplateParamSearchRepository;

    public CarTemplateParamServiceImpl(
        CarTemplateParamRepository carTemplateParamRepository,
        CarTemplateParamMapper carTemplateParamMapper,
        CarTemplateParamSearchRepository carTemplateParamSearchRepository
    ) {
        this.carTemplateParamRepository = carTemplateParamRepository;
        this.carTemplateParamMapper = carTemplateParamMapper;
        this.carTemplateParamSearchRepository = carTemplateParamSearchRepository;
    }

    @Override
    public CarTemplateParamDTO save(CarTemplateParamDTO carTemplateParamDTO) {
        LOG.debug("Request to save CarTemplateParam : {}", carTemplateParamDTO);
        CarTemplateParam carTemplateParam = carTemplateParamMapper.toEntity(carTemplateParamDTO);
        carTemplateParam = carTemplateParamRepository.save(carTemplateParam);
        carTemplateParamSearchRepository.index(carTemplateParam);
        return carTemplateParamMapper.toDto(carTemplateParam);
    }

    @Override
    public CarTemplateParamDTO update(CarTemplateParamDTO carTemplateParamDTO) {
        LOG.debug("Request to update CarTemplateParam : {}", carTemplateParamDTO);
        CarTemplateParam carTemplateParam = carTemplateParamMapper.toEntity(carTemplateParamDTO);
        carTemplateParam = carTemplateParamRepository.save(carTemplateParam);
        carTemplateParamSearchRepository.index(carTemplateParam);
        return carTemplateParamMapper.toDto(carTemplateParam);
    }

    @Override
    public Optional<CarTemplateParamDTO> partialUpdate(CarTemplateParamDTO carTemplateParamDTO) {
        LOG.debug("Request to partially update CarTemplateParam : {}", carTemplateParamDTO);

        return carTemplateParamRepository
            .findById(carTemplateParamDTO.getId())
            .map(existingCarTemplateParam -> {
                carTemplateParamMapper.partialUpdate(existingCarTemplateParam, carTemplateParamDTO);

                return existingCarTemplateParam;
            })
            .map(carTemplateParamRepository::save)
            .map(savedCarTemplateParam -> {
                carTemplateParamSearchRepository.index(savedCarTemplateParam);
                return savedCarTemplateParam;
            })
            .map(carTemplateParamMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarTemplateParamDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CarTemplateParams");
        return carTemplateParamRepository.findAll(pageable).map(carTemplateParamMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarTemplateParamDTO> findOne(Long id) {
        LOG.debug("Request to get CarTemplateParam : {}", id);
        return carTemplateParamRepository.findById(id).map(carTemplateParamMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarTemplateParam : {}", id);
        carTemplateParamRepository.deleteById(id);
        carTemplateParamSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarTemplateParamDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of CarTemplateParams for query {}", query);
        return carTemplateParamSearchRepository.search(query, pageable).map(carTemplateParamMapper::toDto);
    }
}
