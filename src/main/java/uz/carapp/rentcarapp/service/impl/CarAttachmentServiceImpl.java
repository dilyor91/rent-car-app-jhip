package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.CarAttachment;
import uz.carapp.rentcarapp.repository.CarAttachmentRepository;
import uz.carapp.rentcarapp.repository.search.CarAttachmentSearchRepository;
import uz.carapp.rentcarapp.service.CarAttachmentService;
import uz.carapp.rentcarapp.service.dto.CarAttachmentDTO;
import uz.carapp.rentcarapp.service.mapper.CarAttachmentMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.CarAttachment}.
 */
@Service
@Transactional
public class CarAttachmentServiceImpl implements CarAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(CarAttachmentServiceImpl.class);

    private final CarAttachmentRepository carAttachmentRepository;

    private final CarAttachmentMapper carAttachmentMapper;

    private final CarAttachmentSearchRepository carAttachmentSearchRepository;

    public CarAttachmentServiceImpl(
        CarAttachmentRepository carAttachmentRepository,
        CarAttachmentMapper carAttachmentMapper,
        CarAttachmentSearchRepository carAttachmentSearchRepository
    ) {
        this.carAttachmentRepository = carAttachmentRepository;
        this.carAttachmentMapper = carAttachmentMapper;
        this.carAttachmentSearchRepository = carAttachmentSearchRepository;
    }

    @Override
    public CarAttachmentDTO save(CarAttachmentDTO carAttachmentDTO) {
        LOG.debug("Request to save CarAttachment : {}", carAttachmentDTO);
        CarAttachment carAttachment = carAttachmentMapper.toEntity(carAttachmentDTO);
        carAttachment = carAttachmentRepository.save(carAttachment);
        carAttachmentSearchRepository.index(carAttachment);
        return carAttachmentMapper.toDto(carAttachment);
    }

    @Override
    public CarAttachmentDTO update(CarAttachmentDTO carAttachmentDTO) {
        LOG.debug("Request to update CarAttachment : {}", carAttachmentDTO);
        CarAttachment carAttachment = carAttachmentMapper.toEntity(carAttachmentDTO);
        carAttachment = carAttachmentRepository.save(carAttachment);
        carAttachmentSearchRepository.index(carAttachment);
        return carAttachmentMapper.toDto(carAttachment);
    }

    @Override
    public Optional<CarAttachmentDTO> partialUpdate(CarAttachmentDTO carAttachmentDTO) {
        LOG.debug("Request to partially update CarAttachment : {}", carAttachmentDTO);

        return carAttachmentRepository
            .findById(carAttachmentDTO.getId())
            .map(existingCarAttachment -> {
                carAttachmentMapper.partialUpdate(existingCarAttachment, carAttachmentDTO);

                return existingCarAttachment;
            })
            .map(carAttachmentRepository::save)
            .map(savedCarAttachment -> {
                carAttachmentSearchRepository.index(savedCarAttachment);
                return savedCarAttachment;
            })
            .map(carAttachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarAttachmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CarAttachments");
        return carAttachmentRepository.findAll(pageable).map(carAttachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarAttachmentDTO> findOne(Long id) {
        LOG.debug("Request to get CarAttachment : {}", id);
        return carAttachmentRepository.findById(id).map(carAttachmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarAttachment : {}", id);
        carAttachmentRepository.deleteById(id);
        carAttachmentSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarAttachmentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of CarAttachments for query {}", query);
        return carAttachmentSearchRepository.search(query, pageable).map(carAttachmentMapper::toDto);
    }
}
