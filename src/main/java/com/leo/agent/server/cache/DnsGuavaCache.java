package com.leo.agent.server.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DnsGuavaCache {

    private static int DEFAULT_TIME_LIMIT = 60 * 5;
    private static int CAPACITY;
    private static Cache<String, String> limitedDNSMapping;

    public static void init(int capacity) {
        limitedDNSMapping = CacheBuilder.
                newBuilder().
                recordStats().
                expireAfterWrite(DEFAULT_TIME_LIMIT, TimeUnit.SECONDS).
                maximumSize(capacity).
                build();
    }

    // method reserve for avoiding dns pollution
    private static String put(String domain, String ip) {
        limitedDNSMapping.put(domain, ip);
        return ip;
    }

    public static String get(String domain) throws UnknownHostException {
        if (CAPACITY == 0)
            return null;

        String ip = limitedDNSMapping.getIfPresent(domain);
        ip = ip == null ? getFromLocalDNS(domain) : ip;
        return ip == null ? null : put(domain, ip);
    }

    public static boolean isCached(String domain) {
        return CAPACITY > 0 && limitedDNSMapping.getIfPresent(domain) != null;
    }

    public static int getCapacity() {
        return CAPACITY;
    }

    public static void setCapacity(int capacity) {
        CAPACITY = capacity;
    }

    public static long getSize() {
        return limitedDNSMapping.size();
    }

    public static void flush() {
        limitedDNSMapping.invalidateAll();
    }

    private static String getFromLocalDNS(String domain) throws UnknownHostException {
        return InetAddress.getByName(domain).getHostAddress();
    }

    public static String list() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("%-30s%s\n%s\n", "Domain", "IP", "--------------------          ---------------"));
        limitedDNSMapping.asMap().entrySet().forEach(e -> buffer.append(String.format("%-30s%s\n", e, limitedDNSMapping.getIfPresent(e))));
        return buffer.append("\n").toString();
    }

    static class LRUMap<K, V> extends LinkedHashMap<K, V> {
        private int capacity;

        public LRUMap(int capacity) {
            super(capacity, 0.75F, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }

    }

}
