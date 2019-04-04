package info.gomi.gomi001;

public class Model {


    String userName;
    String phoneNo;
    String itemName;
    String itemType;
    String price;
    String adImageUrl;
    String userId;
    String postAdId;
    String latitide;
    String longtide;
    String buyerId;
    String adStatus;

    public String getAdStatus() {
        return adStatus;
    }

    public void setAdStatus(String adStatus) {
        this.adStatus = adStatus;
    }



    public Model(String userName, String phoneNo, String itemName, String itemType, String price, String adImageUrl, String userId, String postAdId, String latitide, String longtide, String buyerId, String adStatus) {
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.itemName = itemName;
        this.itemType = itemType;
        this.price = price;
        this.adImageUrl = adImageUrl;
        this.userId = userId;
        this.postAdId = postAdId;
        this.latitide = latitide;
        this.longtide = longtide;
        this.buyerId = buyerId;
        this.adStatus = adStatus;
    }



    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public Model(String userName, String phoneNo, String itemName, String itemType, String price, String adImageUrl, String userId, String postAdId, String latitide, String longtide, String buyerId) {
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.itemName = itemName;
        this.itemType = itemType;
        this.price = price;
        this.adImageUrl = adImageUrl;
        this.userId = userId;
        this.postAdId = postAdId;
        this.latitide = latitide;
        this.longtide = longtide;
        this.buyerId = buyerId;

    }

    public Model() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAdImageUrl() {
        return adImageUrl;
    }

    public void setAdImageUrl(String adImageUrl) {
        this.adImageUrl = adImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdId() {
        return postAdId;
    }

    public void setAdId(String postAdId) {
        this.postAdId = postAdId;
    }

    public String getLatitude() {
        return latitide;
    }

    public void setLatitude(String latitide) {
        this.latitide = latitide;
    }

    public String getLogitude() {
        return longtide;
    }

    public void setLogitude(String longtide) {
        this.longtide = longtide;
    }
}