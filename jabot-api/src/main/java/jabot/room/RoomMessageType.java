package jabot.room;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public enum RoomMessageType {
    MSG,
    DELAYED_MSG,
    SUBJECT,
    JOINED,
    KICKED,
    LEFT,
    VOICE_GRANTED,
    VOICE_REVOKED,
    BANNED,
    MEMBERSHIP_GRANTED,
    MEMBERSHIP_REVOKED,
    MODERATOR_GRANTED,
    MODERATOR_REVOKED,
    OWNERSHIP_GRANTED,
    OWNERSHIP_REVOKED,
    ADMIN_GRANTED,
    ADMIN_REVOKED,
    NICKNAME_CHANGED
}
