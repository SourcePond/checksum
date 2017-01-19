/*Copyright (C) 2017 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.checksum.impl.resources;

import ch.sourcepond.io.checksum.api.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import static ch.sourcepond.io.checksum.impl.resources.Observers.INITIAL_CHECKSUM;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ObserversTest {

    public class TestCancelObserver implements CancelObserver<Path> {

        @Override
        public void updateCancelled(final Path pSource) {

        }
    }

    public class TestFailureObserver implements FailureObserver<Path> {

        @Override
        public void updateFailed(final Path pSource, final IOException pFailure) {

        }
    }

    public class TestSuccessObserver implements SuccessObserver<Path> {

        @Override
        public void updateSucceeded(final Path pSource, final Checksum pPrevious, final Checksum pCurrent) {

        }
    }

    private final ExecutorService observerExecutor = newSingleThreadExecutor();
    private final Checksum checksum = mock(Checksum.class);
    private final Path source = mock(Path.class);
    private final ChannelSource accessor = mock(ChannelSource.class);
    private final Observers<Path, ChannelSource> observers = new Observers<>(observerExecutor, source, accessor);
    private final CancelObserver<Path> cancelObserver = mock(CancelObserver.class);
    private final FailureObserver<Path> failureObserver = mock(FailureObserver.class);
    private final SuccessObserver<Path> successObserver = mock(SuccessObserver.class);

    @Test
    public void verifyInitialChecksum() {
        assertArrayEquals(new byte[0], Observers.INITIAL_CHECKSUM.getValue());
        assertEquals("", Observers.INITIAL_CHECKSUM.getHexValue());
    }

    @Test
    public void getSource() {
        assertSame(source, observers.getSource());
    }

    @Test
    public void getAccessor() {
        assertSame(accessor, observers.getAccessor());
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerIfCancelObserverIsNull() {
        observers.addCancelObserver(null);
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerIfFailureObserverIsNull() {
        observers.addFailureObserver(null);
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerIfSuccessObserverIsNull() {
        observers.addSuccessObserver(null);
    }

    @Test
    public void noNullPointerIfNullCancelObserverIsBeingRemoved() {
        observers.removeCancelObserver(null);
    }

    @Test
    public void noNullPointerIfNullFailureObserverIsBeingRemoved() {
        observers.removeFailureObserver(null);
    }

    @Test
    public void noNullPointerIfNullSuccessObserverIsBeingRemoved() {
        observers.removeSuccessObserver(null);
    }

    @Test
    public void verifyCancelObservers() {
        observers.addCancelObserver(cancelObserver);
        observers.informCancelObservers();
        verify(cancelObserver, timeout(100)).updateCancelled(source);
        observers.removeCancelObserver(cancelObserver);
        observers.informCancelObservers();
        verifyNoMoreInteractions(cancelObserver);
    }

    @Test
    public void verifySuccessObservers() {
        observers.addSuccessObserver(successObserver);
        observers.informSuccessObservers(checksum);
        verify(successObserver, timeout(100)).updateSucceeded(source, INITIAL_CHECKSUM, checksum);
        observers.removeSuccessObserver(successObserver);
        observers.informSuccessObservers(checksum);
        verifyNoMoreInteractions(successObserver);
    }

    @Test
    public void verifyUpdateChecksum() {
        observers.addSuccessObserver(successObserver);
        observers.informSuccessObservers(checksum);
        verify(successObserver, timeout(100)).updateSucceeded(source, INITIAL_CHECKSUM, checksum);

        final Checksum updatedChecksum = mock(Checksum.class);
        observers.informSuccessObservers(updatedChecksum);
        verify(successObserver, timeout(100)).updateSucceeded(source, checksum, updatedChecksum);

        final Checksum updatedChecksum2 = mock(Checksum.class);
        observers.informSuccessObservers(updatedChecksum2);
        verify(successObserver, timeout(100)).updateSucceeded(source, updatedChecksum, updatedChecksum2);
    }

    @Test
    public void verifyFailureObservers() {
        final IOException expected = new IOException();
        observers.addFailureObserver(failureObserver);
        observers.informFailureObservers(expected);
        verify(failureObserver, timeout(100)).updateFailed(source, expected);
        observers.removeFailureObserver(failureObserver);
        observers.informFailureObservers(expected);
        verifyNoMoreInteractions(successObserver);
    }
}
