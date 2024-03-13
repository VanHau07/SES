package vn.sesgroup.hddt.controller.dm;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import com.api.message.JSONRoot;
import com.api.message.Msg;
import com.api.message.MsgPage;
import com.api.message.MsgParam;
import com.api.message.MsgParams;
import com.api.message.MsgRsp;
import com.api.message.MspResponseStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import vn.sesgroup.hddt.controller.AbstractController;
import vn.sesgroup.hddt.dto.BaseDTO;
import vn.sesgroup.hddt.dto.CurrentUserProfile;
import vn.sesgroup.hddt.dto.JsonGridDTO;
import vn.sesgroup.hddt.resources.RestAPIUtility;
import vn.sesgroup.hddt.utils.Constants;
import vn.sesgroup.hddt.utils.Json;

@Controller
@RequestMapping("/dmproduct")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class DMProductController extends AbstractController{
	private static final Logger log = LogManager.getLogger(DMProductController.class);
	@Autowired RestAPIUtility restAPI; 
	
	private String code;
	private String name;
	private String stock;
	
	private void LoadParameter(CurrentUserProfile cup, Locale locale, HttpServletRequest req, String action) {
		try {
			BaseDTO baseDTO = new BaseDTO(req);
			Msg msg = baseDTO.createMsg(cup, Constants.MSG_ACTION_CODE.LOAD_PARAMS);
			
			/*DANH SACH THAM SO*/
			HashMap<String, String> hashConds = null;
			ArrayList<HashMap<String, String>> conds = null;
			MsgParam msgParam = null;
			MsgParams msgParams = new MsgParams();
			
			msgParam = new MsgParam();
			msgParam.setId("param01");
			msgParam.setParam("DMStock");
			msgParams.getParams().add(msgParam);
			
			/*END: DANH SACH THAM SO*/
			msg.setObjData(msgParams);
			
			JSONRoot root = new JSONRoot(msg);
			MsgRsp rsp = restAPI.callAPINormal("/commons/get-full-params", cup.getLoginRes().getToken(), HttpMethod.POST, root);
			MspResponseStatus rspStatus = rsp.getResponseStatus();
			
			if(rspStatus.getErrorCode() == 0 && rsp.getObjData() != null) {
				LinkedHashMap<String, String> hItem = null;
				
				JsonNode jsonData = Json.serializer().nodeFromObject(rsp.getObjData());
			
				if(null != jsonData.at("/param01") && jsonData.at("/param01") instanceof ArrayNode) {
					hItem = new LinkedHashMap<String, String>();
					for(JsonNode o: jsonData.at("/param01")) {
						hItem.put(commons.getTextJsonNode(o.get("Code")), commons.getTextJsonNode(o.get("Name")));
					}
					req.setAttribute("map_stock", hItem);
				}
				
			}
		}catch(Exception e) {}
	}
	
	@RequestMapping(value = "/init", method = {RequestMethod.POST, RequestMethod.GET})
	public String init(Locale locale, Principal principal, HttpServletRequest req) throws Exception{
		req.setAttribute("_TitleView_", Constants.PREFIX_TITLE + " - Danh sách sản phẩm");
		
		CurrentUserProfile cup = getCurrentlyAuthenticatedPrincipal();

		LoadParameter(cup, locale, req, "DETAIL");
		
		return "dm/product";
	}
	
	private BaseDTO checkDataSearch(Locale locale, HttpServletRequest req, HttpSession session) {
		BaseDTO dto = new BaseDTO();
		dto.setErrorCode(0);
		
		code = commons.getParameterFromRequest(req, "code").trim().replaceAll("\\s+", " ");
		name = commons.getParameterFromRequest(req, "name").trim().replaceAll("\\s+", " ");
		stock = commons.getParameterFromRequest(req, "stock").trim().replaceAll("\\s+", " ");
		
		return dto;
	}
	
	@RequestMapping(value = "/search",  produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public BaseDTO execSearch(Locale locale, HttpServletRequest req, HttpSession session) throws Exception{
		JsonGridDTO grid = new JsonGridDTO();
		
		BaseDTO baseDTO = checkDataSearch(locale, req, session);
		if(0 != baseDTO.getErrorCode()) {
			grid.setErrorCode(baseDTO.getErrorCode());
			grid.setErrorMessages(baseDTO.getErrorMessages());
			grid.setResponseData(Constants.MAP_ERROR.get(999));
			return grid;
		}
		
		CurrentUserProfile cup = getCurrentlyAuthenticatedPrincipal();		
		baseDTO = new BaseDTO(req);
		Msg msg = baseDTO.createMsg(cup, Constants.MSG_ACTION_CODE.SEARCH);
		
		HashMap<String, Object> hData = new HashMap<>();
		hData.put("Code", code);
		hData.put("Name", name);
		hData.put("Stock", stock);
				
		msg.setObjData(hData);
		
		JSONRoot root = new JSONRoot(msg);
		MsgRsp rsp = restAPI.callAPINormal("/dmproduct/list", cup.getLoginRes().getToken(), HttpMethod.POST, root);
		MspResponseStatus rspStatus = rsp.getResponseStatus();
		if(rspStatus.getErrorCode() == 0) {
			MsgPage page = rsp.getMsgPage();
			grid.setTotal(page.getTotalRows());
			
			JsonNode jsonData = Json.serializer().nodeFromObject(rsp.getObjData());
			JsonNode rows = null;
			HashMap<String, String> hItem = null;
			if(!jsonData.at("/rows").isMissingNode()) {
				rows = jsonData.at("/rows");
				for(JsonNode row: rows) {
					hItem = new HashMap<String, String>();
					
					hItem.put("_id", commons.getTextJsonNode(row.at("/_id")));
					hItem.put("Code", commons.getTextJsonNode(row.at("/Code")));
					hItem.put("Name", commons.getTextJsonNode(row.at("/Name")));
					hItem.put("Stock", commons.getTextJsonNode(row.at("/Stock")));
					hItem.put("Slsx", commons.getTextJsonNode(row.at("/Slsx")));
					if(!row.at("/Price").isMissingNode())
						hItem.put("Price", commons.formatNumberReal(row.at("/Price").doubleValue()));
					hItem.put("Unit", commons.getTextJsonNode(row.at("/Unit")));
					if(!row.at("/VatRate").isMissingNode())
						hItem.put("VatRate", commons.formatNumberReal(row.at("/VatRate").doubleValue()) + "%");
					
					hItem.put("UserCreated", commons.getTextJsonNode(row.at("/InfoCreated/CreateUserName")));
					if(!row.at("/InfoCreated/CreateDate").isMissingNode() && null != row.at("/InfoCreated/CreateDate")) {
						try {
							hItem.put("DateCreated", commons.convertLocalDateTimeToString(commons.convertLongToLocalDateTime(row.at("/InfoCreated/CreateDate").asLong(0L)), Constants.FORMAT_DATE.FORMAT_DATE_TIME_WEB));
						}catch(Exception e) {}
					}
						
					hItem.put("UserUpdated", commons.getTextJsonNode(row.at("/InfoUpdated/UpdatedUserName")));
					if(!row.at("/InfoUpdated/UpdatedDate").isMissingNode() && null != row.at("/InfoUpdated/UpdatedDate")) {
						try {
							hItem.put("DateUpdated", commons.convertLocalDateTimeToString(commons.convertLongToLocalDateTime(row.at("/InfoUpdated/UpdatedDate").asLong(0L)), Constants.FORMAT_DATE.FORMAT_DATE_TIME_WEB));	
						}catch(Exception e) {}
					}
					hItem.put("thdoi_tonkho", commons.getTextJsonNode(row.at("/thdoi_tonkho")));	
					grid.getRows().add(hItem);
				}
			}
			
		}else {
			grid = new JsonGridDTO();
			grid.setErrorCode(rspStatus.getErrorCode());
			grid.setResponseData(rspStatus.getErrorDesc());
		}
		
		return grid;
	}
	
}
