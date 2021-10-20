package ru.prumi.server.auth;

import ru.prumi.server.db.SQLHandler;

import java.sql.SQLException;

public class SQLAuthService implements AuthService {
    @Override
    public String getFolderByUsernameAndPassword(String username, String password) {
        return SQLHandler.getFolderByUsernameAndPassword(username, password);
    }

    @Override
    public void insertNewUser(String username, String password, String folder) throws SQLException {
        SQLHandler.insertNewUser(username, password, folder);
    }
}
