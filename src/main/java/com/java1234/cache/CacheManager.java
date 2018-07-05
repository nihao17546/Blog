package com.java1234.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author nihao
 * @create 2018/7/3
 **/
public class CacheManager<K,V> {
    private final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private LoadingCache<K, V> cachePool;

    private CacheManager(Long expired, TimeUnit unit) {
        this.cachePool = CacheBuilder.newBuilder().expireAfterWrite(expired, unit).build(new CacheLoader<K, V>() {
            @Override
            public V load(K key) throws Exception {
                return null;
            }
        });
    }

    private CacheManager(Long expired, TimeUnit unit, CacheLoader<K, V> cacheLoader) {
        this.cachePool = CacheBuilder.newBuilder().expireAfterWrite(expired, unit).build(cacheLoader);
    }

    /**
     * 创建缓存管理器
     *
     * @param expired
     *            过期时间
     * @param unit
     *            过期时间单位
     * @return
     */
    public static final <K, V> CacheManager<K, V> build(Long expired, TimeUnit unit) {
        return new CacheManager<K, V>(expired, unit);
    }

    /**
     * 创建缓存管理器
     *
     * @param expired
     *            过期时间
     * @param unit
     *            过期时间单位
     * @param cacheLoader
     *            缓存加载器
     * @return
     */
    public static final <K, V> CacheManager<K, V> build(Long expired, TimeUnit unit, CacheLoader<K, V> cacheLoader) {
        return new CacheManager<K, V>(expired, unit, cacheLoader);
    }

    public void set(K key, V val) {
        cachePool.put(key, val);
    }

    public void invalidate(K key) {
        cachePool.invalidate(key);
    }

    public V getIfPresent(K key) {
        return cachePool.getIfPresent(key);
    }

    public V get(K key) {
        try {
            return cachePool.get(key);
        } catch (ExecutionException e) {
            logger.error("获取缓存异常", e);
        }catch (CacheLoader.InvalidCacheLoadException e) {
        }

        return null;
    }
}
