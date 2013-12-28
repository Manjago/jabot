package jabot.room;

public interface RoomMessageFormatter {
    Object message(String from, String body);
    Object setSubject(String from, String subject);
    Object kicked(String participant, String actor, String reason);
    Object banned(String participant, String actor, String reason);
    Object nickChanged(String oldNick, String newNick);
    Object joined(String participant);
    Object left(String participant);
    Object voiceGranted(String participant);
    Object voiceRevoked(String participant);
    Object memberGranted(String participant);
    Object memberRevoked(String participant);
    Object moderGranted(String participant);
    Object moderRevoked(String participant);
    Object ownerGranted(String participant);
    Object ownerRevoked(String participant);
    Object adminGranted(String participant);
    Object adminRevoked(String participant);
}
