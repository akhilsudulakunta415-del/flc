package flc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Centre {
    private List<Lesson> timetable = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private List<Booking> allBookings = new ArrayList<>();

    private HashMap<String, Double> priceByExercise = new HashMap<>();

    private static final int NUM_WEEKENDS = 8;
    private static final int MAX_CAPACITY = 4;

    private String[] exerciseTypes = {"Yoga", "Zumba", "Aquacise", "Box Fit", "Body Blitz"};
    private String[] days = {"Saturday", "Sunday"};
    private String[] timeSlots = {"Morning", "Afternoon", "Evening"};

    public Centre() {
        seedData();
    }

    private void seedData() {
        members.add(new Member("M-001", "Alice"));
        members.add(new Member("M-002", "Bob"));
        members.add(new Member("M-003", "Charlie"));
        members.add(new Member("M-004", "Diana"));
        members.add(new Member("M-005", "Ethan"));
        members.add(new Member("M-006", "Fiona"));
        members.add(new Member("M-007", "George"));
        members.add(new Member("M-008", "Hannah"));
        members.add(new Member("M-009", "Ian"));
        members.add(new Member("M-010", "Julia"));

        priceByExercise.put("Yoga", 12.0);
        priceByExercise.put("Zumba", 14.0);
        priceByExercise.put("Aquacise", 11.0);
        priceByExercise.put("Box Fit", 10.0);
        priceByExercise.put("Body Blitz", 13.0);

        // First four weekends in May 2026, then four in June 2026 (24 lessons per month)
        LocalDate[] weekendSaturdays = new LocalDate[] {
            LocalDate.of(2026, 5, 2),
            LocalDate.of(2026, 5, 9),
            LocalDate.of(2026, 5, 16),
            LocalDate.of(2026, 5, 23),
            LocalDate.of(2026, 6, 6),
            LocalDate.of(2026, 6, 13),
            LocalDate.of(2026, 6, 20),
            LocalDate.of(2026, 6, 27)
        };

        int lessonCounter = 0;
        int nextLessonId = 1;
        for (int w = 0; w < NUM_WEEKENDS; w++) {
            LocalDate saturday = weekendSaturdays[w];
            for (String day : days) {
                LocalDate lessonDate = "Saturday".equals(day) ? saturday : saturday.plusDays(1);
                for (String time : timeSlots) {
                    String exercise = exerciseTypes[lessonCounter % exerciseTypes.length];
                    double price = priceByExercise.get(exercise);
                    timetable.add(new Lesson(nextLessonId++, exercise, day, time, lessonDate, MAX_CAPACITY, price));
                    lessonCounter++;
                }
            }
        }
    }

    public List<Lesson> getTimetable() {
        return Collections.unmodifiableList(timetable);
    }

    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<Booking> getAllBookings() {
        return Collections.unmodifiableList(allBookings);
    }

    public Member findMemberById(String memberId) {
        for (Member m : members) {
            if (m.getMemberId().equalsIgnoreCase(memberId)) {
                return m;
            }
        }
        return null;
    }

    public Lesson findLesson(String day, String time, String exerciseName) {
        for (Lesson l : timetable) {
            if (l.getDay().equalsIgnoreCase(day)
                && l.getTime().equalsIgnoreCase(time)
                && l.getExerciseName().equalsIgnoreCase(exerciseName)) {
                return l;
            }
        }
        return null;
    }

    public Lesson findLessonById(int lessonId) {
        for (Lesson l : timetable) {
            if (l.getId() == lessonId) {
                return l;
            }
        }
        return null;
    }

    public List<Lesson> getLessonsInMonth(int month) {
        return timetable.stream()
            .filter(l -> l.getDate().getMonthValue() == month)
            .sorted(Comparator.comparing(Lesson::getDate)
                .thenComparing(l -> daySortKey(l.getDay()))
                .thenComparing(l -> timeSortKey(l.getTime())))
            .collect(Collectors.toList());
    }

    private static int daySortKey(String day) {
        return "Saturday".equalsIgnoreCase(day) ? 0 : 1;
    }

    private static int timeSortKey(String time) {
        if ("Morning".equalsIgnoreCase(time)) {
            return 0;
        }
        if ("Afternoon".equalsIgnoreCase(time)) {
            return 1;
        }
        return 2;
    }

    public void printMonthlyLessonReport(int month) {
        List<Lesson> lessons = getLessonsInMonth(month);
        System.out.println();
        System.out.println("=== Monthly lesson report (month " + month + ") ===");
        if (lessons.isEmpty()) {
            System.out.println("No lessons scheduled in this month.");
            return;
        }
        System.out.println("Attended counts and average ratings (attended members only).");
        System.out.println("---");
        for (Lesson l : lessons) {
            long attended = l.getAttendedCount();
            double avg = l.getAverageRating();
            String avgStr = attended == 0 ? "n/a" : String.format("%.2f", avg);
            System.out.printf(
                "ID %d | %s | %s %s %-10s | attended: %d | avg rating: %s%n",
                l.getId(),
                l.getDate(),
                l.getDay(),
                l.getTime(),
                l.getExerciseName(),
                attended,
                avgStr
            );
        }
        System.out.println("--- Total lesson slots: " + lessons.size());
        System.out.println();
    }

    public void printMonthlyChampionReport(int month) {
        List<Lesson> lessons = getLessonsInMonth(month);
        System.out.println();
        System.out.println("=== Monthly champion exercise type (income) — month " + month + " ===");
        if (lessons.isEmpty()) {
            System.out.println("No lessons in this month.");
            System.out.println();
            return;
        }
        Map<String, Double> incomeByExercise = new HashMap<>();
        for (Lesson l : lessons) {
            long attended = l.getAttendedCount();
            double income = attended * l.getPrice();
            incomeByExercise.merge(l.getExerciseName(), income, Double::sum);
        }
        double best = incomeByExercise.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        System.out.println("Income by exercise type (from attended bookings × lesson price):");
        incomeByExercise.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> System.out.printf("  %-10s £%.2f%n", e.getKey(), e.getValue()));
        System.out.println();
        if (best <= 0) {
            System.out.println("No attended bookings in this month — no champion.");
        } else {
            System.out.print("Highest income: ");
            String top = incomeByExercise.entrySet().stream()
                .filter(e -> Double.compare(e.getValue(), best) == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
            System.out.println(top + " (£" + String.format("%.2f", best) + ")");
        }
        System.out.println();
    }

    public Booking addBooking(Member member, Lesson lesson) {
        if (member == null || lesson == null) {
            return null;
        }
        if (lesson.getCurrentBookings() >= lesson.getCapacity()) {
            System.out.println("Error: Lesson is full.");
            return null;
        }
        for (Booking existingBooking : allBookings) {
            if (existingBooking.getMember().equals(member)
                && existingBooking.getLesson().equals(lesson)
                && ("booked".equals(existingBooking.getStatus()) || "changed".equals(existingBooking.getStatus()))) {
                System.out.println("Error: Member has already booked this lesson.");
                return null;
            }
        }

        Booking newBooking = new Booking(member, lesson);
        lesson.addBooking(newBooking);
        allBookings.add(newBooking);
        return newBooking;
    }

    public Booking changeBooking(Booking bookingToChange, Lesson newLesson) {
        if (bookingToChange == null || newLesson == null) {
            return null;
        }
        if (!"booked".equals(bookingToChange.getStatus()) && !"changed".equals(bookingToChange.getStatus())) {
            System.out.println("Error: Only active bookings (booked/changed) can be moved.");
            return null;
        }

        Lesson oldLesson = bookingToChange.getLesson();

        if (newLesson.getCurrentBookings() >= newLesson.getCapacity()) {
            System.out.println("Error: New lesson is full.");
            return null;
        }

        if (bookingToChange.getLesson().equals(newLesson)) {
            System.out.println("Error: Cannot change to the same lesson.");
            return null;
        }

        for (Booking existingBooking : allBookings) {
            if (existingBooking.getMember().equals(bookingToChange.getMember())
                && existingBooking.getLesson().equals(newLesson)
                && ("booked".equals(existingBooking.getStatus()) || "changed".equals(existingBooking.getStatus()))) {
                System.out.println("Error: Member has already booked this new lesson.");
                return null;
            }
        }

        bookingToChange.setStatus("changed");
        oldLesson.removeBooking(bookingToChange);

        newLesson.addBooking(bookingToChange);
        bookingToChange.setLesson(newLesson);
        bookingToChange.setStatus("booked");

        return bookingToChange;
    }

    public boolean cancelBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        String st = booking.getStatus();
        if ("cancelled".equals(st)) {
            System.out.println("Error: Booking already cancelled.");
            return false;
        }
        if ("attended".equals(st)) {
            System.out.println("Error: Cannot cancel after the lesson was attended.");
            return false;
        }
        if (!"booked".equals(st) && !"changed".equals(st)) {
            System.out.println("Error: Booking cannot be cancelled.");
            return false;
        }
        booking.setStatus("cancelled");
        booking.getLesson().removeBooking(booking);
        return true;
    }

    public void attendLesson(Booking booking, int ratingValue) {
        attendLesson(booking, ratingValue, "");
    }

    public void attendLesson(Booking booking, int ratingValue, String review) {
        if (booking == null || !"booked".equals(booking.getStatus())) {
            System.out.println("Error: Booking is not active (must be 'booked').");
            return;
        }
        if (ratingValue < 1 || ratingValue > 5) {
            System.out.println("Error: Rating must be between 1 and 5.");
            return;
        }
        booking.setStatus("attended");
        booking.getRating().setValue(ratingValue);
        booking.getRating().setReview(review != null ? review : "");
    }

    public List<Lesson> getLessonsByDay(String day) {
        return timetable.stream()
            .filter(l -> l.getDay().equalsIgnoreCase(day))
            .sorted(Comparator.comparing(Lesson::getDate).thenComparing(l -> timeSortKey(l.getTime())))
            .collect(Collectors.toList());
    }

    public List<Lesson> getLessonsByExercise(String exerciseName) {
        return timetable.stream()
            .filter(l -> l.getExerciseName().equalsIgnoreCase(exerciseName))
            .sorted(Comparator.comparing(Lesson::getDate)
                .thenComparing(l -> daySortKey(l.getDay()))
                .thenComparing(l -> timeSortKey(l.getTime())))
            .collect(Collectors.toList());
    }
}
