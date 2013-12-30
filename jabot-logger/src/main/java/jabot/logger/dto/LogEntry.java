package jabot.logger.dto;

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

        if ( conference == null || from == null || entryType == null){
            return false;
        }

        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getEventDate() {
        return eventDate != null ? new Date(eventDate.getTime()) : null;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate != null ? new Date(eventDate.getTime()) : null;
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
                '}';
    }
}
