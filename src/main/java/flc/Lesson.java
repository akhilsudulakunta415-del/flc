package flc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lesson {
    private final int id;
    private String exerciseName;
    private String day; // Saturday or Sunday
    private String time; // Morning, Afternoon, Evening
    private LocalDate date;
    private int capacity;
    private double price;
    private ArrayList<Booking> bookings = new ArrayList<>();

    public Lesson(int id, String exerciseName, String day, String time, LocalDate date, int capacity, double price) {
        this.id = id;
        this.exerciseName = exerciseName;
        this.day = day;
        this.time = time;
        this.date = date;
        this.capacity = capacity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public boolean addBooking(Booking b) {
        if (bookings.size() >= capacity) {
            return false;
        }
        bookings.add(b);
        return true;
    }

    public boolean removeBooking(Booking b) {
        return bookings.remove(b);
    }

    public int getCurrentBookings() {
        return bookings.size();
    }

    public double getAverageRating() {
        double sum = 0.0;
        int count = 0;
        for (Booking b : bookings) {
            if ("attended".equals(b.getStatus()) && b.getRating() != null && b.getRating().getValue() >= 1) {
                sum += b.getRating().getValue();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    public long getAttendedCount() {
        return bookings.stream().filter(b -> "attended".equals(b.getStatus())).count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lesson)) {
            return false;
        }
        Lesson lesson = (Lesson) o;
        return id == lesson.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return exerciseName + " on " + day + " " + time + " (" + getCurrentBookings() + "/" + capacity + ")";
    }
}
