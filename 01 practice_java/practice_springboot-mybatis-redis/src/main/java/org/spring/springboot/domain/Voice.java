package org.spring.springboot.domain;

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Component;


/**
 * 语音详单类
 * @author heym 2018/06/05
 *
 */
@Component
public class Voice implements Serializable{
	private static final long serialVersionUID = -1L;

    /** 文件名 */
    private String oriFileName;

    /** 话单类型 */
    private String recordType;

    /** 发起方 */
    private String callingNumber;

    /** 落地方 */
    private String calledNumber;
    
    /** 通话起始时间*/
    private String callStartTm;

    /** 计费时长 */
    private String callDuration;
    
    /** 被叫漫游号码  全数字或全空*/
    private String calledMSRN;
  
    /** 入中继群号 入中继群号（十六进制） */
    private String incomingTkgp16;

    /** 入中继群号 入中继群号（十进制） */
    private String incomingTkgp;

    /** 出中继群号 出中继群号（十六进制） */
    private String outgoingTkgp16;

    /** 出中继群号 出中继群号（十进制） */
    private String outgoingTkgp;
    
    /** 话单序列号 全数字 */
    private String cdrSeq;

    /** 通话终止原因 全数字 */
    private String causeForTerm;

    /** 本局MSC号 Msc号码 */
    private String mscNum;

    /** 用户呼叫参考 十六进制或全空 */
    private String callReference;
    
    /** 系统处理时间 */
    private Date dbInsrTm;

    /** 系统处理日期 YYYYMMDD*/
    private int dbInsrDt;

	public String getOriFileName() {
		return oriFileName;
	}

	public void setOriFileName(String oriFileName) {
		this.oriFileName = oriFileName;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getCallingNumber() {
		return callingNumber;
	}

	public void setCallingNumber(String callingNumber) {
		this.callingNumber = callingNumber;
	}

	public String getCalledNumber() {
		return calledNumber;
	}

	public void setCalledNumber(String calledNumber) {
		this.calledNumber = calledNumber;
	}

	public String getCallStartTm() {
		return callStartTm;
	}

	public void setCallStartTm(String callStartTm) {
		this.callStartTm = callStartTm;
	}

	public String getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}

	public String getCalledMSRN() {
		return calledMSRN;
	}

	public void setCalledMSRN(String calledMSRN) {
		this.calledMSRN = calledMSRN;
	}

	public String getIncomingTkgp16() {
		return incomingTkgp16;
	}

	public void setIncomingTkgp16(String incomingTkgp16) {
		this.incomingTkgp16 = incomingTkgp16;
	}

	public String getIncomingTkgp() {
		return incomingTkgp;
	}

	public void setIncomingTkgp(String incomingTkgp) {
		this.incomingTkgp = incomingTkgp;
	}

	public String getOutgoingTkgp16() {
		return outgoingTkgp16;
	}

	public void setOutgoingTkgp16(String outgoingTkgp16) {
		this.outgoingTkgp16 = outgoingTkgp16;
	}

	public String getOutgoingTkgp() {
		return outgoingTkgp;
	}

	public void setOutgoingTkgp(String outgoingTkgp) {
		this.outgoingTkgp = outgoingTkgp;
	}

	public String getCdrSeq() {
		return cdrSeq;
	}

	public void setCdrSeq(String cdrSeq) {
		this.cdrSeq = cdrSeq;
	}

	public String getCauseForTerm() {
		return causeForTerm;
	}

	public void setCauseForTerm(String causeForTerm) {
		this.causeForTerm = causeForTerm;
	}

	public String getMscNum() {
		return mscNum;
	}

	public void setMscNum(String mscNum) {
		this.mscNum = mscNum;
	}

	public String getCallReference() {
		return callReference;
	}

	public void setCallReference(String callReference) {
		this.callReference = callReference;
	}

	public Date getDbInsrTm() {
		return dbInsrTm;
	}

	public void setDbInsrTm(Date dbInsrTm) {
		this.dbInsrTm = dbInsrTm;
	}

	public int getDbInsrDt() {
		return dbInsrDt;
	}

	public void setDbInsrDt(int dbInsrDt) {
		this.dbInsrDt = dbInsrDt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Voice [oriFileName=" + oriFileName + ", recordType=" + recordType + ", callingNumber=" + callingNumber
				+ ", calledNumber=" + calledNumber + ", callStartTm=" + callStartTm + ", callDuration=" + callDuration
				+ ", calledMSRN=" + calledMSRN + ", incomingTkgp16=" + incomingTkgp16 + ", incomingTkgp=" + incomingTkgp
				+ ", outgoingTkgp16=" + outgoingTkgp16 + ", outgoingTkgp=" + outgoingTkgp + ", cdrSeq=" + cdrSeq
				+ ", causeForTerm=" + causeForTerm + ", mscNum=" + mscNum + ", callReference=" + callReference
				+ ", dbInsrTm=" + dbInsrTm + ", dbInsrDt=" + dbInsrDt + "]";
	}


	
}
