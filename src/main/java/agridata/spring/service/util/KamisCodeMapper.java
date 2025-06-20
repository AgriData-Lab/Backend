package agridata.spring.service.util;

import java.util.Map;

public class KamisCodeMapper {

    public record KamisCode(String itemCode, String kindCode, String itemCategoryCode, String rankCode ){}

}
