package agridata.spring.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ItemLoader {

    private List<ItemLocation> itemLocations;

    @PostConstruct
    public void loadData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ClassPathResource("location-mark.json").getInputStream();
        TypeReference<List<ItemLocation>> typeRef = new TypeReference<>() {};
        itemLocations = mapper.readValue(is, typeRef);
    }

    public List<ItemLocation> getItemLocations() {
        return itemLocations;
    }
}
