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
package com.jaspersoft.jasperserver.war;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author swood
 *
 */
public class OlapGetChart extends HttpServlet {
    private static final Log logger = LogFactory.getLog(OlapGetChart.class);

    String basePath;

	/**
	 * TODO Should initialize fileNotFound image
	 */
	public OlapGetChart() {
		super();
	}

	/** Initializes the servlet.
     */
    final static String fileNotFound="/WEB-INF/jpivot/img_not_found.gif";
    
    public void init(ServletConfig config) throws ServletException {        
        super.init(config);             
    }
    
    /** Destroys the servlet.
     */
    public void destroy() {
        
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {

        String filename = request.getParameter("filename");
        logger.debug("GetChart called: filename="+filename);
        if (filename == null) {
            throw new ServletException("Parameter 'filename' must be supplied");
        }

        //  Replace ".." with ""
        //  This is to prevent access to the rest of the file system
        filename = searchReplace(filename, "..", "");

        //  Check the file exists
        File file = new File(System.getProperty("java.io.tmpdir"), filename);
        if (!file.exists()) {
            logger.error("File '" + file.getAbsolutePath() + "' does not exist");
            URL url = this.getClass().getResource(fileNotFound);
            
            URI uri;
			try {
				uri = new URI(url.toString());
			} catch (URISyntaxException e) {
				throw new ServletException(e);
			}
            file = new File(uri.getPath());
        } else {
        	//  Serve it up
        	sendTempFile(file, response);
        }
       
        return;

    }
    public static void sendTempFile(File file, HttpServletResponse response)
            throws IOException, FileNotFoundException {

        String mimeType = null;
        String filename = file.getName();
        if (filename.length() > 5) {
            if (filename.substring(filename.length() - 5, filename.length()).equals(".jpeg") || 
                filename.substring(filename.length() - 5, filename.length()).equals(".jpg")) {
                mimeType = "image/jpeg";
            }
            else if (filename.substring(filename.length() - 4, filename.length()).equals(".png")) {
                mimeType = "image/png";
            }
            else if (filename.substring(filename.length() - 4, filename.length()).equals(".gif")) {
                mimeType = "image/gif";
            }
        }
        sendTempFile(file, response, mimeType);
    }

    /**
     * Binary streams the specified file to the HTTP response in 1KB chunks
     *
     * @param file The file to be streamed.
     * @param response The HTTP response object.
     * @param mimeType The mime type of the file, null allowed.
     *
     * @throws IOException  if there is an I/O problem.
     * @throws FileNotFoundException  if the file is not found.
     */
    public static void sendTempFile(File file, HttpServletResponse response,
                                    String mimeType)
            throws IOException, FileNotFoundException {

        if (file.exists()) {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

            //  Set HTTP headers
            if (mimeType != null) {
                response.setHeader("Content-Type", mimeType);
            }
            response.setHeader("Content-Length", String.valueOf(file.length()));
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            response.setHeader("Last-Modified", sdf.format(new Date(file.lastModified())));

            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            byte[] input = new byte[1024];
            boolean eof = false;
            while (!eof) {
                int length = bis.read(input);
                if (length == -1) {
                    eof = true;
                } else {
                    bos.write(input, 0, length);
                }
            }
            bos.flush();
            bis.close();
            bos.close();
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        return;
    }

      /**
     * Perform a search/replace operation on a String
     * There are String methods to do this since (JDK 1.4)
     *
     * @param inputString  the String to have the search/replace operation.
     * @param searchString  the search String.
     * @param replaceString  the replace String.
     *
     * @return the String with the replacements made.
     */
    public static String searchReplace(String inputString,
                                       String searchString,
                                       String replaceString) {

        int i = inputString.indexOf(searchString);
        if (i == -1) {
            return inputString;
        }

        String r = "";
        r += inputString.substring(0, i) + replaceString;
        if (i + searchString.length() < inputString.length()) {
            r += searchReplace(inputString.substring(i + searchString.length()),
                               searchString,
                               replaceString);
        }

        return r;
    }
   
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Serve up charts for OLAP";
    }

}
