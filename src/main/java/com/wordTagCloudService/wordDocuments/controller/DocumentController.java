package com.wordTagCloudService.wordDocuments.controller;

import com.wordTagCloudService.wordDocuments.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final ConcurrentMap<String, Integer> wordCounts = new ConcurrentHashMap<>();

    private final DocumentService documentService;

    private final Map<String, MultipartFile> uploadedFiles = new HashMap<>();
    Map<String, Integer> wordCountMap = new HashMap<>();

    @Autowired
    public DocumentController(RedisTemplate<String, Integer> redisTemplate, DocumentService documentService) {
        this.redisTemplate = redisTemplate;
        this.documentService = documentService;
    }

    int n=0;
    int l=0;
    @PostMapping("/add")
    public String addDocuments(@RequestParam("files") MultipartFile[] files) throws IOException {

        StringBuilder processedFiles = new StringBuilder("Added Files are:\n");
        try{
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            System.out.println(fileName);

            if (redisTemplate.hasKey(fileName)) {
                processedFiles.append("File ").append(fileName).append(" already exists.\n");
                continue;
            } else {
                n++;
                wordCountMap = documentService.WordCountFreuiency(file, l);
                System.out.println("value of n is ----------" + n);
            }

            int wordCount = documentService.calculateWordCount(file); // Pass the MultipartFile directly
            // Store word count in Redis-------------------
            redisTemplate.opsForValue().set(fileName, wordCount);

            uploadedFiles.put(fileName, file);// not needed now
            System.out.println(uploadedFiles);

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
        Map<String, Integer> wordCounts = new HashMap<>();
        redisTemplate.keys("*").forEach(key -> {
            Integer wordCount = redisTemplate.opsForValue().get(key);
            wordCounts.put(key, wordCount);
        });
        return wordCounts;
    }

    @GetMapping("/allwords")
    public Map<String, Integer> getWordCountMap() {
        return wordCountMap;
    }

    @GetMapping("/nearby-words/{word}")
    public Map<String, Integer> getNearbyWords(@PathVariable String word) {
        Map<String, Integer> nearbyWords = new HashMap<>();

        // Assuming wordCountMap is populated with word frequencies
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            String currentWord = entry.getKey();
            int count = entry.getValue();

            // Check if the current word is near the target word (using a simple distance of 1)
            if (isNearby(word, currentWord)) {
                nearbyWords.put(currentWord, count);
            }
        }

        return nearbyWords;
    }

    // isNearby method to check if two words are nearby (using a distance of 1)
    private boolean isNearby(String targetWord, String currentWord) {
        return Math.abs(targetWord.length() - currentWord.length()) <= 1;
    }

}
