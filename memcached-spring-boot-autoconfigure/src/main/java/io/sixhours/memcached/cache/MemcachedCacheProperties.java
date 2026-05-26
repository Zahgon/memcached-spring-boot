/*
 * Copyright 2016-2026 Sixhours
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sixhours.memcached.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.boot.convert.DurationUnit;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configuration properties for Memcached cache.
 *
 * @author Igor Bolic
 */
@ConfigurationProperties(prefix = "memcached.cache")
public class MemcachedCacheProperties {

    /**
     * Comma-separated list of hostname:port for memcached servers. The default hostname:port is 'localhost:11211'.
     */
    private List<InetSocketAddress> servers = Default.SERVERS;

    /**
     * Authentication configuration values. Requires binary protocol if used.
     * Defaults to empty authentication configuration.
     */
    private Authentication authentication = Default.AUTHENTICATION;

    /**
     * Comma-separated list of cache names to disable
     */
    private Set<String> disabledCacheNames = new HashSet<>();

    /**
     * Comma-separated list of cache names for which metrics will be collected
     */
    private List<String> metricsCacheNames = new ArrayList<>();

    /**
     * Memcached server provider. Use 'appengine' if running on Google Cloud Platform;
     * Use 'aws' for Amazon ElastiCache with node auto discovery. Defaults to 'static'.
     */
    private Provider provider = Default.PROVIDER;

    /**
     * Cache expiration in seconds. The default is 0s, meaning the cache will never expire.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration expiration = Duration.ofSeconds(Default.EXPIRATION);

    /**
     * Expiration per cache. The map contains cache name as the key and expiration as the value.
     * <p>
     * The expiration value in the map will override global {@code expiration}, but only for the cache with the name
     * specified as the map key.
     */
    private Map<String, Duration> expirationPerCache = new HashMap<>();

    /**
     * Cached object key prefix. The default is 'memcached:spring-boot'.
     */
    private String prefix = Default.PREFIX;

    /**
     * Memcached client protocol. Supports two main protocols: the classic text (ascii), and the newer binary protocol.
     * The default is 'text' protocol.
     */
    private Protocol protocol = Default.PROTOCOL;

    /**
     * Memcached client operation timeout in milliseconds. The default is 2500 milliseconds.
     */
    private Duration operationTimeout = Duration.ofMillis(Default.OPERATION_TIMEOUT);

    /**
     * Amazon ElastiCache configuration polling interval in milliseconds that refreshes the list of cache node hostnames and IP
     * addresses.  The default is 60000 milliseconds
     */
    private Duration serversRefreshInterval = Default.SERVERS_REFRESH_INTERVAL;

    /**
     * Memcached client hash strategy for distribution of data between servers. Supports 'standard' (array based :
     * "hash(key) mod server_count"), 'libmemcached' (consistent hash), 'ketama' (consistent hash),
     * 'php' (make easier to share data with PHP based clients), 'election', 'roundrobin', 'random'.
     * The default is 'standard'.
     */
    private HashStrategy hashStrategy = Default.HASH_STRATEGY;

    public List<InetSocketAddress> getServers() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Populate server list from comma-separated list of hostname:port strings.
     *
     * @param value Comma-separated list
     */
    public void setServers(String value) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Authentication getAuthentication() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setAuthentication(Authentication authentication) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Set<String> getDisabledCacheNames() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setDisabledCacheNames(Set<String> disabledCacheNames) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public List<String> getMetricsCacheNames() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setMetricsCacheNames(List<String> metricsCacheNames) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Provider getProvider() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setProvider(Provider provider) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Duration getExpiration() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setExpiration(Duration expiration) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setExpirationPerCache(Map<String, String> expirationPerCache) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Map<String, Duration> getExpirationPerCache() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public String getPrefix() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setPrefix(String prefix) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Protocol getProtocol() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setProtocol(Protocol protocol) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Duration getOperationTimeout() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setOperationTimeout(Duration operationTimeout) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public Duration getServersRefreshInterval() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setServersRefreshInterval(Duration serversRefreshInterval) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private void validateExpiration(Duration expiration) {
        if (expiration == null || expiration.isNegative()) {
            throw new IllegalArgumentException("Invalid expiration. Duration must be greater than or equal to 0 (zero) seconds.");
        }
    }

    public HashStrategy getHashStrategy() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void setHashStrategy(HashStrategy hashStrategy) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static class Authentication {

        /**
         * Login username of the memcached server.
         */
        private String username;

        /**
         * Login password of the memcached server.
         */
        private String password;

        /**
         * Authentication mechanisms to be used for login negotiation.
         */
        private Mechanism mechanism;

        public String getUsername() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        public void setUsername(String username) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        public String getPassword() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        public void setPassword(String password) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        public Mechanism getMechanism() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        public void setMechanism(Mechanism mechanism) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        public boolean isEmpty() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * Supported authentication mechanisms.
         */
        public enum Mechanism {

            PLAIN("PLAIN"), CRAM_MD5("CRAM-MD5");

            private final String value;

            Mechanism(String value) {
                this.value = value;
            }

            public String asString() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        }
    }

    public enum Protocol {

        TEXT, BINARY
    }

    public enum Provider {

        STATIC, APPENGINE, AWS
    }

    public enum HashStrategy {

        STANDARD,
        LIBMEMCACHED,
        KETAMA,
        PHP,
        ELECTION,
        ROUNDROBIN,
        RANDOM
    }
}
