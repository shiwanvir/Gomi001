package info.gomi.gomi001;

public class ModelContctUs {
    String email;
    String message;
    String cusPhoneNo;
    String subject;
    String userId;


    public ModelContctUs() {
    }

    public ModelContctUs(String email, String message, String phoneNo, String subject, String userId) {
        this.email = email;
        this.message = message;
        cusPhoneNo = phoneNo;
        this.subject = subject;
        this.userId = userId;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhoneNo() {
        return cusPhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        cusPhoneNo = phoneNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
