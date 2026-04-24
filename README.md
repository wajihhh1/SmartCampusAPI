
Question 1
The default configuration of JAX-RS means that a new instance of a resource class will be created with every new incoming HTTP request. This is what is known as per-request scope. It means that the instance variables in the resource class are not shared between requests and are destroyed after the response has been sent.

This has a huge data management implication. Resource instances are new, so you can no longer store common data (like a room or sensors) as instance variables in the resource class itself - it will be lost each time you make a request. To solve this, we implement a singleton DataStore class (with singleton design pattern) whose lifetime is throughout the application lifetime. All resource classes have access to the shared DataStore instance. We also use ConcurrentHashMap instead of a normal HashMap to eliminate a race condition that can arise when a large number of requests issue commands to access or modify data at the same time.

Question 2
HATEOAS (Hypermedia as the Engine of Application State) is the fact that API responses do not contain raw data, but instead links to the adjacent resources and actions that can be performed. As an illustration, our discovery endpoint will provide links to /api/v1/rooms and /api/v1/sensors so that the client does not have to hardcode the URLs to continue onward.

This is very advantageous to client developers as they do not have to memorise or hard-code the endpoint paths - the API itself provides instructions on what can be done. It also implies that in the event that the API alters its URL structure, clients which are dynamically following links will not require adjustment, contrary to clients which are using static documentation.

Question 3
Sending back only IDs consumes less network bandwidth as the response body is very small. Nevertheless, it requires the client to issue more HTTP requests to retrieve the information on each room separately, which adds latency and load to the server, particularly in cases when there are numerous rooms.

Full room objects require more bandwidth per response but allow the client to be self-sufficient in a single request, cutting down on round trips. In the majority of applications, full objects are preferable to returning a list but in cases where the list is very large, full object pagination would be the most appropriate.

Question 4
Yes, in my implementation DELETE is idempotent. Idempotent The result of making a request several times is the same as that of making the request once. Provided that a client makes a request with DELETE /api/v1/rooms/LIB-301 and the room is present, it will be deleted and 200 OK will be returned. In case of repeated request, the room does not exist anymore and a 404 Not Found is sent. The final state of the server is identical in the two cases - the room does not exist - and this fulfills the definition of idempotency. The code of response can vary but the server state does not alter during the second call.

Question 5
When a client makes a request with a Content-Type header of text/plain or application/xml, rather than application/json, the JAX-RS will automatically reject the request prior to it reaching our method. The framework returns an HTTP 415 Unsupported Media Type response, which means that the server does not support the given format. This helps to guard the API against invalid or unanticipated input without needing any manual verification within our resource procedures.

Question 6
A query parameter (e.g. /api/v1/sensors?type=CO2) is better to use than a path parameter (e.g. /api/v1/sensors/type/CO2) to filter as query parameters are optional. This allows /api/v1/sensors to either give all sensors (no query parameter) or a narrowed-down set of sensors (with a query parameter).

Using a path-based approach, you would have to have a different endpoint between filtered and unfiltered results, which is not in compliance with the principles of REST as it makes it look like there are two URLs of the same resource. The universally recognised standard of filtering, sorting, and searching collections is also query parameters, making the API more developer-friendly.

Question 7
The sub resource locator pattern enables us to pass on the treatment of nested paths to a special class.
The method is used to handle complexity in huge APIs. With all the nested endpoints being specified in a single resource class, it would be a very big and hard-to-maintain class. With the division of the responsibilities into the focused classes, each of the classes has one clear purpose; SensorResource is the one in charge of sensors, SensorReadingResource is the one in charge of readings. This conforms to the Single Responsibility Principle, is easier to test, and enables the classes of resource classes to be worked on by different developers without conflict.

Question 8
A 404 Not Found is usually an indication that the URL endpoint does not exist in the first place. Nevertheless, with a client POSTing a valid JSON body to a valid endpoint but with a roomId referring to a non-existent room, the endpoint was located successfully - the issue is with the content of the request body. The more appropriate status code would be HTTP 422 Unprocessable Entity since the request was correct and directed to the correct endpoint, but the server is unable to process it, as there is a semantic error in the data, namely a reference to a resource that does not exist.

Question 9

The revelation of raw Java stack traces is a considerable security threat due to a number of reasons. Stack traces tell you about the internal layout of the application, including the names of packages, classes, method names, and lines. With this information, an attacker can determine which frameworks and libraries are being used and look up any known vulnerabilities of those particular versions. Even file paths on the server, the structure of database queries, and flow of logic can be revealed in stack traces and can be used by an attacker to map the system and create targeted exploits. Our GlobalExceptionMapper is used to avoid this by intercepting all unforeseen errors and responding with a more generic 500 response with no internal information.

Question 10
With a JAX-RS filter based logging, the logging code is implemented centrally and applied automatically to all the requests and responses across the entire API. This is in accordance with the DRY principle (Don't Repeat Yourself). Either adding logging to each resource method separately would be a duplication of similar code, potentially leading to forgetting to log a new method, or it would be more difficult to modify the logging format in the future. The filters are also run at the framework level; that is, they intercept all requests, including those that do not make it to a resource method, providing a more comprehensive observability.