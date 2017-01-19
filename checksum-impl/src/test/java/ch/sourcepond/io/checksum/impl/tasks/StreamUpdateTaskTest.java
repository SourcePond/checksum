package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.StreamSource;
import ch.sourcepond.io.checksum.impl.resources.URLStreamSource;

import java.net.URL;

import static org.mockito.Mockito.when;

/**
 *
 */
@SuppressWarnings("unchecked")
public class StreamUpdateTaskTest extends UpdateTaskTest<URL, StreamSource> {

    @Override
    protected UpdateTask<URL, StreamSource> newTask() {
        when(resource.getAccessor()).thenReturn(new URLStreamSource(getClass().getResource("/testfile_01.txt")));
        return new StreamUpdateTask(digesterPool, resource, reader);
    }
}
