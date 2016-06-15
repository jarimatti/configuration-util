# Configuration Util

This is a quick example on how to create a reference configuration inside an
OSGi bundle and let user override it.  Implementation is pure Java and tests
are done with ScalaTest.

The idea is simple:

1. Set bundle configuration policy to optional.
2. Load reference configuration.
3. Override reference configuration with user provided values, if any.

The intended behaviour is captured in the test `ConfigurationUtilSpec.scala`.
 
