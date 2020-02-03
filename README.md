# Hunter Six - Java Spring RESTful API Test

## How to build
```./gradlew clean build```

## How to test
```./gradlew test```

## Exercises
### Exercise 1
Make the tests run green (there should be one failing test)

####solution: Person class' counter field is updated to be static

### Exercise 2
Update the existing `/person/{lastName}/{firstName}` endpoint to return an appropriate RESTful response when the requested person does not exist in the list
- prove your results

####solution: tests are added to cover.
curl -X GET \
  http://localhost:8080/person/smith/terry \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' 
### Exercise 3
Write a RESTful API endpoint to retrieve a list of all people with a particular surname
- pay attention to what should be returned when there are no match, one match, multiple matches
- prove your results

####solution: test cases are added to cover



curl -X GET \
  http://localhost:8080/person/smith \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' 
### Exercise 4
Write a RESTful API endpoint to add a new value to the list
- pay attention to what should be returned when the record already exists
- prove your resutls

####soluiton: tests are added to cover the cases and the below requests can be used

curl -X POST \
  http://localhost:8080/person \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -d '{
	"firstName":"can",
	"lastName": "smith"
}'

