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
import uz.carapp.rentcarapp.domain.Param;
import uz.carapp.rentcarapp.repository.ParamRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Param} entity.
 */
public interface ParamSearchRepository extends ElasticsearchRepository<Param, Long>, ParamSearchRepositoryInternal {}

interface ParamSearchRepositoryInternal {
    Page<Param> search(String query, Pageable pageable);

    Page<Param> search(Query query);

    @Async
    void index(Param entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ParamSearchRepositoryInternalImpl implements ParamSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ParamRepository repository;

    ParamSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ParamRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Param> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Param> search(Query query) {
        SearchHits<Param> searchHits = elasticsearchTemplate.search(query, Param.class);
        List<Param> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Param entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Param.class);
    }
}
