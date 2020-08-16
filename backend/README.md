# Semantics and definitions

* a configuration value can be of any type but is represented as a string
* a configuration key can be any non blank string and must be non null
* a configuration value is identified by a unique tuple of key and a reference time 
* by default, the reference time is the time a query is processed
* a configuration entry consists of a configuration key value pair with all of its meta data
* if no configuration entry could be found, a null value will be returned as the configuration value

# TODOs

* Find a way to register integration tests annotated with @IntegrationTest with surefire (exclusion) / failsafe (inclusion)
