package ch.sourcepond.io.checksum.impl.tasks;

/**
 * Created by rolandhauser on 09.01.17.
 */
public class StreamUpdateTaskTest extends UpdateTaskTest {

    @Override
    protected UpdateTask newTask() {
        return new StreamUpdateTask(resource, new URLStreamSource(getClass().getResource("/testfile_01.txt")), reader);
    }
}
