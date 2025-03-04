package uz.carapp.rentcarapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import uz.carapp.rentcarapp.domain.DocAttachment;
import uz.carapp.rentcarapp.repository.DocAttachmentRepository;

/**
 * Spring Data Elasticsearch repository for the {@link DocAttachment} entity.
 */
public interface DocAttachmentSearchRepository
    extends ElasticsearchRepository<DocAttachment, Long>, DocAttachmentSearchRepositoryInternal {}

interface DocAttachmentSearchRepositoryInternal {
    Stream<DocAttachment> search(String query);

    Stream<DocAttachment> search(Query query);

    @Async
    void index(DocAttachment entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DocAttachmentSearchRepositoryInternalImpl implements DocAttachmentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DocAttachmentRepository repository;

    DocAttachmentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DocAttachmentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<DocAttachment> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<DocAttachment> search(Query query) {
        return elasticsearchTemplate.search(query, DocAttachment.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(DocAttachment entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), DocAttachment.class);
    }
}
