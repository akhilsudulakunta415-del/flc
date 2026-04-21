package flc;

public class Member {
    private String memberId;
    private String name;
    private boolean registered;

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
        this.registered = true;
    }

    public Member() {
        this.memberId = "M-000";
        this.name = "Anonymous";
        this.registered = true;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    @Override
    public String toString() {
        return "Member{" + "memberId='" + memberId + '\'' + ", name='" + name + '\'' + ", registered=" + registered + '}';
    }
}
