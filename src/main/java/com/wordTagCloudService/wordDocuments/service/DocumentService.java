package com.wordTagCloudService.wordDocuments.service;


import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class DocumentService {

    ArrayList<String> lineArrayList = new ArrayList<>();
    Map<String, Integer> wordCountMap = new HashMap<>();

    public Map<String, Integer> WordCountFreuiency(MultipartFile file,int l) throws IOException {
            try {
                String content = extractText(file);
                System.out.println(content);
                String[] lines = content.split("\\s+");
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        lineArrayList.add(line);
                    }
                }
                l = l + 1; // debug
                System.out.println(lineArrayList);
                System.out.println("the value of l is -------" + l);
                Set<String> distinctWords = new HashSet<>(lineArrayList);
                for (String word : distinctWords) {
                    System.out.println(word);
                    wordCountMap.put(word, Collections.frequency(lineArrayList, word));
                }
                System.out.println(wordCountMap);
                // Sorting the wordCountMap based on count
                List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCountMap.entrySet());
                entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

                // Creating a new LinkedHashMap to store the sorted entries
                Map<String, Integer> sortedWordCountMap = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : entries) {
                    sortedWordCountMap.put(entry.getKey(), entry.getValue());
                }
                return sortedWordCountMap;
            }
            catch (IOException e) {
                e.printStackTrace();
                // Handle the exception as appropriate (throwing, logging, etc.)
                return Collections.emptyMap();
            }

    }
    public int calculateWordCount(MultipartFile file) {
        try {
            String content = extractText(file);
            return countWords(content);
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Handle the exception as appropriate
        }
    }

    private String extractText(MultipartFile file) throws IOException {
        try (InputStream stream = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(stream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }

    private int countWords(String content) {
        String[] words = content.trim().split("\\s+");
        System.out.println(words);
        return words.length;
    }

}
