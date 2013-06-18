/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.config.annotation.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.config.annotation.SecurityConfiguratorAdapter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.PortMapperImpl;

/**
 * Allows configuring a shared {@link PortMapper} instance used to determine the
 * ports when redirecting between HTTP and HTTPS. The {@link PortMapper} can be
 * obtained from {@link HttpConfiguration#getSharedObject(Class)}.
 *
 * @author Rob Winch
 * @since 3.2
 */
public final class PortMapperConfigurator extends SecurityConfiguratorAdapter<DefaultSecurityFilterChain,HttpConfiguration> {
    private PortMapper portMapper;
    private Map<String, String> httpsPortMappings = new HashMap<String,String>();

    /**
     * Creates a new instance
     */
    PortMapperConfigurator() {
    }

    /**
     * Allows specifying the {@link PortMapper} instance.
     * @param portMapper
     * @return
     */
    public PortMapperConfigurator portMapper(PortMapper portMapper) {
        this.portMapper = portMapper;
        return this;
    }

    /**
     * Adds a port mapping
     * @param httpPort the HTTP port that maps to a specific HTTPS port.
     * @return {@link HttpPortMapping} to define the HTTPS port
     */
    public HttpPortMapping http(int httpPort) {
        return new HttpPortMapping(httpPort);
    }

    @Override
    public void init(HttpConfiguration http) throws Exception {
        http.setSharedObject(PortMapper.class, getPortMapper());
    }

    /**
     * Gets the {@link PortMapper} to use. If {@link #portMapper(PortMapper)}
     * was not invoked, builds a {@link PortMapperImpl} using the port mappings
     * specified with {@link #http(int)}.
     *
     * @return the {@link PortMapper} to use
     */
    private PortMapper getPortMapper() {
        if(portMapper == null) {
            PortMapperImpl portMapper = new PortMapperImpl();
            portMapper.setPortMappings(httpsPortMappings);
            this.portMapper = portMapper;
        }
        return portMapper;
    }

    /**
     * Allows specifying the HTTPS port for a given HTTP port when redirecting
     * between HTTP and HTTPS.
     *
     * @author Rob Winch
     * @since 3.2
     */
    public final class HttpPortMapping {
        private final int httpPort;

        /**
         * Creates a new instance
         * @param httpPort
         * @see PortMapperConfigurator#http(int)
         */
        private HttpPortMapping(int httpPort) {
            this.httpPort = httpPort;
        }

        /**
         * Maps the given HTTP port to the provided HTTPS port and vice versa.
         * @param httpsPort the HTTPS port to map to
         * @return the {@link PortMapperConfigurator} for further customization
         */
        public PortMapperConfigurator mapsTo(int httpsPort) {
            httpsPortMappings.put(String.valueOf(httpPort), String.valueOf(httpsPort));
            return PortMapperConfigurator.this;
        }
    }
}