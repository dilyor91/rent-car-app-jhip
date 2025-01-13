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
import uz.carapp.rentcarapp.domain.Attachment;
import uz.carapp.rentcarapp.repository.AttachmentRepository;
import uz.carapp.rentcarapp.repository.search.AttachmentSearchRepository;
import uz.carapp.rentcarapp.service.AttachmentService;
import uz.carapp.rentcarapp.service.dto.AttachmentDTO;
import uz.carapp.rentcarapp.service.mapper.AttachmentMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Attachment}.
 */
@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    private final AttachmentRepository attachmentRepository;

    private final AttachmentMapper attachmentMapper;

    private final AttachmentSearchRepository attachmentSearchRepository;

    public AttachmentServiceImpl(
        AttachmentRepository attachmentRepository,
        AttachmentMapper attachmentMapper,
        AttachmentSearchRepository attachmentSearchRepository
    ) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
        this.attachmentSearchRepository = attachmentSearchRepository;
    }

    @Override
    public AttachmentDTO save(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to save Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment = attachmentRepository.save(attachment);
        attachmentSearchRepository.index(attachment);
        return attachmentMapper.toDto(attachment);
    }

    @Override
    public AttachmentDTO update(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to update Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment = attachmentRepository.save(attachment);
        attachmentSearchRepository.index(attachment);
        return attachmentMapper.toDto(attachment);
    }

    @Override
    public Optional<AttachmentDTO> partialUpdate(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to partially update Attachment : {}", attachmentDTO);

        return attachmentRepository
            .findById(attachmentDTO.getId())
            .map(existingAttachment -> {
                attachmentMapper.partialUpdate(existingAttachment, attachmentDTO);

                return existingAttachment;
            })
            .map(attachmentRepository::save)
            .map(savedAttachment -> {
                attachmentSearchRepository.index(savedAttachment);
                return savedAttachment;
            })
            .map(attachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentDTO> findAll() {
        LOG.debug("Request to get all Attachments");
        return attachmentRepository.findAll().stream().map(attachmentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the attachments where Brand is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AttachmentDTO> findAllWhereBrandIsNull() {
        LOG.debug("Request to get all attachments where Brand is null");
        return StreamSupport.stream(attachmentRepository.findAll().spliterator(), false)
            .filter(attachment -> attachment.getBrand() == null)
            .map(attachmentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttachmentDTO> findOne(Long id) {
        LOG.debug("Request to get Attachment : {}", id);
        return attachmentRepository.findById(id).map(attachmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Attachment : {}", id);
        attachmentRepository.deleteById(id);
        attachmentSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentDTO> search(String query) {
        LOG.debug("Request to search Attachments for query {}", query);
        try {
            return StreamSupport.stream(attachmentSearchRepository.search(query).spliterator(), false)
                .map(attachmentMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
