package ch.sourcepond.io.checksum.impl;

import static java.nio.file.StandardOpenOption.APPEND;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 */
class TestDataWriter implements Runnable {
	private final Path testFile;
	private final String data;

	public TestDataWriter(final Path pTestFile, final String pData) {
		testFile = pTestFile;
		data = pData;
	}

	@Override
	public void run() {
		try (final BufferedWriter wr = Files.newBufferedWriter(testFile, APPEND)) {
			wr.write(data);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}