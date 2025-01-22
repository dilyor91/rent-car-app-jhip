package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.Parametr;
import uz.carapp.rentcarapp.repository.ParametrRepository;
import uz.carapp.rentcarapp.repository.search.ParametrSearchRepository;
import uz.carapp.rentcarapp.service.ParametrService;
import uz.carapp.rentcarapp.service.dto.ParametrDTO;
import uz.carapp.rentcarapp.service.mapper.ParametrMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Parametr}.
 */
@Service
@Transactional
public class ParametrServiceImpl implements ParametrService {

    private static final Logger LOG = LoggerFactory.getLogger(ParametrServiceImpl.class);

    private final ParametrRepository parametrRepository;

    private final ParametrMapper parametrMapper;

    private final ParametrSearchRepository parametrSearchRepository;

    public ParametrServiceImpl(
        ParametrRepository parametrRepository,
        ParametrMapper parametrMapper,
        ParametrSearchRepository parametrSearchRepository
    ) {
        this.parametrRepository = parametrRepository;
        this.parametrMapper = parametrMapper;
        this.parametrSearchRepository = parametrSearchRepository;
    }

    @Override
    public ParametrDTO save(ParametrDTO parametrDTO) {
        LOG.debug("Request to save Parametr : {}", parametrDTO);
        Parametr parametr = parametrMapper.toEntity(parametrDTO);
        parametr = parametrRepository.save(parametr);
        parametrSearchRepository.index(parametr);
        return parametrMapper.toDto(parametr);
    }

    @Override
    public ParametrDTO update(ParametrDTO parametrDTO) {
        LOG.debug("Request to update Parametr : {}", parametrDTO);
        Parametr parametr = parametrMapper.toEntity(parametrDTO);
        parametr = parametrRepository.save(parametr);
        parametrSearchRepository.index(parametr);
        return parametrMapper.toDto(parametr);
    }

    @Override
    public Optional<ParametrDTO> partialUpdate(ParametrDTO parametrDTO) {
        LOG.debug("Request to partially update Parametr : {}", parametrDTO);

        return parametrRepository
            .findById(parametrDTO.getId())
            .map(existingParametr -> {
                parametrMapper.partialUpdate(existingParametr, parametrDTO);

                return existingParametr;
            })
            .map(parametrRepository::save)
            .map(savedParametr -> {
                parametrSearchRepository.index(savedParametr);
                return savedParametr;
            })
            .map(parametrMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParametrDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Parametrs");
        return parametrRepository.findAll(pageable).map(parametrMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParametrDTO> findOne(Long id) {
        LOG.debug("Request to get Parametr : {}", id);
        return parametrRepository.findById(id).map(parametrMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Parametr : {}", id);
        parametrRepository.deleteById(id);
        parametrSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParametrDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Parametrs for query {}", query);
        return parametrSearchRepository.search(query, pageable).map(parametrMapper::toDto);
    }
}
