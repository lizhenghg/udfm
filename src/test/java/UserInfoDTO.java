

import java.io.Serializable;

/**
 *
 * 用户实体类(用于接受前端用户信息)
 *
 */
public class UserInfoDTO implements Serializable {

	private String uid;

	private String hisuid;

	private String password;

	private String telephonenumber;

	public UserInfoDTO(String uid, String hisuid, String password, String telephonenumber) {
		this.uid = uid;
		this.hisuid = hisuid;
		this.password = password;
		this.telephonenumber = telephonenumber;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getHisuid() {
		return hisuid;
	}

	public void setHisuid(String hisuid) {
		this.hisuid = hisuid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTelephonenumber() {
		return telephonenumber;
	}

	public void setTelephonenumber(String telephonenumber) {
		this.telephonenumber = telephonenumber;
	}
}