/*
 * Copyright (c) 2002-2021 Manorrock.com. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice, 
 *      this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *   3. Neither the name of the copyright holder nor the names of its 
 *      contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.oyena.converter.bytes;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

/**
 * A FacesConverter that can convert bytes notion to a value and vice versa.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class BytesConverter implements Converter {

    /**
     * Stores the gigabyte constant.
     */
    private static final long GIGABYTE = 1024L * 1024 * 1024;

    /**
     * Stores the kilobyte constant.
     */
    private static final long KILOBYTE = 1024L;

    /**
     * Stores the megabyte constant.
     */
    private static final long MEGABYTE = 1024L * 1024;
    
    /**
     * Stores the terabyte constant.
     */
    private static final long TERABYTE = 1024L * 1024 * 1024 * 1024;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String result = null;
        Long longValue = null;

        if (value instanceof Long) {
            longValue = (Long) value;
        } else if (value != null) {
            longValue = Long.valueOf(value.toString());
        }

        if (longValue != null) {
            if (longValue < KILOBYTE) {
                result = longValue.toString() + " bytes";
            } else if (longValue < MEGABYTE) {
                result = Long.toString(longValue / KILOBYTE) + " KB";
            } else if (longValue < GIGABYTE) {
                result = Long.toString(longValue / MEGABYTE) + " MB";
            } else if (longValue < TERABYTE) {
                result = Long.toString(longValue / GIGABYTE) + " GB";
            }
        }

        return result;
    }
}
