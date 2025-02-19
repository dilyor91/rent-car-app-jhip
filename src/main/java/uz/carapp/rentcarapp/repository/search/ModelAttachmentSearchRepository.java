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
import uz.carapp.rentcarapp.domain.ModelAttachment;
import uz.carapp.rentcarapp.repository.ModelAttachmentRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ModelAttachment} entity.
 */
public interface ModelAttachmentSearchRepository
    extends ElasticsearchRepository<ModelAttachment, Long>, ModelAttachmentSearchRepositoryInternal {}

interface ModelAttachmentSearchRepositoryInternal {
    Page<ModelAttachment> search(String query, Pageable pageable);

    Page<ModelAttachment> search(Query query);

    @Async
    void index(ModelAttachment entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ModelAttachmentSearchRepositoryInternalImpl implements ModelAttachmentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ModelAttachmentRepository repository;

    ModelAttachmentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ModelAttachmentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ModelAttachment> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ModelAttachment> search(Query query) {
        SearchHits<ModelAttachment> searchHits = elasticsearchTemplate.search(query, ModelAttachment.class);
        List<ModelAttachment> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ModelAttachment entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ModelAttachment.class);
    }
}
