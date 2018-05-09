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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.visitor;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.visitor.RelationsVisitor;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.activity.ReadUserWorkflowActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.HypermediaRepresentation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.dto.UserWorkflow;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.representation.UserWorkflowCollectionRepresentation;

import java.util.List;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class UserWorkflowCollectionRelationsVisitor extends RelationsVisitor<UserWorkflowCollectionRepresentation> {


    public void resolve(ReadUserWorkflowActivity workflowActivity,  Relation relation, Boolean isLink){

        List<UserWorkflow> userWorkflows = representation.getBody();

        if (!isLink){
            for (UserWorkflow userWorkflow : userWorkflows) {
                workflowActivity.setData(userWorkflow);
                HypermediaRepresentation workflowRepresentation = (HypermediaRepresentation) workflowActivity.proceed();
                representation.addEmbedded(relation, workflowRepresentation);
            }
        }

    }


}
