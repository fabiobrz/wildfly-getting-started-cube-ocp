package org.wildfly.examples;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.awaitility.Awaitility;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@RunWith(Arquillian.class)
public class GettingStartedOpenShiftApplicationIT {

    @RouteURL("hello-world-svc")
    private URL url;

    @Test
    public void helloEndpointShouldReplyWithHttp200() {
        final String serviceUrl = url.toString();

        try (Client client = ClientBuilder.newClient()) {
            // The OpenShift Route resource for the application to be available outside the cluster will take some time to be ready
            Awaitility.await()
                    .atMost(60, TimeUnit.SECONDS)
                    .until( () -> {
                        Response statusResponse = client
                                .target(URI.create(serviceUrl))
                                .path("/hello/World")
                                .request()
                                .get();
                        return statusResponse.getStatus() == HttpStatus.SC_OK;
                    } );

            Response response = client
                    .target(URI.create(serviceUrl))
                    .path("/hello/World")
                    .request()
                    .get();

            Assert.assertEquals(200, response.getStatus());
            Assert.assertEquals("Hello 'World'.", response.readEntity(String.class));

        }
    }
}
