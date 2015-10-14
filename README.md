Answers Cloud Services API Client Library for Java
===================
Helper library for connecting to the Answers Cloud Services (ForeSee in particular) web API in a headless manner from Java. You can use this to simplify connecting to the ACS api without requiring a browser or user interaction to grant access to a particular account.
###Installation
###Simple Usage
```java

private static Response MakeCurrentUserRequest(AcsApiClient apiClient){
	try{
		String getTarget = "https://portal2.foreseeresults.com/services/currentUser/";

		// request signature for the url: since /currentUser action is a GET action signature
		// should be generated for a GET method.  OAuthMessage includes definitions for most
		// major REST actions (and all REST actions you should encounter on Foresee services)
		String authorizeSignature = apiClient.getSignedUrl(OAuthMessage.GET, getTarget)
				
		// Make a standard REST request adding the signature calculated above
		// The authorizeSignature may be empty at this point, you will receive
		// a 401 from the server and must invoke InitializeOAuth() and try again
		// If the token has expired you will receive 401 and must InitializeOAuth()
		// and re-invoke as well
		Request request = new Request.Builder().url(postTarget)
				.header("Authorization", authorizeSignature).build();
		OkHttpClient client = new OkHttpClient();
		return client.newCall(request).execute();
	}catch(Exception exc){
		_logger.error("Failed to make current user request: " + exc.getMessage());
		return null;
	}
}

public static void main(String[] args){
	AcsApiClientConfig config = new AcsApiClientConfig(
		"clientId", "clientSecret", "user_name", "password");
	AcsApiClient apiClient = new AcsApiClient(config);

	Response response = MakeCurrentUserRequest(apiClient);

	// The first 401 means either the oauth engine hasn't been initialized yet
	// or the token has expired
	if(response.code() == 401){
		// InitializeOAuth will automatically request a new access token and store
		// it locally inside the AcsApiClient object.  The fully signed Authorization
		// header (including OAuth keyword) is retrieved from the library using
		// getSignedUrl().  Directly utilizing the access token isn't recommended
		apiClient.InitializeOAuth();
	
		response = MakeCurrentUserRequest(apiClient);
	}

	// work with your response
	// if(response.code() == 401) you are actually unauthorized
	// if(response.code() == 200) process your response data
	// etc
}

```
Note: The exact syntax of your RestEngine and how to invoke it may differ; this sample
looks and feels how OkHttp works.
###Errors
###Authentication
###Accessing without Credentials
###Date Criteria
###Fiscal Calendars
###Date Comparisons
