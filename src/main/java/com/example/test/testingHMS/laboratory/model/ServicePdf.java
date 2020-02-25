package com.example.test.testingHMS.laboratory.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
@Entity
public class ServicePdf {
		@Id
		@JsonIgnore
	    private String sid;

		@JsonIgnore
	    private String fileName;

		@JsonIgnore
	    private String regId;

		
		private String fileuri;
	    

	    @Lob
	    @JsonIgnore
	    private byte[] data;

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
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

 

	
	
}
