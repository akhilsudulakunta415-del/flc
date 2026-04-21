package flc;

public class Booking {
    private static int NEXT_ID = 1;
    private int id;
    private Member member;
    private Lesson lesson;
    private String status; // booked, attended, changed, cancelled
    private Rating rating;

    public Booking(Member member, Lesson lesson) {
        this.id = NEXT_ID++;
        this.member = member;
        this.lesson = lesson;
        this.status = "booked";
        this.rating = new Rating();
    }

    public int getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Rating getRating() {
        return rating;
    }

    public String toString() {
        return "Booking{" + "id=" + id + ", member=" + member + ", lesson=" + lesson + ", status='" + status + '\'' + '}';
    }
}
