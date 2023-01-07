/*
 * Copyright (c) 2002-2023 Manorrock.com. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice, 
 *      this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *   3. Neither the name of the copyright holder nor the names of its 
 *      contributors may be used to endorse or promote products derived from this
 *      software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.oyena.lifecycle.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.IOException;
import java.io.Writer;

/**
 * The JSON (application/json) response writer.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
@RestResponseWriterContentType("application/json")
public class DefaultJsonResponseWriter implements RestResponseWriter {

    /**
     * Stores the JSON-B builder.
     */
    private final JsonbBuilder jsonbBuilder;
    
    /**
     * Stores the JSON-B context.
     */
    private Jsonb jsonb;
    
    /**
     * Constructor.
     */
    public DefaultJsonResponseWriter() {
        jsonbBuilder = JsonbBuilder.newBuilder();
        jsonb = jsonbBuilder.build();
    }
    
    /**
     * Write the response.
     *
     * @param facesContext the Faces context.
     */
    @Override
    public void writeResponse(FacesContext facesContext) {
        Object result = facesContext.getAttributes().get(
                RestLifecycle.class.getPackage().getName() + ".RestResult");
        if (result == null) {
            try {
                facesContext.getExternalContext().responseSendError(204, "No content");
                facesContext.responseComplete();
            } catch (IOException ioe) {
                throw new FacesException(ioe);
            }
        } else {
            try { 
               Writer writer = facesContext.getExternalContext().getResponseOutputWriter();
                writer.write(jsonb.toJson(result));
                writer.flush();
                facesContext.responseComplete();
            } catch (IOException ioe) {
                throw new FacesException(ioe);
            }
        }
    }
}
