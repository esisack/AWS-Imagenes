package ar.com.gcba.AWSImages.services;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

	void uploadFile(MultipartFile multipartFile);
	
	byte[] downloadFile(String keyName);
	
	MultipartFile uploadFileBase64(String stringBase64, String fileName);
	
	
}
