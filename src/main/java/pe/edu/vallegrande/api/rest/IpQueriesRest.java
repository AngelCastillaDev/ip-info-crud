package pe.edu.vallegrande.api.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.api.model.IpQueriesModel;
import pe.edu.vallegrande.api.service.IpQueriesService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ip")
public class IpQueriesRest {
    private final IpQueriesService service;

    public IpQueriesRest(IpQueriesService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public Flux<IpQueriesModel> getIpQueriesByStatus(@RequestParam String status) {
        return service.getByStatus(status);
    }

    @GetMapping("/all")
    public Flux<IpQueriesModel> getAllIpQueries() {
        return service.getAll();
    }

    @PostMapping("/insert")
    public Mono<IpQueriesModel> insertIpData() {
        return service.fetchAndInsertIpData();
    }

    @PostMapping("/insertByIp")
    public Mono<String> insertIpDataByIp(@RequestParam String ip) {
        return service.fetchAndInsertIpDataByIp(ip);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteIp(@PathVariable Long id) {
        return service.deleteIp(id);
    }

    @PutMapping("/restore/{id}")
    public Mono<String> restoreIp(@PathVariable Long id) {
        return service.restoreIp(id);
    }
}
