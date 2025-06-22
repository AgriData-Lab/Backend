package agridata.spring.controller;

import agridata.spring.dto.ItemLoader;
import agridata.spring.dto.ItemLocation;
import agridata.spring.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemLocationController {
    private final UserQueryService userQueryService;

    private final ItemLoader itemLoader;

    @GetMapping("/filter")
    public ResponseEntity<List<ItemLocation>> getItemsByItem(/* @RequestBody ItemRequest request*/) {
        String item = getUserPrefer();
//        String item = request.getItem();
        System.out.println("🔍 요청된 아이템: " + item);

        List<ItemLocation> filtered = itemLoader.getItemLocations().stream()
                .filter(loc -> loc.getItem().equals(item))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtered);
    }


    public String getUserPrefer() {
        return userQueryService.getUserPreferItem();
    }


}

