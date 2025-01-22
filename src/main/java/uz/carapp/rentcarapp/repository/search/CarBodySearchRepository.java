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
import uz.carapp.rentcarapp.domain.CarBody;
import uz.carapp.rentcarapp.repository.CarBodyRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CarBody} entity.
 */
public interface CarBodySearchRepository extends ElasticsearchRepository<CarBody, Long>, CarBodySearchRepositoryInternal {}

interface CarBodySearchRepositoryInternal {
    Page<CarBody> search(String query, Pageable pageable);

    Page<CarBody> search(Query query);

    @Async
    void index(CarBody entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CarBodySearchRepositoryInternalImpl implements CarBodySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CarBodyRepository repository;

    CarBodySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CarBodyRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<CarBody> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<CarBody> search(Query query) {
        SearchHits<CarBody> searchHits = elasticsearchTemplate.search(query, CarBody.class);
        List<CarBody> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(CarBody entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CarBody.class);
    }
}
