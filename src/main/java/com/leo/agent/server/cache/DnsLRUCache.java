package com.leo.agent.server.cache;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DnsLRUCache {

    private static int CAPACITY;
    private static Map<String, String> limitedDNSMapping;

    public static void init(int capacity) {
        limitedDNSMapping = new LRUMap<>(CAPACITY = capacity);
    }

    // method reserve for avoiding dns pollution
    private static String put(String domain, String ip) {
        return limitedDNSMapping.put(domain, ip);
    }

    public static String get(String domain) throws UnknownHostException {
        if (CAPACITY == 0)
            return null;

        String ip = limitedDNSMapping.get(domain);
        ip = ip == null ? getFromLocalDNS(domain) : ip;
        return ip == null ? null : put(domain, ip);
    }

    public static boolean isCached(String domain) {
        return CAPACITY > 0 && limitedDNSMapping.get(domain) != null;
    }

    public static int getCapacity() {
        return CAPACITY;
    }

    public static void setCapacity(int capacity) {
        CAPACITY = capacity;
    }

    public static int getSize() {
        return limitedDNSMapping.size();
    }

    public static void flush() {
        limitedDNSMapping.clear();
    }

    private static String getFromLocalDNS(String domain) throws UnknownHostException {
        return InetAddress.getByName(domain).getHostAddress();
    }

    public static String list() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("%-30s%s\n%s\n", "Domain", "IP", "--------------------          ---------------"));
        limitedDNSMapping.keySet().forEach(e -> buffer.append(String.format("%-30s%s\n", e, limitedDNSMapping.get(e))));
        return buffer.append("\n").toString();
    }

    static class LRUMap<K, V> extends LinkedHashMap<K, V> {
        private int capacity;

        public LRUMap(int capacity) {
            super(capacity, 0.75F, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
            return size() > capacity;
        }

    }

}
