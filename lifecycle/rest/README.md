
# Manorrock Oyena - REST lifecycle

This module delivers you with an REST framework that you can use with Eclipse
Mojarra or Apache MyFaces.

## Prerequisites

Your project should already be configured to use Eclipse Mojarra or Apache
MyFaces.

## Configuration

To use it in your web application you will need to add the following Maven
dependency:

```xml
    <dependency>
      <groupId>com.manorrock.oyena.lifecycle</groupId>
      <artifactId>oyena-lifecycle-rest</artifactId>
      <version>x.y.z</version>
    </dependency>
```

Where you need to replace x.y.z with the version you want to use.

## Using it

To actually use it you will need to create a CDI bean with the following content
and use your IDE to resolve the necessary imports:

```java
    @RequestScoped
    public class RestBean implements Serializable {
 
      /**
       * Execute "Hello World".
       *
       * @return "Hello World"
       */
      @RestPath("/helloWorld")
      public String helloWorld() {
        return "Hello World";
      }
    }
```

## Try it

Deploy the web application to the server of your choice.

Assuming the web application is deployed at `/myapp` on your localhost server
listening on port 8080 you would browse to 
http://localhost:8080/myapp/rest/helloWorld to try it.

Enjoy!

## More information

### Accessing request headers

If you want to be able to access request header parameters the RestHeaderParameter
annotation can be used to achieve that.

```java
    @RestPath("/header")
    public String header(@RestHeaderParameter("Accept-Encoding") String param) {
        return param;
    }
```

The example above sets the `param` method parameter to the value of the 
'Accept-Encoding' request header.

### Accessing query parameters

If you want to be able to access query parameters the RestQueryParameter
annotation can be used to achieve that.

```java
    @RestPath("/query")
    public String query(@RestQueryParameter("param") String param) {
        return param;
    }
```

The example above sets the `param` method parameter to the query parameter `param`.

### Regular expression mapping

If you want to use regular expression mapping the example below shows you how to
do so.

```java
    @RestPath("(?<path>.*)")
    public String helloWorld(@RestPathParameter("path") String path) {
        return path;
    }
```

The example above uses a Java Regex pattern to create a regular expression mapping
and the RestPathParameter annotation is then used to funnel the `path` Regex 
capture group to the `path` method parameter.

### Overriding the Servlet mapping

If you do not want to use the `/rest/*` mapping that is setup by default for
Oyena REST you can add a servlet mapping to the web.xml file to change it:

```xml
    <web-app xmlns="http://java.sun.com/xml/ns/javaee"
	 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
	 version="4.0">
      <servlet-mapping>
        <servlet-name>Oyena REST Servlet</servlet-name>
        <url-pattern>/mymapping/*</url-pattern>
      </servlet-mapping>
    </web-app>
```
