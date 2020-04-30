package com.byted.camp.todolist.beans;
//修改：加入优先级
public enum Priority {
    IMPORTANT(2), ORDINARY(1), BORING(0);

    public final int intValue;

    Priority(int intValue) {
        this.intValue = intValue;
    }

    public static Priority from(int intValue) {
        for (Priority priority : Priority.values()) {
            if (priority.intValue == intValue) {
                return priority;
            }
        }
        return IMPORTANT; // default
    }
}
