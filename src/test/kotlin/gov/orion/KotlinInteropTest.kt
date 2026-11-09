package gov.orion

import gov.orion.api.AgentApi
import gov.orion.invoker.ApiClient
import gov.orion.model.HubEventsSchema
import java.net.http.HttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class KotlinInteropTest {
    @Test
    fun `kotlin can call generated OpenAPI classes`() {
        val client = ApiClient(HttpClient.newBuilder(), ApiClient.createDefaultObjectMapper(), "http://localhost:5000")
            .setRequestInterceptor { req -> req.header("Authorization", "Bearer kt_api_key") }
        assertEquals("http://localhost:5000", client.baseUri)
        assertNotNull(AgentApi(client))
        assertEquals("AgentUpserted", HubEventsSchema.JSON_PROPERTY_AGENT_UPSERTED)
    }
}
