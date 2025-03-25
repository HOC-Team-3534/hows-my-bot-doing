package org.team3534.config;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import org.team3534.env.EnvironmentConfig;

public class TbaAuthHeaderFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) {
        // Add the auth header (ideally, get the key from configuration)
        requestContext.getHeaders().add("X-TBA-Auth-Key", EnvironmentConfig.TBA_API_KEY.toString());
    }
}
