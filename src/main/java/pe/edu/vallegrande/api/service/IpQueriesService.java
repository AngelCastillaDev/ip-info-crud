package pe.edu.vallegrande.api.service;

import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.api.model.IpQueriesModel;
import pe.edu.vallegrande.api.repository.IpQueriesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IpQueriesService {
    private final IpQueriesRepository repository;
    private final OkHttpClient client = new OkHttpClient();

    @Getter
    private final String token;

    public IpQueriesService(IpQueriesRepository repository, @Value("${spring.contentmoderator.token}") String token) {
        this.repository = repository;
        this.token = token;
    }

    public Flux<IpQueriesModel> getByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Flux<IpQueriesModel> getAll() {
        return repository.findAll();
    }

    public Mono<IpQueriesModel> fetchAndInsertIpData() {
        String ip = fetchIp();
        IpQueriesModel ipData = fetchIpInfo(ip);
        return repository.save(ipData);
    }

    public Mono<String> fetchAndInsertIpDataByIp(String ip) {
        if (!isValidIp(ip)) {
            return Mono.just("La dirección IP proporcionada no es válida.");
        }

        IpQueriesModel ipData = fetchIpInfo(ip);

        if (ipData == null) {
            return Mono.just("Error: No se pudo obtener información de la IP. Puede que no exista.");
        }

        return repository.save(ipData)
                .map(savedIp -> "Datos de la IP insertados correctamente: " + savedIp.getIp());
    }

    public Mono<String> deleteIp(Long id) {
        return repository.findById(id)
                .flatMap(existingIp -> {
                    existingIp.setStatus("I"); // Cambiar estado a "Inactivo"
                    return repository.save(existingIp)
                            .then(Mono.just("IP eliminada lógicamente con éxito: " + existingIp.getIp()));
                })
                .switchIfEmpty(Mono.just("IP no encontrada."));
    }

    public Mono<String> restoreIp(Long id) {
        return repository.findById(id)
                .flatMap(existingIp -> {
                    existingIp.setStatus("A"); // Cambiar estado a "Activo"
                    return repository.save(existingIp)
                            .then(Mono.just("IP restaurada con éxito: " + existingIp.getIp()));
                })
                .switchIfEmpty(Mono.just("IP no encontrada."));
    }

    private boolean isValidIp(String ip) {
        String ipRegex =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipRegex);
    }

    private String fetchIp() {
        Request request = new Request.Builder()
                .url("https://api.ipify.org/?format=json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                return new JSONObject(jsonData).getString("ip");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private IpQueriesModel fetchIpInfo(String ip) {
        Request request = new Request.Builder()
                .url("https://ipinfo.io/" + ip + "?token=" + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                IpQueriesModel ipQueries = new IpQueriesModel();
                ipQueries.setIp(jsonObject.getString("ip"));
                ipQueries.setCity(jsonObject.getString("city"));
                ipQueries.setRegion(jsonObject.getString("region"));
                ipQueries.setCountry(jsonObject.getString("country"));
                ipQueries.setLoc(jsonObject.getString("loc"));
                ipQueries.setOrg(jsonObject.getString("org"));
                ipQueries.setPostal(jsonObject.getString("postal"));
                ipQueries.setTimezone(jsonObject.getString("timezone"));
                ipQueries.setStatus("A");

                return ipQueries;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
