Answers Cloud Services API Client Library for Java
===================
Helper library for connecting to the Answers Cloud Services (ForeSee in particular) web API in a headless manner from Java. You can use this to simplify connecting to the ACS api without requiring a browser or user interaction to grant access to a particular account.
###Installation
###Simple Usage
```java

		private static Response MakeCurrentUserRequest(AcsApiClient apiClient){
			try{
				String postTarget = "https://portal2.foreseeresults.com/services/currentUser/";
				Request request = new Request.Builder().url(postTarget)
						.header("Authorization", apiClient.getSignedUrl(OAuthMessage.GET, postTarget)).build();
				OkHttpClient client = new OkHttpClient();
				return client.newCall(request).execute();
			}catch(Exception exc){
				_logger.error("Failed to make current user request: " + exc.getMessage());
				return null;
			}
		}
		
        AcsApiClientConfig config = new AcsApiClientConfig(
                "clientId",
                "clientSecret",
                "user_name",
                "password");

        AcsApiClient apiClient = new AcsApiClient(config);
		Response response = MakeCurrentUserRequest(apiClient);
		if(response.code() == 401){
			apiClient.InitializeOAuth();
			response = MakeCurrentUserRequest(apiClient);
		}
		
		// work with your response
		// if(response.code() == 401) you are actually unauthorized
		// if(response.code() == 200) process your response data
		// etc
```
Note: The exact syntax of your RestEngine and how to invoke it may differ; this sample
looks and feels how OkHttp works.
###Errors
###Authentication
###Accessing without Credentials
###Date Criteria
###Fiscal Calendars
###Date Comparisons
