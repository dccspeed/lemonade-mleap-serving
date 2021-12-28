# lemonade-mleap-serving
ML model serving using MLeap + Java + Dropwizard.

A custom project aimed to provide similar functionalities to [MLeap Serving](https://combust.github.io/mleap-docs/mleap-serving/), 
adding some requirements from [Lemonade Project](https://www.lemonade.org.br):

- Limit the list of fields returned;
- Support to specify MLeap model URL using environment variables;
- Load MLeap model from a HTTP/HTTPS URL or from a HDFS cluster;
- Different from MLeap Serving, each instance serves only a single model;
- Log all requests (TODO);
- Evaluate if there is a drift in data and execute actions, e.g. undeploy or retrain model (TODO);


## Building
First, clone the project and change to its directory:

```
$ git clone https://github.com/dccspeed/lemonade-mleap-serving.git
$ cd lemonade-mleap-serving.git
```

Project requires JDK 8+ and is using Maven. To build it, just run:
```
$ mvn package
```

An Über Jar will be created in `target` directory. 

## Execute and test the server:

```
$ export MLEAP_MODEL=file:///path_to_model
$ java -jar target/custom-mleap-serving-0.1.0.jar server config.yaml
```
If you want just to test, we suggest you to use the MLeap sample models:

- https://github.com/combust/mleap/raw/master/mleap-benchmark/src/main/resources/models/airbnb.model.lr.zip
- https://github.com/combust/mleap/raw/master/mleap-benchmark/src/main/resources/models/airbnb.model.rf.zip

To test the service, execute a HTTP call, for example, using `curl`:
```
$ curl localhost:8150/model/transform -X POST -d @/tmp/frame.airbnb.json -H 'Content-type: application/json'
```
The output is something like: 
```
{"status":"OK","message":"Success","rows":[{"instant_bookable_index_oh":[0.0],"host_is_superhost_index_oh":[0.0],"state_index":1.0,"number_of_reviews":56.0,"state_index_oh":[0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0],"price_prediction":232.62463916840318,"cleaning_fee":30.0,"square_feet":1250.0,"cancellation_policy_index":0.0,"state":"NY","features_lr":[1.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,4.140280956934401,3.5433405944541416,0.20448502691363585,0.7035254942843019,0.1070402397121551,2.001013740068328,3.4397510125672155,10.492952125986108],"security_deposit":50.0,"unscaled_continuous_features":[2.0,3.0,50.0,30.0,2.0,56.0,1250.0,90.0],"room_type_index_oh":[1.0,0.0],"bathrooms":2.0,"scaled_continuous_features":[4.140280956934401,3.5433405944541416,0.20448502691363585,0.7035254942843019,0.1070402397121551,2.001013740068328,3.4397510125672155,10.492952125986108],"bedrooms":3.0,"extra_people":2.0,"room_type_index":0.0,"review_scores_rating":90.0,"host_is_superhost_index":1.0,"host_is_superhost":"1.0","cancellation_policy":"strict","cancellation_policy_index_oh":[1.0,0.0,0.0,0.0,0.0,0.0],"instant_bookable":"1.0","instant_bookable_index":1.0,"room_type":"Entire home/apt"}]}
```
A sample "leap frame" is available at: 
- https://raw.githubusercontent.com/combust/mleap/master/mleap-benchmark/src/main/resources/leap_frame/frame.airbnb.json

## Limiting the result field list

You may want to limit returned fields. Change the input json file and add one or more of these options:
-  returnOnlyScalarFields (boolean): Ignore all fields of type Vector (DenseVector, SparseVector, Map, etc.)
- selectedFields (list of strings): Return only the fields listed.

For example, if json file contains:
```
{
  "returnOnlyScalarFields": true,
  "selectedFields": ["price_prediction"],
  "schema": {...}
}
```
Server will return only a single field:
```
{"status":"OK","message":"Success","rows":[{"price_prediction":232.62463916840318}]}
```

## Configuration

You may change the default port (8150) by changing the config.yaml file. The model may be also hard-coded. Notice that server 
is configured to read configuration file and expand environment variables.

## Docker support
A Docker image can be built using the provided Dockerfile. Change the `MLEAP_MODEL` 
environment variable defined in the Dockerfile to the correct model.
## Bugs, suggestions and questions
Please, open an issue.