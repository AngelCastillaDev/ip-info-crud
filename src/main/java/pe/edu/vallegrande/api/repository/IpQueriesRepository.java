package pe.edu.vallegrande.api.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import pe.edu.vallegrande.api.model.IpQueriesModel;

public interface IpQueriesRepository extends ReactiveCrudRepository<IpQueriesModel, Long> {
    Flux<IpQueriesModel> findByStatus(String status);
}
