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

package com.jaspersoft.jasperserver.api.common.util;


import javax.net.ssl.TrustManager;
import java.io.InputStream;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public interface FTPService {

    /*
     * Establishes a data connection with the FTPS server
     *
     * @param host ftp server host name
     * @param userName login user name
     * @param password login password
     * @return FTPServiceClient interface to access ftp server
     */
    FTPServiceClient connect(String host, String userName, String password) throws Exception;


    /*
     * Establishes a data connection with the FTP server
     *
     * @param host ftp server host name
     * @param port ftp server port number
     * @param userName login user name
     * @param password login password
     * @return FTPServiceClient interface to access ftp server
     */
    FTPServiceClient connectFTP(String host, int port, String userName, String password) throws Exception;

    /*
     * Establishes a data connection with the FTPS server
     *
     * @param host ftp server host name
     * @param port ftp server port number
     * @param userName login user name
     * @param password login password
     * @return FTPServiceClient interface to access ftp server
     */
    FTPServiceClient connectFTPS(String host, int port, String userName, String password) throws Exception;

    /*
     * Establishes a data connection with the FTPS server
     *
     * @param host ftp server host name
     * @param port ftp server port number
     * @param protocol ftp protocol
     * @param isImplicit the security mode for FTPS (Implicit/ Explicit)
     * @param pbsz pbsz value: 0 to (2^32)-1 decimal integer.
     * @param prot PROT command
     * @param userName login user name
     * @param password login password
     * @return FTPServiceClient interface to access ftp server
     */
    FTPServiceClient connectFTPS(String host, int port, String protocol, boolean isImplicit, long pbsz, String prot, String userName, String password) throws Exception;

    /*
     * Establishes a data connection with the FTPS server
     *
     * @param host ftp server host name
     * @param port ftp server port number
     * @param protocol ftp protocol
     * @param isImplicit the security mode for FTPS (Implicit/ Explicit)
     * @param pbsz pbsz value: 0 to (2^32)-1 decimal integer.
     * @param prot PROT command
     * @param userName login user name
     * @param password login password
     * @param useDefaultTrustManager - only do date validation if using default trust manager. Otherwise, use the value in trust manager parameter
     * @param Trust Manager - set to NULL to use JVM trust manager (if useDefaultTrustManager is set to true, this value will be ignored)
     * @return FTPServiceClient interface to access ftp server
     */
    FTPServiceClient connectFTPS(String host, int port, String protocol, boolean isImplicit, long pbsz, String prot, String userName, String password, boolean useDefaultTrustManager, TrustManager trustManager) throws Exception;

    /*
    * Establishes a data connection with the SFTP server
    *
    * @param host ftp server host name
    * @param port ftp server port number
    * @param userName login user name
    * @param password login user password
    * @param sshKey login user SSH private key
    * @param sshPassphrase login user SSH private key passphrase
    */
    FTPServiceClient connectSFTP(String host, int port, String userName, String password, String sshKeyPath, String sshKeyData, String sshPassphrase) throws Exception;

    public interface FTPServiceClient {

        /*
        * Closes the connection to the FTP server
        */
        void disconnect() throws Exception;

        /*
        * Change the current working directory of the FTP session
        *
        * @param directoryPath new path of the current working directory
        */
        void changeDirectory(String directoryPath) throws Exception;

        /*
        * retrieve the specific file from the ftp server
        *
        * @param fileName name of the file
        * @return InputStream of the file
        */
        InputStream getFile(String fileName) throws Exception;

        /*
        * create a file in the ftp server
        *
        * @param fileName name of the file
        * @param inputData content of the file
        */
        void putFile(String fileName, InputStream inputData) throws Exception;

        /*
        * returns whether the file exists in the ftp server or not
        *
        * @param fileName name of the file
        * @return return true if file exists in the ftp server.  Otherwise, return false.
        */
        boolean exists(String fileName) throws Exception;

    }

}
