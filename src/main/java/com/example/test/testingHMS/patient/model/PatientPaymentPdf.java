package com.example.test.testingHMS.patient.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
@Entity
public class PatientPaymentPdf {
	
	@Id
	@JsonIgnore
    private String pid;

	@JsonIgnore
    private String fileName;

    private String fileuri;
    
    private String regId;
    
    private String billNo;

    @Lob
    @JsonIgnore
    private byte[] data;

    public PatientPaymentPdf() {
		super();
	}

    

   
    
	public PatientPaymentPdf(String fileName, String fileuri, String regId, byte[] data, String billNo) {
		super();
		this.fileName = fileName;
		this.fileuri = fileuri;
		this.regId = regId;
		this.billNo = billNo;
		this.data = data;
	}




	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileuri() {
		return fileuri;
	}

	public void setFileuri(String fileuri) {
		this.fileuri = fileuri;
	}

	public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }



	public String getRegId() {
		return regId;
	}



	public void setRegId(String regId) {
		this.regId = regId;
	}





	public String getBillNo() {
		return billNo;
	}





	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	
    
}
