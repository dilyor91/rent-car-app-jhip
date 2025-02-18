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
import uz.carapp.rentcarapp.domain.CarTemplateParam;
import uz.carapp.rentcarapp.repository.CarTemplateParamRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CarTemplateParam} entity.
 */
public interface CarTemplateParamSearchRepository
    extends ElasticsearchRepository<CarTemplateParam, Long>, CarTemplateParamSearchRepositoryInternal {}

interface CarTemplateParamSearchRepositoryInternal {
    Page<CarTemplateParam> search(String query, Pageable pageable);

    Page<CarTemplateParam> search(Query query);

    @Async
    void index(CarTemplateParam entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CarTemplateParamSearchRepositoryInternalImpl implements CarTemplateParamSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CarTemplateParamRepository repository;

    CarTemplateParamSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CarTemplateParamRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<CarTemplateParam> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<CarTemplateParam> search(Query query) {
        SearchHits<CarTemplateParam> searchHits = elasticsearchTemplate.search(query, CarTemplateParam.class);
        List<CarTemplateParam> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(CarTemplateParam entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CarTemplateParam.class);
    }
}
