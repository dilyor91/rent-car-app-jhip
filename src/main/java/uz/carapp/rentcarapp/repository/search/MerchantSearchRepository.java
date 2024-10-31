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
import uz.carapp.rentcarapp.domain.Merchant;
import uz.carapp.rentcarapp.repository.MerchantRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Merchant} entity.
 */
public interface MerchantSearchRepository extends ElasticsearchRepository<Merchant, Long>, MerchantSearchRepositoryInternal {}

interface MerchantSearchRepositoryInternal {
    Page<Merchant> search(String query, Pageable pageable);

    Page<Merchant> search(Query query);

    @Async
    void index(Merchant entity);

    @Async
    void deleteFromIndexById(Long id);
}

class MerchantSearchRepositoryInternalImpl implements MerchantSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final MerchantRepository repository;

    MerchantSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, MerchantRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Merchant> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Merchant> search(Query query) {
        SearchHits<Merchant> searchHits = elasticsearchTemplate.search(query, Merchant.class);
        List<Merchant> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Merchant entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Merchant.class);
    }
}
