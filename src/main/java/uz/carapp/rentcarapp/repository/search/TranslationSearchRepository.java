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
import uz.carapp.rentcarapp.domain.Translation;
import uz.carapp.rentcarapp.repository.TranslationRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Translation} entity.
 */
public interface TranslationSearchRepository extends ElasticsearchRepository<Translation, Long>, TranslationSearchRepositoryInternal {}

interface TranslationSearchRepositoryInternal {
    Page<Translation> search(String query, Pageable pageable);

    Page<Translation> search(Query query);

    @Async
    void index(Translation entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TranslationSearchRepositoryInternalImpl implements TranslationSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TranslationRepository repository;

    TranslationSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TranslationRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Translation> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Translation> search(Query query) {
        SearchHits<Translation> searchHits = elasticsearchTemplate.search(query, Translation.class);
        List<Translation> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Translation entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Translation.class);
    }
}
