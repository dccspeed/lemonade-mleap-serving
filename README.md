# lemonade-mleap-serving
ML model serving using MLeap + Java + Dropwizard.

A custom project aimed to provide similar functionalities to [MLeap Serving](https://combust.github.io/mleap-docs/mleap-serving/), 
adding some requirements from [Lemonade Project](https://www.lemonade.org.br):

- Limit the list of fields returned;
- Support to specify MLeap model URL using environment variables;
- Load MLeap model from a HTTP/HTTPS URL or from a HDFS cluster;
- Log all requests (TODO);
- Evaluate if there is a drift in data and execute actions, e.g. undeploy or retrain model (TODO);
