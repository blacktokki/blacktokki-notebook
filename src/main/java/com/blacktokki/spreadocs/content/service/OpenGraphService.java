package com.blacktokki.spreadocs.content.service;

import java.util.Optional;

import org.opengraph.OpenGraph;

public interface OpenGraphService {
    static public Optional<OpenGraph> ofNullable(String url, boolean ignoreSpecErrors) {
        try {
            return Optional.of(new OpenGraph(url, ignoreSpecErrors));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
