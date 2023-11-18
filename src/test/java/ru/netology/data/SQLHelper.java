package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {

    private static QueryRunner runner = new QueryRunner();

    private SQLHelper() {

    }

    private static Connection getConn() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    @SneakyThrows
    public static DataHelper.VerificationCode getVerificationCode() {
        var conn = getConn();
        var codeSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";
        var code = runner.query(conn, codeSQL, new ScalarHandler<String>());

        return new DataHelper.VerificationCode(code);
    }

    @SneakyThrows
    public static String getUserStatus() {
        var conn = getConn();
        var statusSQL = "SELECT status FROM users WHERE login = 'vasya'";
        var status = runner.query(conn, statusSQL, new ScalarHandler<String>());

        return status;
    }

    @SneakyThrows
    public static void cleanDatabase() {
        var conn = getConn();

        runner.execute(conn, "DELETE FROM auth_codes");
        runner.execute(conn, "DELETE FROM card_transactions");
        runner.execute(conn, "DELETE FROM cards");
        runner.execute(conn, "DELETE FROM users");
    }
}
