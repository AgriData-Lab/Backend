package agridata.spring.service;

public interface RegionPriceService {
    public String getPriceData(String itemCode, String kindCode, String itemCategoryCode, String rankCode,
                               String countryCode, String regDay);
}
