package flc;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Five key-behaviour tests (one focal area per core type), per coursework guidance.
 */
public class KeyMethodTests {

    private Centre centre;
    private Member alice;

    @Before
    public void setUp() {
        centre = new Centre();
        alice = centre.findMemberById("M-001");
        assertNotNull(alice);
    }

    /** Centre: duplicate booking for the same member and lesson is rejected. */
    @Test
    public void centreAddBookingRejectsDuplicateSameLesson() {
        Lesson lesson = centre.findLessonById(1);
        assertNotNull(lesson);
        assertNotNull(centre.addBooking(alice, lesson));
        assertNull(centre.addBooking(alice, lesson));
    }

    /** Lesson: cannot exceed capacity when attaching bookings directly. */
    @Test
    public void lessonAddBookingReturnsFalseWhenFull() {
        LocalDate d = LocalDate.of(2026, 7, 1);
        Lesson lesson = new Lesson(9001, "Yoga", "Saturday", "Morning", d, 2, 12.0);
        Member m1 = new Member("T-1", "One");
        Member m2 = new Member("T-2", "Two");
        Member m3 = new Member("T-3", "Three");
        assertTrue(lesson.addBooking(new Booking(m1, lesson)));
        assertTrue(lesson.addBooking(new Booking(m2, lesson)));
        assertFalse(lesson.addBooking(new Booking(m3, lesson)));
        assertEquals(2, lesson.getCurrentBookings());
    }

    /** Booking + Centre: cancel frees a slot; cannot cancel after attend or twice. */
    @Test
    public void centreCancelBookingFreesLessonSlot() {
        Lesson lesson = centre.findLessonById(5);
        assertNotNull(lesson);
        int before = lesson.getCurrentBookings();
        Booking b = centre.addBooking(alice, lesson);
        assertNotNull(b);
        assertEquals(before + 1, lesson.getCurrentBookings());
        assertTrue(centre.cancelBooking(b));
        assertEquals("cancelled", b.getStatus());
        assertEquals(before, lesson.getCurrentBookings());
        assertFalse(centre.cancelBooking(b));

        Lesson other = centre.findLessonById(6);
        assertNotNull(other);
        Booking b2 = centre.addBooking(alice, other);
        assertNotNull(b2);
        centre.attendLesson(b2, 5, "Completed");
        assertFalse(centre.cancelBooking(b2));
        assertEquals("attended", b2.getStatus());
    }

    /** Member: lookup is case-insensitive; unknown id returns null. */
    @Test
    public void memberLookupByIdIsCaseInsensitive() {
        assertNotNull(centre.findMemberById("m-001"));
        assertNull(centre.findMemberById("M-999"));
    }

    /** Rating (via attend flow): review text and score 1–5 are stored. */
    @Test
    public void ratingRecordedWhenAttendingViaCentre() {
        Lesson lesson = centre.findLessonById(10);
        assertNotNull(lesson);
        Booking b = centre.addBooking(alice, lesson);
        assertNotNull(b);
        centre.attendLesson(b, 4, "Great instructor, good pace.");
        assertEquals("attended", b.getStatus());
        assertEquals(4, b.getRating().getValue());
        assertEquals("Great instructor, good pace.", b.getRating().getReview());
    }

}
