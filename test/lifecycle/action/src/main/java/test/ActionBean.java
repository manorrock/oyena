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
package test;

import com.manorrock.oyena.lifecycle.action.ActionHeaderParameter;
import com.manorrock.oyena.lifecycle.action.ActionMapping;
import com.manorrock.oyena.lifecycle.action.ActionQueryParameter;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * The action bean.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
@RequestScoped
public class ActionBean implements Serializable {
 
    /**
     * Execute the index action.
     * 
     * @return /index.xhtml
     */
    @ActionMapping("/index")
    public String execute() {
        return "/index.xhtml";
    }
    
    /**
     * Execute the header action.
     * 
     * @param header the <code>header</code> header parameter.
     * @return /index.xhtml
     */
    @ActionMapping("/header")
    public String executeHeader(@ActionHeaderParameter("header") String header) {
        return "/index.xhtml";
    }
    
    /**
     * Execute the page 2 action.
     * 
     * @param request the HTTP servlet request.
     * @return /index.xhtml
     */
    @ActionMapping("/page2") 
    public String executePage2(HttpServletRequest request) {
        return "/index.xhtml";
    }
    
    /**
     * Execute the page 3 action.
     * 
     * @param request the HTTP servlet request.
     * @param facesContext the Faces context.
     * @return /index.xhtml
     */
    @ActionMapping("/page3") 
    public String executePage3(HttpServletRequest request, FacesContext facesContext) {
        return "/index.xhtml";
    }
    
    /**
     * Execute the page 4 action.
     * 
     * @param request the HTTP servlet request.
     * @param facesContext the Faces context.
     * @return /index.xhtml
     */
    @ActionMapping("regex:/page[A-Z]")
    public String executePageAthroughZ(HttpServletRequest request, FacesContext facesContext) {
        return "/index.xhtml";
    }
    
    /**
     * Execute the mypath action.
     * 
     * @param request the HTTP servlet request.
     * @param facesContext the Faces context.
     * @return /index.xhtml
     */
    @ActionMapping("regex:/mypath/(?<path>.*)")
    public String executeMyPath(HttpServletRequest request, FacesContext facesContext) {
        return "/index.xhtml";
    }
    
    /**
     * Execute the myquery action.
     * 
     * @param q the <code>q</code> query parameter.
     * @return /index.xhtml
     */
    @ActionMapping("/myquery")
    public String executeMyQuery(@ActionQueryParameter("q") String q) {
        return "/index.xhtml";
    }
}
