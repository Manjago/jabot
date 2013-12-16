package jabot;

/**
 * @author Kirill Temnenkov (ktemnenkov@intervale.ru)
 * parse address name@server/resource
 */
public final class Addr3D {
    public static final String SERVER = "@";
    public static final String RESOURCE = "/";
    private final String name;
    private final String server;
    private final String resource;

    private Addr3D(String name, String server, String resource) {
        this.name = name;
        this.server = server;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public String getResource() {
        return resource;
    }

    public String getNameServer() {
        return name + SERVER + server;
    }

    public String getFullName() {
        return name + SERVER + server + RESOURCE + resource;
    }

    public static Addr3D fromRaw(String raw) {
        if (raw == null || !raw.contains(SERVER)) {
            throw new IllegalArgumentException("bad arg " + raw);
        }
        String[] items = raw.split(SERVER + "|" + RESOURCE);


        final int allItems = 3;
        final int userServer = 2;

        switch (items.length) {
            case allItems:
                return new Addr3D(items[0], items[1], items[2]);
            case userServer:
                return new Addr3D(items[0], items[1], "");
            default:
                throw new IllegalArgumentException("bad arg " + raw);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Addr3D{");
        sb.append("name='").append(name).append('\'');
        sb.append(", server='").append(server).append('\'');
        sb.append(", resource='").append(resource).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
