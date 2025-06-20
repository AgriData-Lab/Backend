package agridata.spring.service.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class KamisCodeLoader {

    private final Map<String, KamisCodeMapper.KamisCode> itemMap = new HashMap<>();

    @PostConstruct
    public void init() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/items.csv"), StandardCharsets.UTF_8))) {

            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;  // 빈 줄 무시

                String[] parts = line.split(",");
                if (parts.length < 3) {
                    System.err.println("⚠️ 잘못된 CSV 라인 (필드 부족): " + line);
                    continue;
                }

                String categoryCode = parts[0].trim(); // itemCategoryCode
                String itemCode = parts[1].trim();
                String itemName = parts[2].trim();

                // ✅ kindCode, rankCode는 일단 비워두기
                String kindCode = ""; // API에 포함되지 않게 하기 위해 공백
                String rankCode = "";

                itemMap.put(itemName, new KamisCodeMapper.KamisCode(itemCode, kindCode, categoryCode, rankCode));
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV 로딩 실패", e);
        }
    }

    public KamisCodeMapper.KamisCode getCode(String itemName) {
        return itemMap.get(itemName);
    }
}
