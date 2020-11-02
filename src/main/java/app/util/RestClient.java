package app.util;

import java.io.Closeable;
import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Класс обертка для выполнения rest запросов
 *
 * @author vasil
 */
@Slf4j
public class RestClient implements Closeable {

    private final Client client;

    public RestClient() {
        client = ClientBuilder.newBuilder()
                .property("javax.ws.rs.client.http.connectionTimeout", 5000)
                .property("javax.ws.rs.client.http.socketTimeout", 5000)
                .build();
    }

    public Client getInstance() {
        return client;
    }

    @Override
    public void close() throws IOException {
        log.info("REST client close");
        client.close();
    }

}
