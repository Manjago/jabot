import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;

public class Test {
    public static void main(String[] args) throws SQLException {
        JdbcConnectionPool cp = JdbcConnectionPool.create(
                "jdbc:h2:mem:test", "sa", "sa");
        try (Connection conn = cp.getConnection()) {

            try (Statement statement = conn.createStatement()) {
                statement.execute("CREATE TABLE LOGDATA\n" +
                        "(\n" +
                        "    ID IDENTITY PRIMARY KEY NOT NULL,\n" +
                        "    NICK VARCHAR(200) NOT NULL\n" +
                        ");\n");
            }

        }

        try (Connection conn = cp.getConnection()) {
            conn.setAutoCommit(false);


            try (CallableStatement cs = conn.prepareCall("{ ? = call IDENTITY()}")) {
                cs.registerOutParameter(1, Types.BIGINT);

                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO LOGDATA ( NICK )\n" +
                        "VALUES (?)")) {

                    for (int i = 1; i < 3; ++i) {

                        String value = "" + i;


                        ps.setString(1, value);
                        ps.execute();
                        System.out.println("commited value = " + value);

                        cs.execute();
                        long id = cs.getLong(1);
                        System.out.println("id=" + id);
                        conn.commit();


                    }


                }
            }
        }


    }
}
