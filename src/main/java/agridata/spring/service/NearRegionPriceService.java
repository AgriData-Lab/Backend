package agridata.spring.service;

public interface NearRegionPriceService {
    public String getPriceData(String itemCode, String kindCode, String itemCategoryCode, String rankCode,
                               String countryCode, String regDay);
}
