package vn.sesgroup.hddt.user.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.api.message.JSONRoot;
import com.api.message.Msg;
import com.api.message.MsgHeader;
import com.api.message.MsgPage;
import com.api.message.MsgRsp;
import com.api.message.MspResponseStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import vn.sesgroup.hddt.configuration.ConfigConnectMongo;
import vn.sesgroup.hddt.user.dao.AbstractDAO;
import vn.sesgroup.hddt.user.dao.TraCuuMailUserDAO;
import vn.sesgroup.hddt.user.service.TCTNService;
import vn.sesgroup.hddt.utility.Constants;
import vn.sesgroup.hddt.utility.Json;

@Repository
@Transactional
public class TraCuuMailUserImpl extends AbstractDAO implements TraCuuMailUserDAO{
	private static final Logger log = LogManager.getLogger(TraCuuMailUserImpl.class);
	@Autowired
	ConfigConnectMongo cfg;
	@Autowired TCTNService tctnService;
	
	@Override
	public MsgRsp list(JSONRoot jsonRoot) throws Exception {
		Msg msg = jsonRoot.getMsg();
		MsgHeader header = msg.getMsgHeader();
		MsgPage page = msg.getMsgPage();
		Object objData = msg.getObjData();
	
		String en = "";
		String tt = "";
		String acti = "";
		
		JsonNode jsonData = null;
		if(objData != null) {
			jsonData = Json.serializer().nodeFromObject(objData);
			en = commons.getTextJsonNode(jsonData.at("/Email"));
			tt = commons.getTextJsonNode(jsonData.at("/Title"));
			acti = commons.getTextJsonNode(jsonData.at("/IsActive"));
		}
		int abcs = commons.stringToInteger(acti);
		Boolean isacti = true;
if(abcs == 1) {
	isacti = true;
}
if(abcs == 2) {
	isacti = false;
}
		MsgRsp rsp = new MsgRsp(header);
		MspResponseStatus responseStatus = null;
		
		ObjectId objectId = null;
		Document docTmp = null;
		Iterable<Document> cursor = null;
		Iterator<Document> iter = null;
		List<Document> pipeline = new ArrayList<Document>();
		

		
		
		Document docMatch = new Document("IssuerId", header.getIssuerId())
				.append("IsDelete", new Document("$ne", true));
		if(!"".equals(en))
			docMatch.append("Email", commons.regexEscapeForMongoQuery(en));
		if(!"".equals(tt))
			docMatch.append("Title", commons.regexEscapeForMongoQuery(tt));
		if(!"".equals(acti))
			docMatch.append("IsActive",isacti );
		
	
		
		Document fillter = new Document("_id", 1).append("Email", 1).append("Title", 1)
				.append("MailCheck", 1).append("IsActive", 1);
		
		
		pipeline = new ArrayList<Document>();
		pipeline.add(new Document("$match", docMatch));
		pipeline.add(
				new Document("$sort", 
					new Document("_id", -1)
				)
			);
		pipeline.add(new Document("$project", fillter));
		pipeline.addAll(createFacetForSearchNotSort(page));
	

		MongoClient mongoClient = cfg.mongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase(cfg.dbName).getCollection("LogEmailUser");
		try {
			docTmp =   collection.aggregate(pipeline).allowDiskUse(true).iterator().next();		
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		mongoClient.close();
		
		rsp = new MsgRsp(header);
		responseStatus = null;
		if(null == docTmp) {
			responseStatus = new MspResponseStatus(9999, Constants.MAP_ERROR.get(9999));
			rsp.setResponseStatus(responseStatus);
			return rsp;
		}
		
		page.setTotalRows(docTmp.getInteger("total", 0));
		rsp.setMsgPage(page);
		
		List<Document> rows = null;
		if(docTmp.get("data") != null && docTmp.get("data") instanceof List) {
			rows = docTmp.getList("data", Document.class);
		}
		
		ArrayList<HashMap<String, Object>> rowsReturn = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hItem = null;
		if(null != rows) {
			for(Document doc: rows) {
				objectId = (ObjectId) doc.get("_id");
				
				hItem = new HashMap<String, Object>();
				hItem.put("_id", objectId.toString());
				hItem.put("Email", doc.get("Email"));
				hItem.put("Title", doc.get("Title"));	
				hItem.put("MailCheck", doc.get("MailCheck"));	
				hItem.put("IsActive", doc.get("IsActive"));	
				rowsReturn.add(hItem);
			}
		}
		responseStatus = new MspResponseStatus(0, "SUCCESS");
		rsp.setResponseStatus(responseStatus);
		
		HashMap<String, Object> mapDataR = new HashMap<String, Object>();
		mapDataR.put("rows", rowsReturn);
		rsp.setObjData(mapDataR);
		return rsp;
	}

	@Override
	public MsgRsp detail(JSONRoot jsonRoot, String _id) throws Exception {
		Msg msg = jsonRoot.getMsg();
		MsgHeader header = msg.getMsgHeader();
		MsgPage page = msg.getMsgPage();
		
		MsgRsp rsp = new MsgRsp(header);
		rsp.setMsgPage(page);
		MspResponseStatus responseStatus = null;
		
		ObjectId objectId = null;
		try {
			objectId = new ObjectId(_id);
		}catch(Exception e) {}
		
		Document docFind = new Document("IssuerId", header.getIssuerId())
				.append("IsDelete", new Document("$ne", true)).append("_id", objectId);
		
		Document docTmp = null;
	
		
		MongoClient mongoClient = cfg.mongoClient();
		MongoCollection<Document> collection = mongoClient.getDatabase(cfg.dbName).getCollection("LogEmailUser");
		try {
			docTmp =   collection.find(docFind).allowDiskUse(true).iterator().next();		
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		mongoClient.close();
		
		if(null == docTmp) {
			responseStatus = new MspResponseStatus(9999, Constants.MAP_ERROR.get(9999));
			rsp.setResponseStatus(responseStatus);
			return rsp;
		}
		
		rsp.setObjData(docTmp);
		responseStatus = new MspResponseStatus(0, Constants.MAP_ERROR.get(0));
		rsp.setResponseStatus(responseStatus);
		return rsp;
	}


	
	
}
