package vn.sesgroup.hddt.user.controller;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.api.message.JSONRoot;
import com.api.message.MsgRsp;

import vn.sesgroup.hddt.dto.FileInfo;
import vn.sesgroup.hddt.user.dao.HHDSDUserDao;
import vn.sesgroup.hddt.utility.Commons;

@RestController
@RequestMapping(value = "/hdsduser")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class HDSDUserController {
	Commons commons = new Commons();
	@Autowired private HHDSDUserDao dao;
	

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
//	@RequestMapping(value = "/dowloadfile", method = RequestMethod.POST,
//			consumes = {MediaType.APPLICATION_JSON_VALUE},		//MediaType.TEXT_PLAIN_VALUE, 
//			produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
//	public ResponseEntity<?> printEinvoice(@RequestBody HashMap<String, String> mapInput) throws Exception{
//		FileInfo fileInfo = dao.dowloadfile(mapInput);
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("content-disposition", "attachment; filename=" + "einvoice.pdf");
//		headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
//        
//		return ResponseEntity.ok()
//				.headers(headers)
//				.cacheControl(CacheControl.noCache())
//				.body(SerializationUtils.serialize(fileInfo));
//	}
	
	@RequestMapping(value = "/dowloadfile", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, // MediaType.TEXT_PLAIN_VALUE,
			produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public ResponseEntity<?> getFilesForSign(@RequestBody JSONRoot jsonRoot) throws Exception {
		FileInfo fileInfo = dao.dowloadfile(jsonRoot);

		HttpHeaders headers = new HttpHeaders();
		headers.add("content-disposition", "attachment; filename=" + "template.data");
		headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);

		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
		return ResponseEntity.ok().headers(headers).cacheControl(CacheControl.noCache())
				.body(SerializationUtils.serialize(fileInfo));
	}
}
