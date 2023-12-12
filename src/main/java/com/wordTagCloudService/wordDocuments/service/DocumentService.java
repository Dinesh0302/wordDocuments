package com.wordTagCloudService.wordDocuments.service;


import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentService {
    private final Map<String, Integer> wordCountMap = new ConcurrentHashMap<>();

    public Map<String, Integer> WordCountFreuiency(MultipartFile file) throws IOException {
        try (InputStream stream = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(stream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);

            StringTokenizer tokenizer = new StringTokenizer(extractor.getText());

            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken().toLowerCase();
                wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
            }
        }
        System.out.println("wordCountMap" + wordCountMap);
        return sortByValueDescending(new HashMap<>(wordCountMap));

    }
    public static Map<String, Integer> sortByValueDescending(Map<String, Integer> wordCountMap) {

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCountMap.entrySet());
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Creating a new LinkedHashMap to store the sorted entries
        Map<String, Integer> sortedWordCountMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sortedWordCountMap.put(entry.getKey(), entry.getValue());
        }
        return sortedWordCountMap;
    }

    public int calculateWordCount(MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(stream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return countWords(extractor.getText());
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int countWords(String content) {
        StringTokenizer tokenizer = new StringTokenizer(content);
        return tokenizer.countTokens();
    }

}
