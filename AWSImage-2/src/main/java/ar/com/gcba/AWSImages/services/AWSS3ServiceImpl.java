package ar.com.gcba.AWSImages.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

	@Autowired
	private AmazonS3 amazonS3;
	
	@Value("${aws.s3.bucket}")
	private String bucketName;

	@Override
	@Async
	public void uploadFile(final MultipartFile multipartFile) {
		LOGGER.info("File upload in progress.");
		try {
			final File file = convertMultiPartFileToFile(multipartFile);
			uploadFileToS3Bucket(bucketName, file);
			LOGGER.info("File upload is completed.");
			file.delete();	// To remove the file locally created in the project folder.
		} catch (final AmazonServiceException ex) {
			LOGGER.info("File upload is failed.");
			LOGGER.error("Error= {} while uploading file.", ex.getMessage());
		}
	}

	@Override
	@Async
	public byte[] downloadFile(final String keyName) {
		byte[] content = null;
		LOGGER.info("Downloading an object with key= " + keyName);
		LOGGER.info("Downloading an object with key= " + bucketName);
		final S3Object s3Object = amazonS3.getObject(bucketName, keyName);

		final S3ObjectInputStream stream = s3Object.getObjectContent();
		try {
			content = IOUtils.toByteArray(stream);
			LOGGER.info("File downloaded successfully.");
			s3Object.close();
		} catch(final IOException ex) {
			LOGGER.info("IO Error Message= " + ex.getMessage());
		}
		return content;
	}

	
	@Override
	@Async
	public MultipartFile uploadFileBase64(String stringBase64, String fileName) {
		 byte[] decodedBytes = Base64
		          .getDecoder()
		          .decode(stringBase64);
		// fileName = getDocumentFullPath(fileName);
		return new MockMultipartFile(fileName, fileName, "image/jpg",  decodedBytes);
	}

	private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
		final File file = new File(multipartFile.getOriginalFilename());
		try (final FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(multipartFile.getBytes());
		} catch (final IOException ex) {
			LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
		}
		return file;
	}

	private void uploadFileToS3Bucket(final String bucketName, final File file) {
		final String uniqueFileName = file.getName();
		LOGGER.info("Uploading file with name= " + uniqueFileName);
		final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
		amazonS3.putObject(putObjectRequest);
	}
	
	 private String getDocumentFullPath(String doc) {
	        String[] prePath = doc.split("", 6);
	        String[] path = new String[5];

	        if (prePath.length > 5) {
	            for (int i = 0; i < 5; i++) {
	            path[i] = prePath[i];
	            }

	        }
	        else {
	             path = prePath;
	        }

	        return String.format("%s/%d", String.join("/",  path), doc);
	 }
}
