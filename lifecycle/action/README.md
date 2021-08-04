
# OmniFaces Oyena Action

The OmniFaces Oyena Action module delivers you with an action framework that you
can use with Eclipse Mojarra or Apache MyFaces.

## Prerequisites

Your project should already be configured to use Eclipse Mojarra or Apache
MyFaces.

## Configuration

To use it in your web application you will need to do add the following Maven
dependency:

```xml
    <dependency>
      <groupId>org.Manorrock.com.oyena</groupId>
      <artifactId>oyena-action</artifactId>
      <version>x.y.z</version>
    </dependency>
```

Where you need to replace x.y.z with the version you want to use.

## Using it

To actually use it you will need to do the following:

1. Create a CDI bean
2. Create a Facelet page

### Create a CDI bean

Create a CDI bean with the following content and use your IDE to resolve the
necessary imports:

```java
    @RequestScoped
    public class IndexPageBean implements Serializable {
 
      /**
       * Execute the page.
       * 
       * @return /index.xhtml
       */
      @ActionMapping("/index")
      public String execute() {
        return "/index.xhtml";
      }
    }
```

### Create a Facelet page

Create the /index.xhtml Facelet page in the root of your web application with the 
following content:

```xml
    <!DOCTYPE html>

    <html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://xmlns.jcp.org/jsf/html">
      <h:head>
        <title>Index page</title>
      </h:head>
	    
      <h:body>
        Hello from there!
      </h:body>
    </html>
```

## Try it

Deploy the web application to the server of your choice.

Assuming the web application is deployed at `/myapp` on your localhost server
listening on port 8080 you would browse to 
http://localhost:8080/myapp/action/index to try it.

Enjoy!

## More information

### Accessing request headers

If you want to be able to access request header parameters the ActionHeaderParameter
annotation can be used to achieve that.

```java
    @ActionMapping("/header")
    public String header(@ActionHeaderParameter("Accept-Encoding") String param) {
        return param;
    }
```

The example above sets the `param` method parameter to the value of the 
'Accept-Encoding' request header.

### Regular Expression mapping

If you want to use regular expression mapping for your action see the example
below on how to do that:

```java
    @ActionMapping("regex:/page[A-Z]")
    public String executePageAthroughZ() {
        return "/pageAthroughZ.xhtml";
    }
```

The example above matches against the regular expression '/page[A-Z]'. Note the
format of the regular expression is the same format as the `Pattern` class in the
`java.util.regex` package.

Now if you want to use capture groups you can do that as well. The example below
shows you how to do so.

```java
    @ActionMapping("regex:/(?<path>.*)")
    public String capturePath(@ActionPathParameter("path") String path) {
        return path;
    }
```

The example above uses a Java Regex pattern to create a regular expression
mapping and the ActionPathParameter annotation is then used to funnel the `path`
Regex capture group to the `path` method parameter.

### Accessing query parameters

If you want to be able to access query parameters the ActionQueryParameter
annotation can be used to achieve that.

```java
    @RestPath("/query")
    public String query(@ActionQueryParameter("param") String param) {
        return param;
    }
```

The example above sets the `param` method parameter to the query parameter `param`.

### Overriding the Servlet mapping

If you do not want to use the `/action/*` mapping that is setup by default for
Oyena Action you can add a servlet mapping to the web.xml file to change it:

```xml
    <web-app xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
	 version="4.0">
      <servlet-mapping>
        <servlet-name>Oyena Action Servlet</servlet-name>
        <url-pattern>/mymapping/*</url-pattern>
      </servlet-mapping>
    </web-app>
```
