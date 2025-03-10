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
import uz.carapp.rentcarapp.domain.CarTemplate;
import uz.carapp.rentcarapp.repository.CarTemplateRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CarTemplate} entity.
 */
public interface CarTemplateSearchRepository extends ElasticsearchRepository<CarTemplate, Long>, CarTemplateSearchRepositoryInternal {}

interface CarTemplateSearchRepositoryInternal {
    Page<CarTemplate> search(String query, Pageable pageable);

    Page<CarTemplate> search(Query query);

    @Async
    void index(CarTemplate entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CarTemplateSearchRepositoryInternalImpl implements CarTemplateSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CarTemplateRepository repository;

    CarTemplateSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CarTemplateRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<CarTemplate> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<CarTemplate> search(Query query) {
        SearchHits<CarTemplate> searchHits = elasticsearchTemplate.search(query, CarTemplate.class);
        List<CarTemplate> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(CarTemplate entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CarTemplate.class);
    }
}
