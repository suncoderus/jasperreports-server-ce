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

package com.jaspersoft.jasperserver.api.security;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSSecurityException;
import com.jaspersoft.jasperserver.api.security.validators.Validator;
import com.jaspersoft.jasperserver.api.security.validators.ValidatorRule;
import com.jaspersoft.jasperserver.api.security.validators.ValidatorRuleImpl;
import com.jaspersoft.jasperserver.core.util.StringUtil;
import mondrian.tui.MockHttpServletRequest;
import org.junit.Test;
import org.owasp.esapi.errors.ValidationException;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.isInputValidationOn;
import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.isSQLValidationOn;
import static com.jaspersoft.jasperserver.api.security.validators.Validator.getDefaultEncoding;
import static com.jaspersoft.jasperserver.api.security.validators.Validator.getDefaultEncodingErrorMessage;
import static com.jaspersoft.jasperserver.api.security.validators.Validator.isParamValueValid;
import static com.jaspersoft.jasperserver.api.security.validators.Validator.validateSQL;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for Validation
 *
 * @author Anton Fomin
 * @author Normando Macaraeg
 * @version $Id$
 */
public class ValidatorTest extends UnitilsJUnit4 {

    @Test
    public void testSQLValidationIsEnabled() {
        assertTrue("SQL Validation is disabled. This is too dangerous to ship as a default!", isSQLValidationOn());
    }

    @Test
    public void validateSQLTest1() {
        validateSQL("  SeLeCt * from blah where a = 'ololo';");
    }

    @Test
    public void validateSQLTest2() {
        validateSQL("  SeLeCt * from blah where a = 'ololo';;;;;   ");
    }

    @Test(expected = JSSecurityException.class)
    public void validateSQLTest3() {
        validateSQL("  SeLeCt * from blah where a = 'ololo';create table test (col blob);");
    }

    @Test(expected = JSSecurityException.class)
    public void validateSQLTest4() {
        validateSQL("  SeLeCt * from blah where a = 'ololo';create table test (col blob)");
    }

    @Test
    public void testValidateWithGoodRequest() throws ValidationException
    {
        if (!isInputValidationOn()) return;

        String param = "j_username";
        String valueGood = "jasperadmin";
        String paramPwd = "j_password";
        String valueGoodPwd = "jasperadmin";
        String paramTimezone = "userTimezone";
        String valueGoodTimezone = "America/New_York";
        String paramLocale = "userLocale";
        String valueGoodLocale = "zh_TW";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter(param, valueGood);
        request.setupAddParameter(paramPwd, valueGoodPwd);
        request.setupAddParameter(paramTimezone, valueGoodTimezone);
        request.setupAddParameter(paramLocale, valueGoodLocale);

        assertTrue(Validator.validateRequestParams(request));
    }

    @Test
    public void testValidateWithMaliciousRequest() throws ValidationException
    {
        if (!isInputValidationOn()) return;

        String param = "j_username";
        String badUsrName = "<script>alert(1)</script>";
        String paramPwd = "j_password";
        String valueGoodPwd = "jasperadmin";
        String paramTimezone = "userTimezone";
        String valueGoodTimezone = "America/New_York";
        String paramLocale = "userLocale";
        String valueGoodLocale = "zh_TW";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter(paramPwd, valueGoodPwd);
        request.setupAddParameter(paramTimezone, valueGoodTimezone);
        request.setupAddParameter(paramLocale, valueGoodLocale);

        request.setupAddParameter(param, badUsrName);
        assertFalse(Validator.validateRequestParams(request));
    }

    @Test
    public void testValidateNoScript()
    {
        if (!isInputValidationOn()) return;

        String input_2 = "<script>alert('evil')</script>";
        // TEST #2
        // validate should see the script tag and throw the exception
        assertFalse(isParamValueValid(input_2, input_2, "!Script"));
    }

    @Test
    public void testValidateNoTagViaBlacklist()
    {
        if (!isInputValidationOn()) return;

        String input_2 = "test <script>alert('evil')</script> test2 ";
        // TEST #2
        // validate should see the tag and throw the exception
        assertFalse(isParamValueValid(input_2, input_2, "!Script"));
    }

    /**
     * Test that backlist rule passes empty string as a valid input when rule allows nulls
     * @throws Exception
     */
    @Test
    public void testValidateEmptyStringViaBlacklist2()
    {
        if (!isInputValidationOn()) return;

        String input_2 = "";
        boolean allowNull = true;
        ValidatorRule rule = new ValidatorRuleImpl(null, "!Script", 200, allowNull, Validator.class.getName());
        assertTrue(isParamValueValid(input_2, input_2, rule));
    }

    /**
     * Test that backlist rule fails empty string as a valid input when rule does NOT allow nulls
     * @throws Exception
     */
    @Test
    public void testValidateEmptyStringBlacklist3()
    {
        if (!isInputValidationOn()) return;

        String input_2 = "";
        boolean allowNull = false;
        ValidatorRule rule = new ValidatorRuleImpl(null, "!Script", 200, allowNull, Validator.class.getName());
        assertFalse(isParamValueValid(input_2, input_2, rule));
    }

    @Test
    public void testValidateMultiLineNoTagViaBlacklist()
    {
        if (!isInputValidationOn()) return;

        String input_2 = "test <script>\n alert('evil')\n</script> test2 ";
        // TEST #2
        // validate should see the tag and throw the exception
        assertFalse(isParamValueValid(input_2, input_2, "!Script"));
    }

    @Test
    public void testValidateNoTagHex()
    {
        if (!isInputValidationOn()) return;

        String input_1 = "%3C%73%63%72%69%70%74%3E %3C%2F%73%63%72%69%70%74%3E"; // hex encoded <script>  </script>

        assertFalse(isParamValueValid(input_1, input_1, "!Script")); // canonicalization is done BEFORE validation.

    }

    @Test
    public void testValidateDoubleDashViaBlacklist()
    {
        if (!isInputValidationOn()) return;

        String input_2 = "test -- test - test";
        // TEST #2
        // validate should see the tag and throw the exception
        assertFalse(isParamValueValid(input_2, input_2, "!DoubleDash"));
    }

    @Test
    public void testValidateMultilineCommentBlacklist()
    {
        if (!isInputValidationOn()) return;

        String input_2 = "test /*\n test\n*/ - test";
        // TEST #2
        // validate should see the tag and throw the exception
        assertFalse(isParamValueValid(input_2, input_2, "!SQLComment"));
    }

    @Test
    public void testValidateSQLCommentBlacklist()
    {
        if (!isInputValidationOn()) return;

        String input_2 = "test \n test -- test \n - test";
        // TEST #2
        // validate should see the tag and throw the exception
        assertFalse(isParamValueValid(input_2, input_2, "!SQLComment"));
    }

    @Test
    public void testValidateJSONArrayGoodInput()
    {
        if (!isInputValidationOn()) return;

        String inputGood = "[{id:'JServerJdbcDS.accounts',label:'accounts',type:'ItemGroupType',itemId:'accounts',descr:'accounts',labelId:'',descrId:'',resourceId:'accounts',children:[{id:'JServerJdbcDS.accounts.account_type',label:'account_type',type:'ItemType',itemId:'account_type',descr:'account_type',labelId:'',descrId:'',resourceId:'accounts.account_type',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.annual_revenue',label:'annual_revenue',type:'ItemType',itemId:'annual_revenue',descr:'annual_revenue',labelId:'',descrId:'',resourceId:'accounts.annual_revenue',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.assigned_user_id',label:'assigned_user_id',type:'ItemType',itemId:'assigned_user_id',descr:'assigned_user_id',labelId:'',descrId:'',resourceId:'accounts.assigned_user_id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.billing_address_city',label:'billing_address_city',type:'ItemType',itemId:'billing_address_city',descr:'billing_address_city',labelId:'',descrId:'',resourceId:'accounts.billing_address_city',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.billing_address_country',label:'billing_address_country',type:'ItemType',itemId:'billing_address_country',descr:'billing_address_country',labelId:'',descrId:'',resourceId:'accounts.billing_address_country',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.billing_address_postalcode',label:'billing_address_postalcode',type:'ItemType',itemId:'billing_address_postalcode',descr:'billing_address_postalcode',labelId:'',descrId:'',resourceId:'accounts.billing_address_postalcode',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.billing_address_state',label:'billing_address_state',type:'ItemType',itemId:'billing_address_state',descr:'billing_address_state',labelId:'',descrId:'',resourceId:'accounts.billing_address_state',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.billing_address_street',label:'billing_address_street',type:'ItemType',itemId:'billing_address_street',descr:'billing_address_street',labelId:'',descrId:'',resourceId:'accounts.billing_address_street',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.created_by',label:'created_by',type:'ItemType',itemId:'created_by',descr:'created_by',labelId:'',descrId:'',resourceId:'accounts.created_by',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.date_entered',label:'date_entered',type:'ItemType',itemId:'date_entered',descr:'date_entered',labelId:'',descrId:'',resourceId:'accounts.date_entered',dataType:'java.sql.Timestamp',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.date_modified',label:'date_modified',type:'ItemType',itemId:'date_modified',descr:'date_modified',labelId:'',descrId:'',resourceId:'accounts.date_modified',dataType:'java.sql.Timestamp',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.deleted',label:'deleted',type:'ItemType',itemId:'deleted',descr:'deleted',labelId:'',descrId:'',resourceId:'accounts.deleted',dataType:'java.lang.Boolean',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.description',label:'description',type:'ItemType',itemId:'description',descr:'description',labelId:'',descrId:'',resourceId:'accounts.description',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.email1',label:'email1',type:'ItemType',itemId:'email1',descr:'email1',labelId:'',descrId:'',resourceId:'accounts.email1',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.email2',label:'email2',type:'ItemType',itemId:'email2',descr:'email2',labelId:'',descrId:'',resourceId:'accounts.email2',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.employees',label:'employees',type:'ItemType',itemId:'employees',descr:'employees',labelId:'',descrId:'',resourceId:'accounts.employees',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.id',label:'id',type:'ItemType',itemId:'id',descr:'id',labelId:'',descrId:'',resourceId:'accounts.id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.industry',label:'industry',type:'ItemType',itemId:'industry',descr:'industry',labelId:'',descrId:'',resourceId:'accounts.industry',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.modified_user_id',label:'modified_user_id',type:'ItemType',itemId:'modified_user_id',descr:'modified_user_id',labelId:'',descrId:'',resourceId:'accounts.modified_user_id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.name',label:'name',type:'ItemType',itemId:'name',descr:'name',labelId:'',descrId:'',resourceId:'accounts.name',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.ownership',label:'ownership',type:'ItemType',itemId:'ownership',descr:'ownership',labelId:'',descrId:'',resourceId:'accounts.ownership',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.parent_id',label:'parent_id',type:'ItemType',itemId:'parent_id',descr:'parent_id',labelId:'',descrId:'',resourceId:'accounts.parent_id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.phone_alternate',label:'phone_alternate',type:'ItemType',itemId:'phone_alternate',descr:'phone_alternate',labelId:'',descrId:'',resourceId:'accounts.phone_alternate',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.phone_fax',label:'phone_fax',type:'ItemType',itemId:'phone_fax',descr:'phone_fax',labelId:'',descrId:'',resourceId:'accounts.phone_fax',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.phone_office',label:'phone_office',type:'ItemType',itemId:'phone_office',descr:'phone_office',labelId:'',descrId:'',resourceId:'accounts.phone_office',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.rating',label:'rating',type:'ItemType',itemId:'rating',descr:'rating',labelId:'',descrId:'',resourceId:'accounts.rating',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.shipping_address_city',label:'shipping_address_city',type:'ItemType',itemId:'shipping_address_city',descr:'shipping_address_city',labelId:'',descrId:'',resourceId:'accounts.shipping_address_city',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.shipping_address_country',label:'shipping_address_country',type:'ItemType',itemId:'shipping_address_country',descr:'shipping_address_country',labelId:'',descrId:'',resourceId:'accounts.shipping_address_country',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.shipping_address_postalcode',label:'shipping_address_postalcode',type:'ItemType',itemId:'shipping_address_postalcode',descr:'shipping_address_postalcode',labelId:'',descrId:'',resourceId:'accounts.shipping_address_postalcode',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.shipping_address_state',label:'shipping_address_state',type:'ItemType',itemId:'shipping_address_state',descr:'shipping_address_state',labelId:'',descrId:'',resourceId:'accounts.shipping_address_state',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.shipping_address_street',label:'shipping_address_street',type:'ItemType',itemId:'shipping_address_street',descr:'shipping_address_street',labelId:'',descrId:'',resourceId:'accounts.shipping_address_street',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.sic_code',label:'sic_code',type:'ItemType',itemId:'sic_code',descr:'sic_code',labelId:'',descrId:'',resourceId:'accounts.sic_code',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.ticker_symbol',label:'ticker_symbol',type:'ItemType',itemId:'ticker_symbol',descr:'ticker_symbol',labelId:'',descrId:'',resourceId:'accounts.ticker_symbol',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts.website',label:'website',type:'ItemType',itemId:'website',descr:'website',labelId:'',descrId:'',resourceId:'accounts.website',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'}]}, {id:'JServerJdbcDS.accounts_bugs',label:'accounts_bugs',type:'ItemGroupType',itemId:'accounts_bugs',descr:'accounts_bugs',labelId:'',descrId:'',resourceId:'accounts_bugs',children:[{id:'JServerJdbcDS.accounts_bugs.account_id',label:'account_id',type:'ItemType',itemId:'account_id',descr:'account_id',labelId:'',descrId:'',resourceId:'accounts_bugs.account_id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts_bugs.bug_id',label:'bug_id',type:'ItemType',itemId:'bug_id',descr:'bug_id',labelId:'',descrId:'',resourceId:'accounts_bugs.bug_id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts_bugs.date_modified',label:'date_modified',type:'ItemType',itemId:'date_modified1',descr:'date_modified1',labelId:'',descrId:'',resourceId:'accounts_bugs.date_modified',dataType:'java.sql.Timestamp',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts_bugs.deleted',label:'deleted',type:'ItemType',itemId:'deleted1',descr:'deleted1',labelId:'',descrId:'',resourceId:'accounts_bugs.deleted',dataType:'java.lang.Boolean',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts_bugs.id',label:'id',type:'ItemType',itemId:'id1',descr:'id1',labelId:'',descrId:'',resourceId:'accounts_bugs.id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'}]}, {id:'JServerJdbcDS.accounts_contacts',label:'accounts_contacts',type:'ItemGroupType',itemId:'accounts_contacts',descr:'accounts_contacts',labelId:'',descrId:'',resourceId:'accounts_contacts',children:[{id:'JServerJdbcDS.accounts_contacts.account_id',label:'account_id',type:'ItemType',itemId:'account_id1',descr:'account_id1',labelId:'',descrId:'',resourceId:'accounts_contacts.account_id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts_contacts.contact_id',label:'contact_id',type:'ItemType',itemId:'contact_id',descr:'contact_id',labelId:'',descrId:'',resourceId:'accounts_contacts.contact_id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts_contacts.date_modified',label:'date_modified',type:'ItemType',itemId:'date_modified2',descr:'date_modified2',labelId:'',descrId:'',resourceId:'accounts_contacts.date_modified',dataType:'java.sql.Timestamp',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts_contacts.deleted',label:'deleted',type:'ItemType',itemId:'deleted2',descr:'deleted2',labelId:'',descrId:'',resourceId:'accounts_contacts.deleted',dataType:'java.lang.Boolean',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.accounts_contacts.id',label:'id',type:'ItemType',itemId:'id2',descr:'id2',labelId:'',descrId:'',resourceId:'accounts_contacts.id',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'}]}, {id:'JServerJdbcDS.config',label:'config',type:'ItemGroupType',itemId:'config',descr:'config',labelId:'',descrId:'',resourceId:'config',children:[{id:'JServerJdbcDS.config.category',label:'category',type:'ItemType',itemId:'category',descr:'category',labelId:'',descrId:'',resourceId:'config.category',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.config.name',label:'name',type:'ItemType',itemId:'name1',descr:'name1',labelId:'',descrId:'',resourceId:'config.name',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'},{id:'JServerJdbcDS.config.value',label:'value',type:'ItemType',itemId:'value',descr:'value',labelId:'',descrId:'',resourceId:'config.value',dataType:'java.lang.String',defaultMask:'none',defaultAgg:'none'}]}]]";
        assertTrue(isParamValueValid(inputGood, inputGood, "AlphaNumPunctuation"));
    }

    @Test
    public void testValidateJSONArrayMaliciousHexInput() {
        if (!isInputValidationOn()) return;

        String inputBad = "[{id:'JServerJdbcDS.accounts%3Cscript%3Ealert(1)%3C%2Fscript%3E'}]";
        assertFalse(isParamValueValid(inputBad, inputBad, "AlphaNumPunctuation"));
    }

    @Test
    public void testValidateJSONObjectGoodInput()
    {
        if (!isInputValidationOn()) return;

        String inputGood = "{'userName': 'normID', 'fullName': 'norm macaraeg', 'password': 'mypassword', 'email': 'norm@norm.com', 'enabled': true, 'roles': [{'roleName': 'ROLE_USER', 'FLOW_ID': 'roleListFlow'}], 'attributes': [], 'FLOW_ID': 'userListFlow'}";
        assertTrue(isParamValueValid(inputGood, inputGood, "AlphaNumPunctuation"));
    }

    @Test
    public void testValidateBoolean1() {
        if (!isInputValidationOn()) return;

        String param = "autoGenerateJoins";
        String valueGood_1 = "tRuE";

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setupAddParameter(param, valueGood_1);
        assertTrue(Validator.validateRequestParams(request));
    }

    @Test
    public void testValidateBoolean2() {
        if (!isInputValidationOn()) return;

        String param = "autoGenerateJoins";
        String valueGood_2 = "FaLsE";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter(param, valueGood_2);
        assertTrue(Validator.validateRequestParams(request));
    }

    @Test
    public void testValidateBoolean3() {
        if (!isInputValidationOn()) return;

        String param = "autoGenerateJoins";
        String valueBad = "neitherTRUEnorFALSE";

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setupAddParameter(param, valueBad);
        assertFalse(Validator.validateRequestParams(request));
    }

    private String valueGood1 = "{'userName': 'normID', 'fullName': 'norm macaraeg', 'password': 'mypassword', 'email': 'norm@norm.com', 'enabled': true, 'roles': [{'roleName': 'ROLE_USER', 'FLOW_ID': 'roleListFlow'}], 'attributes': [], 'FLOW_ID': 'userListFlow'}";
    private String valueGood12 = "{'userName': 'billID', 'fullName': 'billy gean', 'password': 'mypassword', 'email': 'bill@bill.com', 'enabled': true, 'roles': [{'roleName': 'ROLE_ADMINISTRATOR', 'FLOW_ID': 'roleListFlow'}], 'attributes': [], 'FLOW_ID': 'userListFlow'}";
    private String valueBad1 = "{'userName': 'normID', 'fullName': '%3Cscript%3Ealert(1)%3C%2Fscript%3E', 'password': 'mypassword', 'email': 'norm@norm.com', 'enabled': true, 'roles': [{'roleName': 'ROLE_USER', 'FLOW_ID': 'roleListFlow'}], 'attributes': [], 'FLOW_ID': 'userListFlow'}";
    private String valueGood2 = "normID|organization_1";
    private String valueGood21 = "billID|organization_2";
    private String valueBad2 = "normID|organization_1|%3Cscript%3Ealert(1)%3C%2Fscript%3E";

    private Map<String, String[]> paramMap;
    private Mock<HttpServletRequest> requestMock;

    private HttpServletRequest getRequest(String... values) {
        paramMap = new LinkedHashMap<String, String[]>();
        paramMap.put("entity", values);
        requestMock.returns(paramMap).getParameterMap();
        return requestMock.getMock();
    }

    /**
     * Following tests are commented out because we don't use security.validation.input.on=true anymore.
     * See new tests for XSS protection : XSSEscapeXmlELResolverTest, [js-sdk] xssUtilTests.js
     */

    /**
     * The code should properly canonicalize %3C and %3E, which is a common hacker trick.
     * %3C = <
     * %3E = >
     * @throws ValidationException
     */
    /*@Test
    public void testValidateEntityBad1() throws ValidationException {
        if (!isInputValidationOn()) return;
        assertFalse(Validator.validateRequestParams(getRequest(valueBad1)));
    }

    @Test
    public void testValidateEntityBad2() throws ValidationException{
        if (!isInputValidationOn()) return;
        assertFalse(Validator.validateRequestParams(getRequest(valueBad2)));
    }

    @Test
    public void testValidateEntityGood1() throws ValidationException {
        if (!isInputValidationOn()) return;
        assertTrue(Validator.validateRequestParams(getRequest(valueGood1)));
    }

    @Test
    public void testValidateEntityGood2() throws ValidationException {
        if (!isInputValidationOn()) return;
        assertTrue(Validator.validateRequestParams(getRequest(valueGood2)));
    }

    @Test
    public void testValidateEntityGoodTwoValues1() throws ValidationException {
        if (!isInputValidationOn()) return;
        assertTrue(Validator.validateRequestParams(getRequest(valueGood1, valueGood12)));
    }

    @Test
    public void testValidateEntityGoodTwoValues2() throws ValidationException {
        if (!isInputValidationOn()) return;
        assertTrue(Validator.validateRequestParams(getRequest(valueGood2, valueGood21)));
    }

    @Test
    public void testValidateEntityGoodOneBad1() throws ValidationException {
        if (!isInputValidationOn()) return;
       assertFalse(Validator.validateRequestParams(getRequest(valueGood1, valueBad1)));
    }

    @Test
    public void testValidateEntityTwoGoodOneBad() throws ValidationException {
        if (!isInputValidationOn()) return;
        assertFalse(Validator.validateRequestParams(getRequest(valueGood1, valueBad1, valueGood12)));
    }

    @Test
    public void testValidateEntityGoodOneBad2() throws ValidationException {
        if (!isInputValidationOn()) return;
        assertFalse(Validator.validateRequestParams(getRequest(valueGood2, valueBad2)));
    }*/

    @Test
    public void getDecodedMapTest() throws Exception {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put(URLEncoder.encode("ключ1", "UTF-8"), new String[] {URLEncoder.encode("Jožin z bažin", "UTF-8"), URLEncoder.encode("močálem se plíží", "UTF-8")});
        paramMap.put(URLEncoder.encode("ключ2", "UTF-8"), new String[] {URLEncoder.encode("Jožin z bažin", "UTF-8"), URLEncoder.encode("k vesnici se blíží", "UTF-8")});
        paramMap.put(URLEncoder.encode("ключ3", "UTF-8"), new String[] {"Jožin z bažin %", "k vesnici %se blíží"});

        Map<String, String[]> decodedParamMap = StringUtil.getDecodedMap(paramMap, getDefaultEncoding(), getDefaultEncodingErrorMessage());

        assertTrue(decodedParamMap.keySet().contains("ключ1"));
        assertTrue(decodedParamMap.keySet().contains("ключ2"));
        assertArrayEquals(new String[] {"Jožin z bažin", "močálem se plíží"} , decodedParamMap.get("ключ1"));
        assertArrayEquals(new String[] {"Jožin z bažin", "k vesnici se blíží"} , decodedParamMap.get("ключ2"));
        assertArrayEquals(new String[]{"Jožin z bažin %", "k vesnici %se blíží"}, decodedParamMap.get("ключ3"));
    }

    @Test
    public void testGoodRequestDate1() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("RequestDate", "99-99-9999"); // good mm-dd-yyyy
        Validator.validateRequestParams(request);
    }
    @Test
    public void testGoodRequestDate2() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("RequestDate", "12-01-9900"); // good dd-mm-yyyy
        Validator.validateRequestParams(request);
    }
    @Test
    public void testGoodRequestDate3() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("RequestDate", "9999-99-99"); // good nnnn-nn-nn
        Validator.validateRequestParams(request);
    }
    @Test
    public void testGoodRequestDate4() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("RequestDate", "19970601000000"); // good yyyymmdd000000
        Validator.validateRequestParams(request);
    }
    @Test
    public void testGoodRequestDate5() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("RequestDate", "15 Juillet 1901"); // good dd MMM yyyy
        Validator.validateRequestParams(request);
    }

}
