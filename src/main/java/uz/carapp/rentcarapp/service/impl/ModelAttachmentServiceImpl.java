package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.ModelAttachment;
import uz.carapp.rentcarapp.repository.ModelAttachmentRepository;
import uz.carapp.rentcarapp.repository.search.ModelAttachmentSearchRepository;
import uz.carapp.rentcarapp.service.ModelAttachmentService;
import uz.carapp.rentcarapp.service.dto.ModelAttachmentDTO;
import uz.carapp.rentcarapp.service.mapper.ModelAttachmentMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.ModelAttachment}.
 */
@Service
@Transactional
public class ModelAttachmentServiceImpl implements ModelAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(ModelAttachmentServiceImpl.class);

    private final ModelAttachmentRepository modelAttachmentRepository;

    private final ModelAttachmentMapper modelAttachmentMapper;

    private final ModelAttachmentSearchRepository modelAttachmentSearchRepository;

    public ModelAttachmentServiceImpl(
        ModelAttachmentRepository modelAttachmentRepository,
        ModelAttachmentMapper modelAttachmentMapper,
        ModelAttachmentSearchRepository modelAttachmentSearchRepository
    ) {
        this.modelAttachmentRepository = modelAttachmentRepository;
        this.modelAttachmentMapper = modelAttachmentMapper;
        this.modelAttachmentSearchRepository = modelAttachmentSearchRepository;
    }

    @Override
    public ModelAttachmentDTO save(ModelAttachmentDTO modelAttachmentDTO) {
        LOG.debug("Request to save ModelAttachment : {}", modelAttachmentDTO);
        ModelAttachment modelAttachment = modelAttachmentMapper.toEntity(modelAttachmentDTO);
        modelAttachment = modelAttachmentRepository.save(modelAttachment);
        modelAttachmentSearchRepository.index(modelAttachment);
        return modelAttachmentMapper.toDto(modelAttachment);
    }

    @Override
    public ModelAttachmentDTO update(ModelAttachmentDTO modelAttachmentDTO) {
        LOG.debug("Request to update ModelAttachment : {}", modelAttachmentDTO);
        ModelAttachment modelAttachment = modelAttachmentMapper.toEntity(modelAttachmentDTO);
        modelAttachment = modelAttachmentRepository.save(modelAttachment);
        modelAttachmentSearchRepository.index(modelAttachment);
        return modelAttachmentMapper.toDto(modelAttachment);
    }

    @Override
    public Optional<ModelAttachmentDTO> partialUpdate(ModelAttachmentDTO modelAttachmentDTO) {
        LOG.debug("Request to partially update ModelAttachment : {}", modelAttachmentDTO);

        return modelAttachmentRepository
            .findById(modelAttachmentDTO.getId())
            .map(existingModelAttachment -> {
                modelAttachmentMapper.partialUpdate(existingModelAttachment, modelAttachmentDTO);

                return existingModelAttachment;
            })
            .map(modelAttachmentRepository::save)
            .map(savedModelAttachment -> {
                modelAttachmentSearchRepository.index(savedModelAttachment);
                return savedModelAttachment;
            })
            .map(modelAttachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModelAttachmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ModelAttachments");
        return modelAttachmentRepository.findAll(pageable).map(modelAttachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ModelAttachmentDTO> findOne(Long id) {
        LOG.debug("Request to get ModelAttachment : {}", id);
        return modelAttachmentRepository.findById(id).map(modelAttachmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ModelAttachment : {}", id);
        modelAttachmentRepository.deleteById(id);
        modelAttachmentSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModelAttachmentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ModelAttachments for query {}", query);
        return modelAttachmentSearchRepository.search(query, pageable).map(modelAttachmentMapper::toDto);
    }
}
