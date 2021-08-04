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
package com.manorrock.oyena.lifecycle.action;

import jakarta.faces.webapp.FacesServlet;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration.Dynamic;
import java.util.Set;

/**
 * The ServletContainerInitializer that automatically registers the Manorrock 
 * Oyena Acton Servlet and the '/action/*' mapping if the Manorrock Oyena
 * Action Servlet has not already been registered.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class ActionInitializer implements ServletContainerInitializer {   

    /**
     * On startup.
     * 
     * @param classes the classes.
     * @param servletContext the servlet context.
     * @throws ServletException when a Servlet error occurs.
     */
    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) 
            throws ServletException {
        if (servletContext.getServletRegistration("Manorrock Oyena Action Servlet") == null) {
            Dynamic dynamic = servletContext.addServlet("Manorrock Oyena Action Servlet", FacesServlet.class.getName());
            dynamic.addMapping("/action/*");
            dynamic.setInitParameter("jakarta.faces.LIFECYCLE_ID", ActionLifecycle.class.getName());
        }
    }
}
