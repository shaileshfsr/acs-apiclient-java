Answers Cloud Services API Client Library for Java
===================
Helper library for connecting to the Answers Cloud Services (ForeSee in particular) web API in a headless manner from Java. You can use this to simplify connecting to the ACS api without requiring a browser or user interaction to grant access to a particular account.
###Installation
Dependencies
 - jdk <1.5+>: Recommended 1.7+ due to lack of SSL support
 - (optional for Date Support) com.fasterxml.jackson.core: jackson-core (2.6.1)
 - (optional for Date Support) com.fasterxml.jackson.core: jackson-annotations (2.6.1)
 - (optional for Date Support) com.fasterxml.jackson.core: jackson-databind (2.6.1)

Logging Setup (LoggerAbstraction class)
Implement this contract in your source code and provide an instance of your implementation
to the AcsApiClient.  Optionally: the AcsApiClient can receive null as a parameter and it will
ignore logging.
```java
public abstract class LoggerAbstraction {
    public abstract void debug(String message);
    public abstract void error(String message);
    public abstract void error(String message, Exception exc);
    public abstract void warn(String message);
    public abstract void info(String message);
}
```

###SSL/HTTPS Security Issues
If you are using a public release of the JDK earlier then 1.7 you will not be able to authenticate against our certificates
as they are too new and we no longer support certificate stacks from so long ago which are insecure.  Attempts to use this library 
will result in a crash if you do not override the certificate validation code of the Java framework and bypass certificate authentication 
(or program your own certificate validation mechanism to bring 1.5 and 1.6 up-to-date).

Warning: Bypassing certificates is dangerous!

An example for how to override the certificate management.
```java
static{
	TrustManager[] trustAllCertificates = new TrustManager[] {
		new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		}
	};

	HostnameVerifier trustAllHostnames = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true; // Just allow them all.
		}
	};

	try {
		System.setProperty("jsse.enableSNIExtension", "false");
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCertificates, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
	}
	catch (GeneralSecurityException e) {
		throw new ExceptionInInitializerError(e);
	}
}
```

###Simple Usage
```java

private static Response MakeCurrentUserRequest(AcsApiClient apiClient){
	try{
		String getTarget = "https://portal2.foreseeresults.com/services/currentUser/";

		// request signature for the url: since /currentUser action is a GET action signature
		// should be generated for a GET method.  OAuthMessage includes definitions for most
		// major REST actions (and all REST actions you should encounter on Foresee services)
		String authorizeSignature = apiClient.getSignedUrl(OAuthMessage.GET, getTarget);
				
		// Make a standard REST request adding the signature calculated above
		// The authorizeSignature may be empty at this point, you will receive
		// a 401 from the server and must invoke InitializeOAuth() and try again
		// If the token has expired you will receive 401 and must InitializeOAuth()
		// and re-invoke as well
		RequestAbstraction request = new RequestBuilderImp().builder().withUrl(postTarget)
				.withHeader("Authorization", authorizeSignature).build();
		ClientAbstraction client = new ClientBuilderImp().create();
		return client.newCall(request).execute();
	}catch(Exception exc){
		_logger.error("Failed to make current user request: " + exc.getMessage());
		return null;
	}
}

public static void main(String[] args){
	AcsApiClientConfig config = new AcsApiClientConfig(
		"clientId", "clientSecret", "user_name", "password");
		
	// bbax: LoggerImp() is a class you will define in your own source code
	// ClientBuilderImp(), RequestBuilderImp() and PostBodyBuilderImp() are all
	// provided by the API if you wish to use our libraries.  Otherwise the interfaces
	// can be implemented for your own desired HTTP engine following the contracts at:
	// com.foresee.interfaces.http.*
	AcsApiClient apiClient = new AcsApiClient(config, new ClientBuilderImp(), new RequestBuilderImp(), new PostBodyBuilderImp(), new LoggerImp());

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

### Date Usage
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
	dateBuilder.SetAbsoluteDateRangeSequence(2, 
		AcsApiDateRange.AcsApiRangeSequence.DAYS, 
		AcsApiDateRange.AcsApiPeriodModifier.PRIOR);
		
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
	dateBuilder.SetAbsoluteDateRangeSequence(2, 
		AcsApiDateRange.AcsApiRangeSequence.DAYS, 
		AcsApiDateRange.AcsApiPeriodModifier.PRIOR);
	
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"c":"G","r":"DAYS","n":2,"p":"PRIOR"}
	_logger.info(result);

	//TestThirdMonthPrior()
	dateBuilder = new AcsApiDateRangeBuilder();
	dateBuilder.SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange.MONTH, 3, 
		AcsApiDateRange.AcsApiPeriodModifier.PRIOR);
		
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"c":"G","r":"MONTH","n":3,"p":"PRIOR"}
	_logger.info(result);

	//TestCurrentMonth()
	dateBuilder = new AcsApiDateRangeBuilder();
	dateBuilder.SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange.YEAR, 2011, 
		AcsApiDateRange.AcsApiPeriodModifier.PRIOR_YEAR);
	
	result = dateBuilder.DateRange.ToAcsApiJson();
	//{"c":"G","r":"YEAR","n":2011,"p":"PRIOR_YEAR"}
	_logger.info(result);

	//TestCustomCalendar()
	dateBuilder = new AcsApiDateRangeBuilder();
	dateBuilder.UseCustomCalendar("abc12345");
	dateBuilder.SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange.MONTH, 0, 
		AcsApiDateRange.AcsApiPeriodModifier.CURRENT);
		
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