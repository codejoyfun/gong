package com.runwise.supply.entity;

/**
 * Created by mike on 2017/11/17.
 */

public class HostResponse {
//            "dbPort": 80,
//            "host": "erp.runwise.cn",
//            "port": 80,
//            "dbHost": "erp.runwise.cn",
//            "dbName": "LBZ-Golive-01"

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    private String dbPort;
    private String host;
    private String port;
    private String dbHost;
    private String dbName;
}
