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
import uz.carapp.rentcarapp.domain.CarParam;
import uz.carapp.rentcarapp.repository.CarParamRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CarParam} entity.
 */
public interface CarParamSearchRepository extends ElasticsearchRepository<CarParam, Long>, CarParamSearchRepositoryInternal {}

interface CarParamSearchRepositoryInternal {
    Page<CarParam> search(String query, Pageable pageable);

    Page<CarParam> search(Query query);

    @Async
    void index(CarParam entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CarParamSearchRepositoryInternalImpl implements CarParamSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CarParamRepository repository;

    CarParamSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CarParamRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<CarParam> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<CarParam> search(Query query) {
        SearchHits<CarParam> searchHits = elasticsearchTemplate.search(query, CarParam.class);
        List<CarParam> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(CarParam entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CarParam.class);
    }
}
