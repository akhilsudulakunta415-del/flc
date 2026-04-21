package flc;

public class Rating {
    private int value;
    private String review = "";

    public Rating() { }

    public Rating(int v) { this.value = v; }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review != null ? review : "";
    }
}
