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
import uz.carapp.rentcarapp.domain.MerchantBranch;
import uz.carapp.rentcarapp.repository.MerchantBranchRepository;
import uz.carapp.rentcarapp.repository.search.MerchantBranchSearchRepository;
import uz.carapp.rentcarapp.service.MerchantBranchService;
import uz.carapp.rentcarapp.service.dto.MerchantBranchDTO;
import uz.carapp.rentcarapp.service.mapper.MerchantBranchMapper;

/**
 * Service Implementation for managing {@link uz.carapp.rentcarapp.domain.MerchantBranch}.
 */
@Service
@Transactional
public class MerchantBranchServiceImpl implements MerchantBranchService {

    private static final Logger LOG = LoggerFactory.getLogger(MerchantBranchServiceImpl.class);

    private final MerchantBranchRepository merchantBranchRepository;

    private final MerchantBranchMapper merchantBranchMapper;

    private final MerchantBranchSearchRepository merchantBranchSearchRepository;

    public MerchantBranchServiceImpl(
        MerchantBranchRepository merchantBranchRepository,
        MerchantBranchMapper merchantBranchMapper,
        MerchantBranchSearchRepository merchantBranchSearchRepository
    ) {
        this.merchantBranchRepository = merchantBranchRepository;
        this.merchantBranchMapper = merchantBranchMapper;
        this.merchantBranchSearchRepository = merchantBranchSearchRepository;
    }

    @Override
    public MerchantBranchDTO save(MerchantBranchDTO merchantBranchDTO) {
        LOG.debug("Request to save MerchantBranch : {}", merchantBranchDTO);
        MerchantBranch merchantBranch = merchantBranchMapper.toEntity(merchantBranchDTO);
        merchantBranch = merchantBranchRepository.save(merchantBranch);
        merchantBranchSearchRepository.index(merchantBranch);
        return merchantBranchMapper.toDto(merchantBranch);
    }

    @Override
    public MerchantBranchDTO update(MerchantBranchDTO merchantBranchDTO) {
        LOG.debug("Request to update MerchantBranch : {}", merchantBranchDTO);
        MerchantBranch merchantBranch = merchantBranchMapper.toEntity(merchantBranchDTO);
        merchantBranch = merchantBranchRepository.save(merchantBranch);
        merchantBranchSearchRepository.index(merchantBranch);
        return merchantBranchMapper.toDto(merchantBranch);
    }

    @Override
    public Optional<MerchantBranchDTO> partialUpdate(MerchantBranchDTO merchantBranchDTO) {
        LOG.debug("Request to partially update MerchantBranch : {}", merchantBranchDTO);

        return merchantBranchRepository
            .findById(merchantBranchDTO.getId())
            .map(existingMerchantBranch -> {
                merchantBranchMapper.partialUpdate(existingMerchantBranch, merchantBranchDTO);

                return existingMerchantBranch;
            })
            .map(merchantBranchRepository::save)
            .map(savedMerchantBranch -> {
                merchantBranchSearchRepository.index(savedMerchantBranch);
                return savedMerchantBranch;
            })
            .map(merchantBranchMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantBranchDTO> findAll() {
        LOG.debug("Request to get all MerchantBranches");
        return merchantBranchRepository
            .findAll()
            .stream()
            .map(merchantBranchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MerchantBranchDTO> findOne(Long id) {
        LOG.debug("Request to get MerchantBranch : {}", id);
        return merchantBranchRepository.findById(id).map(merchantBranchMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete MerchantBranch : {}", id);
        merchantBranchRepository.deleteById(id);
        merchantBranchSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantBranchDTO> search(String query) {
        LOG.debug("Request to search MerchantBranches for query {}", query);
        try {
            return StreamSupport.stream(merchantBranchSearchRepository.search(query).spliterator(), false)
                .map(merchantBranchMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
