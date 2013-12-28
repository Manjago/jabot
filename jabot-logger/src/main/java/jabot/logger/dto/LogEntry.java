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

    public boolean isValid(){
        return eventDate != null && text != null && conference != null && from != null;
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
        final StringBuilder sb = new StringBuilder("LogEntry{");
        sb.append("id=").append(id);
        sb.append(", eventDate=").append(eventDate);
        sb.append(", text='").append(text).append('\'');
        sb.append(", conference='").append(conference).append('\'');
        sb.append(", from='").append(from).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
