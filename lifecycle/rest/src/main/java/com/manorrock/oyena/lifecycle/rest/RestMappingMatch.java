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

import jakarta.enterprise.inject.spi.Bean;
import java.lang.reflect.Method;

/**
 * The REST mapping match class.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class RestMappingMatch {

    /**
     * Stores the bean.
     */
    private Bean<?> bean;

    /**
     * Stores the method.
     */
    private Method method;

    /**
     * Stores the path info.
     */
    private String pathInfo;

    /**
     * Stores the RestPath.
     */
    private String restPath;

    /**
     * Get the bean.
     *
     * @return the bean.
     */
    public Bean<?> getBean() {
        return bean;
    }

    /**
     * Get the length.
     *
     * @return the length.
     */
    public int getLength() {
        return pathInfo.length();
    }

    /**
     * Get the method.
     *
     * @return the method.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Get the path info.
     *
     * @return the path info.
     */
    public String getPathInfo() {
        return pathInfo;
    }

    /**
     * Get the REST path.
     *
     * @return the REST path.
     */
    public String getRestPath() {
        return restPath;
    }

    /**
     * Set the bean.
     *
     * @param bean the bean.
     */
    public void setBean(Bean<?> bean) {
        this.bean = bean;
    }

    /**
     * Set the method.
     *
     * @param method the method.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Set the path info.
     *
     * @param pathInfo the path info.
     */
    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    /**
     * Set the REST path.
     *
     * @param restPath the REST path.
     */
    public void setRestPath(String restPath) {
        this.restPath = restPath;
    }
}
