package com.abreu.shorturl.services;

import com.abreu.shorturl.models.Url;
import com.abreu.shorturl.models.dto.UrlRequestDTO;
import com.abreu.shorturl.models.dto.UrlResponseDTO;
import com.abreu.shorturl.repositories.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public UrlResponseDTO shortUrl(UrlRequestDTO request, HttpServletRequest servletRequest) {

        String id;

        do {
            id = RandomStringUtils.randomAlphanumeric(5, 10);
        } while (urlRepository.existsById(id));

        urlRepository.save(new Url(id, request.url(), LocalDateTime.now().plusMinutes(5)));

        var redirectUrl = servletRequest.getRequestURL().toString().replace("shorturl", id);

        return new UrlResponseDTO(redirectUrl);
    }


    @Transactional(readOnly = true)
    public HttpHeaders redirect(String id) {

        var url = urlRepository.findById(id).orElseThrow(RuntimeException::new);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(url.getUrl()));

        return httpHeaders;
    }
}
