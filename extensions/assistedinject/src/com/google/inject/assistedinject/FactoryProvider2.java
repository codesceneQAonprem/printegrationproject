 Method defaultMethod = entry.getValue();
        MethodHandle handle = null;
        try {
          handle =
              superMethodHandle(
                  SuperMethodSupport.METHOD_LOOKUP, defaultMethod, factory, userLookups);
        } catch (ReflectiveOperationException e1) {
          // If the user-specified lookup failed, try again w/ the private lookup hack.
          // If _that_ doesn't work, try the below workaround.
          if (allowPrivateLookupFallback
              && SuperMethodSupport.METHOD_LOOKUP != SuperMethodLookup.PRIVATE_LOOKUP) {
            try {
              handle =
                  superMethodHandle(
                      SuperMethodLookup.PRIVATE_LOOKUP, defaultMethod, factory, userLookups);
            } catch (ReflectiveOperationException e2) {
              // ignored, use below workaround.
            }
          }
        }

        Supplier<String> failureMsg =
            () ->
                "Unable to use non-public factory "
                    + factoryRawType.getName()
                    + ". Please call"
                    + " FactoryModuleBuilder.withLookups(MethodHandles.lookup()) (with a"
                    + " lookups that has access to the factory), or make the factory"
                    + " public.";
        if (handle != null) {
          methodHandleBuilder.put(defaultMethod, handle);
        } else if (!allowMethodHandleWorkaround) {
          errors.addMessage(failureMsg.get());
        } else {
          boolean foundMatch = false;
          for (Method otherMethod : otherMethods.get(defaultMethod.getName())) {
            if (dataSoFar.containsKey(otherMethod) && isCompatible(defaultMethod, otherMethod)) {
              if (foundMatch) {
                errors.addMessage(failureMsg.get());
                break;
              } else {
                assistDataBuilder.put(defaultMethod, dataSoFar.get(otherMethod));
                foundMatch = true;
              }
            }
          }
          // We always expect to find at least one match, because we only deal with javac-generated
          // default methods. If we ever allow user-specified default methods, this will need to
          // change.
          if (!foundMatch) {
            throw new IllegalStateException("Can't find method compatible with: " + defaultMethod);
          }
        }
      }

      // If we generated any errors (from finding matching constructors, for instance), throw an
      // exception.
      if (errors.hasErrors()) {
        throw errors.toException();
      }

      assistDataByMethod = assistDataBuilder.buildOrThrow();
      methodHandleByMethod = methodHandleBuilder.buildOrThrow();
    } catch (ErrorsException e) {
      throw new ConfigurationException(e.getErrors().getMessages());
    }
  }