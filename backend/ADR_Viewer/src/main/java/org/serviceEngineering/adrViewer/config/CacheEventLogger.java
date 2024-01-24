package org.serviceEngineering.adrViewer.config;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple implementation of Spring's CacheEventListener for logging cache events.
 * <p>
 * This class implements the CacheEventListener interface to capture cache events such as PUT, GET, and EVICT.
 * It logs relevant information about the cache event, including the event type, key, old value, and new value.
 */
public class CacheEventLogger implements CacheEventListener<Object, Object> {

    private final Logger LOG = LoggerFactory.getLogger(CacheEventLogger.class);

    /**
     * Handles a cache event by logging relevant information.
     *
     * @param cacheEvent The cache event containing information about the event type, key, old value, and new value.
     */
    @Override
    public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
        LOG.info("Cache event = {}, Key = {},  Old value = {}, New value = {}", cacheEvent.getType(),
                cacheEvent.getKey(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
    }
}
