package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.Translation;
import uz.carapp.rentcarapp.repository.TranslationRepository;
import uz.carapp.rentcarapp.repository.search.TranslationSearchRepository;
import uz.carapp.rentcarapp.service.TranslationService;
import uz.carapp.rentcarapp.service.dto.TranslationDTO;
import uz.carapp.rentcarapp.service.mapper.TranslationMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Translation}.
 */
@Service
@Transactional
public class TranslationServiceImpl implements TranslationService {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationServiceImpl.class);

    private final TranslationRepository translationRepository;

    private final TranslationMapper translationMapper;

    private final TranslationSearchRepository translationSearchRepository;

    public TranslationServiceImpl(
        TranslationRepository translationRepository,
        TranslationMapper translationMapper,
        TranslationSearchRepository translationSearchRepository
    ) {
        this.translationRepository = translationRepository;
        this.translationMapper = translationMapper;
        this.translationSearchRepository = translationSearchRepository;
    }

    @Override
    public TranslationDTO save(TranslationDTO translationDTO) {
        LOG.debug("Request to save Translation : {}", translationDTO);
        Translation translation = translationMapper.toEntity(translationDTO);
        translation = translationRepository.save(translation);
        translationSearchRepository.index(translation);
        return translationMapper.toDto(translation);
    }

    @Override
    public TranslationDTO update(TranslationDTO translationDTO) {
        LOG.debug("Request to update Translation : {}", translationDTO);
        Translation translation = translationMapper.toEntity(translationDTO);
        translation = translationRepository.save(translation);
        translationSearchRepository.index(translation);
        return translationMapper.toDto(translation);
    }

    @Override
    public Optional<TranslationDTO> partialUpdate(TranslationDTO translationDTO) {
        LOG.debug("Request to partially update Translation : {}", translationDTO);

        return translationRepository
            .findById(translationDTO.getId())
            .map(existingTranslation -> {
                translationMapper.partialUpdate(existingTranslation, translationDTO);

                return existingTranslation;
            })
            .map(translationRepository::save)
            .map(savedTranslation -> {
                translationSearchRepository.index(savedTranslation);
                return savedTranslation;
            })
            .map(translationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TranslationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Translations");
        return translationRepository.findAll(pageable).map(translationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TranslationDTO> findOne(Long id) {
        LOG.debug("Request to get Translation : {}", id);
        return translationRepository.findById(id).map(translationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Translation : {}", id);
        translationRepository.deleteById(id);
        translationSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TranslationDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Translations for query {}", query);
        return translationSearchRepository.search(query, pageable).map(translationMapper::toDto);
    }
}
