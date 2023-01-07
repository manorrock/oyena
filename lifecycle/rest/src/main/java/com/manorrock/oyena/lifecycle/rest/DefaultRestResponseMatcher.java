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
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import java.util.Iterator;
import java.util.Set;

/**
 * The default REST response matcher.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
public class DefaultRestResponseMatcher implements RestResponseMatcher {

    /**
     * Stores the bean manager.
     */
    @Inject
    private BeanManager beanManager;

    /**
     * Get the response writer for the response content type.
     *
     * @param responseContentType the response content type.
     * @return the response writer.
     */
    @Override
    public RestResponseWriter getResponseWriter(String responseContentType) {
        RestResponseWriter result = null;
        AnnotatedType<RestResponseWriter> type = beanManager.createAnnotatedType(RestResponseWriter.class);
        Set<Bean<?>> beans = beanManager.getBeans(type.getBaseType());
        Iterator<Bean<?>> iterator = beans.iterator();
        while (iterator.hasNext()) {
            Bean<?> bean = iterator.next();
            RestResponseWriterContentType contentType = bean.getBeanClass().getAnnotation(RestResponseWriterContentType.class);
            if (contentType != null && contentType.value().equals(responseContentType)) {
                result = (RestResponseWriter) CDI.current().select(bean.getBeanClass()).get();
                break;
            }
        }
        if (result == null) {
            beans = beanManager.getBeans(type.getBaseType(), new Default.Literal());
            iterator = beans.iterator();
            Bean<?> bean = iterator.next();
            result = (RestResponseWriter) CDI.current().select(bean.getBeanClass()).get();
        }
        return result;
    }
}
