package com.wordTagCloudService.wordDocuments.controller;

import com.wordTagCloudService.wordDocuments.service.DocumentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final RedisTemplate<String, Integer> redisTemplate;

    private final DocumentService documentService;

    Map<String, Integer> wordCountMap = new ConcurrentHashMap<>();

    @Autowired
    public DocumentController(RedisTemplate<String, Integer> redisTemplate, DocumentService documentService) {
        this.redisTemplate = redisTemplate;
        this.documentService = documentService;
    }

    @PostMapping("/add")
    public String addDocuments(@RequestParam("files") MultipartFile[] files) throws IOException {

        StringBuilder processedFiles = new StringBuilder("Added Files are:\n");
        try{
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            if (redisTemplate.hasKey(fileName)) {
                processedFiles.append("File ").append(fileName).append(" already exists.\n");
                continue;
            } else {
                wordCountMap = documentService.WordCountFreuiency(file);
            }

            int wordCount = documentService.calculateWordCount(file); // Pass the MultipartFile directly
            // Store word count in Redis-------------------// also we can store just in Hashmap
            redisTemplate.opsForValue().set(fileName, wordCount);
            processedFiles.append(fileName).append("\n");
        }
        }
            catch (Exception e) {
                e.printStackTrace();
                return "An error occurred during document processing.";
            }
        return processedFiles.toString();
    }

    @GetMapping("/word-counts")
    public Map<String, Integer> getWordCounts() {
        ConcurrentMap<String, Integer> wordCounts = new ConcurrentHashMap<>();
        try {
            redisTemplate.keys("*").forEach(key -> {
                Integer wordCount = redisTemplate.opsForValue().get(key);
                wordCounts.put(key, wordCount);
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        return wordCounts;
    }

    @GetMapping("/allwords")
    public Map<String, Integer> getWordCountMap() {
        return wordCountMap;
    }

    @GetMapping("/nearby-words/{word}")
    public Map<String, Integer> getNearbyWords(@PathVariable String word) {
        Map<String, Integer> nearbyWords = new ConcurrentHashMap<>();
        try {
            for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
                String currentWord = entry.getKey();
                int count = entry.getValue();
                // Check if the current word is similar to the target word
                if (isSimilarWord(word, currentWord) && !currentWord.equals(word) || currentWord.equals(word)) {
                    nearbyWords.put(currentWord, count);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return documentService.sortByValueDescending(nearbyWords);
    }

    private boolean isSimilarWord(String target, String current) {
        int distance = StringUtils.getLevenshteinDistance(target.toLowerCase(), current.toLowerCase());
        // Adjust the threshold as needed, for example, 50% similarity (take 50 to 75% for better result)
        double similarityThreshold = 0.50;
        double similarity = 1.0 - ((double) distance / Math.max(target.length(), current.length()));
        return similarity >= similarityThreshold;
    }
}
