Tests for [Restful-Booker API](https://restful-booker.herokuapp.com/) using [REST Assured](https://rest-assured.io/), [Retrofit](https://square.github.io/retrofit/) and [Feign](https://github.com/OpenFeign/feign) as HTTP client.

## Running tests
Using Gradle
```
gradle test -DTAG=tag_option
```
Using Docker
```
docker build . image_name 
docker run -e TAG=tag_option image_name
```
Tag options: `create`, `delete`, `search`, `update`.
## Building test report
```
gradle allureserve
```
