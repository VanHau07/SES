package vn.sesgroup.hddt.user.dao;

import com.api.message.JSONRoot;
import com.api.message.MsgRsp;

public interface ConfigEmailMailJetDAO {
	public MsgRsp crud(JSONRoot jsonRoot) throws Exception;
	public MsgRsp detail(JSONRoot jsonRoot) throws Exception;
}
