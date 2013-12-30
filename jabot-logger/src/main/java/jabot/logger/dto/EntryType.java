package jabot.logger.dto;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public enum EntryType {
    MSG((byte) 0), SUBJECTONSTART((byte)1);

    private final byte msgType;

    EntryType(byte msgType) {
        this.msgType = msgType;
    }

    public byte getMsgType() {
        return msgType;
    }

    public static EntryType fromMsgType(byte v) {
        for (EntryType item : EntryType.values()) {
            if (item.getMsgType() == v) {
                return item;
            }
        }

        return null;
    }


}
