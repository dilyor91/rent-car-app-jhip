package uz.carapp.rentcarapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import uz.carapp.rentcarapp.domain.CarMileage;
import uz.carapp.rentcarapp.repository.CarMileageRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CarMileage} entity.
 */
public interface CarMileageSearchRepository extends ElasticsearchRepository<CarMileage, Long>, CarMileageSearchRepositoryInternal {}

interface CarMileageSearchRepositoryInternal {
    Stream<CarMileage> search(String query);

    Stream<CarMileage> search(Query query);

    @Async
    void index(CarMileage entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CarMileageSearchRepositoryInternalImpl implements CarMileageSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CarMileageRepository repository;

    CarMileageSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CarMileageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<CarMileage> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<CarMileage> search(Query query) {
        return elasticsearchTemplate.search(query, CarMileage.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(CarMileage entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CarMileage.class);
    }
}
