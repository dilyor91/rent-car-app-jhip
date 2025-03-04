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
import uz.carapp.rentcarapp.domain.DocAttachment;
import uz.carapp.rentcarapp.repository.DocAttachmentRepository;
import uz.carapp.rentcarapp.repository.search.DocAttachmentSearchRepository;
import uz.carapp.rentcarapp.service.DocAttachmentService;
import uz.carapp.rentcarapp.service.dto.DocAttachmentDTO;
import uz.carapp.rentcarapp.service.mapper.DocAttachmentMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.DocAttachment}.
 */
@Service
@Transactional
public class DocAttachmentServiceImpl implements DocAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocAttachmentServiceImpl.class);

    private final DocAttachmentRepository docAttachmentRepository;

    private final DocAttachmentMapper docAttachmentMapper;

    private final DocAttachmentSearchRepository docAttachmentSearchRepository;

    public DocAttachmentServiceImpl(
        DocAttachmentRepository docAttachmentRepository,
        DocAttachmentMapper docAttachmentMapper,
        DocAttachmentSearchRepository docAttachmentSearchRepository
    ) {
        this.docAttachmentRepository = docAttachmentRepository;
        this.docAttachmentMapper = docAttachmentMapper;
        this.docAttachmentSearchRepository = docAttachmentSearchRepository;
    }

    @Override
    public DocAttachmentDTO save(DocAttachmentDTO docAttachmentDTO) {
        LOG.debug("Request to save DocAttachment : {}", docAttachmentDTO);
        DocAttachment docAttachment = docAttachmentMapper.toEntity(docAttachmentDTO);
        docAttachment = docAttachmentRepository.save(docAttachment);
        docAttachmentSearchRepository.index(docAttachment);
        return docAttachmentMapper.toDto(docAttachment);
    }

    @Override
    public DocAttachmentDTO update(DocAttachmentDTO docAttachmentDTO) {
        LOG.debug("Request to update DocAttachment : {}", docAttachmentDTO);
        DocAttachment docAttachment = docAttachmentMapper.toEntity(docAttachmentDTO);
        docAttachment = docAttachmentRepository.save(docAttachment);
        docAttachmentSearchRepository.index(docAttachment);
        return docAttachmentMapper.toDto(docAttachment);
    }

    @Override
    public Optional<DocAttachmentDTO> partialUpdate(DocAttachmentDTO docAttachmentDTO) {
        LOG.debug("Request to partially update DocAttachment : {}", docAttachmentDTO);

        return docAttachmentRepository
            .findById(docAttachmentDTO.getId())
            .map(existingDocAttachment -> {
                docAttachmentMapper.partialUpdate(existingDocAttachment, docAttachmentDTO);

                return existingDocAttachment;
            })
            .map(docAttachmentRepository::save)
            .map(savedDocAttachment -> {
                docAttachmentSearchRepository.index(savedDocAttachment);
                return savedDocAttachment;
            })
            .map(docAttachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocAttachmentDTO> findAll() {
        LOG.debug("Request to get all DocAttachments");
        return docAttachmentRepository.findAll().stream().map(docAttachmentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocAttachmentDTO> findOne(Long id) {
        LOG.debug("Request to get DocAttachment : {}", id);
        return docAttachmentRepository.findById(id).map(docAttachmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DocAttachment : {}", id);
        docAttachmentRepository.deleteById(id);
        docAttachmentSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocAttachmentDTO> search(String query) {
        LOG.debug("Request to search DocAttachments for query {}", query);
        try {
            return StreamSupport.stream(docAttachmentSearchRepository.search(query).spliterator(), false)
                .map(docAttachmentMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
