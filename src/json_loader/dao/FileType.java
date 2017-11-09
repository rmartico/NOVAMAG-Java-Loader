package json_loader.dao;

public class FileType {
	
	
	private String m_type;
	private String m_expReg;
	private boolean isText;
	
	
	/**
	 * @return the m_type
	 */
	public String getType() {
		return m_type;
	}
	/**
	 * @param m_type the m_type to set
	 */
	public void setType(String m_type) {
		this.m_type = m_type;
	}
	
	/**
	 * @return the m_expReg
	 */
	public String getExpReg() {
		return m_expReg;
	}
	/**
	 * @param m_expReg the m_expReg to set
	 */
	public void setExpReg(String m_expReg) {
		this.m_expReg = m_expReg;
	}
	/**
	 * @return the isText
	 */
	public boolean getIsText() {
		return isText;
	}
	/**
	 * @param isText the isText to set
	 */
	public void setIsText(boolean isText) {
		this.isText = isText;
	}

}
