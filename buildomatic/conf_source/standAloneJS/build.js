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

        importPackage(java.io);
        
        function writeFile(path, contents) {
            var fw = new FileWriter(path);
            fw.write(contents);
            fw.close();
        }

        print('hi, this is js');
        var appConFile = '../target/jasperserver-pro/WEB-INF/applicationContext-adhoc.xml';
        var appCon = new XML(readFile(appConFile));
        appCon.appendChild(<bean id="mybean" class="com.jaspersoft.ji.SomeBean">
            <property name="foo" value="bar"/>
        </bean>);
        writeFile('newAppCon.xml', appCon.toString());
      


  