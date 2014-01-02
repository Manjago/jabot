package jabot.logger.dto;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public enum EntryType {
    MSG((byte) 0), SUBJECTONSTART((byte) 1),
    DELAYMSG((byte) 2), SUBJECTSET((byte) 3), KICKED((byte) 4),
    BANNED((byte) 5), NICKCHANGED((byte) 6), JOINED((byte) 7),
    LEFT((byte) 8),
    VOICE_GRANTED((byte) 9), VOICE_REVOKED((byte) 10),
    MEMBER_GRANTED((byte) 11), MEMBER_REVOKED((byte) 12),
    OWNER_GRANTED((byte) 13), OWNER_REVOKED((byte) 14),
    ADMIN_GRANTED((byte) 15), ADMIN_REVOKED((byte) 16),
    MODER_GRANTED((byte) 17), MODER_REVOKED((byte) 18);
    private final byte msgType;

    EntryType(byte msgType) {
        this.msgType = msgType;
    }

    public static EntryType fromMsgType(byte v) {
        for (EntryType item : EntryType.values()) {
            if (item.getMsgType() == v) {
                return item;
            }
        }

        return null;
    }

    public byte getMsgType() {
        return msgType;
    }


}
