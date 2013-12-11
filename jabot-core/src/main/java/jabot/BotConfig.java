package jabot;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 */
public class BotConfig {
    private String login;
    private String password;
    private int port;
    private String serviceName;
    private String host;
    private String chatPlugins;
    private String roomsConfig;

    public BotConfig() {
    }

    public BotConfig(BotConfig b) {
        this.login = b.getLogin();
        this.password = b.getPassword();
        this.port = b.getPort();
        this.serviceName = b.getServiceName();
        this.host = b.getHost();
        this.chatPlugins = b.getChatPlugins();
        this.roomsConfig = b.getRoomsConfig();
    }

    public String getRoomsConfig() {
        return roomsConfig;
    }

    public void setRoomsConfig(String roomsConfig) {
        this.roomsConfig = roomsConfig;
    }

    public String getChatPlugins() {
        return chatPlugins;
    }

    public void setChatPlugins(String chatPlugins) {
        this.chatPlugins = chatPlugins;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BotConfig{");
        sb.append("login='").append(login).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", port=").append(port);
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", chatPlugins='").append(chatPlugins).append('\'');
        sb.append(", roomsConfig='").append(roomsConfig).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
