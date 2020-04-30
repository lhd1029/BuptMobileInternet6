package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

import com.byted.camp.todolist.operation.db.FeedReaderContract;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    private TodoContract() {
    }
    public static class ToDoEntry implements BaseColumns {

        public static final String TABLE_NAME = "todoList";

        public static final String COLUMN_NAME_TODONAME = "todoname";

        public static final String COLUMN_NAME_TODOTIME = "todotime";

        public static final String COLUMN_NAME_TODOSTATE = "state";

        public static final String COLUMN_NAME_TODOPRIORITY = "priority";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TodoContract.ToDoEntry.TABLE_NAME + " (" +
                    TodoContract.ToDoEntry._ID + " INTEGER PRIMARY KEY," +
                    TodoContract.ToDoEntry.COLUMN_NAME_TODONAME + " TEXT," +
                    TodoContract.ToDoEntry.COLUMN_NAME_TODOTIME + " TEXT," +
                    ToDoEntry.COLUMN_NAME_TODOPRIORITY + " TEXT," +
                    ToDoEntry.COLUMN_NAME_TODOSTATE + " INT)";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;
}
