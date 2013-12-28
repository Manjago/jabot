package jabot.room;

public interface RoomMessageFormatter {
    String message(String from, String body);
    String setSubject(String from, String subject);
    String kicked(String participant, String actor, String reason);
    String banned(String participant, String actor, String reason);
    String nickChanged(String oldNick, String newNick);
    String joined(String participant);
    String left(String participant);
    String voiceGranted(String participant);
    String voiceRevoked(String participant);
    String memberGranted(String participant);
    String memberRevoked(String participant);
    String moderGranted(String participant);
    String moderRevoked(String participant);
    String ownerGranted(String participant);
    String ownerRevoked(String participant);
    String adminGranted(String participant);
    String adminRevoked(String participant);
}
