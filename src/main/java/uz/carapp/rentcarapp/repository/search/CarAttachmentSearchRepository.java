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
import uz.carapp.rentcarapp.domain.CarAttachment;
import uz.carapp.rentcarapp.repository.CarAttachmentRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CarAttachment} entity.
 */
public interface CarAttachmentSearchRepository
    extends ElasticsearchRepository<CarAttachment, Long>, CarAttachmentSearchRepositoryInternal {}

interface CarAttachmentSearchRepositoryInternal {
    Page<CarAttachment> search(String query, Pageable pageable);

    Page<CarAttachment> search(Query query);

    @Async
    void index(CarAttachment entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CarAttachmentSearchRepositoryInternalImpl implements CarAttachmentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CarAttachmentRepository repository;

    CarAttachmentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CarAttachmentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<CarAttachment> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<CarAttachment> search(Query query) {
        SearchHits<CarAttachment> searchHits = elasticsearchTemplate.search(query, CarAttachment.class);
        List<CarAttachment> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(CarAttachment entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), CarAttachment.class);
    }
}
