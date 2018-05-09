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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.IlikeEscapeAwareExpression;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.LikeEscapeAwareExpression;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import com.jaspersoft.jasperserver.api.metadata.view.domain.AncestorFolderFilter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.Filter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElement;
import com.jaspersoft.jasperserver.api.metadata.view.domain.ParentFolderFilter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.PropertyFilter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.ReferenceFilter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.URIFilter;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class HibernateFilter implements Filter {

	public final Junction junction;
	private final ReferenceResolver referenceResolver;
	
	private Criterion criterion;
	
	public HibernateFilter(Junction junction, ReferenceResolver referenceResolver) {
		this.junction = junction;
		this.referenceResolver = referenceResolver;
	}

	protected final Criterion last() {
		return criterion;
	}
	
	protected void add(Criterion newCriterion) {
		this.criterion = newCriterion;
		if (junction != null) {
			junction.add(newCriterion);
		}
	}
	
	public void applyParentFolderFilter(ParentFolderFilter filter) {
		add(parentFolderRestriction(filter.getFolderURI()));
	}

	protected SimpleExpression parentFolderRestriction(String folderURI) {
		return Restrictions.eq("parent.URI", folderURI);
	}

	public void applyPropertyFilter(PropertyFilter filter) {
		Criterion propCriterion;
		switch (filter.getOp()) {
			case PropertyFilter.EQ:
				propCriterion = Restrictions.eq(filter.getProperty(), filter.getValue());
				break;
			case PropertyFilter.LIKE:
				if (filter.getEscapeCharacter() == null) {
					if (filter.getMatchMode() == null) {
						propCriterion = Restrictions.like(filter.getProperty(), filter.getValue());
					} else {
						propCriterion = Restrictions.like(filter.getProperty(), (String) filter.getValue(), filter.getMatchMode());
					}
				} else {
					if (filter.getMatchMode() == null) {
						propCriterion = new LikeEscapeAwareExpression(
								filter.getProperty(), (String) filter.getValue(), filter.getEscapeCharacter());
					} else {
						propCriterion = new LikeEscapeAwareExpression(filter.getProperty(),
								(String) filter.getValue(), filter.getMatchMode(), filter.getEscapeCharacter());
					}
				}
				break;
			case PropertyFilter.GT:
				propCriterion = Restrictions.gt(filter.getProperty(), filter.getValue());
				break;
			case PropertyFilter.LT:
				propCriterion = Restrictions.lt(filter.getProperty(), filter.getValue());
				break;
			case PropertyFilter.BETWEEN:
				propCriterion = Restrictions.between(filter.getProperty(), filter.getLowValue(), filter.getHighValue());
				break;
			case PropertyFilter.IN:
				propCriterion = Restrictions.in(filter.getProperty(), filter.getValues());
				break;
			case PropertyFilter.IN_IGNORE_CASE:
				propCriterion = createInIgnoreCaseCriterion(filter.getProperty(), filter.getValues());
				break;
			case PropertyFilter.LIKE_IGNORE_CASE:
				if (filter.getEscapeCharacter() == null) {
					if (filter.getMatchMode() == null) {
						propCriterion = Restrictions.ilike(filter.getProperty(), filter.getValue());
					} else {
						propCriterion = Restrictions.ilike(filter.getProperty(), (String) filter.getValue(), filter.getMatchMode());
					}
				} else {
					if (filter.getMatchMode() == null) {
						propCriterion = new IlikeEscapeAwareExpression(
								filter.getProperty(), (String) filter.getValue(), filter.getEscapeCharacter());
					} else {
						propCriterion = new IlikeEscapeAwareExpression(filter.getProperty(),
								(String) filter.getValue(), filter.getMatchMode(), filter.getEscapeCharacter());
					}
				}
				break;
			default:
				throw new JSException("jsexception.hibernate.unknown.property.filter.operation", new Object[]{new Byte(filter.getOp())});
		}
		add(propCriterion);
	}

	public void applyNegatedFilter(FilterElement element) {
		HibernateFilter notFilter = new HibernateFilter(null, referenceResolver);
		element.apply(notFilter);
		add(Restrictions.not(notFilter.last()));
	}

	public void applyConjunction(List filterElements) {
		Conjunction conjunction = Restrictions.conjunction();
		applyJunction(filterElements, conjunction);
	}

	public void applyDisjunction(List filterElements) {
		Disjunction disjunction = Restrictions.disjunction();
		applyJunction(filterElements, disjunction);
	}

	public void applyOr(FilterElement lhs, FilterElement rhs) {
		HibernateFilter lhsFilter = new HibernateFilter(null, referenceResolver);
		lhs.apply(lhsFilter);
		HibernateFilter rhsFilter = new HibernateFilter(null, referenceResolver);
		rhs.apply(rhsFilter);
		LogicalExpression or = Restrictions.or(lhsFilter.last(), rhsFilter.last());
		add(or);
	}

	protected void applyJunction(List filterElements, Junction subJunction) {
		HibernateFilter junctionFilter = new HibernateFilter(subJunction, referenceResolver);
		for (Iterator it = filterElements.iterator(); it.hasNext();) {
			FilterElement filterElement = (FilterElement) it.next();
			filterElement.apply(junctionFilter);
		}
		add(subJunction);
	}

	public void applyReferenceFilter(ReferenceFilter filter) {
		RepoResource persistentRef = referenceResolver.getPersistentReference(filter.getReferredURI(), filter.getReferenceClass());
		Criterion propCriterion = Restrictions.eq(filter.getProperty(), persistentRef);
		add(propCriterion);
	}

	public void applyURIFilter(URIFilter filter) {
		String uri = filter.getURI();
		String folder = RepositoryUtils.getParentPath(uri);
		if (folder == null) {
			throw new JSException("Invalid resource URI " + uri);
		}
		
		String name = RepositoryUtils.getName(uri);
		add(Restrictions.conjunction()
				.add(parentFolderRestriction(folder))
				.add(Restrictions.eq("name", name)));
	}

	public void applyAncestorFolderFilter(AncestorFolderFilter filter) {
		Criterion ancestorCriterion = ancestorFolderRestriction(filter.getFolderURI());
		if (ancestorCriterion != null) {
			add(ancestorCriterion);
		}
	}

	protected Criterion ancestorFolderRestriction(String folderURI) {
		if (Folder.SEPARATOR.equals(folderURI)) {
			return null;
		}
		
		return Restrictions.or(
                folderURI.indexOf("%") == -1 ?
                    Restrictions.eq("parent.URI", folderURI) :
                    Restrictions.like("parent.URI", folderURI),
				Restrictions.like("parent.URI", folderURI + "/%")
				);
	}

	private Criterion createInIgnoreCaseCriterion(String property, Object[] objValues) {
		String valueParam = StringHelper.repeat( "?, ", objValues.length-1 )  + "?";
		String[] values = new String[objValues.length];
		Type[] types = new Type[objValues.length];
		Arrays.fill(types, Hibernate.STRING);
		for (int i = 0; i < objValues.length; i++) {
			values[i] = ((String) objValues[i]).toUpperCase();
		}

		return Restrictions.sqlRestriction("upper({alias}." + property + ") in (" + valueParam + ")", values, types);
	}
}
