package agridata.spring.service;

public interface KamisApiService {

    public String getPriceData(String itemCode, String kindCode, String itemCategorycode, String rankCode, String countryCode,
                               String startDate, String endDate);
}
