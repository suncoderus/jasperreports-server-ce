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

package mondrian.olap4j;

import mondrian.olap.Annotation;
import mondrian.olap.OlapElement;
import mondrian.rolap.RolapMemberBase;
import org.olap4j.metadata.Member;

import java.util.Map;

/**
 * Mondrian Olap4j Member helper service. Provides some extra service methods which is not included in olap4j API yet.
 *
 * @author Vasily Spachynsky, vsabadosh
 * @version $Id$
 */
public class MondrianOlap4jMemberHelper {

    private String rolapAnnotationGroupMarker;

    private static MondrianOlap4jMemberHelper instance = null;

    private MondrianOlap4jMemberHelper() {

    }

    public static MondrianOlap4jMemberHelper getInstance() {
        if (instance == null) {
            synchronized (MondrianOlap4jMemberHelper.class) {
                if (instance == null) {
                    instance = new MondrianOlap4jMemberHelper();
                }
            }
        }
        return instance;
    }

    /**
     *  @param member olap member
     *  @return The olap member group name or null.
     */
    public String getMemberGroup(Member member) {
        if (member instanceof MondrianOlap4jMember) {
            OlapElement olapElement = ((MondrianOlap4jMember) member).getOlapElement();

            if (olapElement instanceof RolapMemberBase) {
                Map<String, Annotation> annotations = ((RolapMemberBase) olapElement).getAnnotationMap();
                if (annotations != null && annotations.get(rolapAnnotationGroupMarker) != null) {
                    return annotations.get(rolapAnnotationGroupMarker).getValue().toString();
                }
            }
        }
        return null;
    }

    public void setRolapAnnotationGroupMarker(String rolapAnnotationGroupMarker) {
        this.rolapAnnotationGroupMarker = rolapAnnotationGroupMarker;
    }
}