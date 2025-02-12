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
import uz.carapp.rentcarapp.domain.Model;
import uz.carapp.rentcarapp.repository.ModelRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Model} entity.
 */
public interface ModelSearchRepository extends ElasticsearchRepository<Model, Long>, ModelSearchRepositoryInternal {}

interface ModelSearchRepositoryInternal {
    Page<Model> search(String query, Pageable pageable);

    Page<Model> search(Query query);

    @Async
    void index(Model entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ModelSearchRepositoryInternalImpl implements ModelSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ModelRepository repository;

    ModelSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ModelRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Model> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Model> search(Query query) {
        SearchHits<Model> searchHits = elasticsearchTemplate.search(query, Model.class);
        List<Model> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Model entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Model.class);
    }
}
