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
package com.jaspersoft.jasperserver.remote.services.async;

import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/*
*  @author inesterenko
*/
public class ImportRunnable extends BaseImportExportTaskRunnable<State> {

    protected final static Log log = LogFactory.getLog(ImportRunnable.class);

    public ImportRunnable(Map<String, Boolean> exportParams, InputStream stream) throws Exception{
        this(exportParams, stream, LocaleContextHolder.getLocale());
    }

    public ImportRunnable(Map<String, Boolean> exportParams, InputStream stream, Locale locale) throws Exception{
        super(new State());

        this.parameters = exportParams;
        this.file = copyToTempFile(stream);
        this.locale = locale;
    }

    @Override
    public void run(){
        try {
            List<WarningDescriptor> warningDescriptors = new ArrayList<WarningDescriptor>();
            state.setWarnings(warningDescriptors);
            service.doImport(file, parameters, organizationId, brokenDependenciesStrategy, locale, warningDescriptors);
            synchronized (state) {
                state.setPhase(Task.FINISHED);
                state.setMessage(localize("import.finished"));
            }
        } catch (ImportFailedException e) {
            synchronized (state) {
                if (ImportExportService.ERROR_CODE_IMPORT_TENANTS_NOT_MATCH.equals(e.getErrorCode())
                        || ImportExportService.ERROR_CODE_IMPORT_BROKEN_DEPENDENCIES.equals(e.getErrorCode())) {
                    state.setPhase(Task.PENDING);
                    state.setMessage("Import is pending");
                    log.info(String.format("Import task %s is in pending mode.", state.getId()));
                } else {
                    state.setPhase(Task.FAILED);
                    state.setMessage(e.getMessage());
                    log.error("Import failed: ", e);
                }

                ErrorDescriptor errorDescriptor = new ErrorDescriptor();
                errorDescriptor.setErrorCode(e.getErrorCode());
                errorDescriptor.setParameters(e.getParameters());
                state.setError(errorDescriptor);
            }
        } catch (Exception e) {
            synchronized (state) {
                state.setPhase(Task.FAILED);
                state.setMessage(e.getMessage());
            }
            log.error("Import failed: ", e);
        } finally {
            if (!state.getPhase().equals(Task.PENDING)) {
                if(!file.delete()){
                    log.error("Can't delete temp file "+file.getAbsolutePath());
                }
            }
            taskCompletionDate = new Date();
        }
    }

    protected File copyToTempFile(InputStream input) throws IOException {
        File tmp = File.createTempFile("import_", null);

        FileOutputStream fileStream = new FileOutputStream(tmp);

        byte[] buff = new byte[512];
        int read = input.read(buff, 0, "jsEncrypted".getBytes().length);
        if ((new String(buff, "UTF-8")).startsWith("jsEncrypted")) {
            parameters.put("isEncrypted", true);
            read = input.read(buff);
        }
        while (read > 0){
            fileStream.write(buff, 0, read);
            read = input.read(buff);
        }
        fileStream.flush();
        fileStream.close();

        return tmp;
    }

    public State getResult() {
        return state;
    }

    @Override
    public void prepare() {
        state.setMessage(localize("import.in.progress"));
    }
}
