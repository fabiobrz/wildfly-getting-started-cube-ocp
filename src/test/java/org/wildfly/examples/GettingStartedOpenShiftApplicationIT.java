package org.wildfly.examples;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(ArquillianConditionalRunner.class)
public class GettingStartedOpenShiftApplicationIT {

    @RouteURL("hello-world-svc")
    private URL url;

    @Test
    public void helloEndpointShouldReplyWithHttp200() {
        // The OpenShift Route resource for the application to be available outside the cluster will take some time to be ready
        Awaitility.await()
                .atMost(60, TimeUnit.SECONDS)
                .until( () -> {
                    Response statusResponse = RestAssured.given()
                            .when()
                            .get(serviceUrl);
                    return statusResponse.statusCode() == HttpStatus.SC_OK;
                } );

        try (Client client = ClientBuilder.newClient()) {
            jakarta.ws.rs.core.Response response = client
                    .target(URI.create(url.toString()))
                    .path("/hello/World")
                    .request()
                    .get();

            assertEquals(200, response.getStatus());
            assertEquals("Hello 'World'.", response.readEntity(String.class));

        }
    }
}
