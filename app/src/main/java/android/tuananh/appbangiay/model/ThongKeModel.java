package android.tuananh.appbangiay.model;

import java.util.List;

public class ThongKeModel {
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ThongKe> getResult() {
        return result;
    }

    public void setResult(List<ThongKe> result) {
        this.result = result;
    }

    boolean success;
    String message;
    List<ThongKe> result;
}
