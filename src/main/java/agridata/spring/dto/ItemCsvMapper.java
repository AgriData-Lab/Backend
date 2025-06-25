package agridata.spring.dto;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class ItemCsvMapper {

    private final Map<String, ItemCode> itemMap = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("items.csv");
        if (is == null) throw new FileNotFoundException("items.csv not found");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }

                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String itemName = parts[2].trim();
                    itemMap.put(itemName, new ItemCode(
                            parts[0].trim(), parts[1].trim(), itemName
                    ));
                }
            }
        }
    }

    public ItemCode getCode(String itemName) {
        return itemMap.get(itemName.trim());
    }

    @Getter
    @AllArgsConstructor
    public static class ItemCode {
        private final String itemCategoryCode;
        private final String itemCode;
        private final String itemName;
    }
}
