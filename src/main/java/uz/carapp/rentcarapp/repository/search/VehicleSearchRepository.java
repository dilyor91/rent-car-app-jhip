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
import uz.carapp.rentcarapp.domain.Vehicle;
import uz.carapp.rentcarapp.repository.VehicleRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Vehicle} entity.
 */
public interface VehicleSearchRepository extends ElasticsearchRepository<Vehicle, Long>, VehicleSearchRepositoryInternal {}

interface VehicleSearchRepositoryInternal {
    Page<Vehicle> search(String query, Pageable pageable);

    Page<Vehicle> search(Query query);

    @Async
    void index(Vehicle entity);

    @Async
    void deleteFromIndexById(Long id);
}

class VehicleSearchRepositoryInternalImpl implements VehicleSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final VehicleRepository repository;

    VehicleSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, VehicleRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Vehicle> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Vehicle> search(Query query) {
        SearchHits<Vehicle> searchHits = elasticsearchTemplate.search(query, Vehicle.class);
        List<Vehicle> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Vehicle entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Vehicle.class);
    }
}
