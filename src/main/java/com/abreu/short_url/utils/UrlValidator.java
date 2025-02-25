package com.abreu.short_url.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@Component
public class UrlValidator {

    @Value("#{'${app.blacklisted-domains}'.split(',')}")
    private final Set<String> blacklistedDomains;

    public UrlValidator(Set<String> blacklistedDomains) {
        this.blacklistedDomains = blacklistedDomains;
    }


    public String validateFormat(String url) {

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        try {
            String domain = getDomain(url);
            if (blacklistedDomains.contains(domain)) {
                throw new IllegalArgumentException("Blocked domain: " + domain);
            }

            return url;

        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URL: " + ex.getMessage());
        }

    }

    private static String getDomain(String url) throws URISyntaxException {
        URI uri = new URI(url);
        if (!uri.isAbsolute() || uri.getHost() == null || uri.getHost().isEmpty()) {
            throw new IllegalArgumentException("Invalid URL: missing domain.");
        }

        String host = uri.getHost().toLowerCase();

        if (!host.contains(".com")) {
            throw new IllegalArgumentException("Invalid URL: domain must contain a '.com'");
        }


        String[] hostParts = host.split("\\.");
        for (int i = 0; i < hostParts.length - 1; i++) {
            if (hostParts[i].length() < 3) {
                throw new IllegalArgumentException("Subdomain segment too short: " + hostParts[i]);
            }
        }

        return uri.getHost().toLowerCase();
    }
}
