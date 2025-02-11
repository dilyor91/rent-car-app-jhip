package uz.carapp.rentcarapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import uz.carapp.rentcarapp.domain.MerchantRole;
import uz.carapp.rentcarapp.repository.MerchantRoleRepository;

/**
 * Spring Data Elasticsearch repository for the {@link MerchantRole} entity.
 */
public interface MerchantRoleSearchRepository extends ElasticsearchRepository<MerchantRole, Long>, MerchantRoleSearchRepositoryInternal {}

interface MerchantRoleSearchRepositoryInternal {
    Page<MerchantRole> search(String query, Pageable pageable);

    Page<MerchantRole> search(Query query);

    @Async
    void index(MerchantRole entity);

    @Async
    void deleteFromIndexById(Long id);
}

class MerchantRoleSearchRepositoryInternalImpl implements MerchantRoleSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final MerchantRoleRepository repository;

    MerchantRoleSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, MerchantRoleRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<MerchantRole> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<MerchantRole> search(Query query) {
        SearchHits<MerchantRole> searchHits = elasticsearchTemplate.search(query, MerchantRole.class);
        List<MerchantRole> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(MerchantRole entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), MerchantRole.class);
    }
}
