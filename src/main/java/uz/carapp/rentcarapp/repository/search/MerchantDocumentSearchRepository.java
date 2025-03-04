package uz.carapp.rentcarapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import uz.carapp.rentcarapp.domain.MerchantDocument;
import uz.carapp.rentcarapp.repository.MerchantDocumentRepository;

/**
 * Spring Data Elasticsearch repository for the {@link MerchantDocument} entity.
 */
public interface MerchantDocumentSearchRepository
    extends ElasticsearchRepository<MerchantDocument, Long>, MerchantDocumentSearchRepositoryInternal {}

interface MerchantDocumentSearchRepositoryInternal {
    Stream<MerchantDocument> search(String query);

    Stream<MerchantDocument> search(Query query);

    @Async
    void index(MerchantDocument entity);

    @Async
    void deleteFromIndexById(Long id);
}

class MerchantDocumentSearchRepositoryInternalImpl implements MerchantDocumentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final MerchantDocumentRepository repository;

    MerchantDocumentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, MerchantDocumentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<MerchantDocument> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<MerchantDocument> search(Query query) {
        return elasticsearchTemplate.search(query, MerchantDocument.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(MerchantDocument entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), MerchantDocument.class);
    }
}
