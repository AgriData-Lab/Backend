package agridata.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LocationCodeLoader {

    private final Map<String, String> nameToCodeMap = new HashMap<>();
    private final Map<String, String> codeToNameMap = new HashMap<>();

    @PostConstruct
    public void load() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("location-codes.json");

            if (is == null) {
                log.error("❌ location-codes.json 파일을 찾을 수 없습니다.");
                return;
            }

            List<LocationCode> locations = mapper.readValue(is, new TypeReference<>() {});
            for (LocationCode loc : locations) {
                log.info("✅ 지역명 '{}' → '{}'", loc.getName(), loc.getCode());
                nameToCodeMap.put(loc.getName(), loc.getCode());
                codeToNameMap.put(loc.getCode(), loc.getName());
            }
            log.info("📌 지역 코드 {}건 로드 완료", nameToCodeMap.size());
        } catch (Exception e) {
            log.error("❌ 지역 코드 JSON 로딩 실패", e);
        }
    }

    public String getCodeByName(String name) {
        if (name == null) return null;
        name = name.trim().replaceAll("(시|군|구)$", "");
        // 예: "서울" → "1101"
        String code = nameToCodeMap.get(name);
        log.info("🔍 지역명 '{}' → 코드 '{}'", name, code);
        return code;
    }

    public String getNameByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "알 수 없음";  // 또는 "" 등 기본값 지정
        }
        return codeToNameMap.get(code.trim());
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationCode {
        private String name;
        private String code;
    }
}
