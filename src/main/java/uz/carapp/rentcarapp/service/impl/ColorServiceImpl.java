package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.Color;
import uz.carapp.rentcarapp.repository.ColorRepository;
import uz.carapp.rentcarapp.repository.search.ColorSearchRepository;
import uz.carapp.rentcarapp.service.ColorService;
import uz.carapp.rentcarapp.service.dto.ColorDTO;
import uz.carapp.rentcarapp.service.mapper.ColorMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Color}.
 */
@Service
@Transactional
public class ColorServiceImpl implements ColorService {

    private static final Logger LOG = LoggerFactory.getLogger(ColorServiceImpl.class);

    private final ColorRepository colorRepository;

    private final ColorMapper colorMapper;

    private final ColorSearchRepository colorSearchRepository;

    public ColorServiceImpl(ColorRepository colorRepository, ColorMapper colorMapper, ColorSearchRepository colorSearchRepository) {
        this.colorRepository = colorRepository;
        this.colorMapper = colorMapper;
        this.colorSearchRepository = colorSearchRepository;
    }

    @Override
    public ColorDTO save(ColorDTO colorDTO) {
        LOG.debug("Request to save Color : {}", colorDTO);
        Color color = colorMapper.toEntity(colorDTO);
        color = colorRepository.save(color);
        colorSearchRepository.index(color);
        return colorMapper.toDto(color);
    }

    @Override
    public ColorDTO update(ColorDTO colorDTO) {
        LOG.debug("Request to update Color : {}", colorDTO);
        Color color = colorMapper.toEntity(colorDTO);
        color = colorRepository.save(color);
        colorSearchRepository.index(color);
        return colorMapper.toDto(color);
    }

    @Override
    public Optional<ColorDTO> partialUpdate(ColorDTO colorDTO) {
        LOG.debug("Request to partially update Color : {}", colorDTO);

        return colorRepository
            .findById(colorDTO.getId())
            .map(existingColor -> {
                colorMapper.partialUpdate(existingColor, colorDTO);

                return existingColor;
            })
            .map(colorRepository::save)
            .map(savedColor -> {
                colorSearchRepository.index(savedColor);
                return savedColor;
            })
            .map(colorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColorDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Colors");
        return colorRepository.findAll(pageable).map(colorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ColorDTO> findOne(Long id) {
        LOG.debug("Request to get Color : {}", id);
        return colorRepository.findById(id).map(colorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Color : {}", id);
        colorRepository.deleteById(id);
        colorSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColorDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Colors for query {}", query);
        return colorSearchRepository.search(query, pageable).map(colorMapper::toDto);
    }
}
