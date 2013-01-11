package org.carelife.creader.dao;

public class VersionData {

	private String VersionCode;
	private String content;
	private String update_url;
	private int control; //0 for normal state 1 for must be updated
	public int getControl() {
		return control;
	}
	public void setControl(int control) {
		this.control = control;
	}
	public String getUpdate_url() {
		return update_url;
	}
	public void setUpdate_url(String update_url) {
		this.update_url = update_url;
	}
	public String getContent() {
		return content;
	}
	public String getVersionCode() {
		return VersionCode;
	}
	public void setVersionCode(String versionCode) {
		VersionCode = versionCode.replace("\n", "");
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
