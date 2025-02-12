package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.ParamValue;
import uz.carapp.rentcarapp.repository.ParamValueRepository;
import uz.carapp.rentcarapp.repository.search.ParamValueSearchRepository;
import uz.carapp.rentcarapp.service.ParamValueService;
import uz.carapp.rentcarapp.service.dto.ParamValueDTO;
import uz.carapp.rentcarapp.service.mapper.ParamValueMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.ParamValue}.
 */
@Service
@Transactional
public class ParamValueServiceImpl implements ParamValueService {

    private static final Logger LOG = LoggerFactory.getLogger(ParamValueServiceImpl.class);

    private final ParamValueRepository paramValueRepository;

    private final ParamValueMapper paramValueMapper;

    private final ParamValueSearchRepository paramValueSearchRepository;

    public ParamValueServiceImpl(
        ParamValueRepository paramValueRepository,
        ParamValueMapper paramValueMapper,
        ParamValueSearchRepository paramValueSearchRepository
    ) {
        this.paramValueRepository = paramValueRepository;
        this.paramValueMapper = paramValueMapper;
        this.paramValueSearchRepository = paramValueSearchRepository;
    }

    @Override
    public ParamValueDTO save(ParamValueDTO paramValueDTO) {
        LOG.debug("Request to save ParamValue : {}", paramValueDTO);
        ParamValue paramValue = paramValueMapper.toEntity(paramValueDTO);
        paramValue = paramValueRepository.save(paramValue);
        paramValueSearchRepository.index(paramValue);
        return paramValueMapper.toDto(paramValue);
    }

    @Override
    public ParamValueDTO update(ParamValueDTO paramValueDTO) {
        LOG.debug("Request to update ParamValue : {}", paramValueDTO);
        ParamValue paramValue = paramValueMapper.toEntity(paramValueDTO);
        paramValue = paramValueRepository.save(paramValue);
        paramValueSearchRepository.index(paramValue);
        return paramValueMapper.toDto(paramValue);
    }

    @Override
    public Optional<ParamValueDTO> partialUpdate(ParamValueDTO paramValueDTO) {
        LOG.debug("Request to partially update ParamValue : {}", paramValueDTO);

        return paramValueRepository
            .findById(paramValueDTO.getId())
            .map(existingParamValue -> {
                paramValueMapper.partialUpdate(existingParamValue, paramValueDTO);

                return existingParamValue;
            })
            .map(paramValueRepository::save)
            .map(savedParamValue -> {
                paramValueSearchRepository.index(savedParamValue);
                return savedParamValue;
            })
            .map(paramValueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParamValueDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ParamValues");
        return paramValueRepository.findAll(pageable).map(paramValueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParamValueDTO> findOne(Long id) {
        LOG.debug("Request to get ParamValue : {}", id);
        return paramValueRepository.findById(id).map(paramValueMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ParamValue : {}", id);
        paramValueRepository.deleteById(id);
        paramValueSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParamValueDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ParamValues for query {}", query);
        return paramValueSearchRepository.search(query, pageable).map(paramValueMapper::toDto);
    }
}
