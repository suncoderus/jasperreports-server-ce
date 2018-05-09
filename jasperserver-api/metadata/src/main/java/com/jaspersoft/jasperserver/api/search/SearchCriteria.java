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

package com.jaspersoft.jasperserver.api.search;

import org.hibernate.*;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.criterion.*;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.CriteriaImpl;

import java.util.Iterator;

/**
 * Search criteria.
 *
 * @author Stas Chubar
 */
public class SearchCriteria extends DetachedCriteria {

    private final CriteriaImpl impl;
    private final Criteria criteria;

    protected SearchCriteria(String entityName) {
        super(entityName);
        impl = new CriteriaImpl(entityName, null);
        criteria = impl;
    }

    protected SearchCriteria(String entityName, String alias) {
        super(entityName, alias);
        impl = new CriteriaImpl(entityName, alias, null);
        criteria = impl;
    }

    protected SearchCriteria(CriteriaImpl impl, Criteria criteria) {
        super(impl, criteria);
        this.impl = impl;
        this.criteria = criteria;
    }

    /**
     * Get an executable instance of <literal>Criteria</literal>,
     * to actually run the query.
     */
    public Criteria getExecutableCriteria(Session session) {
        impl.setSession((SessionImplementor) session);
        return impl;
    }

    public static SearchCriteria forEntityName(String entityName) {
        return new SearchCriteria(entityName);
    }

    public static SearchCriteria forEntityName(String entityName, String alias) {
        return new SearchCriteria(entityName, alias);
    }

    public static SearchCriteria forClass(Class clazz) {
        return new SearchCriteria(clazz.getName());
    }

    public static SearchCriteria forClass(Class clazz, String alias) {
        return new SearchCriteria(clazz.getName(), alias);
    }

    public SearchCriteria add(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }

    public SearchCriteria addOrder(Order order) {
        criteria.addOrder(order);
        return this;
    }

    public SearchCriteria createAlias(String associationPath, String alias)
            throws HibernateException {
        criteria.createAlias(associationPath, alias);
        return this;
    }

    public SearchCriteria createCriteria(String associationPath, String alias)
            throws HibernateException {
        return new SearchCriteria(impl, criteria.createCriteria(associationPath, alias));
    }

    public SearchCriteria createCriteria(String associationPath)
            throws HibernateException {
        return new SearchCriteria(impl, criteria.createCriteria(associationPath));
    }

    public String getAlias() {
        return criteria.getAlias();
    }

    public SearchCriteria setFetchMode(String associationPath, FetchMode mode)
            throws HibernateException {
        criteria.setFetchMode(associationPath, mode);
        return this;
    }

    public SearchCriteria setProjection(Projection projection) {
        criteria.setProjection(projection);
        return this;
    }

    public SearchCriteria addProjection(Projection projection) {
        Projection oldProjection = ((CriteriaImpl)criteria).getProjection();

        if(projection == null || oldProjection == null) {
            criteria.setProjection(projection);
            return this;
        }

        if(oldProjection instanceof ProjectionList) {
            addProjectionToList(((ProjectionList)oldProjection), projection);
        } else {
            ProjectionList list = Projections.projectionList().add(oldProjection);
            criteria.setProjection(addProjectionToList(list, projection));
        }

        return this;
    }

    private Projection addProjectionToList(ProjectionList projectionList, Projection newProjection) {
        if (newProjection instanceof ProjectionList) {
            ProjectionList newProjectionList = ((ProjectionList) newProjection);

            for (int i = 0; i < newProjectionList.getLength(); i ++) {
                projectionList.add(newProjectionList.getProjection(i));
            }
        } else {
            projectionList.add(newProjection);
        }

        return projectionList;
    }

//    public void removeGrops() {
//        Projection oldProjection = ((CriteriaImpl)criteria).getProjection();
//
//        if (oldProjection instanceof ProjectionList) {
//            ProjectionList oldProjectionList = (ProjectionList) oldProjection;
//            ProjectionList newProjectionList = Projections.projectionList();
//
//            for (int i = 0; i < oldProjectionList.getLength(); i ++) {
//                Projection projection = oldProjectionList.getProjection(i);
//
//                if (!projection.isGrouped()) {
//                    newProjectionList.add(projection);
//                }
//            }
//
//            criteria.setProjection(newProjectionList);
//        } else if (oldProjection instanceof PropertyProjection){
//            if (!oldProjection.isGrouped()) {
//                criteria.setProjection(oldProjection);
//            }
//        }
//    }

    public SearchCriteria setResultTransformer(ResultTransformer resultTransformer) {
        criteria.setResultTransformer(resultTransformer);
        return this;
    }

    public String toString() {
        return "SearchCriteria(" + criteria.toString() + ')';
    }

    CriteriaImpl getCriteriaImpl() {
        return impl;
    }

    public SearchCriteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
        criteria.createAlias(associationPath, alias, joinType);
        return this;
    }

    public SearchCriteria createCriteria(String associationPath, int joinType) throws HibernateException {
        return new SearchCriteria(impl, criteria.createCriteria(associationPath, joinType));
    }

    public SearchCriteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
        return new SearchCriteria(impl, criteria.createCriteria(associationPath, alias, joinType));
    }

    public SearchCriteria setComment(String comment) {
        criteria.setComment(comment);
        return this;
    }

    public SearchCriteria setLockMode(LockMode lockMode) {
        criteria.setLockMode(lockMode);
        return this;
    }

    public SearchCriteria setLockMode(String alias, LockMode lockMode) {
        criteria.setLockMode(alias, lockMode);
        return this;
    }

    public String getAlias(String associationPath, String aliasIfNotExist) {

        Iterator i = impl.iterateSubcriteria();
        while (i.hasNext()) {
            CriteriaImpl.Subcriteria subcriteria = (CriteriaImpl.Subcriteria) i.next();

            if (subcriteria.getPath().equals(associationPath)) {
                return subcriteria.getAlias();
            }
        }

        createAlias(associationPath, aliasIfNotExist);
        return aliasIfNotExist;
    }
}
