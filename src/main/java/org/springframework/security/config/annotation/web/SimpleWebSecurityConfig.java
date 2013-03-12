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


import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.util.RequestMatcher;

/**
 * @author Rob Winch
 *
 */
@Configuration
@EnableWebSecurity
public abstract class SimpleWebSecurityConfig {

    protected abstract AuthenticationManager authenticationMgr() throws Exception;

    protected DefaultSecurityFilterConfigurator defaultFilterConfigurator() {
        ExpressionUrlAuthorizationRegistry interceptUrls = new ExpressionUrlAuthorizationRegistry();
        authorizeUrls(interceptUrls);
        return new DefaultSecurityFilterConfigurator(interceptUrls)
            .permitAll();
    }

    protected abstract void authorizeUrls(ExpressionUrlAuthorizationRegistry interceptUrls);

    @Bean
    public FilterChainProxySecurityBuilder springSecurityFilterChainBuilder() throws Exception {
        DefaultSecurityFilterConfigurator defaultFilterConfigurator = defaultFilterConfigurator();
        SecurityFilterChainSecurityBuilder springSecurityFilterChain = new SecurityFilterChainSecurityBuilder(authenticationMgr());
        if(defaultFilterConfigurator != null) {
            springSecurityFilterChain.apply(defaultFilterConfigurator);
        }
        configure(springSecurityFilterChain);

        FilterChainProxySecurityBuilder result = new FilterChainProxySecurityBuilder()
            .securityFilterChains(springSecurityFilterChain);
        result.ignoring(ignoredRequests());
        configure(result);
        return result;
    }

    protected void configure(FilterChainProxySecurityBuilder securityFilterChains){
    }

    protected List<RequestMatcher> ignoredRequests() {
        return Collections.emptyList();
    }

    protected abstract void configure(SecurityFilterChainSecurityBuilder springSecurityFilterChain) throws Exception;
}