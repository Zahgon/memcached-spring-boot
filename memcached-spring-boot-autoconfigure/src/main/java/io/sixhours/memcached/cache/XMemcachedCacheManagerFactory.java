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

import net.rubyeye.xmemcached.CommandFactory;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.MemcachedSessionLocator;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.auth.PlainCallbackHandler;
import net.rubyeye.xmemcached.autodiscovery.AutoDiscoveryCacheClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.command.TextCommandFactory;
import net.rubyeye.xmemcached.impl.ArrayMemcachedSessionLocator;
import net.rubyeye.xmemcached.impl.ElectionMemcachedSessionLocator;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.impl.LibmemcachedMemcachedSessionLocator;
import net.rubyeye.xmemcached.impl.PHPMemcacheSessionLocator;
import net.rubyeye.xmemcached.impl.RandomMemcachedSessionLocaltor;
import net.rubyeye.xmemcached.impl.RoundRobinMemcachedSessionLocator;
import org.springframework.beans.factory.ObjectProvider;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory for the XMemcached {@link MemcachedCacheManager} instances.
 *
 * @author Igor Bolic
 * @author Sasa Bolic
 */
public class XMemcachedCacheManagerFactory extends MemcachedCacheManagerFactory {

    private final ObjectProvider<XMemcachedClientCustomizer> customizers;

    public XMemcachedCacheManagerFactory(MemcachedCacheProperties properties, ObjectProvider<XMemcachedClientCustomizer> customizers) {
        super(properties);
        this.customizers = customizers;
    }

    @Override
    IMemcachedClient memcachedClient() throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private MemcachedClientBuilder builder(MemcachedCacheProperties.Provider provider, List<InetSocketAddress> servers) {
        return switch(provider) {
            case STATIC ->
                new XMemcachedClientBuilder(servers);
            case AWS ->
                new AutoDiscoveryCacheClientBuilder(servers);
            default ->
                throw new IllegalArgumentException(String.format("Invalid provider=%s for the XMemcached configuration", provider));
        };
    }

    private CommandFactory commandFactory(MemcachedCacheProperties.Protocol protocol) {
        return switch(protocol) {
            case TEXT ->
                new TextCommandFactory();
            case BINARY ->
                new BinaryCommandFactory();
            default ->
                throw new IllegalArgumentException("Invalid protocol for the XMemcached configuration");
        };
    }

    private MemcachedSessionLocator hashStrategyToLocator(MemcachedCacheProperties.HashStrategy hashStrategy) {
        switch(hashStrategy) {
            case STANDARD:
                return new ArrayMemcachedSessionLocator();
            case LIBMEMCACHED:
                return new LibmemcachedMemcachedSessionLocator();
            case KETAMA:
                return new KetamaMemcachedSessionLocator();
            case PHP:
                return new PHPMemcacheSessionLocator();
            case ELECTION:
                return new ElectionMemcachedSessionLocator();
            case ROUNDROBIN:
                return new RoundRobinMemcachedSessionLocator();
            case RANDOM:
                return new RandomMemcachedSessionLocaltor();
            default:
                throw new IllegalArgumentException("Invalid hash strategy for the XMemcached configuration");
        }
    }
}
