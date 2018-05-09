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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import java.util.Date;
import java.io.Serializable;

import com.jaspersoft.jasperserver.api.common.domain.LogEvent;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import org.hibernate.Hibernate;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepoLogEvent implements LogEvent {

	private long id;
	private Date occurrenceDate;
	private byte type;
	private String component;
	private String messageCode;
	private String resourceURI;
	private String text;
	private byte[] data;
	private byte state;
        private transient Blob dataBlob;
    private User user;
	public RepoLogEvent() {
	}

    public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
                if (data == null) {
                   this.dataBlob = null; 
                } else {
                    this.dataBlob = Hibernate.createBlob(data);
                }
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public Date getOccurrenceDate() {
		return occurrenceDate;
	}

	public void setOccurrenceDate(Date occurrenceDate) {
		this.occurrenceDate = occurrenceDate;
	}

	public String getResourceURI() {
		return resourceURI;
	}

	public void setResourceURI(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getState()
	{
		return state;
	}

	public void setState(byte state)
	{
		this.state = state;
	}

        public Blob getDataBlob() {
            return dataBlob;
        }

        public void setDataBlob(Blob dataBlob) {
            this.dataBlob = dataBlob;
            if (dataBlob == null) {
                    this.data = null;
            } else {
                    this.data = toByteArray(dataBlob);
            }
        }

        private byte[] toByteArray(Blob fromBlob) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                return toByteArrayImpl(fromBlob, baos);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }

        private byte[] toByteArrayImpl(Blob fromBlob, ByteArrayOutputStream baos)
                throws SQLException, IOException {
            byte[] buf = new byte[4000];
            InputStream is = fromBlob.getBinaryStream();
            try {
                for (;;) {
                    int dataSize = is.read(buf);

                    if (dataSize == -1) {
                        break;
                    }
                    baos.write(buf, 0, dataSize);
                }
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                    }
                }
            }
            return baos.toByteArray();
        }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LogEvent toClient(ResourceFactory clientMappingFactory) {
        LogEvent logEvent = (LogEvent) clientMappingFactory.newObject(LogEvent.class);
        // this -> logEvent
        logEvent.setId(getId());
        logEvent.setComponent(getComponent());
        logEvent.setData(getData());
        logEvent.setMessageCode(getMessageCode());
        logEvent.setOccurrenceDate(getOccurrenceDate());
        logEvent.setResourceURI(getResourceURI());
        logEvent.setState(getState());
        logEvent.setText(getText());
        logEvent.setType(getType());

        return logEvent;
    }

    public void copyFromClient(LogEvent client) {
        setId(client.getId());
        setComponent(client.getComponent());
        setData(client.getData());
        setMessageCode(client.getMessageCode());
        setOccurrenceDate(client.getOccurrenceDate());
        setResourceURI(client.getResourceURI());
        setState(client.getState());
        setText(client.getText());
        setType(client.getType());
    }
}
