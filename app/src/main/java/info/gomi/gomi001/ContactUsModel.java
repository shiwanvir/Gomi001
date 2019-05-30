package info.gomi.gomi001;

public class ContactUsModel {

    String email;
    String userId;



    String subject;
    String cusPhoneNo;
    String message;


    public ContactUsModel(String email, String userId, String subject, String phoneNo, String message) {
        this.email = email;
        this.userId = userId;
        this.subject = subject;
        this.cusPhoneNo = phoneNo;
        this.message = message;
    }

    public ContactUsModel() {
    }

    public ContactUsModel(String email, String subject, String phoneNo, String message) {
        this.email = email;
        this.subject = subject;
        this.cusPhoneNo = phoneNo;
        this.message = message;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPhoneNo() {
        return cusPhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.cusPhoneNo = phoneNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
