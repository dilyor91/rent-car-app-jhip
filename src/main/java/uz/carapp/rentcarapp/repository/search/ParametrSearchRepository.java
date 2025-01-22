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
import uz.carapp.rentcarapp.domain.Parametr;
import uz.carapp.rentcarapp.repository.ParametrRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Parametr} entity.
 */
public interface ParametrSearchRepository extends ElasticsearchRepository<Parametr, Long>, ParametrSearchRepositoryInternal {}

interface ParametrSearchRepositoryInternal {
    Page<Parametr> search(String query, Pageable pageable);

    Page<Parametr> search(Query query);

    @Async
    void index(Parametr entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ParametrSearchRepositoryInternalImpl implements ParametrSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ParametrRepository repository;

    ParametrSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ParametrRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Parametr> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Parametr> search(Query query) {
        SearchHits<Parametr> searchHits = elasticsearchTemplate.search(query, Parametr.class);
        List<Parametr> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Parametr entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Parametr.class);
    }
}
