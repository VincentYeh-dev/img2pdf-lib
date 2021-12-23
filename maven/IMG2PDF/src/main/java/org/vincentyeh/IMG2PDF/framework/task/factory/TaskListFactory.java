package org.vincentyeh.IMG2PDF.framework.task.factory;

import org.vincentyeh.IMG2PDF.framework.task.factory.exception.TaskBuilderException;
import org.vincentyeh.IMG2PDF.framework.task.factory.exception.TaskListFactoryException;
import org.vincentyeh.IMG2PDF.framework.task.Task;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskListFactory<SOURCE> {
    private final TaskBuilder<SOURCE> builder;

    protected TaskListFactory(TaskBuilder<SOURCE> builder) {
        this.builder = builder;
    }

    protected abstract List<SOURCE> getSourceList() throws TaskListFactoryException;

    public final List<Task> create() throws TaskListFactoryException, TaskBuilderException {
        List<Task> tasks = new ArrayList<>();
        for (SOURCE source : getSourceList()) {
            tasks.add(builder.build(source));
        }
        return tasks;
    }
}
