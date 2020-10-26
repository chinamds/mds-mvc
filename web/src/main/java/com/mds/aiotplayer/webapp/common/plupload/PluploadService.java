/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.plupload;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;

/**
 * Plupload Serviceģ�飬ͬPluploadʵ����һ������ΪҪ�õ�Spring web������������Բ��������Serviceģ��
 */
public class PluploadService {
	private static final String RESP_SUCCESS = "{\"jsonrpc\" : \"2.0\", \"result\" : \"success\", \"id\" : \"id\"}";
	private static final String RESP_ERROR = "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 101, \"message\": \"Failed to open input stream.\"}, \"id\" : \"id\"}";
	public static final String JSON = "application/json";
	public static final int BUF_SIZE = 2 * 1024;

    public static void upload(Plupload plupload, File pluploadDir){
        //String fileName = "" + System.currentTimeMillis()+plupload.getName();//�ڷ�����������Ψһ�ļ���
    	String fileName = plupload.getName();//�ڷ�����������Ψһ�ļ���
    	
        upload(plupload, pluploadDir, fileName);
    }

    private static void upload(Plupload plupload, File pluploadDir, String fileName){

    	String responseString = RESP_SUCCESS;
    	
		boolean isMultipart = ServletFileUpload.isMultipartContent(plupload.getRequest());
		
		if(isMultipart){
	        int chunks = plupload.getChunks();//�û��ϴ��ļ����ָ����ܿ���
	        int nowChunk = plupload.getChunk();//��ǰ�飬��0��ʼ
	
	        //����Request�������͵�ǿ��ת�����ܳ��������ļ�����SpringIOC��������multipartResolver���󼴿ɡ�
	        MultipartHttpServletRequest multipartHttpServletRequest  = (MultipartHttpServletRequest)plupload.getRequest();
	        //���Է���map��ֻ��һ����ֵ��
	        MultiValueMap map = multipartHttpServletRequest.getMultiFileMap();
	
	        if(map!=null){
	            try{
	                Iterator iterator = map.keySet().iterator();
	                while(iterator.hasNext()){
	
	                    String key = (String) iterator.next();
	                    List<MultipartFile> multipartFileList = (List<MultipartFile>) map.get(key);
	
	                    for(MultipartFile multipartFile : multipartFileList){//ѭ��ֻ����һ��
	
	                        plupload.setMultipartFile(multipartFile);//�ֶ���Plupload������MultipartFile����ֵ
	                        File targetFile = new File(FilenameUtils.concat(pluploadDir.getPath(), fileName));//�½�Ŀ���ļ���ֻ�б���д��ʱ�Ż���������
	                        if(chunks > 1){//�û��ϴ������ܿ�������1��Ҫ���кϲ�
	
	                            //File tempFile = new File(FilenameUtils.concat(pluploadDir.getPath(), multipartFile.getName()));
	                            //��һ��ֱ�Ӵ�ͷд�룬���ô�ĩ��д��
	                            savePluploadFile(multipartFile.getInputStream(), targetFile, nowChunk == 0 ? false : true);
	
	                            /*if(chunks-nowChunk==1){//ȫ�����Ѿ��ϴ���ϣ���ʱtargetFile��Ϊ�б���д������ڣ�Ҫ���ļ�����
	                                tempFile.renameTo(targetFile);
	                            }*/
	                        }
	                        else{
	                            //ֻ��һ�飬��ֱ�ӿ����ļ�����
	                            multipartFile.transferTo(targetFile);
	                        }
	                    }
	                }
	            }
	            catch (IOException e){
	            	responseString = RESP_ERROR;
	                e.printStackTrace();
	            }
	        }
		}else {
			responseString = RESP_ERROR;
		}
		
		/*if(nowChunk == chunks - 1){
	    	System.out.println("user:"+this.user);
	    	System.out.println("upload file:"+this.name);
	    	System.out.println("uplaod time:"+this.time);
	    }*/

		plupload.getResponse().setContentType(JSON);
		byte[] responseBytes = responseString.getBytes();
		plupload.getResponse().setContentLength(responseBytes.length);
		try {
			ServletOutputStream output = plupload.getResponse().getOutputStream();
			output.write(responseBytes);
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void savePluploadFile(InputStream inputStream, File tempFile, boolean flag){
        OutputStream outputStream = null;
        try {
            if(flag==false){
                //��ͷд��
                outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
            }
            else{
                //��ĩ��д��
                outputStream = new BufferedOutputStream(new FileOutputStream(tempFile, true));
            }
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = (inputStream.read(bytes)))>0){
                outputStream.write(bytes, 0, len);
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                outputStream.close();
                inputStream.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}