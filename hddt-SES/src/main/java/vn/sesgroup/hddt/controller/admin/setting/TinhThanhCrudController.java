package vn.sesgroup.hddt.controller.admin.setting;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.api.message.JSONRoot;
import com.api.message.Msg;
import com.api.message.MsgRsp;
import com.api.message.MspResponseStatus;
import com.fasterxml.jackson.databind.JsonNode;

import vn.sesgroup.hddt.controller.AbstractController;
import vn.sesgroup.hddt.dto.BaseDTO;
import vn.sesgroup.hddt.dto.CurrentUserProfile;
import vn.sesgroup.hddt.resources.RestAPIUtility;
import vn.sesgroup.hddt.utils.Constants;
import vn.sesgroup.hddt.utils.Json;

@Controller
@RequestMapping({
	"/ql-tinh-cre"
	, "/ql-tinh-edit"
	, "/ql-tinh-del"
	, "/ql-tinh-detail"
})
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class TinhThanhCrudController extends AbstractController{
	private static final Logger log = LogManager.getLogger(TinhThanhCrudController.class);
	@Autowired RestAPIUtility restAPI;
	@Autowired RestTemplate restTemplate;

	private String errorCode;
	private String errorDesc;
	private String _id;
	private String code;
	private String name;



	@RequestMapping(value = "/init", method = {RequestMethod.POST})
	public String init(Locale locale, HttpServletRequest req, HttpSession session
			, @RequestAttribute(name = "transaction", value = "", required = false) String transaction
			, @RequestAttribute(name = "method", value = "", required = false) String method
			) throws Exception{
		errorCode = "";
		errorDesc = "";
		CurrentUserProfile cup = getCurrentlyAuthenticatedPrincipal();
		_id = commons.getParameterFromRequest(req, "_id");
		String header = "Thêm mới tỉnh thành";
		String action = "CREATE";
		boolean isEdit = false;
		
		
		switch (transaction) {
		case "ql-tinh-cre":
			header = "Thêm mới tỉnh thành";
			action = "CREATE";
			isEdit = true;
			break;
		case "ql-tinh-edit":
			header = "Thay đổi thông tin tỉnh thành";
			action = "EDIT";
			isEdit = true;
			break;
		case "ql-tinh-detail":
			header = "Chi tiết tỉnh thành";
			action = "DETAIL";
			isEdit = false;
			break;
		

		default:
			break;
		}
		
		if("|ql-tinh-edit|ql-tinh-detail".indexOf(transaction) != -1)
			inquiry(cup, locale, req, session, action);
			req.setAttribute("_header_", header);
			req.setAttribute("_action_", action);
			req.setAttribute("_isedit_", isEdit);
			req.setAttribute("_id", _id);

			if(!"".equals(errorDesc))
				req.setAttribute("messageError", errorDesc);
		return "setting-admin/tinhthanh-crud";
	}
	private void inquiry(CurrentUserProfile cup, Locale locale, HttpServletRequest req, HttpSession session, String action) throws Exception{
		
		BaseDTO baseDTO = new BaseDTO(req);
		Msg msg = baseDTO.createMsg(cup, Constants.MSG_ACTION_CODE.INQUIRY);
		HashMap<String, String> hData = new HashMap<>();
		msg.setObjData(hData);
		JSONRoot root = new JSONRoot(msg);
		MsgRsp rsp = restAPI.callAPINormal("/ql-tinh/detail/" + _id, cup.getLoginRes().getToken(), HttpMethod.POST, root);
		MspResponseStatus rspStatus = rsp.getResponseStatus();
		if(rspStatus.getErrorCode() == 0) {
			JsonNode jsonData = Json.serializer().nodeFromObject(rsp.getObjData());
			req.setAttribute("_id", commons.getTextJsonNode(jsonData.at("/_id")));
			req.setAttribute("Code", commons.getTextJsonNode(jsonData.at("/code")));
			req.setAttribute("Name", commons.getTextJsonNode(jsonData.at("/name")));

			}else {
			errorDesc = rspStatus.getErrorDesc();
		}
	}
	
	
	public BaseDTO checkDataToAccept(HttpServletRequest req, HttpSession session, String transaction
			, CurrentUserProfile cup) throws Exception{
		BaseDTO dto = new BaseDTO();
		dto.setErrorCode(0);	
		_id = commons.getParameterFromRequest(req, "_id").replaceAll("\\s", "");
		code = commons.getParameterFromRequest(req, "code");
		name = commons.getParameterFromRequest(req, "name");
	

			switch (transaction) {
			case "ql-tinh-cre":
			case "ql-tinh-edit":	
				if("".equals(code)) {
					dto.setErrorCode(1);
					dto.getErrorMessages().add("Code không được để trống.");
				}
				if("".equals(name)) {
					dto.setErrorCode(1);
					dto.getErrorMessages().add("Tên không được để trống.");
				}
			
				break;
			default:
				break;
			}
		return dto;
	}
	
	@RequestMapping(value = "/check-data-save",  produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public BaseDTO execCheckDataToSave(Locale locale, HttpServletRequest req, HttpSession session
			, @RequestAttribute(name = "transaction", required = false, value = "") String transaction) throws Exception {
		String token = "";
		if (null != session.getAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE)) {
			token = session.getAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE).toString();
			session.removeAttribute(token);
		}
		session.removeAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE);
		
		BaseDTO dto = new BaseDTO();
		String messageConfirm = "Bạn có muốn tỉnh thành?";
		switch (transaction) {
		case "ql-tinh-cre":
			messageConfirm = "Bạn có muốn thêm mới tỉnh thành không?";
			break;
		case "ql-tinh-edit":
			messageConfirm = "Bạn có muốn thay đổi thông tin tỉnh thành không?";
			break;
		default:
			dto = new BaseDTO();
			dto.setErrorCode(998);
			dto.setResponseData("Không tìm thấy chức năng giao dịch.");
			return dto;
		}
		
		CurrentUserProfile cup = getCurrentlyAuthenticatedPrincipal();
		dto = checkDataToAccept(req, session, transaction, cup);
		if(0 != dto.getErrorCode()) {
			dto.setErrorCode(999);
			dto.setResponseData(Constants.MAP_ERROR.get(999));
			return dto;
		}
	
		token = commons.csRandomAlphaNumbericString(30);
		session.setAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE, token);
		
		HashMap<String, String> hInfo = new HashMap<String, String>();
		hInfo.put("CONFIRM", messageConfirm);
		hInfo.put("TOKEN", token);
		
		dto.setResponseData(hInfo);
		dto.setErrorCode(0);
		return dto;
	}
	
	@RequestMapping(value = "/save-data",  produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public BaseDTO execSaveData(HttpServletRequest req, HttpSession session
			, @RequestAttribute(name = "transaction", value = "", required = false) String transaction
			, @RequestParam(value = "tokenTransaction", required = false, defaultValue = "") String tokenTransaction) throws Exception{
		BaseDTO dtoRes = new BaseDTO();
		
		CurrentUserProfile cup = getCurrentlyAuthenticatedPrincipal();
		dtoRes = checkDataToAccept(req, session, transaction, cup);
		if(0 != dtoRes.getErrorCode()) {
			dtoRes.setErrorCode(999);
			dtoRes.setResponseData(Constants.MAP_ERROR.get(999));
			return dtoRes;
		}
		
		/*CHECK TOKEN*/
		String token = session.getAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE) == null ? ""
				: session.getAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE).toString();
		session.removeAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE);
		if ("".equals(token) || !tokenTransaction.equals(token)) {
			dtoRes.setErrorCode(1);
			dtoRes.setResponseData("Token giao dịch không hợp lệ.");
			return dtoRes;
		}

		String actionCode = Constants.MSG_ACTION_CODE.CREATED;
		switch (transaction) {
		case "ql-tinh-cre": actionCode = Constants.MSG_ACTION_CODE.CREATED; break;
		case "ql-tinh-edit": actionCode = Constants.MSG_ACTION_CODE.MODIFY; break;
		default:
			dtoRes = new BaseDTO();
			dtoRes.setErrorCode(998);
			dtoRes.setResponseData(Constants.MAP_ERROR.get(998));
			return dtoRes;
		}
		
		dtoRes = new BaseDTO(req);
		Msg msg = dtoRes.createMsg(cup, actionCode);
		HashMap<String, Object> hData = new HashMap<>();
		msg.setObjData(hData);
		JSONRoot root = new JSONRoot(msg);
		hData.put("_id", _id);
		hData.put("code", code);
		hData.put("name", name);
		MsgRsp rsp = restAPI.callAPINormal("/ql-tinh/crud", cup.getLoginRes().getToken(), HttpMethod.POST, root);
		MspResponseStatus rspStatus = rsp.getResponseStatus();
		if(rspStatus.getErrorCode() == 0) {
			dtoRes.setErrorCode(0);
			switch (transaction) {
			case "ql-tinh-cre":
				dtoRes.setResponseData("Thêm mới thông tin  thành công.");
				break;
			case "ql-tinh-edit":
				dtoRes.setResponseData("Cập nhật thông tin  thành công.");
				break;
			default:
				dtoRes.setResponseData("Giao dịch thành công.");
				break;
			}
		}else {
			dtoRes.setErrorCode(rspStatus.getErrorCode());
			dtoRes.setResponseData(rspStatus.getErrorDesc());
		}
		return dtoRes;
	}
	

	@RequestMapping(value = "/check-data",  produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public BaseDTO execCheckData(Locale locale, HttpServletRequest req, HttpSession session
			, @RequestAttribute(name = "transaction", required = false, value = "") String transaction) throws Exception {
		String token = "";
		if (null != session.getAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE)) {
			token = session.getAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE).toString();
			session.removeAttribute(token);
		}
		session.removeAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE);
		
		BaseDTO dto = new BaseDTO();
		String messageConfirm = "Bạn có muốn xóa  không?";
		switch (transaction) {
		case "ql-tinh-del":
			messageConfirm = "Bạn có muốn xóa  không?";
			break;
		default:
			dto = new BaseDTO();
			dto.setErrorCode(998);
			dto.setResponseData("Không tìm thấy chức năng giao dịch.");
			return dto;
		}
		
		CurrentUserProfile cup = getCurrentlyAuthenticatedPrincipal();
		dto = checkData(req, session, transaction, cup);
		if(0 != dto.getErrorCode()) {
			dto.setErrorCode(999);
			dto.setResponseData(Constants.MAP_ERROR.get(999));
			return dto;
		}
		
		token = commons.csRandomAlphaNumbericString(30);
		session.setAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE, token);
		
		HashMap<String, String> hInfo = new HashMap<String, String>();
		hInfo.put("CONFIRM", messageConfirm);
		hInfo.put("TOKEN", token);
		
		dto.setResponseData(hInfo);
		dto.setErrorCode(0);
		return dto;
	}
	public BaseDTO checkData(HttpServletRequest req, HttpSession session, String transaction
			, CurrentUserProfile cup) throws Exception{
		BaseDTO dto = new BaseDTO();
		dto.setErrorCode(0);
		
		_id = commons.getParameterFromRequest(req, "_id").replaceAll("\\s", "");
		if("".equals(_id)) {
			dto.setErrorCode(1);
			dto.getErrorMessages().add("Không tìm thấy thông tin hóa đơn.");
		}
		
		return dto;
	}
	@RequestMapping(value = "/exec-data",  produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public BaseDTO execData(HttpServletRequest req, HttpSession session
			, @RequestAttribute(name = "transaction", value = "", required = false) String transaction
			, @RequestParam(value = "tokenTransaction", required = false, defaultValue = "") String tokenTransaction) throws Exception{
		BaseDTO dtoRes = new BaseDTO();
	
		CurrentUserProfile cup = getCurrentlyAuthenticatedPrincipal();
		dtoRes = checkData(req, session, transaction, cup);
		if(0 != dtoRes.getErrorCode()) {
			dtoRes.setErrorCode(999);
			dtoRes.setResponseData(Constants.MAP_ERROR.get(999));
			return dtoRes;
		}
		
		/*CHECK TOKEN*/
		String token = session.getAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE) == null ? ""
				: session.getAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE).toString();
		session.removeAttribute(Constants.SESSION_TYPE.SESSION_TOKEN_EXECUTE);
		if ("".equals(token) || !tokenTransaction.equals(token)) {
			dtoRes.setErrorCode(1);
			dtoRes.setResponseData("Token giao dịch không hợp lệ.");
			return dtoRes;
		}
		String actionCode = Constants.MSG_ACTION_CODE.DELETE;
		switch (transaction) {
		case "ql-tinh-del":
			actionCode = Constants.MSG_ACTION_CODE.DELETE;
			break;
		
		default:
			dtoRes = new BaseDTO();
			dtoRes.setErrorCode(998);
			dtoRes.setResponseData("Không tìm thấy chức năng giao dịch.");
			return dtoRes;
		}
		
		dtoRes = new BaseDTO(req);
		Msg msg = dtoRes.createMsg(cup, actionCode);
		HashMap<String, Object> hData = new HashMap<>();
		hData.put("_id", _id);
		msg.setObjData(hData);
		
		JSONRoot root = new JSONRoot(msg);
		MsgRsp rsp = restAPI.callAPINormal("/ql-tinh/crud", cup.getLoginRes().getToken(), HttpMethod.POST, root);
		MspResponseStatus rspStatus = rsp.getResponseStatus();
		if(rspStatus.getErrorCode() == 0) {
			dtoRes.setErrorCode(0);
			switch (transaction) {
			case "ql-tinh-del":
				dtoRes.setResponseData("Xóa thông tin hóa đơn thành công.");
				break;
		
			default:
				break;
			}
		}else {
			dtoRes.setErrorCode(rspStatus.getErrorCode());
			dtoRes.setResponseData(rspStatus.getErrorDesc());
		}
		return dtoRes;
	}


	
}
