package vn.sesgroup.hddt.user.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.api.message.JSONRoot;
import com.api.message.Msg;
import com.api.message.MsgHeader;
import com.api.message.MsgRsp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import vn.sesgroup.hddt.dto.FileInfo;
import vn.sesgroup.hddt.user.dao.CTTNCNDAO;
import vn.sesgroup.hddt.utility.Commons;
import vn.sesgroup.hddt.utility.Json;
import vn.sesgroup.hddt.utility.UpdateSignedMultiBillReq;
@RestController
@RequestMapping(value = "/cttncn")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class CTTNCNController {
	Commons commons = new Commons();
	@Autowired private  CTTNCNDAO dao;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST,
			consumes = {MediaType.APPLICATION_JSON_VALUE},		//MediaType.TEXT_PLAIN_VALUE, 
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> list(@RequestBody JSONRoot jsonRoot) throws Exception{
		MsgRsp rsp = dao.list(jsonRoot);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return ResponseEntity.ok()
				.headers(headers)
				.cacheControl(CacheControl.noCache())
				.body(rsp);
	}
	
	@RequestMapping(value = "/crud", method = RequestMethod.POST,
			consumes = {MediaType.APPLICATION_JSON_VALUE},		//MediaType.TEXT_PLAIN_VALUE, 
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> crud(@RequestBody JSONRoot jsonRoot) throws Exception{
		MsgRsp rsp = dao.crud(jsonRoot);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return ResponseEntity.ok()
				.headers(headers)
				.cacheControl(CacheControl.noCache())
				.body(rsp);
	}
	
	@RequestMapping(value = "/detail/{_id}", method = RequestMethod.POST,
			consumes = {MediaType.APPLICATION_JSON_VALUE},		//MediaType.TEXT_PLAIN_VALUE, 
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> detail(@RequestBody JSONRoot jsonRoot
			, @PathVariable(name = "_id", required = false) String _id) throws Exception{
		MsgRsp rsp = dao.detail(jsonRoot, _id);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return ResponseEntity.ok()
				.headers(headers)
				.cacheControl(CacheControl.noCache())
				.body(rsp);
	}
	
	@RequestMapping(value = "/get-file-for-sign", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, // MediaType.TEXT_PLAIN_VALUE,
			produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public ResponseEntity<?> getFilesForSign(@RequestBody JSONRoot jsonRoot) throws Exception {
		FileInfo fileInfo = dao.getFileForSign(jsonRoot);

		HttpHeaders headers = new HttpHeaders();
		headers.add("content-disposition", "attachment; filename=" + "template.data");
		headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);

		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return ResponseEntity.ok().headers(headers).cacheControl(CacheControl.noCache())
				.body(SerializationUtils.serialize(fileInfo));
	}

	@RequestMapping(value = "/sign-single", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> agentSignFile(HttpServletRequest req,
			MultipartHttpServletRequest multipartHttpServletRequest,
			@RequestParam(name = "Base64JsonRoot", defaultValue = "") String _Base64JsonRoot
		) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		
		JSONRoot jsonRoot = null;		
		try {
			jsonRoot = Json.serializer().fromJson(commons.decodeBase64ToString(_Base64JsonRoot), new TypeReference<JSONRoot>() {
			});
		}catch(Exception e) {
			throw new Exception("Lỗi dữ liệu đầu vào");
		}
		
		Msg msg = jsonRoot.getMsg();
		MsgHeader header = msg.getMsgHeader();
		Object objData = msg.getObjData();
		
		JsonNode jsonData = null;
		if(objData != null) {
			jsonData = Json.serializer().nodeFromObject(msg.getObjData());
		}else{
			throw new Exception("Lỗi dữ liệu đầu vào");
		}
		
		InputStream is = multipartHttpServletRequest.getFile("XMLFileSigned").getInputStream();
		
		return ResponseEntity.ok()
				.headers(headers)
				.cacheControl(CacheControl.noCache())
				.body(dao.signSingle(is, jsonRoot));
	}
	
	@RequestMapping(value = "/import-data", method = RequestMethod.POST,
			consumes = {MediaType.APPLICATION_JSON_VALUE},		//MediaType.TEXT_PLAIN_VALUE, 
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> importExcel(@RequestBody JSONRoot jsonRoot) throws Exception{
		MsgRsp rsp = dao.importExcel(jsonRoot);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return ResponseEntity.ok()
				.headers(headers)
				.cacheControl(CacheControl.noCache())
				.body(rsp);
	}
	
	@RequestMapping(value = "/get-file-for-signAll", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, // MediaType.TEXT_PLAIN_VALUE,
			produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public ResponseEntity<?> getFilesForSignAll(@RequestBody JSONRoot jsonRoot) throws Exception {
		FileInfo fileInfo = dao.getFileForSignAll(jsonRoot);

		HttpHeaders headers = new HttpHeaders();
		headers.add("content-disposition", "attachment; filename=" + "template.data");
		headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);

		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return ResponseEntity.ok().headers(headers).cacheControl(CacheControl.noCache())
				.body(SerializationUtils.serialize(fileInfo));
	}
	@RequestMapping(value = "/signAll", method = RequestMethod.POST, consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> agentSignFileAll(HttpServletRequest req,
			MultipartHttpServletRequest multipartHttpServletRequest,
			@RequestParam(name = "Base64JsonRoot", defaultValue = "") String _Base64JsonRoot,
			@RequestParam(name = "Ten", defaultValue = "") String ten
		) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		
		JSONRoot jsonRoot = null;		
		try {
			jsonRoot = Json.serializer().fromJson(commons.decodeBase64ToString(_Base64JsonRoot), new TypeReference<JSONRoot>() {
			});
		}catch(Exception e) {
			throw new Exception("Lỗi dữ liệu đầu vào");
		}
	
		Msg msg = jsonRoot.getMsg();
		MsgHeader header = msg.getMsgHeader();
		Object objData = msg.getObjData();
		
		JsonNode jsonData = null;
		if(objData != null) {
			jsonData = Json.serializer().nodeFromObject(msg.getObjData());
		}else{
			throw new Exception("Lỗi dữ liệu đầu vào");
		}
		String[] words = ten.split("/");
		 String taxcode = words[0];
		 String ms =  words[1];

		UpdateSignedMultiBillReq input = new UpdateSignedMultiBillReq();
		input.setFileData(multipartHttpServletRequest.getFile("zipFile").getBytes());
		input.setTaxcode(taxcode);
		input.setFormIssueInvoiceID(ms);
		return ResponseEntity.ok()
				.headers(headers)
				.cacheControl(CacheControl.noCache())
				.body(dao.signAll(input, jsonRoot));
	}
}