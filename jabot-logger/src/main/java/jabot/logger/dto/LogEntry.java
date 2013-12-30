package jabot.logger.dto;

import jabot.Helper;

import java.util.Date;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class LogEntry {
    private long id;
    private Date eventDate;
    private String text;
    private String conference;
    private String from;
    private boolean fromMe;
    private EntryType entryType;
    private Date delayDate;

    public Date getDelayDate() {
        return Helper.safeDate(delayDate);
    }

    public void setDelayDate(Date delayDate) {
        this.delayDate = Helper.safeDate(delayDate);
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public boolean isValid() {

        if (eventDate == null || text == null) {
            return false;
        }

        if (conference == null || from == null || entryType == null) {
            return false;
        }

        if (failCheckDelayedPart()) {
            return false;
        }

        return true;
    }

    private boolean failCheckDelayedPart() {
        if (EntryType.DELAYMSG.equals(entryType) && delayDate == null) {
            return true;
        }

        if (!EntryType.DELAYMSG.equals(entryType) && delayDate != null) {
            return true;
        }
        return false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getEventDate() {
        return Helper.safeDate(eventDate);
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = Helper.safeDate(eventDate);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", eventDate=" + eventDate +
                ", text='" + text + '\'' +
                ", conference='" + conference + '\'' +
                ", from='" + from + '\'' +
                ", fromMe=" + fromMe +
                ", entryType=" + entryType +
                ", delayDate=" + delayDate +
                '}';
    }
}
