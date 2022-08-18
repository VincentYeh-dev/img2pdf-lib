package org.vincentyeh.IMG2PDF.lib.task.framework.factory;

import org.vincentyeh.IMG2PDF.lib.task.framework.Task;
import org.vincentyeh.IMG2PDF.lib.task.framework.factory.exception.TaskFactoryProcessException;

public interface TaskFactory<SOURCE> {

    Task create(SOURCE source) throws TaskFactoryProcessException;
}
