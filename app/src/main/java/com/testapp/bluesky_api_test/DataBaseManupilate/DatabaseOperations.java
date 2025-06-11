package com.testapp.bluesky_api_test.DataBaseManupilate;


import java.sql.Timestamp;
import java.util.List;

public class DatabaseOperations {

    private final AppDatabase db;

    public DatabaseOperations(AppDatabase db) {
        this.db = db;
    }

    public String recordAccessAndGetHistory() {
        AccessTimeDao accessTimeDao = db.accessTimeDao();
        accessTimeDao.insert(new AccessTime(new Timestamp(System.currentTimeMillis()).toString()));

        StringBuilder sb = new StringBuilder();
        sb.append("DBアクセス履歴:\n");
        List<AccessTime> atList = accessTimeDao.getAll();
        for (AccessTime at : atList) {
            sb.append(at.getAccessTime()).append("\n");
        }
        return sb.toString();
    }
}
