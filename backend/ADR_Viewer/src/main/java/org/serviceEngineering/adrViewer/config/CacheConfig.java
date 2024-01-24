package org.serviceEngineering.adrViewer.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for enabling caching in the application.
 * <p>
 * This class uses the Spring Cache abstraction to enable caching and configures a simple in-memory cache manager
 * based on concurrent maps. The cache manager is initialized with a single cache named "adr".
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates and returns a CacheManager bean for enabling caching in the application.
     * <p>
     * The method configures a ConcurrentMapCacheManager and initializes it with a single cache named "adr".
     *
     * @return The configured CacheManager bean.
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("adr");
    }
}
