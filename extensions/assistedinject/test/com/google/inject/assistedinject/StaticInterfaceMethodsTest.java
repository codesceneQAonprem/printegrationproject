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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import junit.framework.TestCase;
 @SuppressWarnings("unchecked")
  private static <T> void mapInjectorTest(
      Key<T> mapKey,
      TypeLiteral<?> keyType,
      TypeLiteral<?> valueType,
      Iterable<? extends Module> modules,
      boolean allowDuplicates,
      int expectedMapBindings,
      MapResult<?, ?>... results) {
    Injector injector = Guice.createInjector(modules);
    Visitor<T> visitor = new Visitor<>();
    Binding<T> mapBinding = injector.getBinding(mapKey);
    MapBinderBinding<T> mapbinder = (MapBinderBinding<T>) mapBinding.acceptTargetVisitor(visitor);
    assertNotNull(mapbinder);
    assertEquals(mapKey, mapbinder.getMapKey());
    assertEquals(keyType, mapbinder.getKeyTypeLiteral());
    assertEquals(valueType, mapbinder.getValueTypeLiteral());
    assertEquals(allowDuplicates, mapbinder.permitsDuplicates());
    List<Map.Entry<?, Binding<?>>> entries = Lists.newArrayList(mapbinder.getEntries());
    List<MapResult<?, ?>> mapResults = Lists.newArrayList(results);
    assertEquals(
        "wrong entries, expected: " + mapResults + ", but was: " + entries,
        mapResults.size(),
        entries.size());

    for (MapResult<?, ?> result : mapResults) {
      Map.Entry<?, Binding<?>> found = null;
      for (Map.Entry<?, Binding<?>> entry : entries) {
        Object key = entry.getKey();
        Binding<?> value = entry.getValue();
        if (key.equals(result.k) && matches(value, result.v)) {
          found = entry;
          break;
        }
      }
      if (found == null) {
        fail("Could not find entry: " + result + " in remaining entries: " + entries);
      } else {
        assertTrue(
            "mapBinder doesn't contain: " + found.getValue(),
            mapbinder.containsElement(found.getValue()));
        entries.remove(found);
      }
    }

    if (!entries.isEmpty()) {
      fail("Found all entries of: " + mapResults + ", but more were left over: " + entries);
    }

    Key<?> mapOfJavaxProvider = mapKey.ofType(mapOfJavaxProviderOf(keyType, valueType));
    Key<?> mapOfProvider = mapKey.ofType(mapOfProviderOf(keyType, valueType));
    Key<?> mapOfSetOfProvider = mapKey.ofType(mapOfSetOfProviderOf(keyType, valueType));
    Key<?> mapOfSetOfJavaxProvider = mapKey.ofType(mapOfSetOfJavaxProviderOf(keyType, valueType));
    Key<?> mapOfCollectionOfProvider =
        mapKey.ofType(mapOfCollectionOfProviderOf(keyType, valueType));
    Key<?> mapOfCollectionOfJavaxProvider =
        mapKey.ofType(mapOfCollectionOfJavaxProviderOf(keyType, valueType));
    Key<?> mapOfSet = mapKey.ofType(mapOf(keyType, setOf(valueType)));
    Key<?> setOfEntry = mapKey.ofType(setOf(entryOfProviderOf(keyType, valueType)));
    Key<?> setOfJavaxEntry = mapKey.ofType(setOf(entryOfJavaxProviderOf(keyType, valueType)));
    Key<?> collectionOfProvidersOfEntryOfProvider =
        mapKey.ofType(collectionOfProvidersOf(entryOfProviderOf(keyType, valueType)));
    Key<?> collectionOfJavaxProvidersOfEntryOfProvider =
        mapKey.ofType(collectionOfJavaxProvidersOf(entryOfProviderOf(keyType, valueType)));
    Key<?> setOfExtendsOfEntryOfProvider =
        mapKey.ofType(setOfExtendsOf(entryOfProviderOf(keyType, valueType)));
    Key<?> mapOfKeyExtendsValueKey =
        mapKey.ofType(mapOf(keyType, TypeLiteral.get(Types.subtypeOf(valueType.getType()))));

    assertEquals(
        ImmutableSet.of(
            mapOfJavaxProvider,
            mapOfProvider,
            mapOfSetOfProvider,
            mapOfSetOfJavaxProvider,
            mapOfCollectionOfProvider,
            mapOfCollectionOfJavaxProvider,
            mapOfSet,
            mapOfKeyExtendsValueKey),
        mapbinder.getAlternateMapKeys());

    boolean entrySetMatch = false;
    boolean javaxEntrySetMatch = false;
    boolean mapJavaxProviderMatch = false;
    boolean mapProviderMatch = false;
    boolean mapSetMatch = false;
    boolean mapSetProviderMatch = false;
    boolean mapSetJavaxProviderMatch = false;
    boolean mapCollectionProviderMatch = false;
    boolean mapCollectionJavaxProviderMatch = false;
    boolean collectionOfProvidersOfEntryOfProviderMatch = false;
    boolean collectionOfJavaxProvidersOfEntryOfProviderMatch = false;
    boolean setOfExtendsOfEntryOfProviderMatch = false;
    boolean mapOfKeyExtendsValueKeyMatch = false;
    List<Object> otherMapBindings = Lists.newArrayList();
    List<Binding<?>> otherMatches = Lists.newArrayList();
    Multimap<Object, IndexedBinding> indexedEntries =
        MultimapBuilder.hashKeys().hashSetValues().build();
    Indexer indexer = new Indexer(injector);
    int duplicates = 0;
    for (Binding<?> b : injector.getAllBindings().values()) {
      boolean contains = mapbinder.containsElement(b);
      Object visited = ((Binding<T>) b).acceptTargetVisitor(visitor);
      if (visited instanceof MapBinderBinding) {
        if (visited.equals(mapbinder)) {
          assertTrue(contains);
        } else {
          otherMapBindings.add(visited);
        }
      } else if (b.getKey().equals(mapOfProvider)) {
        assertTrue(contains);
        mapProviderMatch = true;
      } else if (b.getKey().equals(mapOfJavaxProvider)) {
        assertTrue(contains);
        mapJavaxProviderMatch = true;
      } else if (b.getKey().equals(mapOfSet)) {
        assertTrue(contains);
        mapSetMatch = true;
      } else if (b.getKey().equals(mapOfSetOfProvider)) {
        assertTrue(contains);
        mapSetProviderMatch = true;
      } else if (b.getKey().equals(mapOfSetOfJavaxProvider)) {
        assertTrue(contains);
        mapSetJavaxProviderMatch = true;
      } else if (b.getKey().equals(mapOfCollectionOfProvider)) {
        assertTrue(contains);
        mapCollectionProviderMatch = true;
      } else if (b.getKey().equals(mapOfCollectionOfJavaxProvider)) {
        assertTrue(contains);
        mapCollectionJavaxProviderMatch = true;
      } else if (b.getKey().equals(setOfEntry)) {
        assertTrue(contains);
        entrySetMatch = true;
        // Validate that this binding is also a MultibinderBinding.
        assertThat(((Binding<T>) b).acceptTargetVisitor(visitor))
            .isInstanceOf(MultibinderBinding.class);
      } else if (b.getKey().equals(setOfJavaxEntry)) {
        assertTrue(contains);
        javaxEntrySetMatch = true;
      } else if (b.getKey().equals(collectionOfProvidersOfEntryOfProvider)) {
        assertTrue(contains);
        collectionOfProvidersOfEntryOfProviderMatch = true;
      } else if (b.getKey().equals(collectionOfJavaxProvidersOfEntryOfProvider)) {
        assertTrue(contains);
        collectionOfJavaxProvidersOfEntryOfProviderMatch = true;
      } else if (b.getKey().equals(setOfExtendsOfEntryOfProvider)) {
        assertTrue(contains);
        setOfExtendsOfEntryOfProviderMatch = true;
      } else if (b.getKey().equals(mapOfKeyExtendsValueKey)) {
        assertTrue(contains);
        mapOfKeyExtendsValueKeyMatch = true;
      } else if (contains) {
        if (b instanceof ProviderInstanceBinding) {
          ProviderInstanceBinding<?> pib = (ProviderInstanceBinding<?>) b;
          if (pib.getUserSuppliedProvider() instanceof ProviderMapEntry) {
            // weird casting required to workaround compilation issues with jdk6
            ProviderMapEntry<?, ?> pme =
                (ProviderMapEntry<?, ?>) (Provider) pib.getUserSuppliedProvider();
            Binding<?> valueBinding = injector.getBinding(pme.getValueKey());
            if (indexer.isIndexable(valueBinding)
                && !indexedEntries.put(pme.getKey(), valueBinding.acceptTargetVisitor(indexer))) {
              duplicates++;
            }
          }
        }
        otherMatches.add(b);
      }
    }
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
