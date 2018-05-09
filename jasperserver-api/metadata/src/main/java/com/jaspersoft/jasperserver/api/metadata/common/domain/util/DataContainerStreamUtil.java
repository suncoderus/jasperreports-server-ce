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
package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.Arrays;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DataContainerStreamUtil {
    private static final Log log = LogFactory.getLog(DataContainerStreamUtil.class);
    private static final int READ_STREAM_BUFFER_SIZE = 10000;

    public static byte[] readData(InputStream is) {
        if (is == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] bytes = new byte[READ_STREAM_BUFFER_SIZE];
        int ln = 0;
        try {
            while ((ln = is.read(bytes)) > 0) {
                baos.write(bytes, 0, ln);
            }
        } catch (IOException e) {
            log.error("Error while reading data.", e);
            throw new JSExceptionWrapper(e);
        }

        return baos.toByteArray();
    }

    public static byte[] readDataAndClose(InputStream is) {
    	try {
    		return readData(is);
    	} finally {
    		close(is);
    	}
    }

    public static byte[] readData(InputStream is, int size) {
        if (is == null) {
            return null;
        }

        try {
            byte[] data = new byte[size];
            int offset = 0;
            while (size > 0) {
                int read = is.read(data, offset, size);
                if (read < 0) {
                    throw new JSException("jsexception.input.stream.exhausted", new Object[]{new Integer(size)});
                }
                offset += read;
                size -= read;
            }
            return data;
        } catch (IOException e) {
            log.error("Error while reading data.", e);
            throw new JSExceptionWrapper(e);
        }
    }

    public static byte[] readData(Blob blob) {
        if (blob == null) {
            return null;
        }
        // this is ComparableBlob--we can get the bytes
        if (blob instanceof ComparableBlob) {
        	return ((ComparableBlob) blob).getBytes();
        }

        try {
            return readData(blob.getBinaryStream());
        } catch (SQLException e) {
            log.error("Error while reading blob data", e);
            throw new JSExceptionWrapper(e);
        }
    }


    public static void pipeData(InputStream is, OutputStream os) throws IOException {
        if (is == null) {
            return;
        }

        byte[] bytes = new byte[READ_STREAM_BUFFER_SIZE];
        int ln = 0;
        while ((ln = is.read(bytes)) > 0) {
            os.write(bytes, 0, ln);
        }
    }

    public static void pipeDataAndCloseInput(InputStream is, OutputStream os) throws IOException {
    	try {
    		pipeData(is, os);
    	} finally {
    		close(is);
    	}
    }

	protected static void close(InputStream is) {
		try {
			is.close();
		} catch (IOException e) {
			log.warn("Failed to close input stream " + is, e);
		}
	}

    public static void writeObjectByteData(ObjectOutputStream objectStream,
                                           InputStream data) throws IOException {
        pipeData(data, objectStream);
    }

    public static void readObjectByteData(ObjectInputStream objectStream,
                                          int size, OutputStream outStream) throws IOException {
        byte[] buffer = new byte[READ_STREAM_BUFFER_SIZE];

        while (size > 0) {
            int read = buffer.length;
            if (read > size)
            {
                read = size;
            }
            objectStream.readFully(buffer, 0, read);

            outStream.write(buffer, 0, read);

            size -= read;
        }
    }


    public static void pipeData(InputStream is, DataContainer dataContainer) {
        boolean close = true;
        OutputStream out = dataContainer.getOutputStream();
        try {
            pipeData(is, out);

            close = false;
            out.close();
        } catch (IOException e) {
            throw new JSExceptionWrapper(e);
        } finally {
            if (close) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("Error closing stream", e);
                }
            }
        }
    }
    
    public static DataContainer pipeGzipSniffedData(InputStream is, DataContainer dataContainer) {
    	GzipSniffInputStream gzipSniffInput = new GzipSniffInputStream(is);
    	pipeData(gzipSniffInput, dataContainer);
    	
    	if (gzipSniffInput.isAutoGzip()) {
    		// if the input was gzip, find out the uncompressed length and create a
    		// data container that transparently inflates the data
    		int uncompressedLength = gzipSniffInput.gzipUncompressedLength();
    		return new GzipDataContainer(dataContainer, uncompressedLength);
    	}
    	
    	return dataContainer;
    }

    protected static class GzipSniffInputStream extends FilterInputStream {

    	private static final int LENGTH_TRAILER_SIZE = 4;
    	
    	// store the first 2 bytes read
    	private int headerOffset = 0;
    	private final byte[] header = new byte[GZIP_HEADER.length];
    	
    	// store the last 4 bytes read as a circular buffer
    	private int trailerOffset = 0;
    	private final byte[] trailer = new byte[LENGTH_TRAILER_SIZE];
    	
		public GzipSniffInputStream(InputStream in) {
			super(in);
		}

		private void bufferHeader(byte b) {
			if (headerOffset < header.length) {
				header[headerOffset] = b;
				++headerOffset;
			}
		}

		private void bufferHeader(byte[] b, int off, int read) {
			if (headerOffset < header.length) {
				int count = Math.min(header.length - headerOffset, read);
				System.arraycopy(b, off, header, headerOffset, count);
				headerOffset += count;
			}
		}
		
		private void bufferTrailer(byte b) {
			trailer[trailerOffset] = b;
			++trailerOffset;
			if (trailerOffset == LENGTH_TRAILER_SIZE) {
				trailerOffset = 0;
			}
		}
		
		private void bufferTrailer(byte[] b, int off, int read) {
			int lastCount = Math.min(read, LENGTH_TRAILER_SIZE);
			for (int i = 0; i < lastCount; ++i) {
				// adding one by one, which is not the fastest way
				bufferTrailer(b[off + read - lastCount + i]);
			}
		}
		
		@Override
		public int read() throws IOException {
			int read = super.read();
			if (read >= 0) {
				byte b = (byte) (read & 0xff);
				bufferHeader(b);
				bufferTrailer(b);
			}
			return read;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int read = super.read(b, off, len);
			if (read > 0) {
				bufferHeader(b, off, read);
				bufferTrailer(b, off, read);
			}
			return read;
		}

		public boolean isAutoGzip() {
			if (headerOffset < GZIP_HEADER.length) {
				return false;
			}
			
			// check if the header bytes match our gzip header
			boolean isGzip = Arrays.areEqual(header, GZIP_HEADER);
			
			if (isGzip && log.isDebugEnabled()) {
				log.debug("detected gzip stream");
			}
			
			return isGzip;
		}
		
		public int gzipUncompressedLength() {
			// the length is the last 4 bytes in little endian order
			// FIXME this limits to 2^32 bytes, we sould have that check in the output stream
			int length = (trailer[trailerOffset] & 0xff)
					| ((trailer[(trailerOffset + 1) % LENGTH_TRAILER_SIZE] & 0xff) << 8)
					| ((trailer[(trailerOffset + 2) % LENGTH_TRAILER_SIZE] & 0xff)  << 16)
					| ((trailer[(trailerOffset + 3) % LENGTH_TRAILER_SIZE] & 0xff)  << 24);
			
			if (log.isDebugEnabled()) {
				log.debug("gzip uncompressed length " + length);
			}
			
			return length;
		}
    }
    
    public static DataContainer createCompressedContainer(DataContainer container) {
    	return new GzipDataContainer(container);
    }
    
    public static DataContainer getRawDataContainer(DataContainer container) {
		// ugly cast
		if (container instanceof GzipDataContainer) {
			// return the compressed data
			return ((GzipDataContainer) container).getDecorated();
		}
		
		return container;
    }
    
	private static final byte[] GZIP_HEADER = new byte[] {
		(byte) GZIPInputStream.GZIP_MAGIC,
		(byte) (GZIPInputStream.GZIP_MAGIC >> 8),
		Deflater.DEFLATED, // Compression method (CM)
		16, // using FCOMMENT
		0,
		0,
		0,
		0,
		0,
		0,
		'j', // the comment
		'r',
		's',
		'a',
		0,
	};
    
    // directly extending DeflaterOutputStream because we are writing a custom gzip header
	protected static class CompressedOutputStream extends DeflaterOutputStream {
		private CRC32 crc;
	
		public CompressedOutputStream(OutputStream out) throws IOException {
			// using default compression
			super(out, new Deflater(Deflater.DEFAULT_COMPRESSION, true));

			crc = new CRC32();
			crc.reset();
			
			// write the header
			out.write(GZIP_HEADER);
		}

		@Override
		public synchronized void write(byte[] buf, int off, int len)
				throws IOException {
			super.write(buf, off, len);
			
    		crc.update(buf, off, len);
		}

		@Override
		public void finish() throws IOException {
			// deflate finish
			super.finish();
			
			// write the trailer
			int crcValue = (int) crc.getValue();
			int size = getUncompressedSize();
			
			if (log.isDebugEnabled()) {
				log.debug("writing gzip trailer for crc " + crcValue + ", length " + size);
			}
			
			byte[] trailer = new byte[] {
					(byte) (crcValue & 0xff),
					(byte) ((crcValue >> 8) & 0xff),
					(byte) ((crcValue >> 16) & 0xff),
					(byte) ((crcValue >> 24) & 0xff),
					(byte) (size & 0xff),
					(byte) ((size >> 8) & 0xff),
					(byte) ((size >> 16) & 0xff),
					(byte) ((size >> 24) & 0xff),
			};
			out.write(trailer);
		}

		protected int getUncompressedSize() {
			return def.getTotalIn();
		}
	}
	
	// Methods to create a blob that can be compared successfully for equality as long as the byte arrays haven't been touched.
	
	public static Blob createComparableBlob(Blob blob) {
		return createComparableBlob(DataContainerStreamUtil.readData(blob));
	}
	
	public static Blob createComparableBlob(InputStream stream) {
		return createComparableBlob(DataContainerStreamUtil.readData(stream));
	}
	
	public static Blob createComparableBlob(byte[] data) {
		return new ComparableBlob(data);
	}
}
