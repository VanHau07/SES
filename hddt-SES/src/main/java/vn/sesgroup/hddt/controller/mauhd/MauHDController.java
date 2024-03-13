package vn.sesgroup.hddt.controller.mauhd;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.api.message.MsgRsp;
import com.api.message.MspResponseStatus;
import com.fasterxml.jackson.databind.JsonNode;

import vn.sesgroup.hddt.controller.AbstractController;
import vn.sesgroup.hddt.dto.BaseDTO;
import vn.sesgroup.hddt.dto.CurrentUserProfile;
import vn.sesgroup.hddt.dto.JsonGridDTO;
import vn.sesgroup.hddt.resources.RestAPIUtility;
import vn.sesgroup.hddt.utils.Constants;
import vn.sesgroup.hddt.utils.Json;

@Controller
@RequestMapping("/mauhd")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class MauHDController extends AbstractController {
	@Autowired
	RestAPIUtility restAPI;

	private String _id;
	private String loaihd;
	private String mausohd;

	
	private BaseDTO checkDataSearch2(Locale locale, HttpServletRequest req, HttpSession session) {
		BaseDTO dto = new BaseDTO();
		dto.setErrorCode(0);
		
		return dto;
	}

	@RequestMapping(value = "/init", method = { RequestMethod.POST, RequestMethod.GET })
	public String init(Locale locale,  HttpSession session,Principal principal, HttpServletRequest req) throws Exception {
		req.setAttribute("_TitleView_", Constants.PREFIX_TITLE + " - Danh sách mẫu hóa đơn");
		req.setAttribute("map_status", Constants.MAP_STATUS);
		BaseDTO dto = new BaseDTO();
		dto.setErrorCode(0);
		
		BaseDTO baseDTO = checkDataSearch2(locale, req, session);
		CurrentUserProfile cup = getCurrentlyAuthenticatedPrincipal();		
		baseDTO = new BaseDTO(req);
		Msg msg = baseDTO.createMsg(cup, Constants.MSG_ACTION_CODE.SEARCH);
		
		HashMap<String, Object> hData = new HashMap<>();
		
		msg.setObjData(hData);
		
		JSONRoot root = new JSONRoot(msg);
		MsgRsp rsp = restAPI.callAPINormal("/tkhai/list", cup.getLoginRes().getToken(), HttpMethod.POST, root);
		Object a = rsp.getObjData();
//		if(a == null) {
//			return "/dm/tkhai";
//		}	
//		else {
//			return "/mauhd/mauhd";	
//		}
		return "/mauhd/mauhd";			
	}

	private BaseDTO checkDataSearch(Locale locale, HttpServletRequest req, HttpSession session) {
		BaseDTO dto = new BaseDTO();
		dto.setErrorCode(0);
		loaihd = commons.getParameterFromRequest(req, "loaihd").replaceAll("\\s", "");
		mausohd = commons.getParameterFromRequest(req, "mausohd").replaceAll("\\s", "");
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
	
		hData.put("LoaiHD", loaihd);
		hData.put("MauSoHD", mausohd);
		msg.setObjData(hData);
		
		JSONRoot root = new JSONRoot(msg);
		MsgRsp rsp = restAPI.callAPINormal("/mauhd/list", cup.getLoginRes().getToken(), HttpMethod.POST, root);
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
					hItem.put("MauSo", commons.getTextJsonNode(row.at("/MauSo")));
					hItem.put("PhoiHD", commons.getTextJsonNode(row.at("/PhoiHD")));
					hItem.put("LoaiHD", Constants.MAP_STATUSLoaiHD.get(commons.getTextJsonNode(row.at("/LoaiHD"))));	
					hItem.put("IsActive", Constants.MAP_STATUS.get(commons.getTextJsonNode(row.at("/IsActive"))));				
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
