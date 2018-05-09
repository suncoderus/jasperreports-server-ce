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

package com.jaspersoft.jasperserver.api;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JSException.java 58870 2015-10-27 22:30:55Z esytnik $
 */
@JasperServerAPI
public class JSTimeBalanceException extends RuntimeException {

    protected Object[] args;
    private boolean wrapperObject;



    public JSTimeBalanceException(String message) {
        super(message);
    }

    public JSTimeBalanceException(String message, Throwable cause) {
        super(message, cause);
        this.wrapperObject = true;
    }

    public JSTimeBalanceException(Throwable cause) {
        super(cause);
        this.wrapperObject = true;
    }

    public JSTimeBalanceException(String message, Object[] args) {
        super(message);
        this.args = args;
    }

    /**
     * @return Returns the args.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @param args The args to set.
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * @return Returns the wrapperObject.
     */
    public boolean isWrapperObject() {
        return wrapperObject;
    }

    /**
     * @param wrapperObject The wrapperObject to set.
     */
    public void setWrapperObject(boolean wrapperObject) {
        this.wrapperObject = wrapperObject;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("\nArguments: ");
        if(args!=null){
            for(Object arg:args){
                builder.append(arg);
                builder.append(',');
            }
        }
        return builder.toString();
    }

}
