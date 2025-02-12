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
import uz.carapp.rentcarapp.domain.Color;
import uz.carapp.rentcarapp.repository.ColorRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Color} entity.
 */
public interface ColorSearchRepository extends ElasticsearchRepository<Color, Long>, ColorSearchRepositoryInternal {}

interface ColorSearchRepositoryInternal {
    Page<Color> search(String query, Pageable pageable);

    Page<Color> search(Query query);

    @Async
    void index(Color entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ColorSearchRepositoryInternalImpl implements ColorSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ColorRepository repository;

    ColorSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ColorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Color> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Color> search(Query query) {
        SearchHits<Color> searchHits = elasticsearchTemplate.search(query, Color.class);
        List<Color> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Color entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Color.class);
    }
}
