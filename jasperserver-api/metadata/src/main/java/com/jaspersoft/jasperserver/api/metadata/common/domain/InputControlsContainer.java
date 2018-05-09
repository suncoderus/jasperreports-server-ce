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

import java.util.List;

/**
 * @author Paul Lysak (pavel.lysak@globallogic.com)
 */
public interface InputControlsContainer extends Resource {
    /**
     * Returns a list of {@link com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference references} to
     * {@link com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl} instances used by this report unit.
     *
     * @return a list of references to the input controls used by this report unit
     */
    String getURI();

    ResourceReference getDataSource();

    List<ResourceReference> getInputControls();

    //TODO more methods from ReportUnit should be moved here

    /**
     * Sets a list of {@link com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference} instances used by this report unit.
     *
     * @param inputControls a list of input controls used by this report unit
     */
    void setInputControls(List<ResourceReference> inputControls);

    /**
     * add an input control to the report unit
     *
     * @param inputControl
     */
    void addInputControl(InputControl inputControl);

    /**
     * add a reference to an input control to the report unit
     *
     * @param inputControlReference
     */
    void addInputControl(ResourceReference inputControlReference);

    /**
     * add an {@link InputControl} to the report unit using its URI
     *
     * @param referenceURI
     */
    void addInputControlReference(String referenceURI);

    /**
     * Remove the input control at the given index.
     *
     * @param index index of the input control to be removed
     * @return removed input control
     */
    ResourceReference removeInputControl(int index);

    /**
     * remove an {@link InputControl} to the report unit using its URI
     *
     * @param referenceURI
     */
    boolean removeInputControlReference(String referenceURI);

    /**
     * remove a local {@link InputControl} resource from the report unit using its name
     *
     * @param name
     * @return removed input control
     */
    InputControl removeInputControlLocal(String name);
}
