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
import uz.carapp.rentcarapp.domain.Brand;
import uz.carapp.rentcarapp.repository.BrandRepository;
import uz.carapp.rentcarapp.repository.search.BrandSearchRepository;
import uz.carapp.rentcarapp.service.BrandService;
import uz.carapp.rentcarapp.service.dto.BrandDTO;
import uz.carapp.rentcarapp.service.mapper.BrandMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Brand}.
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    private static final Logger LOG = LoggerFactory.getLogger(BrandServiceImpl.class);

    private final BrandRepository brandRepository;

    private final BrandMapper brandMapper;

    private final BrandSearchRepository brandSearchRepository;

    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper, BrandSearchRepository brandSearchRepository) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.brandSearchRepository = brandSearchRepository;
    }

    @Override
    public BrandDTO save(BrandDTO brandDTO) {
        LOG.debug("Request to save Brand : {}", brandDTO);
        Brand brand = brandMapper.toEntity(brandDTO);
        brand = brandRepository.save(brand);
        brandSearchRepository.index(brand);
        return brandMapper.toDto(brand);
    }

    @Override
    public BrandDTO update(BrandDTO brandDTO) {
        LOG.debug("Request to update Brand : {}", brandDTO);
        Brand brand = brandMapper.toEntity(brandDTO);
        brand = brandRepository.save(brand);
        brandSearchRepository.index(brand);
        return brandMapper.toDto(brand);
    }

    @Override
    public Optional<BrandDTO> partialUpdate(BrandDTO brandDTO) {
        LOG.debug("Request to partially update Brand : {}", brandDTO);

        return brandRepository
            .findById(brandDTO.getId())
            .map(existingBrand -> {
                brandMapper.partialUpdate(existingBrand, brandDTO);

                return existingBrand;
            })
            .map(brandRepository::save)
            .map(savedBrand -> {
                brandSearchRepository.index(savedBrand);
                return savedBrand;
            })
            .map(brandMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> findAll() {
        LOG.debug("Request to get all Brands");
        return brandRepository.findAll().stream().map(brandMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDTO> findOne(Long id) {
        LOG.debug("Request to get Brand : {}", id);
        return brandRepository.findById(id).map(brandMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Brand : {}", id);
        brandRepository.deleteById(id);
        brandSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> search(String query) {
        LOG.debug("Request to search Brands for query {}", query);
        try {
            return StreamSupport.stream(brandSearchRepository.search(query).spliterator(), false).map(brandMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
