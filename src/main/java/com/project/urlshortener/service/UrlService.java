package com.project.urlshortener.service;

import com.project.urlshortener.dto.AnalyticsResponse;
import com.project.urlshortener.exception.BadRequestException;
import com.project.urlshortener.exception.ResourceNotFoundException;
import com.project.urlshortener.dto.ShortenRequest;
import com.project.urlshortener.dto.ShortenResponse;
import com.project.urlshortener.entity.Url;
import com.project.urlshortener.repository.UrlRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UrlService {
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String BASE_URL = "http://localhost:8080/";

    @Autowired
    private UrlRepository urlRepository;

    @Transactional
    public ShortenResponse shortenUrl(ShortenRequest request) {
        String shortCode;

        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            if (urlRepository.existsByShortCode(request.getCustomAlias())) {
                throw new BadRequestException("Custom alias '" + request.getCustomAlias() + "' is already taken");
            }
            shortCode = request.getCustomAlias();
        } else {
            shortCode = null;
        }

        Url url = new Url();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setCreatedAt(LocalDateTime.now());
        url.setClickCount(0L);
        url.setActive(true);

        if (shortCode != null) {
            url.setShortCode(shortCode);
            url = urlRepository.save(url);
        } else {
            url.setShortCode("temp_" + System.currentTimeMillis());
            url = urlRepository.save(url);

            String generatedCode = toBase62(url.getId());
            url.setShortCode(generatedCode);
            url = urlRepository.save(url);
        }

        return new ShortenResponse(
                url.getOriginalUrl(),
                url.getShortCode(),
                BASE_URL + url.getShortCode(),
                url.getCreatedAt(),
                url.getExpiresAt()
        );
    }
    @Cacheable(value = "urls", key = "#shortCode")
    public Url getUrlByShortCode(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found: " + shortCode));

        if (!url.isActive()) {
            throw new BadRequestException("This short URL has been deactivated");
        }

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This short URL has expired");
        }

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        return url;
    }

    private String toBase62(Long id) {
        StringBuilder result = new StringBuilder();

        while (id > 0) {
            int remainder = (int) (id % 62);
            result.append(BASE62.charAt(remainder));
            id = id / 62;
        }

        return result.reverse().toString();
    }

    public AnalyticsResponse getAnalytics(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found: " + shortCode));

        return new AnalyticsResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                BASE_URL + url.getShortCode(),
                url.getClickCount(),
                url.getCreatedAt(),
                url.getExpiresAt(),
                url.isActive()
        );
    }
}