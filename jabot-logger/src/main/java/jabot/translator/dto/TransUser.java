package jabot.translator.dto;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class TransUser {
    private long id;
    private String jid;
    private boolean enabled;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransUser{");
        sb.append("id=").append(id);
        sb.append(", jid='").append(jid).append('\'');
        sb.append(", enabled=").append(enabled);
        sb.append('}');
        return sb.toString();
    }
}
