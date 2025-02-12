package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.Model;
import uz.carapp.rentcarapp.repository.ModelRepository;
import uz.carapp.rentcarapp.repository.search.ModelSearchRepository;
import uz.carapp.rentcarapp.service.ModelService;
import uz.carapp.rentcarapp.service.dto.ModelDTO;
import uz.carapp.rentcarapp.service.mapper.ModelMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Model}.
 */
@Service
@Transactional
public class ModelServiceImpl implements ModelService {

    private static final Logger LOG = LoggerFactory.getLogger(ModelServiceImpl.class);

    private final ModelRepository modelRepository;

    private final ModelMapper modelMapper;

    private final ModelSearchRepository modelSearchRepository;

    public ModelServiceImpl(ModelRepository modelRepository, ModelMapper modelMapper, ModelSearchRepository modelSearchRepository) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
        this.modelSearchRepository = modelSearchRepository;
    }

    @Override
    public ModelDTO save(ModelDTO modelDTO) {
        LOG.debug("Request to save Model : {}", modelDTO);
        Model model = modelMapper.toEntity(modelDTO);
        model = modelRepository.save(model);
        modelSearchRepository.index(model);
        return modelMapper.toDto(model);
    }

    @Override
    public ModelDTO update(ModelDTO modelDTO) {
        LOG.debug("Request to update Model : {}", modelDTO);
        Model model = modelMapper.toEntity(modelDTO);
        model = modelRepository.save(model);
        modelSearchRepository.index(model);
        return modelMapper.toDto(model);
    }

    @Override
    public Optional<ModelDTO> partialUpdate(ModelDTO modelDTO) {
        LOG.debug("Request to partially update Model : {}", modelDTO);

        return modelRepository
            .findById(modelDTO.getId())
            .map(existingModel -> {
                modelMapper.partialUpdate(existingModel, modelDTO);

                return existingModel;
            })
            .map(modelRepository::save)
            .map(savedModel -> {
                modelSearchRepository.index(savedModel);
                return savedModel;
            })
            .map(modelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModelDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Models");
        return modelRepository.findAll(pageable).map(modelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ModelDTO> findOne(Long id) {
        LOG.debug("Request to get Model : {}", id);
        return modelRepository.findById(id).map(modelMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Model : {}", id);
        modelRepository.deleteById(id);
        modelSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModelDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Models for query {}", query);
        return modelSearchRepository.search(query, pageable).map(modelMapper::toDto);
    }
}
