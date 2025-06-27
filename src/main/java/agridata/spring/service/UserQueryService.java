package agridata.spring.service;

public interface UserQueryService {
    public  String getUserPreferItem();

    public String getUserRegion();

    public boolean isDuplicate(String type, String value);
}
