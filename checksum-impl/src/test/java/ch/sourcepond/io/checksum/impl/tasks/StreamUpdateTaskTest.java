package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.resources.URLStreamSource;

import static org.mockito.Mockito.when;

/**
 *
 */
@SuppressWarnings("unchecked")
public class StreamUpdateTaskTest extends UpdateTaskTest<StreamSource> {

    @Override
    protected UpdateTask<StreamSource> newTask() {
        when(resource.getSource()).thenReturn(new URLStreamSource(getClass().getResource("/testfile_01.txt")));
        return new StreamUpdateTask(digesterPool, observer, resource, reader);
    }
}
