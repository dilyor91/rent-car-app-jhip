package uz.carapp.rentcarapp.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.domain.Merchant;
import uz.carapp.rentcarapp.repository.MerchantRepository;
import uz.carapp.rentcarapp.repository.search.MerchantSearchRepository;
import uz.carapp.rentcarapp.service.MerchantService;
import uz.carapp.rentcarapp.service.dto.MerchantDTO;
import uz.carapp.rentcarapp.service.mapper.MerchantMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.Merchant}.
 */
@Service
@Transactional
public class MerchantServiceImpl implements MerchantService {

    private static final Logger LOG = LoggerFactory.getLogger(MerchantServiceImpl.class);

    private final MerchantRepository merchantRepository;

    private final MerchantMapper merchantMapper;

    private final MerchantSearchRepository merchantSearchRepository;

    public MerchantServiceImpl(
        MerchantRepository merchantRepository,
        MerchantMapper merchantMapper,
        MerchantSearchRepository merchantSearchRepository
    ) {
        this.merchantRepository = merchantRepository;
        this.merchantMapper = merchantMapper;
        this.merchantSearchRepository = merchantSearchRepository;
    }

    @Override
    public MerchantDTO save(MerchantDTO merchantDTO) {
        LOG.debug("Request to save Merchant : {}", merchantDTO);
        Merchant merchant = merchantMapper.toEntity(merchantDTO);
        merchant = merchantRepository.save(merchant);
        merchantSearchRepository.index(merchant);
        return merchantMapper.toDto(merchant);
    }

    @Override
    public MerchantDTO update(MerchantDTO merchantDTO) {
        LOG.debug("Request to update Merchant : {}", merchantDTO);
        Merchant merchant = merchantMapper.toEntity(merchantDTO);
        merchant = merchantRepository.save(merchant);
        merchantSearchRepository.index(merchant);
        return merchantMapper.toDto(merchant);
    }

    @Override
    public Optional<MerchantDTO> partialUpdate(MerchantDTO merchantDTO) {
        LOG.debug("Request to partially update Merchant : {}", merchantDTO);

        return merchantRepository
            .findById(merchantDTO.getId())
            .map(existingMerchant -> {
                merchantMapper.partialUpdate(existingMerchant, merchantDTO);

                return existingMerchant;
            })
            .map(merchantRepository::save)
            .map(savedMerchant -> {
                merchantSearchRepository.index(savedMerchant);
                return savedMerchant;
            })
            .map(merchantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MerchantDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Merchants");
        return merchantRepository.findAll(pageable).map(merchantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MerchantDTO> findOne(Long id) {
        LOG.debug("Request to get Merchant : {}", id);
        return merchantRepository.findById(id).map(merchantMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Merchant : {}", id);
        merchantRepository.deleteById(id);
        merchantSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MerchantDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Merchants for query {}", query);
        return merchantSearchRepository.search(query, pageable).map(merchantMapper::toDto);
    }
}
