package agridata.spring.controller;

import agridata.spring.dto.ItemLoader;
import agridata.spring.dto.ItemLocation;
import agridata.spring.dto.request.ItemRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemLocationController {

    private final ItemLoader itemLoader;

    @GetMapping("/filter")
    public ResponseEntity<List<ItemLocation>> getItemsByItem(@RequestBody ItemRequest request) {
        String item = request.getItem();
        System.out.println("🔍 요청된 아이템: " + item);

        List<ItemLocation> filtered = itemLoader.getItemLocations().stream()
                .filter(loc -> loc.getItem().equals(item))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtered);
    }


}

