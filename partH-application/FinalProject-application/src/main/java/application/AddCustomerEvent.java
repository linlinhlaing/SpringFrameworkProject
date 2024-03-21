package application;

public class AddCustomerEvent {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AddCustomerEvent(String message) {
        this.message = message;
    }
}
