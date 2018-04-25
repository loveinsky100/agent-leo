package com.leo.agent.server;

import com.leo.agent.server.net.nio.AgentServer;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void init() {
        try {
            Configuration.init();
        } catch (Exception e) {
            log.error("\tInitialization failed ({})", e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        Main.init();
        AgentServer.getInstance().start();
    }
}
