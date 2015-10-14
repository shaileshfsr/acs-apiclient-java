Answers Cloud Services API Client Library for Java
===================
Helper library for connecting to the Answers Cloud Services (ForeSee in particular) web API in a headless manner from Java. You can use this to simplify connecting to the ACS api without requiring a browser or user interaction to grant access to a particular account.
###Installation
Dependencies
 - com.squareup.okhttp: okhttp (2.5.0)
 - org.apache.logging.log4j: log4j-api (2.4)
 - org.apache.logging.log4j: log4j-core (2.4)
 - jdk <1.8>

Logging Setup (log4j2.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="INFO">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c{1}:%L - %msg%n" />
        </Console>

    </Appenders>
    <Loggers>
        <Root level="debug">
            <AppenderRef ref="Console" />
        </Root>
    </Loggers>
</Configuration>
```

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
```java
	//TestCustomDate()
	AcsApiDateRangeBuilder dateBuilder = new AcsApiDateRangeBuilder();
	Date dtStart = new GregorianCalendar(1977, 8, 15).getTime();
	Date dtEnd = new GregorianCalendar(1997, 8, 15).getTime();
	dateBuilder.SetCustomDateRange(dtStart, dtEnd);
	String result = dateBuilder.DateRange.ToAcsApiJson();
	// {"c":"G","r":"CUSTOM","f":"1977-09-15","l":"1997-09-15"}
	_logger.info(result);

	//TestCustomRelativeDate()
	Date stEnable = new GregorianCalendar(1977, 8, 15).getTime();
	dateBuilder = new AcsApiDateRangeBuilder(stEnable);
	dateBuilder.SetAbsoluteDateRangeSequence(2, AcsApiDateRange.AcsApiRangeSequence.DAYS, AcsApiDateRange.AcsApiPeriodModifier.PRIOR);
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"a":"1977-09-15","c":"G","r":"DAYS","n":2,"p":"PRIOR"}
	_logger.info(result);

	//TestCalendarYearDefined()
	dateBuilder = new AcsApiDateRangeBuilder();
	dateBuilder.SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange.YEAR, 2013);
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"c":"G","r":"YEAR","n":2013,"p":"DEFINED"}
	_logger.info(result);

	//TestTwoDaysPrior()
	dateBuilder = new AcsApiDateRangeBuilder();
	dateBuilder.SetAbsoluteDateRangeSequence(2, AcsApiDateRange.AcsApiRangeSequence.DAYS, AcsApiDateRange.AcsApiPeriodModifier.PRIOR);
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"c":"G","r":"DAYS","n":2,"p":"PRIOR"}
	_logger.info(result);

	//TestThirdMonthPrior()
	dateBuilder = new AcsApiDateRangeBuilder();
	dateBuilder.SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange.MONTH, 3, AcsApiDateRange.AcsApiPeriodModifier.PRIOR);
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"c":"G","r":"MONTH","n":3,"p":"PRIOR"}
	_logger.info(result);

	//TestCurrentMonth()
	dateBuilder = new AcsApiDateRangeBuilder();
	dateBuilder.SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange.YEAR, 2011, AcsApiDateRange.AcsApiPeriodModifier.PRIOR_YEAR);
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"c":"G","r":"YEAR","n":2011,"p":"PRIOR_YEAR"}
	_logger.info(result);

	//TestCustomCalendar()
	dateBuilder = new AcsApiDateRangeBuilder();
	dateBuilder.UseCustomCalendar("abc12345");
	dateBuilder.SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange.MONTH, 0, AcsApiDateRange.AcsApiPeriodModifier.CURRENT);
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"c":"G","r":"MONTH","k":"abc12345","p":"CURRENT"}
	_logger.info(result);

	//TestManualDateRange()
	AcsApiDateRange dateRange = new AcsApiDateRange();
	dateRange.Range = "YR";
	dateRange.CustomerKey = "abc12345";
	dateRange.PeriodModifier = AcsApiDateRange.AcsApiPeriodModifier.CURRENT.toString();
	result = dateRange.ToAcsApiJson();
	//{"r":"YR","k":"abc12345","p":"CURRENT"}
	_logger.info(result);
```
### Date Usage

