/*
 * Copyright (c) 2002-2021 Manorrock.com. All Rights Reserved.
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
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The default REST parameter producer.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
public class DefaultRestParameterProducer implements RestParameterProducer {
    
    /**
     * Produce an instance for the given type.
     *
     * @param facesContext the Faces context.
     * @param restMappingMatch the REST mapping match.
     * @param parameterType the parameter type.
     * @param parameterAnnotations the parameter annotations.
     * @return the instance.
     */
    @Override
    public Object produce(FacesContext facesContext,
            RestMappingMatch restMappingMatch, Class<?> parameterType,
            Annotation[] parameterAnnotations) {
        
        RestHeaderParameter header = getRestHeaderParameterAnnotation(parameterAnnotations);
        if (header != null) {
            String[] value = facesContext.getExternalContext().getRequestHeaderValuesMap().get(header.value());
            return value != null ? value : new String[] {};
        }
        
        RestPathParameter path = getRestPathParameterAnnotation(parameterAnnotations);
        if (path != null) {
            Pattern pattern = Pattern.compile(restMappingMatch.getRestPath());
            Matcher matcher = pattern.matcher(restMappingMatch.getPathInfo());
            if (matcher.matches()) {
                return matcher.group(path.value());
            } else {
                throw new FacesException("Unable to match @RestPathParameter: " + path.value());
            }
        }

        RestQueryParameter query = getRestQueryParameterAnnotation(parameterAnnotations);
        if (query != null) {
            return facesContext.getExternalContext().getRequestParameterMap().get(query.value());
        }

        return CDI.current().select(parameterType, Any.Literal.INSTANCE).get();
    }

    /**
     * Get the @RestHeaderParameter annotation (if present).
     *
     * @return the @RestHeaderParameter annotation, or null if not present.
     */
    private RestHeaderParameter getRestHeaderParameterAnnotation(Annotation[] annotations) {
        RestHeaderParameter result = null;
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RestHeaderParameter) {
                    result = (RestHeaderParameter) annotation;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Get the @RestPathParameter annotation (if present).
     *
     * @return the @RestPathParameter annotation, or null if not present.
     */
    private RestPathParameter getRestPathParameterAnnotation(Annotation[] annotations) {
        RestPathParameter result = null;
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RestPathParameter) {
                    result = (RestPathParameter) annotation;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Get the @RestQueryParameter annotation (if present).
     *
     * @return the @RestQueryParameter annotation, or null if not present.
     */
    private RestQueryParameter getRestQueryParameterAnnotation(Annotation[] annotations) {
        RestQueryParameter result = null;
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RestQueryParameter) {
                    result = (RestQueryParameter) annotation;
                    break;
                }
            }
        }
        return result;
    }
}
