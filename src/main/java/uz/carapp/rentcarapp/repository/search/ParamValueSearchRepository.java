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
import uz.carapp.rentcarapp.domain.ParamValue;
import uz.carapp.rentcarapp.repository.ParamValueRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ParamValue} entity.
 */
public interface ParamValueSearchRepository extends ElasticsearchRepository<ParamValue, Long>, ParamValueSearchRepositoryInternal {}

interface ParamValueSearchRepositoryInternal {
    Page<ParamValue> search(String query, Pageable pageable);

    Page<ParamValue> search(Query query);

    @Async
    void index(ParamValue entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ParamValueSearchRepositoryInternalImpl implements ParamValueSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ParamValueRepository repository;

    ParamValueSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ParamValueRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ParamValue> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ParamValue> search(Query query) {
        SearchHits<ParamValue> searchHits = elasticsearchTemplate.search(query, ParamValue.class);
        List<ParamValue> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ParamValue entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ParamValue.class);
    }
}
