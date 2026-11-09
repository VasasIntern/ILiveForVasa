package gov.orion;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import gov.orion.api.AccountApi;
import gov.orion.invoker.ApiClient;
import gov.orion.model.HubEventsAgentHeartbeat;
import gov.orion.model.HubEventsSchema;
import io.reactivex.rxjava3.core.Single;
import java.net.http.HttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WiringTest {
    @Test
    void wiresHttpClientOpenApiAndSignalRWithApiKey() {
        ApiClient apiClient = new ApiClient(HttpClient.newBuilder(), ApiClient.createDefaultObjectMapper(), "http://localhost:5000")
            .setRequestInterceptor(req -> req.header("Authorization", "Bearer test_api_key"));
        assertEquals("http://localhost:5000", apiClient.getBaseUri());
        assertNotNull(apiClient.getHttpClient());
        assertNotNull(apiClient.getRequestInterceptor());
        assertNotNull(new AccountApi(apiClient));

        HubConnection hub = HubConnectionBuilder.create(apiClient.getBaseUri() + "/api/_hub/events")
            .withAccessTokenProvider(Single.defer(() -> Single.just("test_api_key")))
            .build();
        hub.on(HubEventsSchema.JSON_PROPERTY_AGENT_HEARTBEAT, (HubEventsAgentHeartbeat hb) -> {}, HubEventsAgentHeartbeat.class);
        assertEquals(HubConnectionState.DISCONNECTED, hub.getConnectionState());
    }
}
