Answers Cloud Services API Client Library for Java
===================
Helper library for connecting to the Answers Cloud Services (ForeSee in particular) web API in a headless manner from Java. You can use this to simplify connecting to the ACS api without requiring a browser or user interaction to grant access to a particular account.
###Installation
###Simple Usage
```java
        AcsApiClientConfig config = new AcsApiClientConfig(
                "clientId",
                "clientSecret",
                "user_name",
                "password");

        AcsApiClient apiClient = new AcsApiClient(config);
		SomeRestEngine eng = new SomeRestEngine(...);
		
		string url = "some foresee endpoint";
		string signature = apiClient.getSignedUrl("GET|POST|PUT|etc", url)
		request = eng.RequestBuilder().url(url).
			header("Authorization", signature).build();
		request.execute();
```
Note: The exact syntax of your RestEngine and how to invoke it may differ; this sample
looks and feels how OkHttp works.
###Errors
###Authentication
###Accessing without Credentials
###Date Criteria
###Fiscal Calendars
###Date Comparisons
