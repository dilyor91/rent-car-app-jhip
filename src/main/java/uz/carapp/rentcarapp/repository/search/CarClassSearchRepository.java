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
import uz.carapp.rentcarapp.domain.CarClass;
import uz.carapp.rentcarapp.repository.CarClassRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CarClass} entity.
 */
public interface CarClassSearchRepository extends ElasticsearchRepository<CarClass, Long>, CarClassSearchRepositoryInternal {}

interface CarClassSearchRepositoryInternal {
    Page<CarClass> search(String query, Pageable pageable);

    Page<CarClass> search(Query query);

    @Async
    void index(CarClass entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CarClassSearchRepositoryInternalImpl implements CarClassSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CarClassRepository repository;

    CarClassSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CarClassRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<CarClass> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<CarClass> search(Query query) {
        SearchHits<CarClass> searchHits = elasticsearchTemplate.search(query, CarClass.class);
        List<CarClass> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(CarClass entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CarClass.class);
    }
}
