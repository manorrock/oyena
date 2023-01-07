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
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.context.FacesContext;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The default action mapping matcher.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
public class DefaultActionMappingMatcher implements ActionMappingMatcher {

    /**
     * Find the action mapping for the given bean.
     *
     * @param facesContext the Faces context.
     * @param bean the bean.
     * @return the action mapping match, or null if not found.
     */
    private ActionMappingMatch determineActionMappingMatch(FacesContext facesContext, Bean<?> bean) {
        ActionMappingMatch result = null;
        Class clazz = bean.getBeanClass();
        AnnotatedType annotatedType = CDI.current().getBeanManager().createAnnotatedType(clazz);
        Set<AnnotatedMethod> annotatedMethodSet = annotatedType.getMethods();
        for (AnnotatedMethod method : annotatedMethodSet) {
            if (method.isAnnotationPresent(ActionMapping.class)) {
                ActionMapping requestMapping = method.getAnnotation(ActionMapping.class);
                String mapping = requestMapping.value();
                String pathInfo = facesContext.getExternalContext().getRequestPathInfo();
                if (pathInfo != null) {
                    if (pathInfo.equals(mapping)) {
                        result = new ActionMappingMatch();
                        result.setBean(bean);
                        result.setMethod(method.getJavaMember());
                        result.setActionMapping(mapping);
                        result.setMappingType(ActionMappingType.EXACT);
                        result.setPathInfo(pathInfo);
                        break;
                    } else if (mapping.endsWith("*")) {
                        mapping = mapping.substring(0, mapping.length() - 1);
                        if (pathInfo.startsWith(mapping)) {
                            if (result == null) {
                                result = new ActionMappingMatch();
                                result.setBean(bean);
                                result.setMethod(method.getJavaMember());
                                result.setActionMapping(mapping);
                                result.setMappingType(ActionMappingType.PREFIX);
                                result.setPathInfo(pathInfo);
                            } else if (mapping.length() > result.getLength()) {
                                result.setBean(bean);
                                result.setMethod(method.getJavaMember());
                                result.setActionMapping(mapping);
                                result.setMappingType(ActionMappingType.PREFIX);
                                result.setPathInfo(pathInfo);
                            }
                        }
                    } else if (mapping.startsWith("*")) {
                        mapping = mapping.substring(1);
                        if (pathInfo.endsWith(mapping)) {
                            result = new ActionMappingMatch();
                            result.setBean(bean);
                            result.setMethod(method.getJavaMember());
                            result.setActionMapping(mapping);
                            result.setMappingType(ActionMappingType.EXTENSION);
                            result.setPathInfo(pathInfo);
                            break;
                        }
                    } else if (mapping.startsWith("regex:")) {
                        mapping = mapping.substring("regex:".length());
                        if (Pattern.matches(mapping, pathInfo)) {
                            result = new ActionMappingMatch();
                            result.setBean(bean);
                            result.setMethod(method.getJavaMember());
                            result.setActionMapping(mapping);
                            result.setMappingType(ActionMappingType.REGEX);
                            result.setPathInfo(pathInfo);
                            break;
                        }
                    }
                }
            }
            if (result != null
                    && (result.getMappingType().equals(ActionMappingType.EXACT)
                    || (result.getMappingType().equals(ActionMappingType.EXTENSION)))) {
                break;
            }
        }
        return result;
    }

    /**
     * Get the beans.
     *
     * @return the beans.
     */
    private Iterator<Bean<?>> getBeans() {
        Set<Bean<?>> beans = CDI.current().getBeanManager().getBeans(
                Object.class, Any.Literal.INSTANCE);
        return beans.iterator();
    }

    /**
     * Match the request to an action mapping.
     *
     * @param facesContext the Faces context.
     * @return the action mapping match.
     */
    @Override
    public ActionMappingMatch match(FacesContext facesContext) {
        ActionMappingMatch match = null;
        Iterator<Bean<?>> beans = getBeans();
        while (beans.hasNext()) {
            Bean<?> bean = beans.next();
            ActionMappingMatch candidate = determineActionMappingMatch(facesContext, bean);
            if (match == null) {
                match = candidate;
            } else if (candidate != null && candidate.getLength() > match.getLength()) {
                match = candidate;
            }
        }
        return match;
    }
}
