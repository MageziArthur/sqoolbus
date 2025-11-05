package com.sqool.sqoolbus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sqoolbus")
public class SqoolbusProperties {
    
    private Database database = new Database();
    private Tenant tenant = new Tenant();
    
    public Database getDatabase() {
        return database;
    }
    
    public void setDatabase(Database database) {
        this.database = database;
    }
    
    public Tenant getTenant() {
        return tenant;
    }
    
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    
    public static class Database {
        private String host = "localhost";
        private String port = "3306";
        private String username = "root";
        private String password = "rootpassword";
        private String name = "sqoolbus_master";
        
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
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class Tenant {
        private String defaultTenant = "default_sqool";
        
        public String getDefaultTenant() {
            return defaultTenant;
        }
        
        public void setDefaultTenant(String defaultTenant) {
            this.defaultTenant = defaultTenant;
        }
    }
}