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
package com.jaspersoft.jasperserver.api.metadata.security;

import org.springframework.security.acls.domain.AbstractPermission;
import org.springframework.security.acls.model.Permission;

/**
 * @author Oleg.Gavavka
 */

public class JasperServerPermission extends AbstractPermission {
    public static final Permission READ = new JasperServerPermission(1 << 1, 'R'); // 2
    public static final Permission WRITE = new JasperServerPermission(1 << 2, 'W'); // 4
    public static final Permission CREATE = new JasperServerPermission(1 << 3, 'C'); // 8
    public static final Permission DELETE = new JasperServerPermission(1 << 4, 'D'); // 16
    public static final Permission ADMINISTRATION = new JasperServerPermission(1 << 0, 'A'); // 1
    public static final Permission EXECUTE = new JasperServerPermission(1 << 5, 'X'); // 32

    public static final Permission NOTHING = new JasperServerPermission(0);

    // Combinations of base permissions we permit
    public static final Permission READ_WRITE_CREATE_DELETE =  new JasperServerPermission(READ.getMask() | WRITE.getMask() | CREATE.getMask() | DELETE.getMask());
    public static final Permission READ_WRITE_CREATE =  new JasperServerPermission(READ.getMask() | WRITE.getMask() | CREATE.getMask());
    public static final Permission READ_WRITE =  new JasperServerPermission( READ.getMask() | WRITE.getMask());
    public static final Permission READ_WRITE_DELETE =  new JasperServerPermission(READ.getMask() | WRITE.getMask() | DELETE.getMask());

    public JasperServerPermission(int mask) {
        super(mask);
    }
    public JasperServerPermission(Permission permission) {
        super(permission.getMask());
    }

    protected JasperServerPermission(int mask, char code) {
        super(mask, code);
    }
}
