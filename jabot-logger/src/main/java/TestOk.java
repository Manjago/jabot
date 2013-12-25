import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class TestOk {
    public static void main(String[] args) throws SQLException {
        JdbcConnectionPool cp = JdbcConnectionPool.create(
                "jdbc:h2:mem:test", "sa", "sa");
        try (Connection conn = cp.getConnection()){
            run(conn);   // first pass
            run(conn);   // second pass
        }
    }

    private static void run(Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{ ? = call IDENTITY()}");
        cs.registerOutParameter(1, Types.BIGINT);
        cs.execute();
        //cs.close(); // !
    }
}
