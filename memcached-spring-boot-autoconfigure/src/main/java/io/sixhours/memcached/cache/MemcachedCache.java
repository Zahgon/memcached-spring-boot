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

import org.springframework.cache.support.AbstractValueAdaptingCache;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cache implementation on top of Memcached.
 *
 * @author Igor Bolic
 */
public class MemcachedCache extends AbstractValueAdaptingCache {

    private static final String KEY_DELIMITER = ":";

    private final IMemcachedClient memcachedClient;

    private final MemcacheCacheMetadata memcacheCacheMetadata;

    private final Lock lock = new ReentrantLock();

    private final AtomicLong hits = new AtomicLong();

    private final AtomicLong misses = new AtomicLong();

    private final AtomicLong puts = new AtomicLong();

    private final AtomicLong evictions = new AtomicLong();

    /**
     * Create an {@code MemcachedCache} with the given settings.
     *
     * @param name            Cache name
     * @param memcachedClient {@link IMemcachedClient}
     * @param expiration      Cache expiration in seconds
     * @param prefix          Cache key prefix
     * @param namespace       Cache invalidation namespace key
     * @param clock           Cache expiration clock
     */
    public MemcachedCache(String name, IMemcachedClient memcachedClient, int expiration, String prefix, String namespace, Clock clock) {
        super(true);
        this.memcachedClient = memcachedClient;
        this.memcacheCacheMetadata = new MemcacheCacheMetadata(name, expiration, prefix, namespace, clock);
    }

    /**
     * Create an {@code MemcachedCache} with the given settings.
     * <p>
     * Uses the UTC timezone system clock as default expiration time clock.
     *
     * @param name            Cache name
     * @param memcachedClient {@link IMemcachedClient}
     * @param expiration      Cache expiration in seconds
     * @param prefix          Cache key prefix
     * @param namespace       Cache invalidation namespace key
     */
    public MemcachedCache(String name, IMemcachedClient memcachedClient, int expiration, String prefix, String namespace) {
        this(name, memcachedClient, expiration, prefix, namespace, Clock.systemUTC());
    }

    @Override
    protected Object lookup(Object key) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public Object getNativeCache() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private <T> T loadValue(Object key, Callable<T> valueLoader) {
        T value;
        try {
            value = valueLoader.call();
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
        put(key, value);
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public void evict(Object key) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public long hits() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public long misses() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public long puts() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public long evictions() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Tracks number of hits and misses per {@code MemcachedCache} instance.
     *
     * @param value Value returned from the underlying cache store.
     * @return The value
     */
    private Object trackHitsMisses(Object value) {
        if (value != null) {
            hits.incrementAndGet();
        } else {
            misses.incrementAndGet();
        }
        return value;
    }

    /**
     * Gets Memcached key value.
     * <p>
     * Prepends cache prefix and namespace value to the given {@code key}. All whitespace characters will be stripped from
     * the {@code key} value, for Memcached key to be valid.
     *
     * @param key The key
     * @return Memcached key
     */
    private String memcachedKey(Object key) {
        return memcacheCacheMetadata.keyPrefix() + namespaceValue() + KEY_DELIMITER + String.valueOf(key).replaceAll("\\s", "");
    }

    /**
     * Gets namespace value from the cache. The value is used for invalidation of the cache data
     * by incrementing current namespace value by 1.
     *
     * @return Namespace integer value returned as {@code String}
     */
    private String namespaceValue() {
        String value = (String) this.memcachedClient.get(this.memcacheCacheMetadata.namespaceKey());
        if (value == null) {
            value = String.valueOf(System.currentTimeMillis());
            this.memcachedClient.set(this.memcacheCacheMetadata.namespaceKey(), this.memcacheCacheMetadata.expiration(), value);
        }
        return value;
    }

    static class MemcacheCacheMetadata {

        private final String name;

        private final int expiration;

        private final String keyPrefix;

        private final String namespaceKey;

        private final Clock clock;

        public MemcacheCacheMetadata(String name, int expiration, String cachePrefix, String namespace, Clock clock) {
            this.name = name;
            this.expiration = expiration;
            StringBuilder sb = new StringBuilder(cachePrefix).append(KEY_DELIMITER).append(name).append(KEY_DELIMITER);
            this.keyPrefix = sb.toString();
            this.namespaceKey = sb.append(namespace).toString();
            this.clock = clock;
        }

        public String name() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * Expiration times are specified in unsigned integer seconds. They can be set from 0, meaning "never expire",
         * to 30 days (60*60*24*30). Any time higher than 30 days is interpreted as a unix timestamp date. If you
         * want to expire an object on january 1st of next year, this is how you do that.
         * <p>
         * The unix timestamp is a way to track time as a running total of seconds. This count starts at the Unix
         * Epoch on January 1st, 1970 at UTC. Therefore, the unix time stamp is merely the number of seconds between
         * a particular date and the Unix Epoch.
         *
         * @return expiration time as seconds (up to 30 days) or as UNIX timestamp epoch seconds (greater than 30 days).
         * @see <a href="https://github.com/memcached/memcached/wiki/Programming#expiration">Memcached Expiration</a>
         * @see <a href="https://www.unixtimestamp.com/">Unix timestamp</a>
         */
        public int expiration() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        public String keyPrefix() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        public String namespaceKey() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }
}
