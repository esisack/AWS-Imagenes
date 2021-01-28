package ar.com.gcba.AWSImages.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
public class AWSS3Config {


	@Value("${aws.access_key_id}")
	private String accessKeyId;

	@Value("${aws.secret_access_key}")
	private String secretAccessKey;

	@Value("${aws.s3.region}")
	private String region;

	@Value("${aws.s3.end_point}")
	private String endpoint;

	@SuppressWarnings("deprecation")
	@Bean
	public AmazonS3 getAmazonS3Cient() {
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
	
		/*
		 * AmazonS3 s3 = AmazonS3ClientBuilder.standard() // .withEndpointConfiguration(
		 * new EndpointConfiguration(endpoint, region)) .withRegion(region)
		 * .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
		 * .build();
		 */

		ClientConfiguration myClientConfig = new ClientConfiguration();
		myClientConfig.setMaxConnections(200);
		myClientConfig.setProtocol(Protocol.HTTPS);
		System.setProperty("com.amazonaws.sdk.disableCertChecking", "true");
		AmazonS3 s3 = new AmazonS3Client(basicAWSCredentials, myClientConfig);

		s3.setEndpoint(endpoint);

		return s3;
	}
}
