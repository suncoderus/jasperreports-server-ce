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
package com.jaspersoft.jasperserver.war.helper;

import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class GenericParametersHelperTest {

    /*
    Below is test classes hierarchy to cover possible cases for GenericParametersHelper functionality
     */

    private interface TestSuperInterface0<SuperInterfaceArg0> {
    }

    private interface TestSuperInterface1<SuperInterfaceArg1> {
    }

    private interface TestInterface<InterfaceArg0, InterfaceArg1> extends TestSuperInterface0<InterfaceArg0>, TestSuperInterface1<InterfaceArg1> {
    }

    private class TestClass<ClassArg0, ClassArg1> implements TestInterface<ClassArg1, ClassArg0> {
    }

    private abstract class AbstractTextClass<AbstractArg0, AbstractArg1> extends TestClass<AbstractArg1, AbstractArg0> {
    }

    private class TestSubClassStringInteger extends AbstractTextClass<String, Integer> {
    }

    private class TestSubClassFloatCharacter extends AbstractTextClass<Float, Character> {
    }

    /*
    TestSubClassStringInteger
    AbstractTextClass         <String, Integer>
    TestClass                 <Integer, String>
    TestInterface             <String, Integer>
    TestSuperInterface0       <String>
    TestSuperInterface1               <Integer>
     */
    @Test
    public void getGenericTypeArgument_TestSubClassStringInteger() {
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassStringInteger.class, TestClass.class, 0), Integer.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassStringInteger.class, TestClass.class, 1), String.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassStringInteger.class, TestInterface.class, 0), String.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassStringInteger.class, TestInterface.class, 1), Integer.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassStringInteger.class, TestSuperInterface0.class, 0), String.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassStringInteger.class, TestSuperInterface1.class, 0), Integer.class);
    }

    /*
    TestSubClassFloatCharacter
    AbstractTextClass          <Float, Character>
    TestClass                  <Character, Float>
    TestInterface              <Float, Character>
    TestSuperInterface0        <Float>
    TestSuperInterface1               <Character>
     */
    @Test
    public void getGenericTypeArgument_TestSubClassFloatCharacter() {
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassFloatCharacter.class, TestClass.class, 0), Character.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassFloatCharacter.class, TestClass.class, 1), Float.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassFloatCharacter.class, TestInterface.class, 0), Float.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassFloatCharacter.class, TestInterface.class, 1), Character.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassFloatCharacter.class, TestSuperInterface0.class, 0), Float.class);
        assertSame(GenericParametersHelper.getGenericTypeArgument(TestSubClassFloatCharacter.class, TestSuperInterface1.class, 0), Character.class);
    }
}
