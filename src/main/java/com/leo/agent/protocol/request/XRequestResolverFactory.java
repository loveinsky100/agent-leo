package com.leo.agent.protocol.request;

public class XRequestResolverFactory {

    public static XRequestResolver getInstance(String id) throws Exception {
        switch (id) {
            case "shadowsocks":
                return new ShadowsocksRequestResolver();
            case "fakedhttp":
                return new FakedHttpRequestResolver();
            default:
                throw new Exception("unknown protocol \"" + id + "\"");
        }
    }

    public static boolean exists(String id) {
        return id.equals("shadowsocks") || id.equals("fakedhttp");
    }
}
