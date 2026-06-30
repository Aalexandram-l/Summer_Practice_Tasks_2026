package com.example.dbstub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeywordExtractorService {

    private static final Set<String> STOP_WORDS = Set.of(
        "и", "в", "на", "с", "к", "у", "за", "по", "из", "от",
        "а", "но", "или", "да", "не", "ни", "что", "как", "это",
        "для", "без", "до", "при", "же", "ли", "бы", "был", "была",
        "было", "были", "будет", "будут"
    );

    public Map<String, Integer> extractKeywords(String text) {
        log.info("Extracting keywords from: {}", text);

        if (text == null || text.trim().isEmpty()) {
            return new HashMap<>();
        }

        String cleaned = text.toLowerCase().replaceAll("[^а-яa-z\\s]", "");
        String[] words = cleaned.split("\\s+");

        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                frequency.put(word, frequency.getOrDefault(word, 0) + 1);
            }
        }

        Map<String, Integer> sorted = frequency.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));

        log.info("Extracted keywords: {}", sorted);
        return sorted;
    }
}
