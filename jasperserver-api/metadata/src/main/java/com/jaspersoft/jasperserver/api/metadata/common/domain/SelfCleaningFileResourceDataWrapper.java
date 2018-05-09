/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class SelfCleaningFileResourceDataWrapper extends FileResourceData {
    public SelfCleaningFileResourceDataWrapper(FileResourceData object){
        super(object);
    }

    @Override
    public InputStream getDataStream() {
        return new SelfCleaningStreamWrapper(super.getDataStream());
    }

    private class SelfCleaningStreamWrapper extends InputStream {
        InputStream stream;

        SelfCleaningStreamWrapper (InputStream stream){
            this.stream = stream;
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            int read = stream.read(b);
            if (read == -1) {
                dispose();
            }
            return read;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int read = stream.read(b, off, len);
            if (read == -1) {
                dispose();
            }
            return read;
        }

        @Override
        public long skip(long n) throws IOException {
            return stream.skip(n);
        }

        @Override
        public int available() throws IOException {
            return stream.available();
        }

        @Override
        public void close() throws IOException {
            stream.close();
        }

        @Override
        public void mark(int readlimit) {
            stream.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            stream.reset();
        }

        @Override
        public boolean markSupported() {
            return stream.markSupported();
        }
    }
}
