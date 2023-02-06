private static <T> void optionalInjectorTest(
      Key<T> keyType,
      Iterable<? extends Module> modules,
      int expectedOtherOptionalBindings,
      BindResult<?> expectedDefault,
      BindResult<?> expectedActual,
      BindResult<?> expectedUserLinkedActual) {
    if (expectedUserLinkedActual != null) {
      assertNull("cannot have actual if expecting user binding", expectedActual);
      assertNull("cannot have default if expecting user binding", expectedDefault);
    }

    Key<Optional<T>> optionalKey =
        keyType.ofType(RealOptionalBinder.optionalOf(keyType.getTypeLiteral()));
    Key<?> javaOptionalKey =
        keyType.ofType(RealOptionalBinder.javaOptionalOf(keyType.getTypeLiteral()));
    Injector injector = Guice.createInjector(modules);
    Binding<Optional<T>> optionalBinding = injector.getBinding(optionalKey);
    Visitor visitor = new Visitor();
    OptionalBinderBinding<Optional<T>> optionalBinder =
        (OptionalBinderBinding<Optional<T>>) optionalBinding.acceptTargetVisitor(visitor);
    assertNotNull(optionalBinder);
    assertEquals(optionalKey, optionalBinder.getKey());

    Binding<?> javaOptionalBinding = injector.getBinding(javaOptionalKey);
    OptionalBinderBinding<?> javaOptionalBinder =
        (OptionalBinderBinding<?>) javaOptionalBinding.acceptTargetVisitor(visitor);
    assertNotNull(javaOptionalBinder);
    assertEquals(javaOptionalKey, javaOptionalBinder.getKey());

    if (expectedDefault == null) {
      assertNull("did not expect a default binding", optionalBinder.getDefaultBinding());
      assertNull("did not expect a default binding", javaOptionalBinder.getDefaultBinding());
    } else {
      assertTrue(
          "expectedDefault: "
              + expectedDefault
              + ", actualDefault: "
              + optionalBinder.getDefaultBinding(),
          matches(optionalBinder.getDefaultBinding(), expectedDefault));
      assertTrue(
          "expectedDefault: "
              + expectedDefault
              + ", actualDefault: "
              + javaOptionalBinder.getDefaultBinding(),
          matches(javaOptionalBinder.getDefaultBinding(), expectedDefault));
    }

    if (expectedActual == null && expectedUserLinkedActual == null) {
      assertNull(optionalBinder.getActualBinding());
      assertNull(javaOptionalBinder.getActualBinding());

    } else if (expectedActual != null) {
      assertTrue(
          "expectedActual: "
              + expectedActual
              + ", actualActual: "
              + optionalBinder.getActualBinding(),
          matches(optionalBinder.getActualBinding(), expectedActual));
      assertTrue(
          "expectedActual: "
              + expectedActual
              + ", actualActual: "
              + javaOptionalBinder.getActualBinding(),
          matches(javaOptionalBinder.getActualBinding(), expectedActual));

    } else if (expectedUserLinkedActual != null) {
      assertTrue(
          "expectedUserLinkedActual: "
              + expectedUserLinkedActual
              + ", actualActual: "
              + optionalBinder.getActualBinding(),
          matches(optionalBinder.getActualBinding(), expectedUserLinkedActual));
      assertTrue(
          "expectedUserLinkedActual: "
              + expectedUserLinkedActual
              + ", actualActual: "
              + javaOptionalBinder.getActualBinding(),
          matches(javaOptionalBinder.getActualBinding(), expectedUserLinkedActual));
    }

    Key<Optional<javax.inject.Provider<T>>> optionalJavaxProviderKey =
        keyType.ofType(RealOptionalBinder.optionalOfJavaxProvider(keyType.getTypeLiteral()));
    Key<?> javaOptionalJavaxProviderKey =
        keyType.ofType(RealOptionalBinder.javaOptionalOfJavaxProvider(keyType.getTypeLiteral()));
    Key<Optional<Provider<T>>> optionalProviderKey =
        keyType.ofType(RealOptionalBinder.optionalOfProvider(keyType.getTypeLiteral()));
    Key<?> javaOptionalProviderKey =
        keyType.ofType(RealOptionalBinder.javaOptionalOfProvider(keyType.getTypeLiteral()));
    assertEquals(
        ImmutableSet.of(optionalJavaxProviderKey, optionalProviderKey),
        optionalBinder.getAlternateKeys());
    assertEquals(
        ImmutableSet.of(javaOptionalJavaxProviderKey, javaOptionalProviderKey),
        javaOptionalBinder.getAlternateKeys());