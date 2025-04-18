package com.example.javaproject.util;


import com.example.javaproject.constant.ConstantsForCOS;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;


public class COSUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(COSUtils.class);
	
	/**获取客户端实例*/
	public static COSClient getCOSClient() throws RuntimeException{
		String secretId = ConstantsForCOS.SECRET_ID;
		String secretKey = ConstantsForCOS.SECRET_KEY;
		COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
		// 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
		// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
		Region region = new Region(ConstantsForCOS.REGION_NANJING);
		ClientConfig clientConfig = new ClientConfig(region);
		// 这里建议设置使用 https 协议
		// 从 5.6.54 版本开始，默认使用了 https
		//clientConfig.setHttpProtocol(HttpProtocol.https);
		// 3 生成 cos 客户端。
		COSClient cosClient = new COSClient(cred, clientConfig);
		return cosClient;
	}

	/**向指定bucket上传文件，相对路径包含在objectName中，文件夹会自动创建*/
	public static void putObject(String bucketName, String objectName, InputStream inputStream){
		COSClient cosClient = getCOSClient();
		//String objectName = "abc/def.txt";
		// 这里创建一个 ByteArrayInputStream 来作为示例，实际中这里应该是您要上传的 InputStream 类型的流
		int inputStreamLength = 1024 * 1024;
		ObjectMetadata objectMetadata = new ObjectMetadata();
		// 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
		// 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
		objectMetadata.setContentLength(inputStreamLength);
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, objectMetadata);
		// 设置单链接限速（如有需要），不需要可忽略
		putObjectRequest.setTrafficLimit(8*1024*1024);
		try {
			PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
			System.out.println(putObjectResult.getRequestId());
		} catch (CosServiceException e) {
			e.printStackTrace();
		} catch (CosClientException e) {
			e.printStackTrace();
		}
	}

	/**向指定bucket上传文件，相对路径包含在objectName中，文件夹会自动创建*/
	public static void putObject(String bucketName, String objectName, File source){
		COSClient cosClient = getCOSClient();
		//String objectName = "abc/abc.txt";
		//String source = "abc.txt";
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, source);
		try {
			PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
			System.out.println(putObjectResult.getRequestId());
		} catch (CosServiceException cse) {
			cse.printStackTrace();
		} catch (CosClientException cce) {
			cce.printStackTrace();
		}
	}

	/**从指定bucket中拉取指定文件，下载到targetFile中(可选)*/
	public static void getObject(String bucketName, String objectName, String targetFile){
		//String key = "test/my_test.json";
		COSClient cosClient = getCOSClient();
		com.qcloud.cos.model.GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
		// 设置下载的单链接限速（如有需要），不需要可忽略
		getObjectRequest.setTrafficLimit(8*1024*1024);

		try {
			//File localFile = new File("my_test.json");
			ObjectMetadata objectMetadata = cosClient.getObject(getObjectRequest, new File(targetFile));
			System.out.println(objectMetadata.getContentLength());
		} catch (CosServiceException cse) {
			cse.printStackTrace();
		} catch (CosClientException cce) {
			cce.printStackTrace();
		}
	}


	public static void main(String[] args) {
		//putObject("xye-card-1252149893", "idcard/1425/ceshi.jpg", new File("D:\\Desktop\\ceshi.jpg"));
		getObject("xye-card-1252149893", "idcard/1425/ceshi.jpg", "D:\\ceshi.jpg");
	}



}
