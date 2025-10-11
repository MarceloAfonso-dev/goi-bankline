package br.com.goibankline.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvironmentConfig {

    private static final Properties appProps = new Properties();
    private static EnvironmentConfig instance;

    static {
        try (InputStream is = EnvironmentConfig.class.getResourceAsStream("/app.properties")) {
            if (is != null) {
                appProps.load(is);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar app.properties: " + e.getMessage());
        }
    }

    public static EnvironmentConfig getInstance() {
        if (instance == null) {
            instance = new EnvironmentConfig();
        }
        return instance;
    }

    public String getBaseUrl() {
        String environment = getEnvironment();

        switch (environment.toLowerCase()) {
            case "aws":
                return buildAwsUrl();
            case "production":
                return buildProductionUrl();
            default:
                return buildLocalUrl();
        }
    }

    public String getApiBaseUrl() {
        return getBaseUrl() + getContextPath();
    }

    private String buildAwsUrl() {
        String route53Domain = System.getenv("ROUTE53_DOMAIN");
        if (route53Domain != null && !route53Domain.isEmpty()) {
            return "https://" + route53Domain;
        }

        String cloudfrontDomain = System.getenv("CLOUDFRONT_DOMAIN");
        if (cloudfrontDomain != null && !cloudfrontDomain.isEmpty()) {
            return "https://" + cloudfrontDomain;
        }

        String ec2PublicIp = System.getenv("EC2_PUBLIC_IP");
        if (ec2PublicIp != null && !ec2PublicIp.isEmpty()) {
            return "http://" + ec2PublicIp + ":8080";
        }

        return "http://localhost:8080";
    }

    private String buildProductionUrl() {
        String domain = appProps.getProperty("app.domain", "localhost:8080");
        String protocol = appProps.getProperty("app.protocol", "https");
        return protocol + "://" + domain;
    }

    private String buildLocalUrl() {
        return "http://localhost:8080";
    }

    public String getContextPath() {
        return appProps.getProperty("app.context.path", "/BancoGOI");
    }

    public String getEnvironment() {
        return System.getenv("APP_ENVIRONMENT") != null ?
               System.getenv("APP_ENVIRONMENT") :
               appProps.getProperty("app.environment", "local");
    }

    public boolean isAwsEnvironment() {
        return "aws".equalsIgnoreCase(getEnvironment()) ||
               System.getenv("AWS_EXECUTION_ENV") != null ||
               System.getenv("EC2_INSTANCE_ID") != null;
    }

    public boolean isLocalEnvironment() {
        return "local".equalsIgnoreCase(getEnvironment());
    }
}
