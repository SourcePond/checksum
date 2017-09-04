package ch.sourcepond.io.checksum.impl.tasks;

import ch.sourcepond.io.checksum.api.StreamSource;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.when;

/**
 *
 */
@SuppressWarnings("unchecked")
public class StreamUpdateTaskTest extends UpdateTaskTest<StreamSource> {

    @Override
    protected UpdateTask<StreamSource> newTask() throws IOException {
        when(resource.getSource()).thenReturn(() -> new SimulateReadDelaysInputStream(getClass().getResourceAsStream("/testfile_01.txt")));
        return new StreamUpdateTask(executor, digesterPool, resource, SECONDS, 1L);
    }
}
