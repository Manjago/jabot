package jabot.logger.dto;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public enum EntryType {
    MSG((byte) 0), SUBJECTONSTART((byte) 1),
    DELAYMSG((byte) 2), SUBJECTSET((byte) 3), KICKED((byte) 4),
    BANNED((byte) 5), NICKCHANGED((byte) 6);
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
