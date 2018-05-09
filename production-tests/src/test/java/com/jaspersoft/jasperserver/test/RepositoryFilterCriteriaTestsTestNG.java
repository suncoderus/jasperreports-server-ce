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

package com.jaspersoft.jasperserver.test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesImpl;
import com.jaspersoft.jasperserver.api.metadata.common.util.RepositoryLabelIDHelper;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElementDisjunction;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.LikeEscapeAwareExpression.escape;
import static com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria.createFilter;
import static com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria.createPropertyEqualsFilter;
import static com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria.createPropertyInFilter;
import static com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria.createPropertyLikeFilter;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isEqualCollection;
import static org.hibernate.criterion.MatchMode.ANYWHERE;
import static org.hibernate.criterion.MatchMode.END;
import static org.hibernate.criterion.MatchMode.START;
import static org.testng.Assert.fail;

/**
 * Testing for
 *      FilterCriteria.createPropertyLikeFilter(...) methods
 *      FilterCriteria.createPropertyInFilter(...) methods
 *
 * @author askorodumov
 * @version $Id$
 */
public class RepositoryFilterCriteriaTestsTestNG extends BaseRepositoryTestTestNG {
    private static final String LABEL = "label";

    private Folder folder;
    private Folder i18nFolder;

    @BeforeClass
    public void init() {
        createTempFolder();
        createTestResources();
    }

    @AfterClass
    public void dispose() {
        deleteTempFolder();
    }

    @Test
    public void likeFilter_withMatcherStart_findExpected() throws Exception {
        System.out.println("RepositoryFilterCriteriaTestsTestNG.likeFilter_withMatcherStart_findExpected");

        // starts with 'Arl'
        String label = "Arl";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, START));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Arl", "Arl!e", "Arlen", "Arlie"));
    }

    @Test
    public void likeFilter_withMatcherStartI18n_findExpected() throws Exception {
        System.out.println("RepositoryFilterCriteriaTestsTestNG.likeFilter_withMatcherStartI18n_findExpected");

        // starts with '辣椒辛'
        String label = "\u8fa3\u6912\u8f9b"; // 辣椒辛
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, START));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList(
                        "\u8fa3\u6912\u8f9b\u8fa3\uff0c\u82e6\u6f80\u7684\u9999\u6c23",
                        "\u8fa3\u6912\u8f9b\u8fa3\uff0c\u751c\u751c\u7684\u5473\u9053"));
    }

    @Test
    public void likeFilter_withMatcherEnd_findExpected() throws Exception {

        // ends with 'Do'
        String label = "Do";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, END));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("aR%Do", "aRMaNDo", "aRN_LDo", "aRNoLDo"));
    }

    @Test
    public void likeFilter_withMatcherEndI18n_findExpected() throws Exception {

        // ends with '貓頭鷹'
        String label = "\u8c93\u982d\u9df9";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, END));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u623f\u5b50\u8c93\u982d\u9df9", "\u68da\u5c4b\u8c93\u982d\u9df9"));
    }

    @Test
    public void likeFilter_withMatcherIgnoreCaseTrue_findExpected() throws Exception {

        // starts with 'Arl', ignoreCase = true
        String label = "Arl";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, START, true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result),
                asList("Arl", "aRL", "arl", "Arl!e", "aRL!e", "Arlen", "aRLeN", "Arlie", "aRLie", "arlen"));
    }

    @Test
    public void likeFilter_withMatcherIgnoreCaseTrueI18n_findExpected() throws Exception {

        // starts with '辣椒辛辣', ignoreCase = true
        String label = "\u8fa3\u6912\u8f9b\u8fa3";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, START, true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList(
                        "\u8fa3\u6912\u8f9b\u8fa3\uff0c\u751c\u751c\u7684\u5473\u9053",
                        "\u8fa3\u6912\u8f9b\u8fa3\uff0c\u82e6\u6f80\u7684\u9999\u6c23"));
    }

    @Test
    public void likeFilter_withMatcherIgnoreCaseFalse_findExpected() throws Exception {

        // starts with 'Arl', ignoreCase = false
        String label = "Arl";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, START, false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Arl", "Arl!e", "Arlen", "Arlie"));
    }

    @Test
    public void likeFilter_withMatcherIgnoreCaseFalseI18n_findExpected() throws Exception {

        // starts with '辣椒辛辣', ignoreCase = false
        String label = "\u8fa3\u6912\u8f9b\u8fa3";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, START, false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList(
                        "\u8fa3\u6912\u8f9b\u8fa3\uff0c\u82e6\u6f80\u7684\u9999\u6c23",
                        "\u8fa3\u6912\u8f9b\u8fa3\uff0c\u751c\u751c\u7684\u5473\u9053"));
    }

    @Test
    public void likeFilter_withMatcherEscaping_findExpected() throws Exception {

        // starts with 'Arn_', escapeChar = '!'
        String label = "Arn_";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!'), START, '!'));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Arn_ld", "Arn_ldo", "Arn_lfo"));
    }

    @Test
    public void likeFilter_withMatcherEscapingI18n_findExpected() throws Exception {

        // starts with '藍光_', escapeChar = '!'
        String label = "\u85cd\u5149_";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!'), START, '!'));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u85cd\u5149_1", "\u85cd\u5149_2"));
    }

    @Test
    public void likeFilter_withMatcherEscapingIgnoreCaseTrue_findExpected() throws Exception {

        // starts with 'Ar[', escapeChar = '!', ignoreCase = true
        String label = "Ar[";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!'), START, '!', true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("ar[o]n", "Ar[o]n", "aR[o]N", "Ar[ro]n", "aR[ro]N"));
    }

    @Test
    public void likeFilter_withMatcherEscapingIgnoreCaseTrueI18n_findExpected() throws Exception {

        // contains '_的紅色光', escapeChar = '!', ignoreCase = true
        String label = "_\u7684\u7d05\u8272\u5149";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!'), ANYWHERE, '!', true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u8000\u773c_\u7684\u7d05\u8272\u5149\u8292", "\u9bae\u8c54_\u7684\u7d05\u8272\u5149\u675f"));
    }

    @Test
    public void likeFilter_withMatcherEscapingIgnoreCaseFalse_findExpected() throws Exception {

        // starts with 'Ar[', escapeChar = '!', ignoreCase = false
        String label = "Ar[";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!'), START, '!', false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Ar[o]n", "Ar[ro]n"));
    }

    @Test
    public void likeFilter_withMatcherEscapingIgnoreCaseFalseI18n_findExpected() throws Exception {

        // starts with '藍光_', escapeChar = '!', ignoreCase = false
        String label = "\u85cd\u5149_";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!'), START, '!', false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u85cd\u5149_1", "\u85cd\u5149_2"));
    }

    @Test
    public void likeFilter_withEscaping_findExpected() throws Exception {

        // starts with 'Ar!!', escapeChar = '!'
        String label = "Ar!!";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!') + "%", '!'));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Ar!!e", "Ar!!n"));
    }

    @Test
    public void likeFilter_withEscapingI18n_findExpected() throws Exception {

        // starts with '龙虾尾!!', escapeChar = '!'
        String label = "\u9f99\u867e\u5c3e!!";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!') + "%", '!'));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u9f99\u867e\u5c3e!!\u7096", "\u9f99\u867e\u5c3e!!\u814c\u5236"));
    }

    @Test
    public void likeFilter_withEscapingIgnoreCaseTrue_findExpected() throws Exception {

        // contains '%D', escapeChar = '!', ignoreCase = true
        String label = "%D";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, "%" + escape(label, '!') + "%", '!', true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Ar%d", "aR%D", "Ar%do", "aR%Do"));
    }

    @Test
    public void likeFilter_withEscapingIgnoreCaseTrueI18n_findExpected() throws Exception {

        // contains '菇%', escapeChar = '!', ignoreCase = true
        String label = "\u83c7%";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, "%" + escape(label, '!') + "%", '!', true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u8611\u83c7%\u9762", "\u8611\u83c7%\u9762\u6b22\u95f9\u7684"));
    }

    @Test
    public void likeFilter_withEscapingIgnoreCaseFalse_findExpected() throws Exception {

        // starts with 'Ar%', escapeChar = '!', ignoreCase = false
        String label = "Ar%";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!') + "%", '!', false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Ar%d", "Ar%do"));
    }

    @Test
    public void likeFilter_withEscapingIgnoreCaseFalseI18n_findExpected() throws Exception {

        // starts with '蘑菇%', escapeChar = '!', ignoreCase = false
        String label = "\u8611\u83c7%";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!') + "%", '!', false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u8611\u83c7%\u9762", "\u8611\u83c7%\u9762\u6b22\u95f9\u7684"));
    }

    @Test
    public void likeFilter_withIgnoreCaseTrue_findExpected() throws Exception {

        // exacts 'Ar[o]n', ignoreCase = true
        String label = "Ar[o]n";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("ar[o]n", "Ar[o]n", "aR[o]N"));
    }

    @Test
    public void likeFilter_withIgnoreCaseTrueI18n_findExpected() throws Exception {

        // exacts '藍光快', ignoreCase = true
        String label = "\u85cd\u5149\u5feb";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label, true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                singletonList("\u85cd\u5149\u5feb"));
    }

    @Test
    public void likeFilter_withIgnoreCaseFalse_findExpected() throws Exception {

        // ends with 'LD', ignoreCase = false
        String label = "LD";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, "%" + label, false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("aRN_LD", "aRNoLD"));
    }

    @Test
    public void likeFilter_withIgnoreCaseFalseI18n_findExpected() throws Exception {

        // ends with '色光芒', ignoreCase = false
        String label = "\u8272\u5149\u8292";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, "%" + label, false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u8000\u773c\u7684\u7d05\u8272\u5149\u8292", "\u8000\u773c_\u7684\u7d05\u8272\u5149\u8292"));
    }

    @Test
    public void likeFilter_caseSensitive_findExpected() throws Exception {

        // starts with 'Arman'
        String label = "Arman";
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label + "%"));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Armand", "Armando"));
    }

    @Test
    public void likeFilter_caseSensitiveI18n_findExpected() throws Exception {

        // starts with '耀眼'
        String label = "\u8000\u773c";
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyLikeFilter(LABEL, label + "%"));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u8000\u773c\u7684\u7d05\u8272\u5149\u8292", "\u8000\u773c_\u7684\u7d05\u8272\u5149\u8292"));
    }

    @Test
    public void inFilter_caseSensitive_findExpected() throws Exception {

        // label in ('aRLeN', 'Arlie')
        String[] labels = {"aRLeN", "Arlie"};
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyInFilter("label", labels));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("aRLeN", "Arlie"));
    }

    @Test
    public void inFilter_caseSensitiveI18n_findExpected() throws Exception {

        // label in ('意大利美食', '小三明治')
        String[] labels = {"\u610f\u5927\u5229\u7f8e\u98df", "\u5c0f\u4e09\u660e\u6cbb"};
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyInFilter("label", labels));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u610f\u5927\u5229\u7f8e\u98df", "\u5c0f\u4e09\u660e\u6cbb"));
    }

    @Test
    public void inFilter_ignoreCaseTrue_findExpected() throws Exception {

        // label in ('aRLeN', 'Arlie')
        String[] labels = {"aRLeN", "Arlie"};
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyInFilter("label", labels, true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Arlen", "arlen", "aRLeN", "Arlie", "aRLie"));
    }

    @Test
    public void inFilter_ignoreCaseTrueI18b_findExpected() throws Exception {

        // label in ('房子貓頭鷹', '棚屋貓頭鷹')
        String[] labels = {"\u623f\u5b50\u8c93\u982d\u9df9", "\u68da\u5c4b\u8c93\u982d\u9df9"};
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyInFilter("label", labels, true));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u623f\u5b50\u8c93\u982d\u9df9", "\u68da\u5c4b\u8c93\u982d\u9df9"));
    }

    @Test
    public void inFilter_ignoreCaseFalse_findExpected() throws Exception {

        // label in ('aRLeN', 'Arlie')
        String[] labels = {"aRLeN", "Arlie"};
        FilterCriteria criteria = createCriteria(folder);
        criteria.addFilterElement(createPropertyInFilter("label", labels, false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("aRLeN", "Arlie"));
    }

    @Test
    public void inFilter_ignoreCaseFalseI18n_findExpected() throws Exception {

        // label in ('蘑菇%面', '藍光快')
        String[] labels = {"\u8611\u83c7%\u9762", "\u85cd\u5149\u5feb"};
        FilterCriteria criteria = createCriteria(i18nFolder);
        criteria.addFilterElement(createPropertyInFilter("label", labels, false));
        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("\u8611\u83c7%\u9762", "\u85cd\u5149\u5feb"));
    }

    @Test
    public void likeFilter_withDisjunctionEscapingIgnoreCaseTrue_findExpected() throws Exception {
        String label = "ares";
        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(folder.getURIString()));
        FilterElementDisjunction disjunction = criteria.addDisjunction();
        disjunction.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!'), '!', true));
        disjunction.addFilterElement(createPropertyLikeFilter(LABEL, escape(label.concat("_"), '!'), START, '!', true));

        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Ares", "Ares_3", "Ares_4", "ares", "ares_1", "ares_2"));
    }

    @Test
    public void likeFilter_withDisjunctionEscapingIgnoreCaseTrueI18n_findExpected() throws Exception {
        String label = "\u85cd\u5149"; // 藍光
        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(i18nFolder.getURIString()));
        FilterElementDisjunction disjunction = criteria.addDisjunction();
        disjunction.addFilterElement(createPropertyLikeFilter(LABEL, escape(label, '!'), '!', true));
        disjunction.addFilterElement(createPropertyLikeFilter(LABEL, escape(label.concat("_"), '!'), START, '!', true));

        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("\u85cd\u5149", "\u85cd\u5149_1", "\u85cd\u5149_2"));
    }

    @Test
    public void verify_RepositoryLabelIDHelper_generateIdBasedOnLabel() throws Exception {
        String id = "ares";
        String expected = "ares_5";
        String actual = RepositoryLabelIDHelper.generateIdBasedOnLabel(getUnsecureRepositoryService(), folder.getURIString(), id);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void verify_ResourceServiceImpl_getExistingNamesAndLabels_criteriaIsCorrect() throws Exception {
        List<Resource> resources = newArrayList(
                newDummyResource("res", "Res"),
                newDummyResource("bar", "Bar")
        );

        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(folder.getURIString()));
        FilterElementDisjunction disjunction = criteria.addDisjunction();
        for (Resource resource : resources) {
            String pattern;
            disjunction.addFilterElement(createPropertyEqualsFilter("name", resource.getName()));
            pattern = escape(resource.getName() + "_", '!');
            disjunction.addFilterElement(createPropertyLikeFilter("name", pattern, START, '!'));
            disjunction.addFilterElement(createPropertyEqualsFilter("label", resource.getLabel()));
            pattern = escape(resource.getLabel() + " (", '!');
            disjunction.addFilterElement(createPropertyLikeFilter("label", pattern, START, '!'));
        }

        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(toLabelsList(result), asList("Bar", "Bar (1)", "Bar (2)", "Res", "Res (1)", "Res (2)"));
    }

    @Test
    public void verify_ResourceServiceImpl_getExistingNamesAndLabelsI18n_criteriaIsCorrect() throws Exception {
        List<Resource> resources = newArrayList(
                newDummyResource("u85cdu5149", "\u85cd\u5149"), // 藍光
                newDummyResource("u7fe1u7fe0", "\u7fe1\u7fe0") // 翡翠
        );

        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(i18nFolder.getURIString()));
        FilterElementDisjunction disjunction = criteria.addDisjunction();
        for (Resource resource : resources) {
            String pattern;
            disjunction.addFilterElement(createPropertyEqualsFilter("name", resource.getName()));
            pattern = escape(resource.getName() + "_", '!');
            disjunction.addFilterElement(createPropertyLikeFilter("name", pattern, START, '!'));
            disjunction.addFilterElement(createPropertyEqualsFilter("label", resource.getLabel()));
            pattern = escape(resource.getLabel() + " (", '!');
            disjunction.addFilterElement(createPropertyLikeFilter("label", pattern, START, '!'));
        }

        List<ResourceLookup> result = loadResourcesList(criteria);

        assertEqualCollections(
                toLabelsList(result),
                asList("\u85cd\u5149", "\u85cd\u5149_1", "\u85cd\u5149_2", "\u7fe1\u7fe0", "\u7fe1\u7fe0 (1)", "\u7fe1\u7fe0 (2)"));
    }

    @Test
    public void verifyMethod_assertEqualCollections_successful() throws Exception {
        assertEqualCollections(emptyList(), emptyList());
        assertEqualCollections(newArrayList("A", "B"), newArrayList("A", "B"));
        assertEqualCollections(newArrayList("A", "B"), newArrayList("B", "A"));
        assertEqualCollections(newArrayList("A", "B", "A"), newArrayList("B", "A", "A"));
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void verifyMethod_assertEqualCollections_throwsException() throws Exception {
        assertEqualCollections(newArrayList("A", "B", "A"), newArrayList("A", "B", "C"));
    }

    private static void assertEqualCollections(Collection actual, final Collection expected) {
        if (!isEqualCollection(actual, expected)) {
            fail("Collections not equal:\n  expected: " + expected + "\n  actual:   " + actual);
        }
    }

    private void createTestResources() {
        folder = saveNewFolder("FilterCriteriaTests");
        Folder copyFolder = saveNewFolder("FilterCriteriaTestsCopy");
        i18nFolder = saveNewFolder("FilterCriteriaI18nTests");
        Folder copyI18nFolder = saveNewFolder("FilterCriteriaI18nTestsCopy");

        String[][] i18nResources = {
                {"\u610f\u5927\u5229\u7f8e\u98df", "u610fu5927u5229u7f8eu98df"}, // 意大利美食"
                {"\u58a8\u897f\u54e5\u83dc", "u58a8u897fu54e5u83dc"}, // 墨西哥菜"
                {"\u8000\u773c\u7684\u7d05\u8272\u5149\u8292", "u8000u773cu7684u7d05u8272u5149u8292"}, // 耀眼的紅色光芒"
                {"\u9bae\u8c54\u7684\u7d05\u8272\u5149\u675f", "u9baeu8c54u7684u7d05u8272u5149u675f"}, // 鮮豔的紅色光束"
                {"\u623f\u5b50\u8c93\u982d\u9df9", "u623fu5b50u8c93u982du9df9"}, // 房子貓頭鷹
                {"\u68da\u5c4b\u8c93\u982d\u9df9", "u68dau5c4bu8c93u982du9df9"}, // 棚屋貓頭鷹
                {"\u8000\u773c_\u7684\u7d05\u8272\u5149\u8292", "u8000u773cu_u7684u7d05u8272u5149u8292"}, // 耀眼_的紅色光芒"
                {"\u9bae\u8c54_\u7684\u7d05\u8272\u5149\u675f", "u9baeu8c54u_u7684u7d05u8272u5149u675f"}, // 鮮豔_的紅色光束"
                {"\u8fa3\u6912\u8f9b\u8fa3\uff0c\u82e6\u6f80\u7684\u9999\u6c23", "u8fa3u6912u8f9bu8fa3uff0cu82e6u6f80u7684u9999u6c23"}, // 辣椒辛辣，苦澀的香氣"
                {"\u8fa3\u6912\u8f9b\u8fa3\uff0c\u751c\u751c\u7684\u5473\u9053", "u8fa3u6912u8f9bu8fa3uff0cu751cu751cu7684u5473u9053"}, // 辣椒辛辣，甜甜的味道"
                {"\u85cd\u5149", "u85cdu5149"}, // 藍光"
                {"\u85cd\u5149\u5feb", "u85cdu5149u5feb"}, // 藍光快"
                {"\u85cd\u5149_1", "u85cdu5149_1"}, // 藍光_1"
                {"\u85cd\u5149_2", "u85cdu5149_2"}, // 藍光_2"
                {"\u7fe1\u7fe0", "u7fe1u7fe0"}, // 翡翠"
                {"\u7fe1\u7fe0 (1)", "u7fe1u7fe0_1"}, // 翡翠 (1)"
                {"\u7fe1\u7fe0 (2)", "u7fe1u7fe0_2"}, // 翡翠 (2)"
                {"\u9f99\u867e\u5c3e!!\u814c\u5236", "u9f99u867eu5c3e__u814cu5236"}, // 龙虾尾!!腌制
                {"\u9f99\u867e\u5c3e!!\u7096", "u9f99u867eu5c3e__u7096"}, // 龙虾尾!!炖
                {"\u8611\u83c7\u9762", "u8611u83c7u9762"}, // 蘑菇面"
                {"\u8611\u83c7%\u9762", "u8611u83c7_u9762"}, // 蘑菇%面"
                {"\u8611\u83c7%\u9762\u6b22\u95f9\u7684", "u8611u83c7_u9762u6b22u95f9u7684"}, // 蘑菇%面欢闹的"
                {"\u5c0f\u4e09\u660e\u6cbb", "u5c0fu4e09u660eu6cbb"} // 小三明治"
        };

        String[][] resources = {
                //  label       name
                {	"Bar",	    "bar"       },
                {	"Bar (1)",	"bar_1"	    },
                {	"Bar (2)",	"bar_2"	    },
                {	"Res",	    "res"	    },
                {	"Res (1)",	"res_1"	    },
                {	"Res (2)",	"res_2"	    },

                {	"bar",	    "bar1"      },
                {	"bar (1)",	"bar2"	    },
                {	"bar (2)",	"bar3"	    },
                {	"res",	    "res1"	    },
                {	"res (1)",	"res2"	    },
                {	"res (2)",	"res3"	    },

                {	"ar",	    "ar"	    },
                {	"Ar!!e",	"ar__e"	    },
                {	"aR!!e",	"ar__e1"	},
                {	"Ar!!n",	"ar__n"	    },
                {	"aR!!N",	"ar__n1"	},
                {	"ar[o]n",	"ar_o_n"	},
                {	"Ar[o]n",	"ar_o_n1"	},
                {	"aR[o]N",	"ar_o_n2"	},
                {	"Ar[ro]n",	"ar_ro_n"	},
                {	"aR[ro]N",	"ar_ro_n1"	},
                {	"Ar%d",	    "ar_d"	    },
                {	"aR%D",	    "ar_d1"	    },
                {	"Ar%do",	"ar_do"	    },
                {	"aR%Do",	"ar_do1"	},
                {	"Ares",	    "ares"	    },
                {	"Ares_3",	"ares_3"	},
                {	"Ares_4",	"ares_4"	},
                {	"ares",	    "ares1"	    },
                {	"ares_1",	"ares_1"	},
                {	"ares_2",	"ares_2"	},
                {	"Arl!e",	"arl_e"	    },
                {	"aRL!e",	"arl_e1"	},
                {	"Arlen",	"arlen"	    },
                {	"arlen",	"arlen1"	},
                {	"aRLeN",	"arlen2"	},
                {	"Arl",	    "arl"	    },
                {	"arl",	    "arl2"	    },
                {	"aRL",	    "arl3"	    },
                {	"Arlie",	"arlie"	    },
                {	"aRLie",	"arlie1"	},
                {	"Armand",	"armand"	},
                {	"aRMaND",	"armand1"	},
                {	"Armando",	"armando"	},
                {	"aRMaNDo",	"armando1"	},
                {	"Arn",	    "arn"	    },
                {	"aRN",	    "arn1"	    },
                {	"Arn_ld",	"arn_ld"	},
                {	"arn_ld",	"arn_ld1"	},
                {	"aRN_LD",	"arn_ld2"	},
                {	"Arn_ldo",	"arn_ldo"	},
                {	"aRN_LDo",	"arn_ldo1"	},
                {	"Arn_lfo",	"arn_lfo2"	},
                {	"aRN_LFo",	"arn_lfo3"	},
                {	"Arnold",	"arnold"	},
                {	"aRNoLD",	"arnold1"	},
                {	"Arnoldo",	"arnoldo"	},
                {	"aRNoLDo",	"arnoldo1"	},
                {	"Arnulfo",	"arnulfo"	},
                {	"aRNuLFo",	"arnulfo1"	},
                {	"Aron",	    "aron"	    },
                {	"aRoN",	    "aron1"	    },
                {	"Arron",	"arron"	    },
                {	"aRRoN",	"arron1"	}
        };

        createResources(folder, resources);
        createResources(copyFolder, resources);
        createResources(i18nFolder, i18nResources);
        createResources(copyI18nFolder, i18nResources);
    }

    private void createResources(Folder folder, String[][] resources) {
        for (String[] row : resources) {
            String name = row[1];
            String label = row[0];

            Resource res = newResource(ListOfValues.class, folder, name);
            res.setLabel(label);
            saveResource(res);
        }

    }

    private FilterCriteria createCriteria(Folder folder) {
        FilterCriteria criteria = createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(folder.getURIString()));
        return criteria;
    }

    @SuppressWarnings("unchecked")
    private List<ResourceLookup> loadResourcesList(FilterCriteria criteria) {
        return getUnsecureRepositoryService().loadResourcesList(getExecutionContext(), criteria);
    }

    private List<String> toLabelsList(List<ResourceLookup> resources) {
        return Lists.transform(resources, new Function<ResourceLookup, String>() {
            @Override
            public String apply(ResourceLookup input) {
                return input.getLabel();
            }
        });
    }

    private Resource newDummyResource(String name, String label) {
        Resource resource = new ListOfValuesImpl();
        resource.setName(name);
        resource.setLabel(label);
        return resource;
    }
}
