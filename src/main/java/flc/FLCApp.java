package flc;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Command-line interface for the Furzefield Leisure Centre booking system.
 */
public final class FLCApp {

    private FLCApp() { }

    public static void main(String[] args) {
        Centre centre = new Centre();
        Scanner in = new Scanner(System.in);
        try {
            runMainMenu(centre, in);
        } finally {
            in.close();
        }
    }

    private static void runMainMenu(Centre centre, Scanner in) {
        System.out.println("Furzefield Leisure Centre — booking system");
        while (true) {
            System.out.println();
            System.out.println("Main menu");
            System.out.println("  1  Book a group exercise lesson");
            System.out.println("  2  Change or cancel a booking");
            System.out.println("  3  Attend a lesson (review + rating)");
            System.out.println("  4  Monthly lesson report");
            System.out.println("  5  Monthly champion exercise type (income)");
            System.out.println("  0  Exit");
            System.out.print("Choose: ");
            String line = in.nextLine().trim();
            switch (line) {
                case "1":
                    bookLesson(centre, in);
                    break;
                case "2":
                    changeOrCancel(centre, in);
                    break;
                case "3":
                    attendLesson(centre, in);
                    break;
                case "4":
                    monthlyLessonReport(centre, in);
                    break;
                case "5":
                    monthlyChampionReport(centre, in);
                    break;
                case "0":
                    System.out.println("Goodbye.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void bookLesson(Centre centre, Scanner in) {
        Member member = promptMember(centre, in);
        if (member == null) {
            return;
        }
        System.out.println("View timetable by: 1 = day (Saturday/Sunday), 2 = exercise name");
        System.out.print("Choice: ");
        String mode = in.nextLine().trim();
        List<Lesson> choices;
        if ("1".equals(mode)) {
            System.out.print("Enter day (Saturday or Sunday): ");
            String day = in.nextLine().trim();
            choices = centre.getLessonsByDay(day);
            if (choices.isEmpty()) {
                System.out.println("No lessons found for that day.");
                return;
            }
        } else if ("2".equals(mode)) {
            System.out.print("Enter exercise name (e.g. Yoga): ");
            String ex = in.nextLine().trim();
            choices = centre.getLessonsByExercise(ex);
            if (choices.isEmpty()) {
                System.out.println("No lessons found for that exercise.");
                return;
            }
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        printLessonList(choices);
        Integer lid = readPositiveInt(in, "Enter lesson ID to book (or 0 to cancel): ");
        if (lid == null || lid == 0) {
            return;
        }
        Lesson lesson = centre.findLessonById(lid);
        if (lesson == null || !choices.contains(lesson)) {
            System.out.println("Invalid lesson ID for this list.");
            return;
        }
        Booking b = centre.addBooking(member, lesson);
        if (b != null) {
            System.out.println("Booking successful. Booking ID: " + b.getId());
        }
    }

    private static void changeOrCancel(Centre centre, Scanner in) {
        Member member = promptMember(centre, in);
        if (member == null) {
            return;
        }
        List<Booking> active = centre.getAllBookings().stream()
            .filter(b -> b.getMember().equals(member))
            .filter(b -> "booked".equals(b.getStatus()) || "changed".equals(b.getStatus()))
            .collect(Collectors.toList());
        if (active.isEmpty()) {
            System.out.println("No active bookings for this member.");
            return;
        }
        System.out.println("Active bookings:");
        for (Booking b : active) {
            Lesson l = b.getLesson();
            System.out.printf(
                "  ID %d | %s | %s %s | lesson ID %d%n",
                b.getId(),
                l.getDate(),
                l.getDay(),
                l.getTime(),
                l.getId()
            );
        }
        Integer bid = readPositiveInt(in, "Enter booking ID (or 0 to cancel): ");
        if (bid == null || bid == 0) {
            return;
        }
        Booking booking = active.stream().filter(b -> b.getId() == bid).findFirst().orElse(null);
        if (booking == null) {
            System.out.println("Booking ID not in your active list.");
            return;
        }
        System.out.println("  1  Change to another lesson");
        System.out.println("  2  Cancel this booking");
        System.out.print("Choice: ");
        String sub = in.nextLine().trim();
        if ("2".equals(sub)) {
            if (centre.cancelBooking(booking)) {
                System.out.println("Booking cancelled.");
            }
            return;
        }
        if (!"1".equals(sub)) {
            System.out.println("Invalid choice.");
            return;
        }
        System.out.println("View new lesson by: 1 = day, 2 = exercise name");
        System.out.print("Choice: ");
        String mode = in.nextLine().trim();
        List<Lesson> choices;
        if ("1".equals(mode)) {
            System.out.print("Enter day (Saturday or Sunday): ");
            choices = centre.getLessonsByDay(in.nextLine().trim());
        } else if ("2".equals(mode)) {
            System.out.print("Enter exercise name: ");
            choices = centre.getLessonsByExercise(in.nextLine().trim());
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        if (choices.isEmpty()) {
            System.out.println("No lessons found.");
            return;
        }
        printLessonList(choices);
        Integer lid = readPositiveInt(in, "Enter new lesson ID (or 0 to cancel): ");
        if (lid == null || lid == 0) {
            return;
        }
        Lesson newLesson = centre.findLessonById(lid);
        if (newLesson == null || !choices.contains(newLesson)) {
            System.out.println("Invalid lesson ID for this list.");
            return;
        }
        Booking updated = centre.changeBooking(booking, newLesson);
        if (updated != null) {
            System.out.println("Booking changed. Booking ID unchanged: " + updated.getId());
        }
    }

    private static void attendLesson(Centre centre, Scanner in) {
        Member member = promptMember(centre, in);
        if (member == null) {
            return;
        }
        List<Booking> booked = centre.getAllBookings().stream()
            .filter(b -> b.getMember().equals(member))
            .filter(b -> "booked".equals(b.getStatus()))
            .collect(Collectors.toList());
        if (booked.isEmpty()) {
            System.out.println("No bookings with status 'booked' for this member.");
            return;
        }
        System.out.println("Bookings ready to attend:");
        for (Booking b : booked) {
            Lesson l = b.getLesson();
            System.out.printf(
                "  Booking ID %d | %s | %s %s | %s%n",
                b.getId(),
                l.getDate(),
                l.getDay(),
                l.getTime(),
                l.getExerciseName()
            );
        }
        Integer bid = readPositiveInt(in, "Enter booking ID to attend (or 0 to cancel): ");
        if (bid == null || bid == 0) {
            return;
        }
        Booking booking = booked.stream().filter(b -> b.getId() == bid).findFirst().orElse(null);
        if (booking == null) {
            System.out.println("Invalid booking ID.");
            return;
        }
        System.out.print("Short review (text): ");
        String review = in.nextLine();
        Integer rating = readPositiveInt(in, "Rating 1–5 (1 Very dissatisfied … 5 Very satisfied): ");
        if (rating == null || rating < 1 || rating > 5) {
            System.out.println("Invalid rating.");
            return;
        }
        centre.attendLesson(booking, rating, review);
        if ("attended".equals(booking.getStatus())) {
            System.out.println("Lesson marked as attended. Thank you.");
        }
    }

    private static void monthlyLessonReport(Centre centre, Scanner in) {
        Integer month = readPositiveInt(in, "Enter month number (1–12, e.g. 5 for May): ");
        if (month == null || month < 1 || month > 12) {
            System.out.println("Invalid month.");
            return;
        }
        centre.printMonthlyLessonReport(month);
    }

    private static void monthlyChampionReport(Centre centre, Scanner in) {
        Integer month = readPositiveInt(in, "Enter month number (1–12, e.g. 5 for May): ");
        if (month == null || month < 1 || month > 12) {
            System.out.println("Invalid month.");
            return;
        }
        centre.printMonthlyChampionReport(month);
    }

    private static Member promptMember(Centre centre, Scanner in) {
        System.out.print("Member ID (e.g. M-001): ");
        String id = in.nextLine().trim();
        Member m = centre.findMemberById(id);
        if (m == null) {
            System.out.println("Member not found.");
        }
        return m;
    }

    private static void printLessonList(List<Lesson> lessons) {
        System.out.println("Lessons:");
        for (Lesson l : lessons) {
            System.out.printf(
                "  ID %d | %s | %s %-10s %-10s | %d/%d booked | £%.2f%n",
                l.getId(),
                l.getDate(),
                l.getDay(),
                l.getTime(),
                l.getExerciseName(),
                l.getCurrentBookings(),
                l.getCapacity(),
                l.getPrice()
            );
        }
    }

    private static Integer readPositiveInt(Scanner in, String prompt) {
        System.out.print(prompt);
        String s = in.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return null;
        }
    }
}
