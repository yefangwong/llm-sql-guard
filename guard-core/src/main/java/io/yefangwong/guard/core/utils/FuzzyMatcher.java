package io.yefangwong.guard.core.utils;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模糊匹配工具：用於尋找與錯誤名稱最接近的正確標識符。
 */
public class FuzzyMatcher {
    private static final LevenshteinDistance DISTANCE = new LevenshteinDistance();
    private static final int MAX_DISTANCE = 5; // 最大容忍距離

    /**
     * 在集合中尋找最接近候選者的名稱。
     */
    public static List<String> findSuggestions(String target, Collection<String> candidates) {
        if (target == null || candidates == null) return new ArrayList<>();

        return candidates.stream()
                .filter(c -> DISTANCE.apply(target.toLowerCase(), c.toLowerCase()) <= MAX_DISTANCE)
                .sorted((c1, c2) -> {
                    int d1 = DISTANCE.apply(target.toLowerCase(), c1.toLowerCase());
                    int d2 = DISTANCE.apply(target.toLowerCase(), c2.toLowerCase());
                    return Integer.compare(d1, d2);
                })
                .limit(3) // 僅回傳前 3 個最像的
                .collect(Collectors.toList());
    }
}
