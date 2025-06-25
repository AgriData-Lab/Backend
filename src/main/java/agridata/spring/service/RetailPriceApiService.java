package agridata.spring.service;

public interface RetailPriceApiService {

    public String getPriceData(String itemCode, String kindCode, String itemCategorycode, String rankCode, String countryCode,
                               String startDate, String endDate);


}
