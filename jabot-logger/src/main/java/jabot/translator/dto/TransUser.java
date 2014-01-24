package jabot.translator.dto;

import java.text.MessageFormat;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return equalsConcrete((TransUser) o);
    }

    private boolean equalsConcrete(TransUser user) {

        if (enabled != user.enabled) {
            return false;
        }
        if (id != user.id) {
            return false;
        }
        if (jid != null ? !jid.equals(user.jid) : user.jid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int shift = 32;
        int result = (int) (id ^ (id >>> shift));
        final int prime = 31;
        result = prime * result + (jid != null ? jid.hashCode() : 0);
        result = prime * result + (enabled ? 1 : 0);
        return result;
    }

    public String displayString() {
        return MessageFormat.format("{0}:{1}", jid, enabled);
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
