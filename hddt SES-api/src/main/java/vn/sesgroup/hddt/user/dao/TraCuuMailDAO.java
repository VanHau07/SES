package vn.sesgroup.hddt.user.dao;

import com.api.message.JSONRoot;
import com.api.message.MsgRsp;

public interface TraCuuMailDAO {
	public MsgRsp list(JSONRoot jsonRoot) throws Exception;
	
}
