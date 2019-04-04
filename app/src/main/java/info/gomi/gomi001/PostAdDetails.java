package info.gomi.gomi001;

public class PostAdDetails {
    String userId;
    String postAdId;
    String userName;
    String phoneNo;
    String itemName;
    String itemType;
    String price;
    String image;
    String latitide;
    String longtide;
    String adStatus;
    String search;
    String buyerId;

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public PostAdDetails(String userId, String postAdId, String userName, String phoneNo, String itemName, String itemType, String price, String image, String latitide, String longtide, String adStatus, String search, String buyerId) {
        this.userId = userId;
        this.postAdId = postAdId;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.itemName = itemName;
        this.itemType = itemType;
        this.price = price;
        this.image = image;
        this.latitide = latitide;
        this.longtide = longtide;
        this.adStatus = adStatus;
        this.search = search;
        this.buyerId = buyerId;
    }

    public PostAdDetails() {


    }

    public PostAdDetails(String userId, String postAdId, String userName, String phoneNo, String itemName, String itemType, String price, String image, String latitide, String longtide, String adStatus,String search) {
        this.userId = userId;
        this.postAdId = postAdId;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.itemName = itemName;
        this.itemType = itemType;
        this.price = price;
        this.image = image;
        this.latitide = latitide;
        this.longtide = longtide;
        this.adStatus = adStatus;
        this.search=search;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostAdId() {
        return postAdId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getLatitide() {
        return latitide;
    }

    public String getLongtide() {
        return longtide;
    }

    public String getAdStatus() {
        return adStatus;
    }
    public String getSearch(){return  search;}
}

