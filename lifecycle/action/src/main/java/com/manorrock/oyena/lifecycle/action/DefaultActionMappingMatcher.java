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

import static com.manorrock.oyena.lifecycle.action.ActionMappingType.EXACT;
import static com.manorrock.oyena.lifecycle.action.ActionMappingType.EXTENSION;
import static com.manorrock.oyena.lifecycle.action.ActionMappingType.PREFIX;
import static com.manorrock.oyena.lifecycle.action.ActionMappingType.REGEX;
import jakarta.enterprise.context.ApplicationScoped;
import static jakarta.enterprise.inject.Any.Literal.INSTANCE;
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
        boolean done = false;
        for (AnnotatedMethod method : annotatedMethodSet) {
            if (method.isAnnotationPresent(ActionMapping.class)) {
                ActionMapping requestMapping = method.getAnnotation(ActionMapping.class);
                result = processAnnotatedMethod(requestMapping, facesContext, result, bean, method);
                if (result != null) {
                    switch (result.getMappingType()) {
                        case EXACT, EXTENSION, REGEX ->
                            done = true;
                    }
                }
            }
            if (result != null
                    && (result.getMappingType().equals(EXACT)
                    || (result.getMappingType().equals(EXTENSION)))) {
                done = true;
            }
            if (done) {
                break;
            }
        }
        return result;
    }

    private ActionMappingMatch processAnnotatedMethod(ActionMapping requestMapping, FacesContext facesContext, ActionMappingMatch result, Bean<?> bean, AnnotatedMethod method) {
        String mapping = requestMapping.value();
        String pathInfo = facesContext.getExternalContext().getRequestPathInfo();
        if (pathInfo != null) {
            ActionMappingType mappingType = determineActionMappingType(mapping, pathInfo);
            if (mappingType != null) {
                switch (mappingType) {
                    case EXACT -> {
                        result = new ActionMappingMatch();
                        result.setBean(bean);
                        result.setMethod(method.getJavaMember());
                        result.setActionMapping(mapping);
                        result.setMappingType(EXACT);
                        result.setPathInfo(pathInfo);
                    }
                    case PREFIX -> {
                        mapping = mapping.substring(0, mapping.length() - 1);
                        if (pathInfo.startsWith(mapping)) {
                            if (result == null) {
                                result = new ActionMappingMatch();
                                result.setBean(bean);
                                result.setMethod(method.getJavaMember());
                                result.setActionMapping(mapping);
                                result.setMappingType(PREFIX);
                                result.setPathInfo(pathInfo);
                            } else if (mapping.length() > result.getLength()) {
                                result.setBean(bean);
                                result.setMethod(method.getJavaMember());
                                result.setActionMapping(mapping);
                                result.setMappingType(PREFIX);
                                result.setPathInfo(pathInfo);
                            }
                        }
                    }
                    case EXTENSION -> {
                        mapping = mapping.substring(1);
                        if (pathInfo.endsWith(mapping)) {
                            result = new ActionMappingMatch();
                            result.setBean(bean);
                            result.setMethod(method.getJavaMember());
                            result.setActionMapping(mapping);
                            result.setMappingType(EXTENSION);
                            result.setPathInfo(pathInfo);
                        }
                    }
                    case REGEX -> {
                        ActionMappingMatch regexMatch = determineRegexMatch(mapping, pathInfo, bean, method);
                        if (regexMatch != null) {
                            result = regexMatch;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Determine the mapping type.
     *
     * @param mapping the mapping.
     * @param pathInfo the path info.
     */
    private ActionMappingType determineActionMappingType(String mapping, String pathInfo) {
        ActionMappingType result = null;
        if (mapping.equals(pathInfo)) {
            result = EXACT;
        } else if (mapping.endsWith("*")) {
            result = PREFIX;
        } else if (mapping.startsWith("*")) {
            result = EXTENSION;
        } else if (mapping.startsWith("regex:")) {
            result = REGEX;
        }
        return result;
    }

    /**
     * Determine if this is a "regex:" match.
     *
     * @param mapping the mapping.
     * @param pathInfo the path info.
     * @param bean the bean.
     * @param method the method.
     * @return the match, or null if not found.
     */
    private ActionMappingMatch determineRegexMatch(
            String mapping,
            String pathInfo,
            Bean<?> bean,
            AnnotatedMethod<?> method) {

        ActionMappingMatch regexMatch = null;
        mapping = mapping.substring("regex:".length());
        if (Pattern.matches(mapping, pathInfo)) {
            regexMatch = new ActionMappingMatch();
            regexMatch.setBean(bean);
            regexMatch.setMethod(method.getJavaMember());
            regexMatch.setActionMapping(mapping);
            regexMatch.setMappingType(REGEX);
            regexMatch.setPathInfo(pathInfo);
        }
        return regexMatch;
    }

    /**
     * Get the beans.
     *
     * @return the beans.
     */
    private Iterator<Bean<?>> getBeans() {
        Set<Bean<?>> beans = CDI.current().getBeanManager().getBeans(
                Object.class, INSTANCE);
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
