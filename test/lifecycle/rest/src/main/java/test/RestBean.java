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

import com.manorrock.oyena.lifecycle.rest.RestHeaderParameter;
import com.manorrock.oyena.lifecycle.rest.RestPath;
import com.manorrock.oyena.lifecycle.rest.RestPathParameter;
import com.manorrock.oyena.lifecycle.rest.RestQueryParameter;
import jakarta.enterprise.context.RequestScoped;
import java.io.Serializable;

/**
 * The REST bean.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
@RequestScoped
public class RestBean implements Serializable {

    /**
     * Test the @RestHeaderParameter annotation.
     *
     * @param value the header parameter.
     * @return /index.xhtml
     */
    @RestPath("/header")
    public String header(@RestHeaderParameter("Accept-Encoding") String[] value) {
        return value[0];
    }

    /**
     * Test the @RestPath annotation.
     *
     * @return /index.xhtml
     */
    @RestPath("/helloWorld")
    public String path() {
        return "Hello World";
    }

    /**
     * Test the @RestQueryParameter annotation.
     *
     * @param param the query parameter.
     * @return /index.xhtml
     */
    @RestPath("/query")
    public String query(@RestQueryParameter("param") String param) {
        return param;
    }

    /**
     * Test the @RestPathParameter annotation.
     *
     * @param path the path.
     * @return /index.xhtml
     */
    @RestPath("/path/(?<path>.*)")
    public String pathParameter(@RestPathParameter("path") String path) {
        return path;
    }
}
