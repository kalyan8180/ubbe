package com.example.test.testingHMS.voucher.service;

import com.example.test.testingHMS.voucher.model.VoucherPdf;

public interface VoucherPdfService {

	void save(VoucherPdf pdf);

	VoucherPdf findById(String vid);

	public String getNextPdfId();

}
