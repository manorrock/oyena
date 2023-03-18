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
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;

/**
 * The default action method executor.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@ApplicationScoped
public class DefaultActionMethodExecutor implements ActionMethodExecutor {

    /**
     * Stores the action parameter producer.
     */
    @Inject
    private ActionParameterProducer actionParameterProducer;
    
    /**
     * Execute the method.
     *
     * @param facesContext the Faces context.
     * @param actionMappingMatch the action mapping match.
     */
    @Override
    public void execute(FacesContext facesContext, ActionMappingMatch actionMappingMatch) {
        Instance instance = CDI.current().select(
                actionMappingMatch.getBean().getBeanClass(), Any.Literal.INSTANCE);
        String viewId;
        try {
            Object[] parameters = new Object[actionMappingMatch.getMethod().getParameterCount()];
            if (parameters.length > 0) {
                for(int i=0; i<parameters.length; i++) {
                    parameters[i] = actionParameterProducer.produce(
                            facesContext,
                            actionMappingMatch,
                            actionMappingMatch.getMethod().getParameterTypes()[i],
                            actionMappingMatch.getMethod().getParameterAnnotations()[i]);
                }
            }
            viewId = (String) actionMappingMatch.getMethod().invoke(
                    instance.get(), parameters);
        } catch (Exception exception) {
            throw new FacesException(exception);
        }
        if (facesContext.getViewRoot() == null) {
            UIViewRoot viewRoot = facesContext.getApplication().getViewHandler().createView(facesContext, viewId);
            facesContext.setViewRoot(viewRoot);
        }
    }
}
