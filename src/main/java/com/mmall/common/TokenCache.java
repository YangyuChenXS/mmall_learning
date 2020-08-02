package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {

    // 声明日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    // 将token key值的前缀声明为常量
    public static final String TOKEN_PREFIX = "token_";

    // 声明内存块
    // initialCapacity(1000)_设置缓存的初始化容量为1000条数据
    // maximumSize(10000)最大值容量为一万，超过一万，会用LRU算法移除缓存项
    // expireAfterAccess(12, TimeUnit.HOURS)_设置有效期为12小时
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
                                                            .build(new CacheLoader<String, String>() {
                                                                //默认的数据加载实现，当调用get取值的时候，如果Key没有对应的值，就调用这个方法进行加载
                                                                @Override
                                                                public String load(String s) throws Exception {
                                                                    // 返回字符串，防止后面的方法调用报空指针异常的错
                                                                    return "null";
                                                                }
                                                            });
    public static void setKey(String key, String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try{
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return  null;
    }
}
