package jabot.room;

import java.util.Date;

public interface RoomMessageFormatter {
    Object message(Date rcvDate, String from, String body, boolean fromMe);
    Object subjectMessageOnStart(Date rcvDate, String from, String body);
    Object delayedMessage(Date rcvDate, String from, String body, boolean fromMe, Date timestamp);
    Object setSubject(Date rcvDate, String from, String subject);
    Object kicked(Date rcvDate, String participant, String actor, String reason);
    Object banned(Date rcvDate, String participant, String actor, String reason);
    Object nickChanged(Date rcvDate, String oldNick, String newNick);
    Object joined(Date rcvDate, String participant);
    Object left(Date rcvDate, String participant);
    Object voiceGranted(Date rcvDate, String participant);
    Object voiceRevoked(Date rcvDate, String participant);
    Object memberGranted(Date rcvDate, String participant);
    Object memberRevoked(Date rcvDate, String participant);
    Object moderGranted(Date rcvDate, String participant);
    Object moderRevoked(Date rcvDate, String participant);
    Object ownerGranted(Date rcvDate, String participant);
    Object ownerRevoked(Date rcvDate, String participant);
    Object adminGranted(Date rcvDate, String participant);
    Object adminRevoked(Date rcvDate, String participant);
}
