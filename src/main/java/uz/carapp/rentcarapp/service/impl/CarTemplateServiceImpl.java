package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.CarTemplate;
import uz.carapp.rentcarapp.repository.CarTemplateRepository;
import uz.carapp.rentcarapp.repository.search.CarTemplateSearchRepository;
import uz.carapp.rentcarapp.service.CarTemplateService;
import uz.carapp.rentcarapp.service.dto.CarTemplateDTO;
import uz.carapp.rentcarapp.service.mapper.CarTemplateMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.CarTemplate}.
 */
@Service
@Transactional
public class CarTemplateServiceImpl implements CarTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(CarTemplateServiceImpl.class);

    private final CarTemplateRepository carTemplateRepository;

    private final CarTemplateMapper carTemplateMapper;

    private final CarTemplateSearchRepository carTemplateSearchRepository;

    public CarTemplateServiceImpl(
        CarTemplateRepository carTemplateRepository,
        CarTemplateMapper carTemplateMapper,
        CarTemplateSearchRepository carTemplateSearchRepository
    ) {
        this.carTemplateRepository = carTemplateRepository;
        this.carTemplateMapper = carTemplateMapper;
        this.carTemplateSearchRepository = carTemplateSearchRepository;
    }

    @Override
    public CarTemplateDTO save(CarTemplateDTO carTemplateDTO) {
        LOG.debug("Request to save CarTemplate : {}", carTemplateDTO);
        CarTemplate carTemplate = carTemplateMapper.toEntity(carTemplateDTO);
        carTemplate = carTemplateRepository.save(carTemplate);
        carTemplateSearchRepository.index(carTemplate);
        return carTemplateMapper.toDto(carTemplate);
    }

    @Override
    public CarTemplateDTO update(CarTemplateDTO carTemplateDTO) {
        LOG.debug("Request to update CarTemplate : {}", carTemplateDTO);
        CarTemplate carTemplate = carTemplateMapper.toEntity(carTemplateDTO);
        carTemplate = carTemplateRepository.save(carTemplate);
        carTemplateSearchRepository.index(carTemplate);
        return carTemplateMapper.toDto(carTemplate);
    }

    @Override
    public Optional<CarTemplateDTO> partialUpdate(CarTemplateDTO carTemplateDTO) {
        LOG.debug("Request to partially update CarTemplate : {}", carTemplateDTO);

        return carTemplateRepository
            .findById(carTemplateDTO.getId())
            .map(existingCarTemplate -> {
                carTemplateMapper.partialUpdate(existingCarTemplate, carTemplateDTO);

                return existingCarTemplate;
            })
            .map(carTemplateRepository::save)
            .map(savedCarTemplate -> {
                carTemplateSearchRepository.index(savedCarTemplate);
                return savedCarTemplate;
            })
            .map(carTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarTemplateDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CarTemplates");
        return carTemplateRepository.findAll(pageable).map(carTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarTemplateDTO> findOne(Long id) {
        LOG.debug("Request to get CarTemplate : {}", id);
        return carTemplateRepository.findById(id).map(carTemplateMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarTemplate : {}", id);
        carTemplateRepository.deleteById(id);
        carTemplateSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarTemplateDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of CarTemplates for query {}", query);
        return carTemplateSearchRepository.search(query, pageable).map(carTemplateMapper::toDto);
    }
}
