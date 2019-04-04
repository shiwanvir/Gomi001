package info.gomi.gomi001;

public class BookedAdDetails {
    String sellerId;
    String adId;
    String buyerId;

    public BookedAdDetails() {
    }

    public BookedAdDetails(String sellerId) {
        this.sellerId = sellerId;
    }

    public BookedAdDetails(String sellerId, String adId, String buyerId) {
        this.sellerId = sellerId;
        this.adId = adId;
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }
}
