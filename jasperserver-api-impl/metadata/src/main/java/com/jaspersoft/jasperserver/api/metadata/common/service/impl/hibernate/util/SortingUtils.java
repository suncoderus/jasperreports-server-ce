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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class SortingUtils {
	
	protected static abstract class CollationKeyDecorator implements Comparable {
		private final CollationKey key;
		
		protected CollationKeyDecorator(Collator collator, String value) {
			this.key = collator.getCollationKey(value);
		}

		public int compareTo(Object o) {
			return this.key.compareTo(((CollationKeyDecorator) o).key);
		}
	}
	
	protected static class FolderNameCollationKey extends CollationKeyDecorator {
		protected final Folder folder;
		
		public FolderNameCollationKey(Collator collator, Folder folder) {
			super(collator, folder.getName());
			this.folder = folder;
		}
	}
	
	protected static class FolderURICollationKey extends CollationKeyDecorator {
		protected final Folder folder;
		
		public FolderURICollationKey(Collator collator, Folder folder) {
			super(collator, folder.getURIString());
			this.folder = folder;
		}
	}
	
	protected static class RepoResourceURICollationKey extends CollationKeyDecorator {
		protected final RepoResourceBase resource;
		
		public RepoResourceURICollationKey(Collator collator, RepoResourceBase resource) {
			super(collator, resource.getResourceURI());
			this.resource = resource;
		}
	}

	public static void sortFoldersByName(final Collator collator, final List folders) {
		for(ListIterator it = folders.listIterator(); it.hasNext();) {
			Folder folder = (Folder) it.next();
			it.set(new FolderNameCollationKey(collator, folder));
		}
		
		Collections.sort(folders);
		
		for(ListIterator it = folders.listIterator(); it.hasNext();) {
			FolderNameCollationKey folderKey = (FolderNameCollationKey) it.next();
			it.set(folderKey.folder);
		}
	}

	public static void sortFoldersByURI(final Collator collator, final List folders) {
		for(ListIterator it = folders.listIterator(); it.hasNext();) {
			Folder folder = (Folder) it.next();
			it.set(new FolderURICollationKey(collator, folder));
		}
		
		Collections.sort(folders);
		
		for(ListIterator it = folders.listIterator(); it.hasNext();) {
			FolderURICollationKey folderKey = (FolderURICollationKey) it.next();
			it.set(folderKey.folder);
		}
	}

	public static void sortRepoResourcesByURI(final Collator collator, final List resources) {
		for(ListIterator it = resources.listIterator(); it.hasNext();) {
			Object ojb = it.next();
            if (ojb instanceof RepoResourceBase){
                RepoResourceBase resource = (RepoResourceBase) ojb;
			    it.set(new RepoResourceURICollationKey(collator, resource));
            }
		}
		
		Collections.sort(resources);
		
		for(ListIterator it = resources.listIterator(); it.hasNext();) {
			RepoResourceURICollationKey resourceKey = (RepoResourceURICollationKey) it.next();
			it.set(resourceKey.resource);
		}
	}

}
