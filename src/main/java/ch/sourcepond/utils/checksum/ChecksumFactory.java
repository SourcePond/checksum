/*Copyright (C) 2015 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.utils.checksum;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * @author rolandhauser
 *
 */
public interface ChecksumFactory {

	/**
	 * @param pInputStream
	 * @return
	 * @throws IOException
	 */
	Checksum create(InputStream pInputStream, String pAlgorithm) throws IOException;

	/**
	 * @param pPath
	 * @param pAlgorithm
	 * @return
	 * @throws IOException
	 */
	PathChecksum create(Path pPath, String pAlgorithm) throws IOException;

	/**
	 * @param pPath
	 * @return
	 */
	PathChecksum create(ExecutorService pCalculator, Path pPath, String pAlgorithm);
}
