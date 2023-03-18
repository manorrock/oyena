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
package com.manorrock.oyena.lifecycle.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The default action parameter producer.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
public class DefaultActionParameterProducer implements ActionParameterProducer {

    /**
     * Produce an instance for the given type.
     *
     * @param facesContext the Faces context.
     * @param actionMappingMatch the Action mapping match.
     * @param parameterType the parameter type.
     * @param parameterAnnotations the parameter annotations.
     * @return the instance.
     */
    @Override
    public Object produce(FacesContext facesContext, ActionMappingMatch actionMappingMatch, Class<?> parameterType,
            Annotation[] parameterAnnotations) {
        
        ActionHeaderParameter header = getActionHeaderParameterAnnotation(parameterAnnotations);
        if (header != null) {
            return facesContext.getExternalContext().getRequestHeaderMap().get(header.value());
        }
        
        ActionPathParameter path = getActionPathParameterAnnotation(parameterAnnotations);
        if (path != null) {
            Pattern pattern = Pattern.compile(actionMappingMatch.getActionMapping());
            Matcher matcher = pattern.matcher(actionMappingMatch.getPathInfo());
            if (matcher.matches()) {
                return matcher.group(path.value());
            } else {
                throw new FacesException("Unable to match @ActionPathParameter: " + path.value());
            }
        }

        ActionQueryParameter query = getActionQueryParameterAnnotation(parameterAnnotations);
        if (query != null) {
            return facesContext.getExternalContext().getRequestParameterMap().get(query.value());
        }
        
        return CDI.current().select(parameterType, Any.Literal.INSTANCE).get();
    }
    
    /**
     * Get the @ActionHeaderParameter annotation (if present).
     *
     * @return the @ActionQueryParameter annotation, or null if not present.
     */
    private ActionHeaderParameter getActionHeaderParameterAnnotation(Annotation[] annotations) {
        ActionHeaderParameter result = null;
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ActionHeaderParameter ahp) {
                    result = ahp;
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * Get the @ActionPathParameter annotation (if present).
     *
     * @return the @RestPathParameter annotation, or null if not present.
     */
    private ActionPathParameter getActionPathParameterAnnotation(Annotation[] annotations) {
        ActionPathParameter result = null;
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ActionPathParameter app) {
                    result = app;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Get the @ActionQueryParameter annotation (if present).
     *
     * @return the @ActionQueryParameter annotation, or null if not present.
     */
    private ActionQueryParameter getActionQueryParameterAnnotation(Annotation[] annotations) {
        ActionQueryParameter result = null;
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ActionQueryParameter) {
                    result = (ActionQueryParameter) annotation;
                    break;
                }
            }
        }
        return result;
    }
}
