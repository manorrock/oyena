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
package com.manorrock.oyena.lifecycle.cdi;

import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.lifecycle.Lifecycle;
import jakarta.faces.lifecycle.LifecycleFactory;
import static jakarta.faces.lifecycle.LifecycleFactory.DEFAULT_LIFECYCLE;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * The CDI LifecycleFactory.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class CdiLifecycleFactory extends LifecycleFactory {

    /**
     * Constructor.
     *
     * @param wrapped the wrapped LifecycleFactory.
     */
    public CdiLifecycleFactory(LifecycleFactory wrapped) {
        super(wrapped);
    }

    @Override
    public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {
        // because we are using CDI to manage our lifecycles this is a no-op.
    }

    @Override
    public Lifecycle getLifecycle(String lifecycleId) {
        Lifecycle result = null;
        if (lifecycleId.equals(DEFAULT_LIFECYCLE)) {
            result = getWrapped().getLifecycle(lifecycleId);
        } else {
            BeanManager beanManager = getBeanManager();
            AnnotatedType<Lifecycle> type = beanManager.createAnnotatedType(Lifecycle.class);
            Set<Bean<?>> beans = beanManager.getBeans(type.getBaseType(), NamedLiteral.of(lifecycleId));
            Iterator<Bean<?>> iterator = beans.iterator();
            while (iterator.hasNext()) {
                Bean<?> bean = iterator.next();
                Named named = bean.getBeanClass().getAnnotation(Named.class);
                if (named.value().equals(lifecycleId)) {
                    result = (Lifecycle) CDI.current().select(named).get();
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * Get the bean manager.
     * 
     * @return the bean manager.
     */
    public BeanManager getBeanManager() {
        BeanManager beanManager = null;
        try {
            InitialContext initialContext = new InitialContext();
            beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");
        } catch (NamingException ne) {
        }
        if (beanManager == null) {
            try {
                InitialContext initialContext = new InitialContext();
                beanManager = (BeanManager) initialContext.lookup("java:comp/env/BeanManager");
            } catch (NamingException ne) {
            }
        }
        return beanManager;
    }

    @Override
    public Iterator<String> getLifecycleIds() {
        ArrayList<String> lifecycleIds = new ArrayList<>();
        getWrapped().getLifecycleIds().forEachRemaining(lifecycleIds::add);
        BeanManager beanManager = getBeanManager();
        AnnotatedType<Lifecycle> type = beanManager.createAnnotatedType(Lifecycle.class);
        Set<Bean<?>> beans = beanManager.getBeans(type.getBaseType());
        Iterator<Bean<?>> iterator = beans.iterator();
        while (iterator.hasNext()) {
            Bean<?> bean = iterator.next();
            if (bean.getBeanClass().isAnnotationPresent(Named.class)) {
                Named named = bean.getBeanClass().getAnnotation(Named.class);
                lifecycleIds.add(named.value());
            }
        }
        return lifecycleIds.iterator();
    }
}
