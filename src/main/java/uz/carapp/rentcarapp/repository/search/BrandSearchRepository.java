package uz.carapp.rentcarapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import uz.carapp.rentcarapp.domain.Brand;
import uz.carapp.rentcarapp.repository.BrandRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Brand} entity.
 */
public interface BrandSearchRepository extends ElasticsearchRepository<Brand, Long>, BrandSearchRepositoryInternal {}

interface BrandSearchRepositoryInternal {
    Stream<Brand> search(String query);

    Stream<Brand> search(Query query);

    @Async
    void index(Brand entity);

    @Async
    void deleteFromIndexById(Long id);
}

class BrandSearchRepositoryInternalImpl implements BrandSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final BrandRepository repository;

    BrandSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, BrandRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Brand> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Brand> search(Query query) {
        return elasticsearchTemplate.search(query, Brand.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Brand entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Brand.class);
    }
}
