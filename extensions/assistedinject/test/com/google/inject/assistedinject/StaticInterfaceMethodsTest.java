/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.assistedinject;
 @SuppressWarnings({"unchecked","rawtypes"})privatestatic<T>voidoptionalInjectorTest(Key<T>keyType,Iterable<?extendsModule>modules,intexpectedOtherOptionalBindings,BindResult<?>expectedDefault,BindResult<?>expectedActual,BindResult<?>expectedUserLinkedActual){if(expectedUserLinkedActual!=null){assertNull("cannothaveactualifexpectinguserbinding",expectedActual);assertNull("cannothavedefaultifexpectinguserbinding",expectedDefault);}Key<Optional<T>>optionalKey=keyType.ofType(RealOptionalBinder.optionalOf(keyType.getTypeLiteral()));Key<?>javaOptionalKey=keyType.ofType(RealOptionalBinder.javaOptionalOf(keyType.getTypeLiteral()));Injectorinjector=Guice.createInjector(modules);Binding<Optional<T>>optionalBinding=injector.getBinding(optionalKey);Visitorvisitor=newVisitor();OptionalBinderBinding<Optional<T>>optionalBinder=(OptionalBinderBinding<Optional<T>>)optionalBinding.acceptTargetVisitor(visitor);assertNotNull(optionalBinder);assertEquals(optionalKey,optionalBinder.getKey());Binding<?>javaOptionalBinding=injector.getBinding(javaOptionalKey);OptionalBinderBinding<?>javaOptionalBinder=(OptionalBinderBinding<?>)javaOptionalBinding.acceptTargetVisitor(visitor);assertNotNull(javaOptionalBinder);assertEquals(javaOptionalKey,javaOptionalBinder.getKey());if(expectedDefault==null){assertNull("didnotexpectadefaultbinding",optionalBinder.getDefaultBinding());assertNull("didnotexpectadefaultbinding",javaOptionalBinder.getDefaultBinding());}else{assertTrue("expectedDefault:"+expectedDefault+",actualDefault:"+optionalBinder.getDefaultBinding(),matches(optionalBinder.getDefaultBinding(),expectedDefault));assertTrue("expectedDefault:"+expectedDefault+",actualDefault:"+javaOptionalBinder.getDefaultBinding(),matches(javaOptionalBinder.getDefaultBinding(),expectedDefault));}if(expectedActual==null&&expectedUserLinkedActual==null){assertNull(optionalBinder.getActualBinding());assertNull(javaOptionalBinder.getActualBinding());}elseif(expectedActual!=null){assertTrue("expectedActual:"+expectedActual+",actualActual:"+optionalBinder.getActualBinding(),matches(optionalBinder.getActualBinding(),expectedActual));assertTrue("expectedActual:"+expectedActual+",actualActual:"+javaOptionalBinder.getActualBinding(),matches(javaOptionalBinder.getActualBinding(),expectedActual));}elseif(expectedUserLinkedActual!=null){assertTrue("expectedUserLinkedActual:"+expectedUserLinkedActual+",actualActual:"+optionalBinder.getActualBinding(),matches(optionalBinder.getActualBinding(),expectedUserLinkedActual));assertTrue("expectedUserLinkedActual:"+expectedUserLinkedActual+",actualActual:"+javaOptionalBinder.getActualBinding(),matches(javaOptionalBinder.getActualBinding(),expectedUserLinkedActual));}Key<Optional<javax.inject.Provider<T>>>optionalJavaxProviderKey=keyType.ofType(RealOptionalBinder.optionalOfJavaxProvider(keyType.getTypeLiteral()));Key<?>javaOptionalJavaxProviderKey=keyType.ofType(RealOptionalBinder.javaOptionalOfJavaxProvider(keyType.getTypeLiteral()));Key<Optional<Provider<T>>>optionalProviderKey=keyType.ofType(RealOptionalBinder.optionalOfProvider(keyType.getTypeLiteral()));Key<?>javaOptionalProviderKey=keyType.ofType(RealOptionalBinder.javaOptionalOfProvider(keyType.getTypeLiteral()));assertEquals(ImmutableSet.of(optionalJavaxProviderKey,optionalProviderKey),optionalBinder.getAlternateKeys());assertEquals(ImmutableSet.of(javaOptionalJavaxProviderKey,javaOptionalProviderKey),javaOptionalBinder.getAlternateKeys())
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import junit.framework.TestCase;

/**
 * Test static methods in interfaces.
 *
 * @author tavianator@tavianator.com (Tavian Barnes)
 */
public class StaticInterfaceMethodsTest extends TestCase {

  private static class Thing {
    final int i;

    @Inject
    Thing(@Assisted int i) {
      this.i = i;
    }
  }

  private interface Factory {
    Thing create(int i);

    static Factory getDefault() {
      return Thing::new;
    }
  }

  public void testAssistedInjection() {
    Injector injector =
        Guice.createInjector(
            new AbstractModule() {
              @Override
              protected void configure() {
                install(new FactoryModuleBuilder().build(Factory.class));
              }
            });
    Factory factory = injector.getInstance(Factory.class);
    assertEquals(1, factory.create(1).i);
  }
}
