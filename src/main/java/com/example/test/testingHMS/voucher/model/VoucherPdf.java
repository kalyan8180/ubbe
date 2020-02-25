package com.example.test.testingHMS.voucher.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
@Entity
public class VoucherPdf {
	
		@Id
		@JsonIgnore
	    private String vid;

		@JsonIgnore
	    private String fileName;

	    private String fileuri;

	    @Lob
	    @JsonIgnore
	    private byte[] data;

		public VoucherPdf(String fileName, String fileuri, byte[] data) {
			super();
			this.fileName = fileName;
			this.fileuri = fileuri;
			this.data = data;
		}

		public String getVid() {
			return vid;
		}

		public void setVid(String vid) {
			this.vid = vid;
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

		public VoucherPdf() {
			super();
			// TODO Auto-generated constructor stub
		}

		public VoucherPdf(String vid, String fileName, String fileuri, byte[] data) {
			super();
			this.vid = vid;
			this.fileName = fileName;
			this.fileuri = fileuri;
			this.data = data;
		}

	 
}


