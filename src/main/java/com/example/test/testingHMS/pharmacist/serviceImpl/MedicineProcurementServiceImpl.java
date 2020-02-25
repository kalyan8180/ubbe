package com.example.test.testingHMS.pharmacist.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.pharmacist.helper.RefMedicineDetails;
import com.example.test.testingHMS.pharmacist.helper.RefProcurementIds;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.MedicineQuantity;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.model.Vendors;
import com.example.test.testingHMS.pharmacist.repository.MedicineDetailsRepository;
import com.example.test.testingHMS.pharmacist.repository.MedicineProcurementRepository;
import com.example.test.testingHMS.pharmacist.repository.MedicineQuantityRepository;
import com.example.test.testingHMS.pharmacist.repository.SalesPaymentPdfRepository;
import com.example.test.testingHMS.pharmacist.service.MedicineProcurementService;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.example.test.testingHMS.utils.ConstantValues;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class MedicineProcurementServiceImpl implements MedicineProcurementService 
{
	private static final Logger Logger=LoggerFactory.getLogger(MedicineProcurementServiceImpl.class);
	
	@Value("${hospital.logo}")
	private Resource hospitalLogo;
	
	@Autowired
	MedicineProcurementRepository medicineProcurementRepository;
	
	@Autowired
	MedicineDetailsServiceImpl medicineDetailsServiceImpl;
	
	@Autowired
	SalesPaymentPdfRepository salesPaymentPdfRepository;
	
	@Autowired
	LocationServiceImpl locationServiceImpl;
	
	@Autowired
	MedicineProcurementServiceImpl  medicineProcurementServiceImpl;
	
	@Autowired
	MedicineDetailsRepository medicineDetailsRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	RefProcurementIds refProcurementIds;
	
	@Autowired
	VendorsServiceImpl vendorsServiceImpl;

	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	MedicineQuantityServiceImpl medicineQuantityServiceImpl;

	@Autowired
	MedicineQuantityRepository medicineQuantityRepository;
	
	@Autowired
	SalesPaymentPdf salesPaymentPdf;
	
	@Autowired
	SalesPaymentPdfServiceImpl salesPaymentPdfServiceImpl;
	
	
	public String getNextMasterProcurementId() 
	{
		MedicineProcurement medicineProcurement=medicineProcurementRepository.findFirstByOrderByMasterProcurementIdDesc();
		String nextId=null;
		if(medicineProcurement==null)
		{
			nextId="MPRO0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(medicineProcurement.getMasterProcurementId().substring(4));
			nextIntId+=1;
			nextId="MPRO"+String.format("%07d",nextIntId);
			
		}
		return nextId;
	}
	
	public String getNextProcurementId() 
	{
		MedicineProcurement medicineProcurement=medicineProcurementRepository.findFirstByOrderByMasterProcurementIdDesc();
		String nextId=null;
		if(medicineProcurement==null)
		{
			nextId="PRO0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(medicineProcurement.getProcurementId().substring(3));
			nextIntId+=1;
			nextId="PRO"+String.format("%07d",nextIntId);
			
		}
		return nextId;
	}
	
	public MedicineProcurement findMasterProcurementId(String procId,String medName,String batch)
	{
		return medicineProcurementRepository.findMasterProcurementId(procId, medName,batch);
	}
	
	@CacheEvict(value="procurementCache",allEntries = true)
	public SalesPaymentPdf updateSave(MedicineProcurement medicineProcurement,Principal principal)
	{
		Vendors vendors=vendorsServiceImpl.findByVendorName(medicineProcurement.getVendorName());
		medicineProcurement.setMedicineProcurmentVendors(vendors);
		
		SalesPaymentPdf salesPaymentPdfInfo=null;
		String procId=medicineProcurement.getProcurementId()+" Procurement";
		Location locationInfo=locationServiceImpl.findByLocationName(medicineProcurement.getLocation());
		medicineProcurement.setMedicineProcurmentLocation(locationInfo);
		medicineProcurement.setDateOfProcurement(Timestamp.valueOf(LocalDateTime.now()));
		medicineProcurement.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		List<RefMedicineDetails> refMedicineDetailsList= medicineProcurement.getRefMedicineDetails();
		medicineProcurement.setDateOfProcurement(Timestamp.valueOf(LocalDateTime.now()));
	//	medicineProcurement.setAmount(medicineProcurement.getAmount());
		medicineProcurement.setProcurementId(medicineProcurement.getProcurementId());
		System.out.println("Master"+getNextProcurementId());
		medicineProcurement.setStatus("Not-Approved");
		String procureId=medicineProcurement.getProcurementId();
		int cnt=refMedicineDetailsList.size();
		System.out.println(cnt);
		
		List<Float> discountInfo=new ArrayList<>();
		
		List<Float> gstInfo=new ArrayList<>();
		
		String invoiceNo=medicineProcurement.getInvoiceNo();
		
		//CreatedBy (Security)
		User userSecurity=userServiceImpl.findByUserName(principal.getName());
		String createdBy=userSecurity.getFirstName()+" "+userSecurity.getLastName();
		
		
		int n=0;
		for(RefMedicineDetails refMedicineDetails:refMedicineDetailsList)
		{
			
			
			System.out.println("Master"+getNextMasterProcurementId());
			MedicineProcurement medicineProcurementP= medicineProcurementRepository.findByItemNameAndProcurementIdAndBatch(refMedicineDetails.getItemName(),medicineProcurement.getProcurementId(),refMedicineDetails.getBatch());
			medicineProcurement.setMasterProcurementId(medicineProcurementP.getMasterProcurementId());
			RefMedicineDetails refMedicineDetailsInfo=refMedicineDetails;
			medicineProcurement.setBatch(refMedicineDetailsInfo.getBatch());
			medicineProcurement.setCostPrice(refMedicineDetailsInfo.getCostPrice());
			medicineProcurement.setMrp(refMedicineDetailsInfo.getMrp());
			medicineProcurement.setFreeSample(refMedicineDetailsInfo.getFreeSample());
			medicineProcurement.setQuantity(refMedicineDetailsInfo.getQuantity());
			medicineProcurement.setGst(refMedicineDetailsInfo.getGst());
			medicineProcurement.setPackSize(refMedicineDetailsInfo.getPackSize());
			medicineProcurement.setPacking(refMedicineDetailsInfo.getPacking());
			float freeSample=(refMedicineDetailsInfo.getFreeSample()!=null) ? Float.valueOf(refMedicineDetailsInfo.getFreeSample()) :0 ;
			medicineProcurement.setDetailedQuantity(((long)(refMedicineDetailsInfo.getQuantity()+freeSample))*refMedicineDetailsInfo.getPackSize());
			medicineProcurement.setExpDate(refMedicineDetailsInfo.getExpDate());
			medicineProcurement.setManufacturedDate(refMedicineDetailsInfo.getManufacturedDate());
			medicineProcurement.setTax(refMedicineDetailsInfo.getTax());
			MedicineDetails medicineDetails=medicineDetailsServiceImpl.findByName(refMedicineDetailsInfo.getItemName());
			System.out.println(refMedicineDetailsInfo.getItemName());
			medicineProcurement.setMedicineProcurmentMedicineDetails(medicineDetails);
			medicineProcurement.setMedicineProcurmentVendors(vendorsServiceImpl.findByVendorName(medicineProcurement.getVendorName()));
			medicineProcurement.setItemName(medicineDetails.getName());
			medicineProcurement.setBatch(refMedicineDetailsInfo.getBatch());
			medicineProcurement.setPacking(refMedicineDetailsInfo.getPacking());
			medicineProcurement.setExpDate(refMedicineDetailsInfo.getExpDate());
			medicineProcurement.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
			medicineProcurement.setDiscount(refMedicineDetails.getDiscount());
			discountInfo.add(refMedicineDetailsInfo.getDiscount());
			gstInfo.add(refMedicineDetailsInfo.getGst());
			
			float totalAmt=medicineProcurement.getQuantity()*medicineProcurement.getCostPrice();
			float discountAmt=(totalAmt*refMedicineDetailsInfo.getDiscount())/100;
			
			float netAmt=totalAmt-discountAmt;
			
			float cgst = gstInfo.get(n) / 2; //6
			float sgst = gstInfo.get(n) / 2; //6

			float gstAmt = (netAmt * gstInfo.get(n)) / 100; //12

			float cgstAmt = gstAmt / 2; //6
			float sgstAmt = gstAmt / 2; //6
			
			float totalGst=cgstAmt+sgstAmt;

			float sum=netAmt+gstAmt;
			//float netAm=medicineProcurement.getQuantity()*medicineProcurement.getCostPrice();
			//netAm=netAm-discountAmt+totalGst;
			medicineProcurement.setAmount(sum);
			medicineProcurementRepository.save(medicineProcurement);
			n++;
			
		
			//for medicine quantity
			MedicineQuantity medicineQuantity = new MedicineQuantity();
			
			MedicineDetails medicineDetailsQuantity = medicineDetailsServiceImpl.findByName(refMedicineDetailsInfo.getItemName());
			
			MedicineQuantity medicineQuantityInfo = medicineQuantityServiceImpl.findByBatchIdAndMedicineDetails(refMedicineDetailsInfo.getBatch(), medicineDetailsQuantity);
			
			if(medicineQuantityInfo!=null) {
				float totalQuantity=medicineQuantityInfo.getTotalQuantity();
				totalQuantity+=medicineProcurement.getQuantity();
				
				medicineQuantityInfo.setBalance(medicineQuantityInfo.getBalance()+medicineProcurement.getQuantity());
				medicineQuantityInfo.setTotalQuantity(totalQuantity);
				medicineQuantityRepository.save(medicineQuantityInfo);
				
			}
			else {
				medicineQuantity.setMedicineDetails(medicineDetailsQuantity);
				medicineQuantity.setBalance(medicineProcurement.getQuantity());
				medicineQuantity.setMedName(medicineDetailsQuantity.getName());
				medicineQuantity.setTotalQuantity(medicineProcurement.getQuantity());
				medicineQuantity.setBatchId(refMedicineDetailsInfo.getBatch());
				medicineQuantityRepository.save(medicineQuantity);
				
			}
		}
		
		/*
		 * String address=
		 * "                                                                                                    Plot No.14,15,16 &17,Nandi Co-op. Society,     \n                                                                                                                  Main Road,Beside Navya Grand Hotel,Miyapur,Hyderabad,TS           "
		 * +
		 * "                                                                                        \n                                                                                                                                                 Phone:040-23046789   "
		 * +
		 * "\n                                                                                                                                        Email : udbhavahospital@gmail.com"
		 * ;
		 */
		
		//pdf code
		// for Generating MedicineProcurement Pdf

		byte[] pdfByte = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		List<MedicineProcurement> medicineProcurementInfo = medicineProcurementRepository
				.findByProcurementId(medicineProcurement.getProcurementId());

		try {

			Document document = new Document(PageSize.A4.rotate());

			Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

			Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();

			// Display a date in day, month, year format
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date).toString();

			PdfPTable table = new PdfPTable(2);

			table.setWidthPercentage(105f);

			Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);

		Image img = Image.getInstance(hospitalLogo.getURL());

			img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
			table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

			Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));

			pq.add(new Chunk(ConstantValues.GRN_ADRESS,redFont));
			PdfPCell cellp = new PdfPCell(pq);
			PdfPCell cell1 = new PdfPCell();

			// for header Bold
			PdfPTable table96 = new PdfPTable(1);
			table96.setWidths(new float[] { 5f });
			table96.setSpacingBefore(10);

			PdfPCell hcell96;
			hcell96 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, headFont1));
			hcell96.setBorder(Rectangle.NO_BORDER);
			hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
			// hcell96.setPaddingLeft(50f);

			table96.addCell(hcell96);
			cell1.addElement(table96);

			PdfPTable table961 = new PdfPTable(1);
			table961.setWidths(new float[] { 5f });
			table961.setSpacingBefore(10);

			
			PdfPCell hcell71;
			hcell71 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
			hcell71.setBorder(Rectangle.NO_BORDER);
			hcell71.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell71.setPaddingLeft(25f);

			table961.addCell(hcell71);
			cell1.addElement(table961);
			
			// for header end
			//cell1.setFixedHeight(107f);
			cell1.addElement(pq);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setColspan(2);
			//table.addCell(cell1);

			//PdfPCell cell19 = new PdfPCell();

			PdfPTable table24 = new PdfPTable(1);
			table24.setWidths(new float[] { 4f });
			//table24.setSpacingBefore(10);

			PdfPCell hcell19;
			hcell19 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell19.setBorder(Rectangle.NO_BORDER);
			hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
			//hcell19.setPaddingTop(30f);
			table24.addCell(hcell19);
			table24.setWidthPercentage(100f);
			cell1.addElement(table24);
			
			PdfPTable table21 = new PdfPTable(1);
			table21.setWidths(new float[] { 10f });
			//table21.setSpacingBefore(10);

			PdfPCell hcell191;
			hcell191 = new PdfPCell(new Phrase("Goods Receipt Note", headFont1));
			hcell191.setBorder(Rectangle.NO_BORDER);
			hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
			table21.addCell(hcell191);

			/*cell19.setFixedHeight(20f);
			cell19.setColspan(2);*/
			cell1.addElement(table21);
			//table.addCell(cell19);

			
			PdfPTable table241 = new PdfPTable(1);
			table241.setWidths(new float[] { 10f });
			//table241.setSpacingBefore(10);

			PdfPCell hcell101;
			hcell101 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell101.setBorder(Rectangle.NO_BORDER);
			hcell101.setPaddingBottom(10f);
			hcell101.setHorizontalAlignment(Element.ALIGN_CENTER);
			table241.addCell(hcell101);
			table241.setWidthPercentage(100f);
			cell1.addElement(table241);

			//PdfPCell cell3 = new PdfPCell();

			PdfPTable table2 = new PdfPTable(2);
			table2.setWidths(new float[] { 5f, 4f });
			table2.setSpacingBefore(10);

			PdfPCell hcell1;
			hcell1 = new PdfPCell(new Phrase("GRN#           :  " + medicineProcurement.getProcurementId(), redFont));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setPaddingLeft(-70f);
			table2.addCell(hcell1);

			hcell1 = new PdfPCell(new Phrase("Challan/Invoice#     :  " + invoiceNo, redFont));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setPaddingLeft(70f);
			table2.addCell(hcell1);

			PdfPCell hcell4;
			hcell4 = new PdfPCell(new Phrase("GRN Dt		      :  " + today, redFont));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(-70f);
			table2.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase("Challan/Invoice Dt  :  " + today, redFont));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(70f);
			table2.addCell(hcell4);

			PdfPCell hcell15;
			hcell15 = new PdfPCell(new Phrase("Created By   :  " + createdBy, redFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-70f);
			table2.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase("Created Dt         		    :  " + today, redFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(70f);
			table2.addCell(hcell15);

			PdfPCell hcell16;
			hcell16 = new PdfPCell(new Phrase("Supllier         :  " + medicineProcurement.getVendorName(), redFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-70f);
			table2.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase("Paymode         		       :  " + "CREDIT", redFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(70f);
			table2.addCell(hcell16);

			PdfPCell hcell17;
			hcell17 = new PdfPCell(new Phrase("Status              :" + medicineProcurement.getStatus(), redFont));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-70f);
			table2.addCell(hcell17);

			hcell17 = new PdfPCell(new Phrase("", redFont));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table2.addCell(hcell17);

			cell1.addElement(table2);
			//cell3.setColspan(2);
			//cell3.setFixedHeight(80f);
			//table.addCell(cell3);

			
			PdfPTable table242 = new PdfPTable(1);
			table242.setWidths(new float[] { 10f });
			//table241.setSpacingBefore(10);

			PdfPCell hcell1021;
			hcell1021 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell1021.setBorder(Rectangle.NO_BORDER);
			hcell1021.setPaddingBottom(10f);
			hcell1021.setHorizontalAlignment(Element.ALIGN_CENTER);
			table242.addCell(hcell1021);
			table242.setWidthPercentage(100f);
			cell1.addElement(table242);
			
			
			//PdfPCell cell31 = new PdfPCell();
			PdfPTable table5 = new PdfPTable(16);
			table5.setWidths(new float[] { 1.4f,6f, 3f, 2.5f, 1.5f, 1.9f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f });

			//table5.setSpacingBefore(10);

			PdfPCell hcell01;
			hcell01 = new PdfPCell(new Phrase("S.No", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			table5.addCell(hcell01);

		
			hcell01 = new PdfPCell(new Phrase("Item Name", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Batch#", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(8f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Exp Dt", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(5f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Qty", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(13f);
			table5.addCell(hcell01);
			
			hcell01 = new PdfPCell(new Phrase("Pack", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(16f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Bonus", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingRight(16f);
			table5.addCell(hcell01);


			hcell01 = new PdfPCell(new Phrase("Sale Rate", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(-3f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Purc Rate", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingRight(-6f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Disc%", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingLeft(12f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Disc.Val", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT); 
			hcell01.setPaddingLeft(14f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("CGST", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-1f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("SGST", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-1f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Purc Value", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingLeft(-10f);
			table5.addCell(hcell01);

			// ******************************
			// PdfPCell cell31 = new PdfPCell();
			PdfPTable table1 = new PdfPTable(16);
			table1.setWidths(new float[] { 1.4f, 6f, 3f, 2.5f, 1.5f, 1.9f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f});

			table1.setSpacingBefore(10);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

		
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

		/*	hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);*/

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("%", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Amt", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("%", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.BLACK);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Amt", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			// *****************************

			// PdfPCell cell3 = new PdfPCell();
			PdfPTable table6 = new PdfPTable(1);
			table6.setWidths(new float[] { 8f });

			table6.setSpacingBefore(10);

			PdfPCell hcell02;
			hcell02 = new PdfPCell(new Phrase("______________________________", redFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			// hcell02.setBackgroundColor(BaseColor.WHITE);
			hcell02.setPaddingTop(-15f);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell02.setPaddingRight(-15f);
			table6.addCell(hcell02);

//**************************************
			PdfPTable table11 = new PdfPTable(16);
			table11.setWidths(new float[] { 1.4f, 6f, 3f, 2.5f, 2f, 1.7f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f});

			table11.setSpacingBefore(10);

			
			int i = 0;
			int j = 0;
			int count = 0;
			

			double totalPurchaseAmt = 0;
			double totalDiscount = 0;
			String expdate=null;

			for (MedicineProcurement medicine : medicineProcurementInfo) {

			
				
				PdfPCell cell;

				MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(medicine.getItemName());

				cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				

				cell = new PdfPCell(new Phrase(medicine.getItemName(), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(medicine.getBatch(), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(3);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(cell);

				
				try
				{
					expdate=medicine.getExpDate().toString().substring(0, 10);
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				expdate=toFormat.format(fromFormat.parse(expdate));
				
				}
				catch(Exception e)
				{
				e.printStackTrace();
				}
				
				cell = new PdfPCell(new Phrase(expdate, redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(3);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				/*cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getQuantity())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(10);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			/*	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);
				
				cell = new PdfPCell(new Phrase(String.valueOf(medicine.getPackSize()), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(10f);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			/*	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);
				if(medicine.getFreeSample()!=null)
				{
				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getFreeSample())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);
				}
				else
				{
					cell = new PdfPCell(new Phrase("0", redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);
				}
				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getMrp())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getCostPrice())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(discountInfo.get(i)),
				 redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				float totalAmt = medicine.getQuantity() * medicine.getCostPrice();
				float discAmt = (totalAmt * discountInfo.get(i)) / 100;

				float netAmount = totalAmt - discAmt;

				cell = new PdfPCell(new Phrase(String.valueOf((discAmt)), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				// Gst Calculation

				float cgst = gstInfo.get(j) / 2;
				float sgst = gstInfo.get(j) / 2;

				float gstAmt = (netAmount * gstInfo.get(j)) / 100;

				float cgstAmt = gstAmt / 2;
				float sgstAmt = gstAmt / 2;
				netAmount+=gstAmt;
				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cgst*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cgstAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(sgst*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(sgstAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				// purchaseValue Calculatuon

				float purchaseAmt = netAmount ;
				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(purchaseAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table11.addCell(cell);

				i++;
				j++;

				totalPurchaseAmt = totalPurchaseAmt + purchaseAmt;
				totalDiscount = totalDiscount + discAmt;

			}

			cell1.setColspan(2);
			table11.setWidthPercentage(100f);
			table1.setWidthPercentage(100f);
			table5.setWidthPercentage(100f);
			//table6.setWidthPercentage(100f);
			cell1.addElement(table5);
			 PdfPCell cellU = new PdfPCell(); Paragraph p = new
					  Paragraph("_________________________________________________________");
					  cellU.addElement(p);
					  
					  table1.addCell(cellU);
					 
		    cell1.addElement(table6);
			cell1.addElement(table1);
			
			
			
			 
			
			cell1.addElement(table11);
			

			table.addCell(cell1);

			// new code for updated row

			PdfPCell cell5 = new PdfPCell();

			PdfPTable table35 = new PdfPTable(4);
			table35.setWidths(new float[] { 7f, 7f, 7f, 7f });
			table35.setSpacingBefore(10);
			table35.setWidthPercentage(100);

			PdfPCell hcell12;
			hcell12 = new PdfPCell(new Phrase("", headFont1));
			hcell12.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell12);

			hcell12 = new PdfPCell(new Phrase("", redFont));
			hcell12.setBorder(Rectangle.NO_BORDER);
			// hcell12.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell12);

			PdfPCell hcell13;
			hcell13 = new PdfPCell(new Phrase("Total Pur Amount     :", redFont));
			hcell13.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell13.setPaddingRight(-100f);
			hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell13);

			hcell13 = new PdfPCell(new Phrase("" + Math.round(totalPurchaseAmt*100.0)/100.0, redFont));
			hcell13.setBorder(Rectangle.NO_BORDER);
			hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell13);

			PdfPCell hcell11;
			hcell11 = new PdfPCell(new Phrase("", headFont1));
			hcell11.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell11);

			hcell11 = new PdfPCell(new Phrase("", redFont));
			hcell11.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell11);

			PdfPCell hcell10;
			hcell10 = new PdfPCell(new Phrase("Discount     :", redFont));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell10.setPaddingRight(-100f);
			hcell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell10);

			hcell10 = new PdfPCell(new Phrase("" + Math.round(totalDiscount*100.0)/100.0, redFont));
			hcell10.setBorder(Rectangle.NO_BORDER);
			hcell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell10);

			PdfPCell hcell9;
			hcell9 = new PdfPCell(new Phrase("", headFont1));
			hcell9.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell9);

			hcell9 = new PdfPCell(new Phrase("", redFont));
			hcell9.setBorder(Rectangle.NO_BORDER);
			// hcell9.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell9);

			// calculation for Round Off
			BigDecimal bd = new BigDecimal(totalPurchaseAmt - Math.floor(totalPurchaseAmt));
			bd = bd.setScale(2, RoundingMode.HALF_DOWN);
			float roundOff = bd.floatValue();
			System.out.println("Round Off Data" + roundOff);

			PdfPCell hcell8;

			hcell8 = new PdfPCell(new Phrase("Round Off     :" + "", redFont));
			hcell8.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell8.setPaddingRight(-100f);
			hcell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell8);

			float roundOffVal=1f-roundOff;
			System.out.println("round value+:" +roundOffVal);
			if (roundOff <= 0.50) {
				hcell8 = new PdfPCell(new Phrase("-" + Math.round(roundOff*100.0)/100.0, redFont));
			} else {
				hcell8 = new PdfPCell(new Phrase("+" + Math.round(roundOffVal*100.0)/100.0, redFont));
			}
			hcell8.setBorder(Rectangle.NO_BORDER);
			hcell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell8);

			PdfPCell hcell7;
			hcell7 = new PdfPCell(new Phrase("", headFont1));
			hcell7.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell7);

			hcell7 = new PdfPCell(new Phrase("", redFont));
			hcell7.setBorder(Rectangle.NO_BORDER);
			// hcell7.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell7);

			PdfPCell hcell6;
			hcell6 = new PdfPCell(new Phrase("Net Amount     :", redFont));

			hcell6.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell6.setPaddingRight(-100f);
			hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell6);

			
			if (roundOff <= 0.50) {
				double netAmt =   (double)(totalPurchaseAmt - roundOff);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			} else {
				double netAmt = (double) (totalPurchaseAmt + roundOffVal);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			}
			// hcell6 = new PdfPCell(new Phrase(""+totalPurchaseAmt, redFont));
			/*if (roundOff <= 0.50) {
				double netAmt =   (double)(totalPurchaseAmt - roundOff);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			} else {
				double netAmt = (double) (totalPurchaseAmt + roundOff);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			}*/
			hcell6.setBorder(Rectangle.NO_BORDER);
			hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell6);

			cell5.setFixedHeight(105f);
			cell5.setColspan(2);
			cell5.addElement(table35);
			table.addCell(cell5);

			// end of updated code

			// PdfPCell cell4 = new PdfPCell();

			PdfPTable table3 = new PdfPTable(2);
			table3.setWidths(new float[] { 5f, 4f });
			table3.setSpacingBefore(10);

			PdfPCell hcell111;
			hcell111 = new PdfPCell(new Phrase("Printed By          :  " + createdBy, redFont));
			hcell111.setBorder(Rectangle.NO_BORDER);
			hcell111.setPaddingLeft(-90f);
			hcell111.setPaddingTop(-5f);
			table3.addCell(hcell111);

			hcell111 = new PdfPCell(new Phrase("Printed Date    :  " + today, redFont));
			hcell111.setBorder(Rectangle.NO_BORDER);
			// hcell11.setPaddingLeft(110f);
			hcell111.setPaddingRight(-70f);
			hcell111.setPaddingTop(-5f);
			hcell111.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table3.addCell(hcell111);

			document.add(table);
			document.add(table3);
			document.close();

			 salesPaymentPdfInfo=salesPaymentPdfRepository.getProcurementPdf(procId);
			System.out.println("finished"+salesPaymentPdfInfo==null);
			
			pdfByte = byteArrayOutputStream.toByteArray();
			
			salesPaymentPdfInfo.setFileuri(salesPaymentPdfInfo.getFileuri());
			salesPaymentPdfInfo.setFileName(salesPaymentPdfInfo.getFileName());
			salesPaymentPdfInfo.setPid(salesPaymentPdfInfo.getPid());
			salesPaymentPdfInfo.setData(pdfByte);
			System.out.println(salesPaymentPdfInfo);
			// System.out.println(discount);
			salesPaymentPdfServiceImpl.save(salesPaymentPdfInfo);

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		return salesPaymentPdfInfo;


}

	
	@Transactional
	@CacheEvict(value="procurementCache",allEntries = true)
	public SalesPaymentPdf computeSave(MedicineProcurement medicineProcurement,Principal principal)
	{
		
		String draft="";
		if(!medicineProcurementRepository.findByInvoiceNo(medicineProcurement.getInvoiceNo()).isEmpty())
		{
			throw new RuntimeException("Invoice Number Already Exists !");
		}
		
		Vendors vendors=vendorsServiceImpl.findByVendorName(medicineProcurement.getVendorName());
		medicineProcurement.setMedicineProcurmentVendors(vendors);
		String procureId=null;
		String invoiceNo=medicineProcurement.getInvoiceNo();
		
		draft=medicineProcurement.getDraft();
		Location locationInfo=locationServiceImpl.findByLocationName(medicineProcurement.getLocation());
		medicineProcurement.setMedicineProcurmentLocation(locationInfo);
		medicineProcurement.setDateOfProcurement(Timestamp.valueOf(LocalDateTime.now()));
		medicineProcurement.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		List<RefMedicineDetails> refMedicineDetailsList= medicineProcurement.getRefMedicineDetails();
		medicineProcurement.setDateOfProcurement(Timestamp.valueOf(LocalDateTime.now()));
	//	medicineProcurement.setAmount(medicineProcurement.getAmount());
		
		
		medicineProcurement.setProcurementId(getNextProcurementId());
		
		if(medicineProcurement.getProcurementType().equalsIgnoreCase("Due"))
		{
			medicineProcurement.setPaid("No");
		}
		else
		{
			medicineProcurement.setPaid("Yes");
		}
		
		procureId=medicineProcurement.getProcurementId();
		System.out.println("Master"+getNextProcurementId());
		int cnt=refMedicineDetailsList.size();
		
		List<Float> discountInfo=new ArrayList<>();
		
		List<Float> gstInfo=new ArrayList<>();
		
		
		//CreatedBy (Security)
		User userSecurity=userServiceImpl.findByUserName(principal.getName());
		String createdBy=userSecurity.getFirstName()+" "+userSecurity.getLastName();
		
		int n=0;
		float tGst=0; //for pdf
		String draftProcId=null;
		
		if(!medicineProcurementRepository.findByInvoiceNoAndDraft(invoiceNo, "YES").isEmpty())
		{
			draftProcId=medicineProcurementRepository.findByInvoiceNoAndDraft(invoiceNo, "YES").get(0).getProcurementId();
		}
		
		
		for(RefMedicineDetails refMedicineDetails:refMedicineDetailsList)
		{
			RefMedicineDetails refMedicineDetailsInfo=refMedicineDetails;
			MedicineDetails medicineDetailsDraft=medicineDetailsServiceImpl.findByName(refMedicineDetailsInfo.getItemName());
			
			/*
			 * Checking draft
			 */
			MedicineProcurement medicineProcurementInfo=medicineProcurementRepository.findByInvoiceNoAndDraftAndMedicineProcurmentMedicineDetails(invoiceNo, "YES",medicineDetailsDraft);
			if(medicineProcurementInfo!=null)
			{
				if(refMedicineDetails.getExpDate()==null)
				{
					throw new RuntimeException("Expiry date is mandatory");
				}
				
				System.out.println("Master nik proc id"+draftProcId);
				medicineProcurement.setMasterProcurementId(medicineProcurementInfo.getMasterProcurementId());
				medicineProcurement.setBatch(refMedicineDetailsInfo.getBatch());
				medicineProcurement.setCostPrice(refMedicineDetailsInfo.getCostPrice());
				medicineProcurement.setMrp(refMedicineDetailsInfo.getMrp());
				medicineProcurement.setFreeSample(refMedicineDetailsInfo.getFreeSample());
				medicineProcurement.setProcurementId(medicineProcurementInfo.getProcurementId());
				medicineProcurement.setQuantity(refMedicineDetailsInfo.getQuantity());
				medicineProcurement.setPackSize(refMedicineDetailsInfo.getPackSize());
				float freeSample=(medicineProcurement.getFreeSample()!=null) ? Float.valueOf(medicineProcurement.getFreeSample()) : 0;
				
				medicineProcurement.setDetailedQuantity(((long)(refMedicineDetailsInfo.getQuantity()+freeSample))*refMedicineDetailsInfo.getPackSize());
				medicineProcurement.setStatus("Not-Approved");
				medicineProcurement.setGst(refMedicineDetailsInfo.getGst());
				medicineProcurement.setPacking(refMedicineDetailsInfo.getPacking());
				medicineProcurement.setExpDate(refMedicineDetailsInfo.getExpDate());
				medicineProcurement.setManufacturedDate(refMedicineDetailsInfo.getManufacturedDate());
				medicineProcurement.setTax(refMedicineDetailsInfo.getTax());
				MedicineDetails medicineDetails=null;
				try
				{
					medicineDetails=medicineDetailsServiceImpl.findByName(refMedicineDetailsInfo.getItemName());
				}
				catch(Exception e)
				{
					throw new RuntimeException("Duplicates Entries For Item "+refMedicineDetailsInfo.getItemName() +" In Item list !");
				}
				medicineProcurement.setMedicineProcurmentMedicineDetails(medicineDetails);
				medicineProcurement.setMedicineProcurmentVendors(vendorsServiceImpl.findByVendorName(medicineProcurement.getVendorName()));
				medicineProcurement.setItemName(medicineDetails.getName());
				medicineProcurement.setPacking(refMedicineDetailsInfo.getPacking());
				medicineProcurement.setExpDate(refMedicineDetailsInfo.getExpDate());
				medicineProcurement.setDiscount(refMedicineDetails.getDiscount());
				discountInfo.add(refMedicineDetailsInfo.getDiscount());
				gstInfo.add(refMedicineDetailsInfo.getGst());
				
				float totalAmt=medicineProcurement.getQuantity()*medicineProcurement.getCostPrice();
				float discountAmt=(totalAmt*refMedicineDetailsInfo.getDiscount())/100;
				
				float netAmt=totalAmt-discountAmt;
				
				float cgst = gstInfo.get(n) / 2; //6
				float sgst = gstInfo.get(n) / 2; //6

				float gstAmt = (netAmt * gstInfo.get(n)) / 100; //12

				float cgstAmt = gstAmt / 2; //6
				float sgstAmt = gstAmt / 2; //6
				
				float totalGst=cgstAmt+sgstAmt;
				

				tGst=totalGst;
				float sum=netAmt+gstAmt;
				//float netAm=medicineProcurement.getQuantity()*medicineProcurement.getCostPrice();
				//netAm=netAm-discountAmt+totalGst;
				medicineProcurement.setAmount(netAmt+gstAmt);
				n++;
				medicineProcurementRepository.save(medicineProcurement);

				
			}
		
			else
			{
			
				System.out.println("Master nik proc id"+draftProcId);
					if(refMedicineDetails.getExpDate()==null)
					{
						throw new RuntimeException("Expiry date is mandatory");
					}
					
					if(draftProcId==null)
					{
					medicineProcurement.setProcurementId(getNextProcurementId());
					}
					else
					{
						medicineProcurement.setProcurementId(draftProcId);
					}
					medicineProcurement.setMasterProcurementId(getNextMasterProcurementId());
					
					System.out.println("Master"+getNextMasterProcurementId());
					medicineProcurement.setBatch(refMedicineDetailsInfo.getBatch());
					medicineProcurement.setCostPrice(refMedicineDetailsInfo.getCostPrice());
					medicineProcurement.setMrp(refMedicineDetailsInfo.getMrp());
					medicineProcurement.setProcurementId(procureId);
					medicineProcurement.setFreeSample(refMedicineDetailsInfo.getFreeSample());
					medicineProcurement.setQuantity(refMedicineDetailsInfo.getQuantity());
					medicineProcurement.setPackSize(refMedicineDetailsInfo.getPackSize());
					float freeSample=(medicineProcurement.getFreeSample()!=null) ? Float.valueOf(medicineProcurement.getFreeSample()) : 0;
					
					medicineProcurement.setDetailedQuantity(((long)(refMedicineDetailsInfo.getQuantity()+freeSample))*refMedicineDetailsInfo.getPackSize());
					medicineProcurement.setStatus("Not-Approved");
					medicineProcurement.setGst(refMedicineDetailsInfo.getGst());
					medicineProcurement.setPacking(refMedicineDetailsInfo.getPacking());
					medicineProcurement.setExpDate(refMedicineDetailsInfo.getExpDate());
					medicineProcurement.setManufacturedDate(refMedicineDetailsInfo.getManufacturedDate());
					medicineProcurement.setTax(refMedicineDetailsInfo.getTax());
					MedicineDetails medicineDetails=null;
					try
					{
						medicineDetails=medicineDetailsServiceImpl.findByName(refMedicineDetailsInfo.getItemName());
					}
					catch(Exception e)
					{
						throw new RuntimeException("Duplicates Entries For Item "+refMedicineDetailsInfo.getItemName() +" In Item list !");
					}
					medicineProcurement.setMedicineProcurmentMedicineDetails(medicineDetails);
					medicineProcurement.setMedicineProcurmentVendors(vendorsServiceImpl.findByVendorName(medicineProcurement.getVendorName()));
					medicineProcurement.setItemName(medicineDetails.getName());
					medicineProcurement.setBatch(refMedicineDetailsInfo.getBatch());
					medicineProcurement.setPacking(refMedicineDetailsInfo.getPacking());
					medicineProcurement.setExpDate(refMedicineDetailsInfo.getExpDate());
					medicineProcurement.setDiscount(refMedicineDetails.getDiscount());
					discountInfo.add(refMedicineDetailsInfo.getDiscount());
					gstInfo.add(refMedicineDetailsInfo.getGst());
					
					
					float totalAmt=(medicineProcurement.getQuantity())*medicineProcurement.getCostPrice();
					float discountAmt=(totalAmt*refMedicineDetailsInfo.getDiscount())/100;
					
					float netAmt=totalAmt-discountAmt;
					
					float cgst = gstInfo.get(n) / 2; //6
					float sgst = gstInfo.get(n) / 2; //6
		
					float gstAmt = (netAmt * gstInfo.get(n)) / 100; //12
		
					float cgstAmt = gstAmt / 2; //6
					float sgstAmt = gstAmt / 2; //6
					
					float totalGst=cgstAmt+sgstAmt;
					
		
					tGst=totalGst;
					float sum=netAmt+gstAmt;
					//float netAm=medicineProcurement.getQuantity()*medicineProcurement.getCostPrice();
					//netAm=netAm-discountAmt+totalGst;
					medicineProcurement.setAmount(netAmt+gstAmt);
					n++;
					medicineProcurementRepository.save(medicineProcurement);
			
			}
		
			//for medicine quantity
			MedicineQuantity medicineQuantity = new MedicineQuantity();
			
			MedicineDetails medicineDetailsQuantity = medicineDetailsServiceImpl.findByName(refMedicineDetailsInfo.getItemName());
			
			MedicineQuantity medicineQuantityInfo = medicineQuantityServiceImpl.findByBatchIdAndMedicineDetails(refMedicineDetailsInfo.getBatch(), medicineDetailsQuantity);
			float freeSample=(medicineProcurement.getFreeSample()!=null) ? Float.valueOf(medicineProcurement.getFreeSample()) : 0;
			
			
			if(medicineProcurement.getDraft().equalsIgnoreCase("NO")) {
			if(medicineQuantityInfo!=null) {
				float totalQuantity=medicineQuantityInfo.getTotalQuantity();
				totalQuantity+=medicineProcurement.getQuantity()*medicineProcurement.getPackSize();
				
				medicineQuantityInfo.setBalance(medicineQuantityInfo.getBalance()+(((long)(medicineProcurement.getQuantity()+freeSample))*medicineProcurement.getPackSize()));
				medicineQuantityInfo.setTotalQuantity(totalQuantity);
				medicineQuantityRepository.save(medicineQuantityInfo);
				
			}
			else {
				medicineQuantity.setMedicineDetails(medicineDetailsQuantity);
				medicineQuantity.setBalance(((long)(medicineProcurement.getQuantity()+freeSample))*medicineProcurement.getPackSize());
				medicineQuantity.setMedName(medicineDetailsQuantity.getName());
				medicineQuantity.setTotalQuantity(medicineProcurement.getQuantity()*medicineProcurement.getPackSize());
				medicineQuantity.setBatchId(refMedicineDetailsInfo.getBatch());
				medicineQuantityRepository.save(medicineQuantity);
				
			}
			
			}
		}
		
		/*
		 * String address=
		 * "                                                                                                    Plot No.14,15,16 &17,Nandi Co-op. Society,     \n                                                                                                                  Main Road,Beside Navya Grand Hotel,Miyapur,Hyderabad,TS           "
		 * +
		 * "                                                                                        \n                                                                                                                                                 Phone:040-23046789   "
		 * +
		 * "\n                                                                                                                                        Email : udbhavahospital@gmail.com"
		 * ;
		 */
		
		//pdf code
		// for Generating MedicineProcurement Pdf
		if(draft.equalsIgnoreCase("NO"))
		{
		
		if(!medicineProcurement.getProcurementType().equals("Due"))
		{
			
			byte[] pdfByte = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		List<MedicineProcurement> medicineProcurementInfo = medicineProcurementRepository
				.findByProcurementId(medicineProcurement.getProcurementId());

		try {

			Document document = new Document(PageSize.A4.rotate());

			Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

			Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();

			// Display a date in day, month, year format
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date).toString();

			PdfPTable table = new PdfPTable(2);

			table.setWidthPercentage(105f);

			Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);

		Image img = Image.getInstance(hospitalLogo.getURL());

			img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
			table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

			Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));

			pq.add(new Chunk(ConstantValues.GRN_ADRESS,redFont));
			PdfPCell cellp = new PdfPCell(pq);
			PdfPCell cell1 = new PdfPCell();

			// for header Bold
			PdfPTable table96 = new PdfPTable(1);
			table96.setWidths(new float[] { 5f });
			table96.setSpacingBefore(10);

			PdfPCell hcell96;
			hcell96 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, headFont1));
			hcell96.setBorder(Rectangle.NO_BORDER);
			hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
			// hcell96.setPaddingLeft(50f);

			table96.addCell(hcell96);
			cell1.addElement(table96);

			PdfPTable table961 = new PdfPTable(1);
			table961.setWidths(new float[] { 5f });
			table961.setSpacingBefore(10);

			
			PdfPCell hcell71;
			hcell71 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
			hcell71.setBorder(Rectangle.NO_BORDER);
			hcell71.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell71.setPaddingLeft(25f);

			table961.addCell(hcell71);
			cell1.addElement(table961);
			
			// for header end
			//cell1.setFixedHeight(107f);
			cell1.addElement(pq);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setColspan(2);
			//table.addCell(cell1);

			//PdfPCell cell19 = new PdfPCell();

			PdfPTable table24 = new PdfPTable(1);
			table24.setWidths(new float[] { 4f });
			//table24.setSpacingBefore(10);

			PdfPCell hcell19;
			hcell19 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell19.setBorder(Rectangle.NO_BORDER);
			hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
			//hcell19.setPaddingTop(30f);
			table24.addCell(hcell19);
			table24.setWidthPercentage(100f);
			cell1.addElement(table24);
			
			PdfPTable table21 = new PdfPTable(1);
			table21.setWidths(new float[] { 10f });
			//table21.setSpacingBefore(10);

			PdfPCell hcell191;
			hcell191 = new PdfPCell(new Phrase("Goods Receipt Note", headFont1));
			hcell191.setBorder(Rectangle.NO_BORDER);
			hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
			table21.addCell(hcell191);

			/*cell19.setFixedHeight(20f);
			cell19.setColspan(2);*/
			cell1.addElement(table21);
			//table.addCell(cell19);

			
			PdfPTable table241 = new PdfPTable(1);
			table241.setWidths(new float[] { 10f });
			//table241.setSpacingBefore(10);

			PdfPCell hcell101;
			hcell101 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell101.setBorder(Rectangle.NO_BORDER);
			hcell101.setPaddingBottom(10f);
			hcell101.setHorizontalAlignment(Element.ALIGN_CENTER);
			table241.addCell(hcell101);
			table241.setWidthPercentage(100f);
			cell1.addElement(table241);

			//PdfPCell cell3 = new PdfPCell();

			PdfPTable table2 = new PdfPTable(2);
			table2.setWidths(new float[] { 5f, 4f });
			table2.setSpacingBefore(10);

			PdfPCell hcell1;
			hcell1 = new PdfPCell(new Phrase("GRN#           :  " + medicineProcurement.getProcurementId(), redFont));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setPaddingLeft(-70f);
			table2.addCell(hcell1);

			hcell1 = new PdfPCell(new Phrase("Challan/Invoice#     :  " + invoiceNo, redFont));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setPaddingLeft(70f);
			table2.addCell(hcell1);

			PdfPCell hcell4;
			hcell4 = new PdfPCell(new Phrase("GRN Dt		      :  " + today, redFont));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(-70f);
			table2.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase("Challan/Invoice Dt  :  " + today, redFont));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(70f);
			table2.addCell(hcell4);

			PdfPCell hcell15;
			hcell15 = new PdfPCell(new Phrase("Created By   :  " + createdBy, redFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-70f);
			table2.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase("Created Dt         		    :  " + today, redFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(70f);
			table2.addCell(hcell15);

			PdfPCell hcell16;
			hcell16 = new PdfPCell(new Phrase("Supllier         :  " + medicineProcurement.getVendorName(), redFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-70f);
			table2.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase("Paymode         		       :  " + "CREDIT", redFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(70f);
			table2.addCell(hcell16);

			PdfPCell hcell17;
			hcell17 = new PdfPCell(new Phrase("Status              :" + medicineProcurement.getStatus(), redFont));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-70f);
			table2.addCell(hcell17);

			hcell17 = new PdfPCell(new Phrase("", redFont));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table2.addCell(hcell17);

			cell1.addElement(table2);
			//cell3.setColspan(2);
			//cell3.setFixedHeight(80f);
			//table.addCell(cell3);

			
			PdfPTable table242 = new PdfPTable(1);
			table242.setWidths(new float[] { 10f });
			//table241.setSpacingBefore(10);

			PdfPCell hcell1021;
			hcell1021 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell1021.setBorder(Rectangle.NO_BORDER);
			hcell1021.setPaddingBottom(10f);
			hcell1021.setHorizontalAlignment(Element.ALIGN_CENTER);
			table242.addCell(hcell1021);
			table242.setWidthPercentage(100f);
			cell1.addElement(table242);
			
			
			//PdfPCell cell31 = new PdfPCell();
			PdfPTable table5 = new PdfPTable(16);
			table5.setWidths(new float[] { 1.4f,6f, 3f, 2.5f, 1.5f, 1.9f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f });

			//table5.setSpacingBefore(10);

			PdfPCell hcell01;
			hcell01 = new PdfPCell(new Phrase("S.No", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			table5.addCell(hcell01);

		
			hcell01 = new PdfPCell(new Phrase("Item Name", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Batch#", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(8f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Exp Dt", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(5f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Qty", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(13f);
			table5.addCell(hcell01);
			
			hcell01 = new PdfPCell(new Phrase("Pack", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(16f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Bonus", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingRight(16f);
			table5.addCell(hcell01);


			hcell01 = new PdfPCell(new Phrase("Sale Rate", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(-3f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Purc Rate", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingRight(-6f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Disc%", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingLeft(12f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Disc.Val", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT); 
			hcell01.setPaddingLeft(14f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("CGST", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-1f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("SGST", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-1f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Purc Value", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingLeft(-10f);
			table5.addCell(hcell01);

			// ******************************
			// PdfPCell cell31 = new PdfPCell();
			PdfPTable table1 = new PdfPTable(16);
			table1.setWidths(new float[] { 1.4f, 6f, 3f, 2.5f, 1.5f, 1.9f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f});

			table1.setSpacingBefore(10);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

		
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

		/*	hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);*/

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("%", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Amt", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("%", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.BLACK);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Amt", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			// *****************************

			// PdfPCell cell3 = new PdfPCell();
			PdfPTable table6 = new PdfPTable(1);
			table6.setWidths(new float[] { 8f });

			table6.setSpacingBefore(10);

			PdfPCell hcell02;
			hcell02 = new PdfPCell(new Phrase("______________________________", redFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			// hcell02.setBackgroundColor(BaseColor.WHITE);
			hcell02.setPaddingTop(-15f);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell02.setPaddingRight(-15f);
			table6.addCell(hcell02);

//**************************************
			PdfPTable table11 = new PdfPTable(16);
			table11.setWidths(new float[] { 1.4f, 6f, 3f, 2.5f, 2f, 1.7f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f});

			table11.setSpacingBefore(10);

			
			int i = 0;
			int j = 0;
			int count = 0;
			

			double totalPurchaseAmt = 0;
			double totalDiscount = 0;
			String expdate=null;

			for (MedicineProcurement medicine : medicineProcurementInfo) {

			
				
				PdfPCell cell;

				MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(medicine.getItemName());

				cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				

				cell = new PdfPCell(new Phrase(medicine.getItemName(), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(medicine.getBatch(), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(3);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(cell);

				
				try
				{
					expdate=medicine.getExpDate().toString().substring(0, 10);
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				expdate=toFormat.format(fromFormat.parse(expdate));
				
				}
				catch(Exception e)
				{
				e.printStackTrace();
				}
				
				cell = new PdfPCell(new Phrase(expdate, redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(3);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				/*cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getQuantity())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(10);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			/*	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);
				
				cell = new PdfPCell(new Phrase(String.valueOf(medicine.getPackSize()), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(10f);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			/*	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);
				if(medicine.getFreeSample()!=null)
				{
				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getFreeSample())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);
				}
				else
				{
					cell = new PdfPCell(new Phrase("0", redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);
				}
				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getMrp())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getCostPrice())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(discountInfo.get(i)),
				 redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				float totalAmt = medicine.getQuantity() * medicine.getCostPrice();
				float discAmt = (totalAmt * discountInfo.get(i)) / 100;

				float netAmount = totalAmt - discAmt;

				cell = new PdfPCell(new Phrase(String.valueOf((discAmt)), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				// Gst Calculation

				float cgst = gstInfo.get(j) / 2;
				float sgst = gstInfo.get(j) / 2;

				float gstAmt = (netAmount * gstInfo.get(j)) / 100;

				float cgstAmt = gstAmt / 2;
				float sgstAmt = gstAmt / 2;
				netAmount+=gstAmt;
				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cgst*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cgstAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(sgst*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(sgstAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				// purchaseValue Calculatuon

				float purchaseAmt = netAmount ;
				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(purchaseAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table11.addCell(cell);

				i++;
				j++;

				totalPurchaseAmt = totalPurchaseAmt + purchaseAmt;
				totalDiscount = totalDiscount + discAmt;

			}

			cell1.setColspan(2);
			table11.setWidthPercentage(100f);
			table1.setWidthPercentage(100f);
			table5.setWidthPercentage(100f);
			//table6.setWidthPercentage(100f);
			cell1.addElement(table5);
			 PdfPCell cellU = new PdfPCell(); Paragraph p = new
					  Paragraph("_________________________________________________________");
					  cellU.addElement(p);
					  
					  table1.addCell(cellU);
					 
		    cell1.addElement(table6);
			cell1.addElement(table1);
			
			
			
			 
			
			cell1.addElement(table11);
			

			table.addCell(cell1);
			// new code for updated row

			PdfPCell cell5 = new PdfPCell();

			PdfPTable table35 = new PdfPTable(4);
			table35.setWidths(new float[] { 7f, 7f, 7f, 7f });
			table35.setSpacingBefore(10);
			table35.setWidthPercentage(100);

			PdfPCell hcell12;
			hcell12 = new PdfPCell(new Phrase("", headFont1));
			hcell12.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell12);

			hcell12 = new PdfPCell(new Phrase("", redFont));
			hcell12.setBorder(Rectangle.NO_BORDER);
			// hcell12.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell12);

			PdfPCell hcell13;
			hcell13 = new PdfPCell(new Phrase("Total Pur Amount     :", redFont));
			hcell13.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell13.setPaddingRight(-100f);
			hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell13);

			hcell13 = new PdfPCell(new Phrase("" + Math.round(totalPurchaseAmt*100.0)/100.0, redFont));
			hcell13.setBorder(Rectangle.NO_BORDER);
			hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell13);

			PdfPCell hcell11;
			hcell11 = new PdfPCell(new Phrase("", headFont1));
			hcell11.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell11);

			hcell11 = new PdfPCell(new Phrase("", redFont));
			hcell11.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell11);

			PdfPCell hcell10;
			hcell10 = new PdfPCell(new Phrase("Discount     :", redFont));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell10.setPaddingRight(-100f);
			hcell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell10);

			hcell10 = new PdfPCell(new Phrase("" + Math.round(totalDiscount*100.0)/100.0, redFont));
			hcell10.setBorder(Rectangle.NO_BORDER);
			hcell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell10);

			PdfPCell hcell9;
			hcell9 = new PdfPCell(new Phrase("", headFont1));
			hcell9.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell9);

			hcell9 = new PdfPCell(new Phrase("", redFont));
			hcell9.setBorder(Rectangle.NO_BORDER);
			// hcell9.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell9);

/*					// calculation for Round Off
			BigDecimal bd = new BigDecimal(totalPurchaseAmt - Math.floor(totalPurchaseAmt));
			bd = bd.setScale(2, RoundingMode.HALF_DOWN);
			float roundOff = bd.floatValue();
			System.out.println("Round Off Data" + roundOff);
*/
			PdfPCell hcell8;

			hcell8 = new PdfPCell(new Phrase("Rounded Off To    :" + "", redFont));
			hcell8.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell8.setPaddingRight(-100f);
			hcell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell8);

			hcell8 = new PdfPCell(new Phrase(String.valueOf(Math.round(totalPurchaseAmt)), redFont));

/*					float roundOffVal=1f-roundOff;
			System.out.println("round value+:" +roundOffVal);
			if (roundOff <= 0.50) {
				hcell8 = new PdfPCell(new Phrase("-" + Math.round(roundOff*100.0)/100.0, redFont));
			} else {
				hcell8 = new PdfPCell(new Phrase("+" + Math.round(roundOffVal*100.0)/100.0, redFont));
			}
*/					hcell8.setBorder(Rectangle.NO_BORDER);
			hcell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell8);

			PdfPCell hcell7;
			hcell7 = new PdfPCell(new Phrase("", headFont1));
			hcell7.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell7);

			hcell7 = new PdfPCell(new Phrase("", redFont));
			hcell7.setBorder(Rectangle.NO_BORDER);
			// hcell7.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell7);

			PdfPCell hcell6;
			hcell6 = new PdfPCell(new Phrase("Net Amount     :", redFont));

			hcell6.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell6.setPaddingRight(-100f);
			hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell6);

			
			/*if (roundOff <= 0.50) {
				double netAmt =   (double)(totalPurchaseAmt - roundOff);*/
				hcell6 = new PdfPCell(new Phrase("" + Math.round(totalPurchaseAmt), redFont));
			/*} else {
				double netAmt = (double) (totalPurchaseAmt + roundOffVal);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			}*/
				
			// hcell6 = new PdfPCell(new Phrase(""+totalPurchaseAmt, redFont));
			/*if (roundOff <= 0.50) {
				double netAmt =   (double)(totalPurchaseAmt - roundOff);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			} else {
				double netAmt = (double) (totalPurchaseAmt + roundOff);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			}*/
			hcell6.setBorder(Rectangle.NO_BORDER);
			hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell6);

			cell5.setFixedHeight(105f);
			cell5.setColspan(2);
			cell5.addElement(table35);
			table.addCell(cell5);

			// end of updated code

			// PdfPCell cell4 = new PdfPCell();

			PdfPTable table3 = new PdfPTable(2);
			table3.setWidths(new float[] { 5f, 4f });
			table3.setSpacingBefore(10);

			PdfPCell hcell111;
			hcell111 = new PdfPCell(new Phrase("Printed By          :  " + createdBy, redFont));
			hcell111.setBorder(Rectangle.NO_BORDER);
			hcell111.setPaddingLeft(-90f);
			hcell111.setPaddingTop(-5f);
			table3.addCell(hcell111);

			hcell111 = new PdfPCell(new Phrase("Printed Date    :  " + today, redFont));
			hcell111.setBorder(Rectangle.NO_BORDER);
			// hcell11.setPaddingLeft(110f);
			hcell111.setPaddingRight(-70f);
			hcell111.setPaddingTop(-5f);
			hcell111.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table3.addCell(hcell111);

			document.add(table);
			document.add(table3);
			document.close();

			System.out.println("finished");
			pdfByte = byteArrayOutputStream.toByteArray();
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
					.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

			salesPaymentPdf = new SalesPaymentPdf();
			salesPaymentPdf.setFileName(procureId+" Procurement Sales");
			salesPaymentPdf.setFileuri(uri);
			salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
			salesPaymentPdf.setData(pdfByte);
			// System.out.println(discount);
			salesPaymentPdfServiceImpl.save(salesPaymentPdf);

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		}
		else
		{
			byte[] pdfByte = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			List<MedicineProcurement> medicineProcurementInfo = medicineProcurementRepository
					.findByProcurementId(medicineProcurement.getProcurementId());

			try {

				Document document = new Document(PageSize.A4.rotate());

				Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
				Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

				Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
				Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
				PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
				document.open();

				// Display a date in day, month, year format
				Date date = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
				String today = formatter.format(date).toString();

				PdfPTable table = new PdfPTable(2);

				table.setWidthPercentage(105f);

				Resource fileResourcee = resourceLoader.getResource("classpath:udbhava.png");

			Image img = Image.getInstance(hospitalLogo.getURL());

				img.scaleAbsolute(75, 95);
				table.setWidthPercentage(105f);

				Phrase pq = new Phrase(new Chunk(img, 5, -83));

				pq.add(new Chunk(ConstantValues.GRN_ADRESS,redFont));
				PdfPCell cellp = new PdfPCell(pq);
				PdfPCell cell1 = new PdfPCell();

				// for header Bold
				PdfPTable table96 = new PdfPTable(1);
				table96.setWidths(new float[] { 5f });
				table96.setSpacingBefore(10);

				PdfPCell hcell96;
				hcell96 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, headFont1));
				hcell96.setBorder(Rectangle.NO_BORDER);
				hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
				// hcell96.setPaddingLeft(50f);

				table96.addCell(hcell96);
				cell1.addElement(table96);

				PdfPTable table961 = new PdfPTable(1);
				table961.setWidths(new float[] { 5f });
				table961.setSpacingBefore(10);

				
				PdfPCell hcell71;
				hcell71 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
				hcell71.setBorder(Rectangle.NO_BORDER);
				hcell71.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell71.setPaddingLeft(25f);

				table961.addCell(hcell71);
				cell1.addElement(table961);
				
				// for header end
				//cell1.setFixedHeight(107f);
				cell1.addElement(pq);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setColspan(2);
				//table.addCell(cell1);

				//PdfPCell cell19 = new PdfPCell();

				PdfPTable table24 = new PdfPTable(1);
				table24.setWidths(new float[] { 4f });
				//table24.setSpacingBefore(10);

				PdfPCell hcell19;
				hcell19 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
				hcell19.setBorder(Rectangle.NO_BORDER);
				hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				//hcell19.setPaddingTop(30f);
				table24.addCell(hcell19);
				table24.setWidthPercentage(100f);
				cell1.addElement(table24);
				
				PdfPTable table21 = new PdfPTable(1);
				table21.setWidths(new float[] { 10f });
				//table21.setSpacingBefore(10);

				PdfPCell hcell191;
				hcell191 = new PdfPCell(new Phrase("Goods Due Receipt Note", headFont1));
				hcell191.setBorder(Rectangle.NO_BORDER);
				hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
				table21.addCell(hcell191);

				/*cell19.setFixedHeight(20f);
				cell19.setColspan(2);*/
				cell1.addElement(table21);
				//table.addCell(cell19);

				
				PdfPTable table241 = new PdfPTable(1);
				table241.setWidths(new float[] { 10f });
				//table241.setSpacingBefore(10);

				PdfPCell hcell101;
				hcell101 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
				hcell101.setBorder(Rectangle.NO_BORDER);
				hcell101.setPaddingBottom(10f);
				hcell101.setHorizontalAlignment(Element.ALIGN_CENTER);
				table241.addCell(hcell101);
				table241.setWidthPercentage(100f);
				cell1.addElement(table241);

				//PdfPCell cell3 = new PdfPCell();

				PdfPTable table2 = new PdfPTable(2);
				table2.setWidths(new float[] { 5f, 4f });
				table2.setSpacingBefore(10);

				PdfPCell hcell1;
				hcell1 = new PdfPCell(new Phrase("GRN#           :  " + medicineProcurement.getProcurementId(), redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(-70f);
				table2.addCell(hcell1);

				hcell1 = new PdfPCell(new Phrase("Challan/Invoice#     :  " + invoiceNo, redFont));
				hcell1.setBorder(Rectangle.NO_BORDER);
				hcell1.setPaddingLeft(70f);
				table2.addCell(hcell1);

				PdfPCell hcell4;
				hcell4 = new PdfPCell(new Phrase("GRN Dt		      :  " + today, redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(-70f);
				table2.addCell(hcell4);

				hcell4 = new PdfPCell(new Phrase("Challan/Invoice Dt  :  " + today, redFont));
				hcell4.setBorder(Rectangle.NO_BORDER);
				hcell4.setPaddingLeft(70f);
				table2.addCell(hcell4);

				PdfPCell hcell15;
				hcell15 = new PdfPCell(new Phrase("Created By   :  " + createdBy, redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(-70f);
				table2.addCell(hcell15);

				hcell15 = new PdfPCell(new Phrase("Created Dt         		    :  " + today, redFont));
				hcell15.setBorder(Rectangle.NO_BORDER);
				hcell15.setPaddingLeft(70f);
				table2.addCell(hcell15);

				PdfPCell hcell16;
				hcell16 = new PdfPCell(new Phrase("Supllier         :  " + medicineProcurement.getVendorName(), redFont));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(-70f);
				table2.addCell(hcell16);

				hcell16 = new PdfPCell(new Phrase("Paymode         		       :  " + "CREDIT", redFont));
				hcell16.setBorder(Rectangle.NO_BORDER);
				hcell16.setPaddingLeft(70f);
				table2.addCell(hcell16);

				PdfPCell hcell17;
				hcell17 = new PdfPCell(new Phrase("Status              :" + medicineProcurement.getStatus(), redFont));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell17.setPaddingLeft(-70f);
				table2.addCell(hcell17);

				hcell17 = new PdfPCell(new Phrase("", redFont));
				hcell17.setBorder(Rectangle.NO_BORDER);
				hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table2.addCell(hcell17);

				cell1.addElement(table2);
				//cell3.setColspan(2);
				//cell3.setFixedHeight(80f);
				//table.addCell(cell3);

				
				PdfPTable table242 = new PdfPTable(1);
				table242.setWidths(new float[] { 10f });
				//table241.setSpacingBefore(10);

				PdfPCell hcell1021;
				hcell1021 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
				hcell1021.setBorder(Rectangle.NO_BORDER);
				hcell1021.setPaddingBottom(10f);
				hcell1021.setHorizontalAlignment(Element.ALIGN_CENTER);
				table242.addCell(hcell1021);
				table242.setWidthPercentage(100f);
				cell1.addElement(table242);
				
				
				//PdfPCell cell31 = new PdfPCell();
				PdfPTable table5 = new PdfPTable(16);
				table5.setWidths(new float[] { 1.4f,6f, 3f, 2.5f, 1.5f, 1.9f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f });

				//table5.setSpacingBefore(10);

				PdfPCell hcell01;
				hcell01 = new PdfPCell(new Phrase("S.No", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table5.addCell(hcell01);

			
				hcell01 = new PdfPCell(new Phrase("Item Name", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Batch#", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell01.setPaddingLeft(8f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Exp Dt", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(5f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Qty", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell01.setPaddingLeft(13f);
				table5.addCell(hcell01);
				
				hcell01 = new PdfPCell(new Phrase("Pack", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell01.setPaddingLeft(16f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Bonus", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell01.setPaddingRight(16f);
				table5.addCell(hcell01);


				hcell01 = new PdfPCell(new Phrase("Sale Rate", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				hcell01.setPaddingLeft(-3f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Purc Rate", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell01.setPaddingRight(-6f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Disc%", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell01.setPaddingLeft(12f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Disc.Val", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT); 
				hcell01.setPaddingLeft(14f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("CGST", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(-1f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("SGST", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
				hcell01.setPaddingLeft(-1f);
				table5.addCell(hcell01);

				hcell01 = new PdfPCell(new Phrase("Purc Value", headFont));
				hcell01.setBorder(Rectangle.NO_BORDER);
				// hcell01.setBackgroundColor(BaseColor.GRAY);
				hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell01.setPaddingLeft(-10f);
				table5.addCell(hcell01);

				// ******************************
				// PdfPCell cell31 = new PdfPCell();
				PdfPTable table1 = new PdfPTable(16);
				table1.setWidths(new float[] { 1.4f, 6f, 3f, 2.5f, 1.5f, 1.9f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f});

				table1.setSpacingBefore(10);

				PdfPCell hcell;
				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

			
				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);
				
				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

			/*	hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);*/

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("%", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Amt", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("%", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				// hcell.setBackgroundColor(BaseColor.BLACK);
				hcell.setPaddingTop(-10f);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("Amt", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				// hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				hcell = new PdfPCell(new Phrase("", redFont));
				hcell.setBorder(Rectangle.NO_BORDER);
				// hcell.setBackgroundColor(BaseColor.GRAY);
				hcell.setPaddingTop(-10f);
				hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(hcell);

				// *****************************

				// PdfPCell cell3 = new PdfPCell();
				PdfPTable table6 = new PdfPTable(1);
				table6.setWidths(new float[] { 8f });

				table6.setSpacingBefore(10);

				PdfPCell hcell02;
				hcell02 = new PdfPCell(new Phrase("______________________________", redFont));
				hcell02.setBorder(Rectangle.NO_BORDER);
				// hcell02.setBackgroundColor(BaseColor.WHITE);
				hcell02.setPaddingTop(-15f);
				hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
				hcell02.setPaddingRight(-15f);
				table6.addCell(hcell02);

	//**************************************
				PdfPTable table11 = new PdfPTable(16);
				table11.setWidths(new float[] { 1.4f, 6f, 3f, 2.5f, 2f, 1.7f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f});

				table11.setSpacingBefore(10);

				
				int i = 0;
				int j = 0;
				int count = 0;
				

				double totalPurchaseAmt = 0;
				double totalDiscount = 0;
				String expdate=null;

				for (MedicineProcurement medicine : medicineProcurementInfo) {

				
					
					PdfPCell cell;

					MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(medicine.getItemName());

					cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					

					cell = new PdfPCell(new Phrase(medicine.getItemName(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(medicine.getBatch(), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(3);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table11.addCell(cell);

					
					try
					{
						expdate=medicine.getExpDate().toString().substring(0, 10);
					SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
					expdate=toFormat.format(fromFormat.parse(expdate));
					
					}
					catch(Exception e)
					{
					e.printStackTrace();
					}
					
					cell = new PdfPCell(new Phrase(expdate, redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(3);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					/*cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((medicine.getQuantity())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(10);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				/*	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
					table11.addCell(cell);
					
					cell = new PdfPCell(new Phrase(String.valueOf(medicine.getPackSize()), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(10f);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				/*	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
					table11.addCell(cell);
					if(medicine.getFreeSample()!=null)
					{
					cell = new PdfPCell(new Phrase(String.valueOf((medicine.getFreeSample())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);
					}
					else
					{
						cell = new PdfPCell(new Phrase("0", redFont));
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingLeft(5);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table11.addCell(cell);
					}
					cell = new PdfPCell(new Phrase(String.valueOf((medicine.getMrp())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf((medicine.getCostPrice())), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(discountInfo.get(i)),
					 redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					float totalAmt = medicine.getQuantity() * medicine.getCostPrice();
					float discAmt = (totalAmt * discountInfo.get(i)) / 100;

					float netAmount = totalAmt - discAmt;

					cell = new PdfPCell(new Phrase(String.valueOf((discAmt)), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					// Gst Calculation

					float cgst = gstInfo.get(j) / 2;
					float sgst = gstInfo.get(j) / 2;

					float gstAmt = (netAmount * gstInfo.get(j)) / 100;

					float cgstAmt = gstAmt / 2;
					float sgstAmt = gstAmt / 2;
					netAmount+=gstAmt;
					cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cgst*100.0)/100.0), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cgstAmt*100.0)/100.0), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(Math.round(sgst*100.0)/100.0), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					cell = new PdfPCell(new Phrase(String.valueOf(Math.round(sgstAmt*100.0)/100.0), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);

					// purchaseValue Calculatuon

					float purchaseAmt = netAmount ;
					cell = new PdfPCell(new Phrase(String.valueOf(Math.round(purchaseAmt*100.0)/100.0), redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table11.addCell(cell);

					i++;
					j++;

					totalPurchaseAmt = totalPurchaseAmt + purchaseAmt;
					totalDiscount = totalDiscount + discAmt;

				}

				cell1.setColspan(2);
				table11.setWidthPercentage(100f);
				table1.setWidthPercentage(100f);
				table5.setWidthPercentage(100f);
				//table6.setWidthPercentage(100f);
				cell1.addElement(table5);
				 PdfPCell cellU = new PdfPCell(); Paragraph p = new
						  Paragraph("_________________________________________________________");
						  cellU.addElement(p);
						  
						  table1.addCell(cellU);
						 
			    cell1.addElement(table6);
				cell1.addElement(table1);
				
				
				
				 
				
				cell1.addElement(table11);
				

				table.addCell(cell1);
				// new code for updated row

				PdfPCell cell5 = new PdfPCell();

				PdfPTable table35 = new PdfPTable(4);
				table35.setWidths(new float[] { 7f, 7f, 7f, 7f });
				table35.setSpacingBefore(10);
				table35.setWidthPercentage(100);

				PdfPCell hcell12;
				hcell12 = new PdfPCell(new Phrase("", headFont1));
				hcell12.setBorder(Rectangle.NO_BORDER);
				table35.addCell(hcell12);

				hcell12 = new PdfPCell(new Phrase("", redFont));
				hcell12.setBorder(Rectangle.NO_BORDER);
				// hcell12.setPaddingLeft(-110f);
				// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell12);

				PdfPCell hcell13;
				hcell13 = new PdfPCell(new Phrase("Total Pur Amount     :", redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				// hcell13.setPaddingLeft(120f);
				hcell13.setPaddingRight(-100f);
				hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell13);

				hcell13 = new PdfPCell(new Phrase("" + Math.round(totalPurchaseAmt*100.0)/100.0, redFont));
				hcell13.setBorder(Rectangle.NO_BORDER);
				hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell13);

				PdfPCell hcell11;
				hcell11 = new PdfPCell(new Phrase("", headFont1));
				hcell11.setBorder(Rectangle.NO_BORDER);
				table35.addCell(hcell11);

				hcell11 = new PdfPCell(new Phrase("", redFont));
				hcell11.setBorder(Rectangle.NO_BORDER);
				table35.addCell(hcell11);

				PdfPCell hcell10;
				hcell10 = new PdfPCell(new Phrase("Discount     :", redFont));
				hcell10.setBorder(Rectangle.NO_BORDER);
				// hcell13.setPaddingLeft(120f);
				hcell10.setPaddingRight(-100f);
				hcell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell10);

				hcell10 = new PdfPCell(new Phrase("" + Math.round(totalDiscount*100.0)/100.0, redFont));
				hcell10.setBorder(Rectangle.NO_BORDER);
				hcell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell10);

				PdfPCell hcell9;
				hcell9 = new PdfPCell(new Phrase("", headFont1));
				hcell9.setBorder(Rectangle.NO_BORDER);
				table35.addCell(hcell9);

				hcell9 = new PdfPCell(new Phrase("", redFont));
				hcell9.setBorder(Rectangle.NO_BORDER);
				// hcell9.setPaddingLeft(-110f);
				// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell9);

/*					// calculation for Round Off
				BigDecimal bd = new BigDecimal(totalPurchaseAmt - Math.floor(totalPurchaseAmt));
				bd = bd.setScale(2, RoundingMode.HALF_DOWN);
				float roundOff = bd.floatValue();
				System.out.println("Round Off Data" + roundOff);
*/
				PdfPCell hcell8;

				hcell8 = new PdfPCell(new Phrase("Rounded Off To    :" + "", redFont));
				hcell8.setBorder(Rectangle.NO_BORDER);
				// hcell13.setPaddingLeft(120f);
				hcell8.setPaddingRight(-100f);
				hcell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell8);

				hcell8 = new PdfPCell(new Phrase(String.valueOf(Math.round(totalPurchaseAmt)), redFont));

/*					float roundOffVal=1f-roundOff;
				System.out.println("round value+:" +roundOffVal);
				if (roundOff <= 0.50) {
					hcell8 = new PdfPCell(new Phrase("-" + Math.round(roundOff*100.0)/100.0, redFont));
				} else {
					hcell8 = new PdfPCell(new Phrase("+" + Math.round(roundOffVal*100.0)/100.0, redFont));
				}
*/					hcell8.setBorder(Rectangle.NO_BORDER);
				hcell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell8);

				PdfPCell hcell7;
				hcell7 = new PdfPCell(new Phrase("", headFont1));
				hcell7.setBorder(Rectangle.NO_BORDER);
				table35.addCell(hcell7);

				hcell7 = new PdfPCell(new Phrase("", redFont));
				hcell7.setBorder(Rectangle.NO_BORDER);
				// hcell7.setPaddingLeft(-110f);
				// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell7);

				PdfPCell hcell6;
				hcell6 = new PdfPCell(new Phrase("Net Amount     :", redFont));

				hcell6.setBorder(Rectangle.NO_BORDER);
				// hcell13.setPaddingLeft(120f);
				hcell6.setPaddingRight(-100f);
				hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell6);

				
				/*if (roundOff <= 0.50) {
					double netAmt =   (double)(totalPurchaseAmt - roundOff);*/
					hcell6 = new PdfPCell(new Phrase("" + Math.round(totalPurchaseAmt), redFont));
				/*} else {
					double netAmt = (double) (totalPurchaseAmt + roundOffVal);
					hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
				}*/
					
				// hcell6 = new PdfPCell(new Phrase(""+totalPurchaseAmt, redFont));
				/*if (roundOff <= 0.50) {
					double netAmt =   (double)(totalPurchaseAmt - roundOff);
					hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
				} else {
					double netAmt = (double) (totalPurchaseAmt + roundOff);
					hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
				}*/
				hcell6.setBorder(Rectangle.NO_BORDER);
				hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table35.addCell(hcell6);

				cell5.setFixedHeight(105f);
				cell5.setColspan(2);
				cell5.addElement(table35);
				table.addCell(cell5);

				// end of updated code

				// PdfPCell cell4 = new PdfPCell();

				PdfPTable table3 = new PdfPTable(2);
				table3.setWidths(new float[] { 5f, 4f });
				table3.setSpacingBefore(10);

				PdfPCell hcell111;
				hcell111 = new PdfPCell(new Phrase("Printed By          :  " + createdBy, redFont));
				hcell111.setBorder(Rectangle.NO_BORDER);
				hcell111.setPaddingLeft(-90f);
				hcell111.setPaddingTop(-5f);
				table3.addCell(hcell111);

				hcell111 = new PdfPCell(new Phrase("Printed Date    :  " + today, redFont));
				hcell111.setBorder(Rectangle.NO_BORDER);
				// hcell11.setPaddingLeft(110f);
				hcell111.setPaddingRight(-70f);
				hcell111.setPaddingTop(-5f);
				hcell111.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table3.addCell(hcell111);

				document.add(table);
				document.add(table3);
				document.close();

				System.out.println("finished");
				pdfByte = byteArrayOutputStream.toByteArray();
				String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/sales/viewFile/")
						.path(salesPaymentPdfServiceImpl.getNextId()).toUriString();

				salesPaymentPdf = new SalesPaymentPdf();
				salesPaymentPdf.setFileName(procureId+" Procurement Due Sales");
				salesPaymentPdf.setFileuri(uri);
				salesPaymentPdf.setPid(salesPaymentPdfServiceImpl.getNextId());
				salesPaymentPdf.setData(pdfByte);
				// System.out.println(discount);
				salesPaymentPdfServiceImpl.save(salesPaymentPdf);

			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}

				return salesPaymentPdf;
		}
		return null;

		
	}
	
	public List<MedicineProcurement> findAll()
	{
		return medicineProcurementRepository.findAll();
	}
	
	public MedicineProcurement findByMasterProcurementId(String id)
	{
		return medicineProcurementRepository.findByMasterProcurementId(id);
	}
	

	public List<MedicineProcurement> findByProcurementId(String id,Principal principal)
	{
		
		User user=userServiceImpl.findByUserName(principal.getName());
		List<MedicineProcurement> medicineProcurementList=medicineProcurementRepository.findByProcurementId(id);
		
		SimpleDateFormat from=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat to=new SimpleDateFormat("dd-MM-yyyy");
		String expDate="";
		
		for(MedicineProcurement medicineProcurementInfo:medicineProcurementList)
		{
			expDate=medicineProcurementInfo.getExpDate();
			try {
				expDate=to.format(from.parse(expDate));
			} catch (Exception e) {
				e.printStackTrace();
			}
			medicineProcurementInfo.setExpDate(expDate);
			medicineProcurementInfo.setVendorName(medicineProcurementInfo.getMedicineProcurmentVendors().getVendorName());
			medicineProcurementInfo.setMedName(medicineProcurementInfo.getMedicineProcurmentMedicineDetails().getName());
			medicineProcurementInfo.setLocation(medicineProcurementInfo.getMedicineProcurmentLocation().getLocationName());
			medicineProcurementInfo.setRole(user.getRole());
		}
		return medicineProcurementList;
	}
	
	public 	List<MedicineProcurement> findByMedicineProcurmentMedicineDetails(MedicineDetails id)
	{
		return medicineProcurementRepository.findByMedicineProcurmentMedicineDetails(id);
	}
	
	/*
	 * Page Loading	
	 */
	@Transactional
	public List<Object> getProcurementIds()
	{
		List<Object> displayList = new ArrayList<>();
		refProcurementIds.setProcurementId(medicineProcurementServiceImpl.getNextMasterProcurementId());
		displayList.add(refProcurementIds);
		displayList.add(vendorsServiceImpl.findAll());
		displayList.add(medicineDetailsServiceImpl.findAll());
		return displayList;
	}
	
	/*
	 * get All procurement
	 */
	public List<Object> getAllProcurement()
	{
		List<Object> displayInfo=new ArrayList<>();
		List<MedicineProcurement> medicineProcurement=medicineProcurementServiceImpl.findAll();
		for(MedicineProcurement medicineProcurementInfo:medicineProcurement)
		{
		
			Vendors vendors=medicineProcurementInfo.getMedicineProcurmentVendors();
			Location location=medicineProcurementInfo.getMedicineProcurmentLocation();
			medicineProcurementInfo.setLocation(location.getLocationName());
			medicineProcurementInfo.setVendorName(vendors.getVendorName());	
		}
		displayInfo.add(medicineProcurement);
		displayInfo.add(vendorsServiceImpl.findAll());
		displayInfo.add(medicineDetailsServiceImpl.findAll());
		return displayInfo; 
	}
	
	/*
	 * Approve procurement
	 */
	@CacheEvict(value="procurementCache",allEntries = true)
	public SalesPaymentPdf approve(String procId,Principal principal)
	{
		
		List<MedicineProcurement> medicineProcurementsList=medicineProcurementRepository.findByProcurementId(procId);
		for(MedicineProcurement m:medicineProcurementsList)
		{
			m.setStatus("Approved");
			MedicineProcurement medicineProcurementInfo=medicineProcurementServiceImpl.findByMasterProcurementId(m.getMasterProcurementId());
			m.setMedicineProcurmentLocation(medicineProcurementInfo.getMedicineProcurmentLocation());
			m.setMedicineProcurmentMedicineDetails(medicineProcurementInfo.getMedicineProcurmentMedicineDetails());
			m.setMedicineProcurmentVendors(medicineProcurementInfo.getMedicineProcurmentVendors());
			medicineProcurementRepository.save(m);
		}
		
		SalesPaymentPdf salesPaymentPdf=salesPaymentPdfRepository.getProcurementPdf(procId+" Procurement");
		Map<String,String> info=new HashMap<>();
		info.put("fileUri", salesPaymentPdf.getFileuri());
		
		//CreatedBy(Security)
		User userSecurity=userServiceImpl.findByUserName(principal.getName());
		String createdBy=userSecurity.getFirstName()+" "+userSecurity.getLastName();
		
		/*
		 * String address=
		 * "                                                                                                    Plot No.14,15,16 &17,Nandi Co-op. Society,     \n                                                                                                                  Main Road,Beside Navya Grand Hotel,Miyapur,Hyderabad,TS           "
		 * +
		 * "                                                                                        \n                                                                                                                                                 Phone:040-23046789   "
		 * +
		 * "\n                                                                                                                                        Email : udbhavahospital@gmail.com"
		 * ;
		 * 
		 */		
		SalesPaymentPdf salesPaymentPdfInfo=null;
		// for Generating MedicineProcurement Pdf
		byte[] pdfByte = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		List<MedicineProcurement> medicineProcurementInfo = medicineProcurementsList;
		String invoiceNo=medicineProcurementInfo.get(0).getInvoiceNo();
		String vendorName=medicineProcurementInfo.get(0).getMedicineProcurmentVendors().getVendorName();
		String status=medicineProcurementInfo.get(0).getStatus();
		String procureId=medicineProcurementInfo.get(0).getProcurementId();
		try {

			Document document = new Document(PageSize.A4.rotate());

			Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

			Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();

			// Display a date in day, month, year format
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date).toString();

			PdfPTable table = new PdfPTable(2);

			table.setWidthPercentage(105f);

			Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);

		Image img = Image.getInstance(hospitalLogo.getURL());

			img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
			table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

			Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));


			pq.add(new Chunk(ConstantValues.GRN_ADRESS,redFont));
			PdfPCell cellp = new PdfPCell(pq);
			PdfPCell cell1 = new PdfPCell();

			// for header Bold
			PdfPTable table96 = new PdfPTable(1);
			table96.setWidths(new float[] { 5f });
			table96.setSpacingBefore(10);

			PdfPCell hcell96;
			hcell96 = new PdfPCell(new Phrase(ConstantValues.PHARMACY_NAME, headFont1));
			hcell96.setBorder(Rectangle.NO_BORDER);
			hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
			// hcell96.setPaddingLeft(50f);

			table96.addCell(hcell96);
			cell1.addElement(table96);

			PdfPTable table961 = new PdfPTable(1);
			table961.setWidths(new float[] { 5f });
			table961.setSpacingBefore(10);

			
			PdfPCell hcell71;
			hcell71 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont1));
			hcell71.setBorder(Rectangle.NO_BORDER);
			hcell71.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell71.setPaddingLeft(25f);

			table961.addCell(hcell71);
			cell1.addElement(table961);
			
			// for header end
			//cell1.setFixedHeight(107f);
			cell1.addElement(pq);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setColspan(2);
			//table.addCell(cell1);

			//PdfPCell cell19 = new PdfPCell();

			PdfPTable table24 = new PdfPTable(1);
			table24.setWidths(new float[] { 4f });
			//table24.setSpacingBefore(10);

			PdfPCell hcell19;
			hcell19 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell19.setBorder(Rectangle.NO_BORDER);
			hcell19.setHorizontalAlignment(Element.ALIGN_CENTER);
			//hcell19.setPaddingTop(30f);
			table24.addCell(hcell19);
			table24.setWidthPercentage(100f);
			cell1.addElement(table24);
			
			PdfPTable table21 = new PdfPTable(1);
			table21.setWidths(new float[] { 10f });
			//table21.setSpacingBefore(10);

			PdfPCell hcell191;
			hcell191 = new PdfPCell(new Phrase("Goods Receipt Note", headFont1));
			hcell191.setBorder(Rectangle.NO_BORDER);
			hcell191.setHorizontalAlignment(Element.ALIGN_CENTER);
			table21.addCell(hcell191);

			/*cell19.setFixedHeight(20f);
			cell19.setColspan(2);*/
			cell1.addElement(table21);
			//table.addCell(cell19);

			
			PdfPTable table241 = new PdfPTable(1);
			table241.setWidths(new float[] { 10f });
			//table241.setSpacingBefore(10);

			PdfPCell hcell101;
			hcell101 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell101.setBorder(Rectangle.NO_BORDER);
			hcell101.setPaddingBottom(10f);
			hcell101.setHorizontalAlignment(Element.ALIGN_CENTER);
			table241.addCell(hcell101);
			table241.setWidthPercentage(100f);
			cell1.addElement(table241);

			//PdfPCell cell3 = new PdfPCell();

			PdfPTable table2 = new PdfPTable(2);
			table2.setWidths(new float[] { 5f, 4f });
			table2.setSpacingBefore(10);

			PdfPCell hcell1;
			hcell1 = new PdfPCell(new Phrase("GRN#           :  " + procId, redFont));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setPaddingLeft(-70f);
			table2.addCell(hcell1);

			hcell1 = new PdfPCell(new Phrase("Challan/Invoice#     :  " + invoiceNo, redFont));
			hcell1.setBorder(Rectangle.NO_BORDER);
			hcell1.setPaddingLeft(70f);
			table2.addCell(hcell1);

			PdfPCell hcell4;
			hcell4 = new PdfPCell(new Phrase("GRN Dt		      :  " + today, redFont));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(-70f);
			table2.addCell(hcell4);

			hcell4 = new PdfPCell(new Phrase("Challan/Invoice Dt  :  " + today, redFont));
			hcell4.setBorder(Rectangle.NO_BORDER);
			hcell4.setPaddingLeft(70f);
			table2.addCell(hcell4);

			PdfPCell hcell15;
			hcell15 = new PdfPCell(new Phrase("Created By   :  " + createdBy, redFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-70f);
			table2.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase("Created Dt         		    :  " + today, redFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(70f);
			table2.addCell(hcell15);

			PdfPCell hcell16;
			hcell16 = new PdfPCell(new Phrase("Supllier         :  " + vendorName, redFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-70f);
			table2.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase("Paymode         		       :  " + "CREDIT", redFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(70f);
			table2.addCell(hcell16);

			PdfPCell hcell17;
			hcell17 = new PdfPCell(new Phrase("Status              :" + status, redFont));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell17.setPaddingLeft(-70f);
			table2.addCell(hcell17);

			hcell17 = new PdfPCell(new Phrase("", redFont));
			hcell17.setBorder(Rectangle.NO_BORDER);
			hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table2.addCell(hcell17);

			cell1.addElement(table2);
			//cell3.setColspan(2);
			//cell3.setFixedHeight(80f);
			//table.addCell(cell3);

			
			PdfPTable table242 = new PdfPTable(1);
			table242.setWidths(new float[] { 10f });
			//table241.setSpacingBefore(10);

			PdfPCell hcell1021;
			hcell1021 = new PdfPCell(new Phrase("_____________________________________________________________________________________________________________________________________", headFont1));
			hcell1021.setBorder(Rectangle.NO_BORDER);
			hcell1021.setPaddingBottom(10f);
			hcell1021.setHorizontalAlignment(Element.ALIGN_CENTER);
			table242.addCell(hcell1021);
			table242.setWidthPercentage(100f);
			cell1.addElement(table242);
			
			
			//PdfPCell cell31 = new PdfPCell();
			PdfPTable table5 = new PdfPTable(16);
			table5.setWidths(new float[] { 1.4f,6f, 3f, 2.5f, 1.5f, 1.9f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f });

			//table5.setSpacingBefore(10);

			PdfPCell hcell01;
			hcell01 = new PdfPCell(new Phrase("S.No", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			table5.addCell(hcell01);

		
			hcell01 = new PdfPCell(new Phrase("Item Name", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Batch#", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(8f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Exp Dt", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(5f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Qty", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(13f);
			table5.addCell(hcell01);
			
			hcell01 = new PdfPCell(new Phrase("Pack", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(16f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Bonus", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingRight(16f);
			table5.addCell(hcell01);


			hcell01 = new PdfPCell(new Phrase("Sale Rate", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingLeft(-3f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Purc Rate", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingRight(-6f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Disc%", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingLeft(12f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Disc.Val", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT); 
			hcell01.setPaddingLeft(14f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("CGST", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-1f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_CENTER);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("SGST", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-1f);
			table5.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Purc Value", headFont));
			hcell01.setBorder(Rectangle.NO_BORDER);
			// hcell01.setBackgroundColor(BaseColor.GRAY);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell01.setPaddingLeft(-10f);
			table5.addCell(hcell01);

			// ******************************
			// PdfPCell cell31 = new PdfPCell();
			PdfPTable table1 = new PdfPTable(16);
			table1.setWidths(new float[] { 1.4f, 6f, 3f, 2.5f, 1.5f, 1.9f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f});

			table1.setSpacingBefore(10);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

		
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

		/*	hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);*/

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("%", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Amt", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("%", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.BLACK);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Amt", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", redFont));
			hcell.setBorder(Rectangle.NO_BORDER);
			// hcell.setBackgroundColor(BaseColor.GRAY);
			hcell.setPaddingTop(-10f);
			hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.addCell(hcell);

			// *****************************

			// PdfPCell cell3 = new PdfPCell();
			PdfPTable table6 = new PdfPTable(1);
			table6.setWidths(new float[] { 8f });

			table6.setSpacingBefore(10);

			PdfPCell hcell02;
			hcell02 = new PdfPCell(new Phrase("______________________________", redFont));
			hcell02.setBorder(Rectangle.NO_BORDER);
			// hcell02.setBackgroundColor(BaseColor.WHITE);
			hcell02.setPaddingTop(-15f);
			hcell02.setHorizontalAlignment(Element.ALIGN_RIGHT);
			hcell02.setPaddingRight(-15f);
			table6.addCell(hcell02);

//**************************************
			PdfPTable table11 = new PdfPTable(16);
			table11.setWidths(new float[] { 1.4f, 6f, 3f, 2.5f, 2f, 1.7f,2.5f, 1.5f, 1.5f, 2f, 2.6f, 2f, 2f, 2f, 2f, 2.8f});

			table11.setSpacingBefore(10);

			
			int i = 0;
			int j = 0;
			int count = 0;
			

			double totalPurchaseAmt = 0;
			double totalDiscount = 0;
			String expdate=null;

			for (MedicineProcurement medicine : medicineProcurementInfo) {

			
				
				PdfPCell cell;

				MedicineDetails medicineDetails1 = medicineDetailsServiceImpl.findByName(medicine.getItemName());

				cell = new PdfPCell(new Phrase(String.valueOf(count = count + 1), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				

				cell = new PdfPCell(new Phrase(medicine.getItemName(), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(medicine.getBatch(), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(3);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				// cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table11.addCell(cell);

				
				try
				{
					expdate=medicine.getExpDate().toString().substring(0, 10);
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
				expdate=toFormat.format(fromFormat.parse(expdate));
				
				}
				catch(Exception e)
				{
				e.printStackTrace();
				}
				
				cell = new PdfPCell(new Phrase(expdate, redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(3);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				/*cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getQuantity())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(10);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			/*	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);
				
				cell = new PdfPCell(new Phrase(String.valueOf(medicine.getPackSize()), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(10f);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			/*	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
				table11.addCell(cell);
				if(medicine.getFreeSample()!=null)
				{
				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getFreeSample())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);
				}
				else
				{
					cell = new PdfPCell(new Phrase("0", redFont));
					cell.setBorder(Rectangle.NO_BORDER);
					cell.setPaddingLeft(5);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table11.addCell(cell);
				}
				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getMrp())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf((medicine.getCostPrice())), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(medicine.getDiscount()),
				 redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				float totalAmt = medicine.getQuantity() * medicine.getCostPrice();
				float discAmt = (totalAmt * medicine.getDiscount()) / 100;

				float netAmount = totalAmt - discAmt;

				cell = new PdfPCell(new Phrase(String.valueOf((discAmt)), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				// Gst Calculation

				float cgst = medicine.getGst() / 2;
				float sgst = medicine.getGst() / 2;

				float gstAmt = (netAmount * medicine.getGst()) / 100;

				float cgstAmt = gstAmt / 2;
				float sgstAmt = gstAmt / 2;
				netAmount+=gstAmt;
				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cgst*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(cgstAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(sgst*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(sgstAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table11.addCell(cell);

				// purchaseValue Calculatuon

				float purchaseAmt = netAmount ;
				cell = new PdfPCell(new Phrase(String.valueOf(Math.round(purchaseAmt*100.0)/100.0), redFont));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table11.addCell(cell);

				i++;
				j++;

				totalPurchaseAmt = totalPurchaseAmt + purchaseAmt;
				totalDiscount = totalDiscount + discAmt;

			}

			cell1.setColspan(2);
			table11.setWidthPercentage(100f);
			table1.setWidthPercentage(100f);
			table5.setWidthPercentage(100f);
			//table6.setWidthPercentage(100f);
			cell1.addElement(table5);
			 PdfPCell cellU = new PdfPCell(); Paragraph p = new
					  Paragraph("_________________________________________________________");
					  cellU.addElement(p);
					  
					  table1.addCell(cellU);
					 
		    cell1.addElement(table6);
			cell1.addElement(table1);
			
			
			
			 
			
			cell1.addElement(table11);
			

			table.addCell(cell1);
			// new code for updated row

			PdfPCell cell5 = new PdfPCell();

			PdfPTable table35 = new PdfPTable(4);
			table35.setWidths(new float[] { 7f, 7f, 7f, 7f });
			table35.setSpacingBefore(10);
			table35.setWidthPercentage(100);

			PdfPCell hcell12;
			hcell12 = new PdfPCell(new Phrase("", headFont1));
			hcell12.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell12);

			hcell12 = new PdfPCell(new Phrase("", redFont));
			hcell12.setBorder(Rectangle.NO_BORDER);
			// hcell12.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell12);

			PdfPCell hcell13;
			hcell13 = new PdfPCell(new Phrase("Total Pur Amount     :", redFont));
			hcell13.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell13.setPaddingRight(-100f);
			hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell13);

			hcell13 = new PdfPCell(new Phrase("" + Math.round(totalPurchaseAmt*100.0)/100.0, redFont));
			hcell13.setBorder(Rectangle.NO_BORDER);
			hcell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell13);

			PdfPCell hcell11;
			hcell11 = new PdfPCell(new Phrase("", headFont1));
			hcell11.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell11);

			hcell11 = new PdfPCell(new Phrase("", redFont));
			hcell11.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell11);

			PdfPCell hcell10;
			hcell10 = new PdfPCell(new Phrase("Discount     :", redFont));
			hcell10.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell10.setPaddingRight(-100f);
			hcell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell10);

			hcell10 = new PdfPCell(new Phrase("" + Math.round(totalDiscount*100.0)/100.0, redFont));
			hcell10.setBorder(Rectangle.NO_BORDER);
			hcell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell10);

			PdfPCell hcell9;
			hcell9 = new PdfPCell(new Phrase("", headFont1));
			hcell9.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell9);

			hcell9 = new PdfPCell(new Phrase("", redFont));
			hcell9.setBorder(Rectangle.NO_BORDER);
			// hcell9.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell9);

			// calculation for Round Off
			BigDecimal bd = new BigDecimal(totalPurchaseAmt - Math.floor(totalPurchaseAmt));
			bd = bd.setScale(2, RoundingMode.HALF_DOWN);
			float roundOff = bd.floatValue();
			System.out.println("Round Off Data" + roundOff);

			PdfPCell hcell8;

			hcell8 = new PdfPCell(new Phrase("Round Off     :" + "", redFont));
			hcell8.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell8.setPaddingRight(-100f);
			hcell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell8);

			float roundOffVal=1f-roundOff;
			System.out.println("round value+:" +roundOffVal);
			if (roundOff <= 0.50) {
				hcell8 = new PdfPCell(new Phrase("-" + Math.round(roundOff*100.0)/100.0, redFont));
			} else {
				hcell8 = new PdfPCell(new Phrase("+" + Math.round(roundOffVal*100.0)/100.0, redFont));
			}
			hcell8.setBorder(Rectangle.NO_BORDER);
			hcell8.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell8);

			PdfPCell hcell7;
			hcell7 = new PdfPCell(new Phrase("", headFont1));
			hcell7.setBorder(Rectangle.NO_BORDER);
			table35.addCell(hcell7);

			hcell7 = new PdfPCell(new Phrase("", redFont));
			hcell7.setBorder(Rectangle.NO_BORDER);
			// hcell7.setPaddingLeft(-110f);
			// hcell12.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell7);

			PdfPCell hcell6;
			hcell6 = new PdfPCell(new Phrase("Net Amount     :", redFont));

			hcell6.setBorder(Rectangle.NO_BORDER);
			// hcell13.setPaddingLeft(120f);
			hcell6.setPaddingRight(-100f);
			hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell6);

			
			if (roundOff <= 0.50) {
				double netAmt =   (double)(totalPurchaseAmt - roundOff);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			} else {
				double netAmt = (double) (totalPurchaseAmt + roundOffVal);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			}
			// hcell6 = new PdfPCell(new Phrase(""+totalPurchaseAmt, redFont));
			/*if (roundOff <= 0.50) {
				double netAmt =   (double)(totalPurchaseAmt - roundOff);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			} else {
				double netAmt = (double) (totalPurchaseAmt + roundOff);
				hcell6 = new PdfPCell(new Phrase("" + Math.round(netAmt*100.0)/100.0, redFont));
			}*/
			hcell6.setBorder(Rectangle.NO_BORDER);
			hcell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table35.addCell(hcell6);

			cell5.setFixedHeight(105f);
			cell5.setColspan(2);
			cell5.addElement(table35);
			table.addCell(cell5);

			// end of updated code

			// PdfPCell cell4 = new PdfPCell();

			PdfPTable table3 = new PdfPTable(2);
			table3.setWidths(new float[] { 5f, 4f });
			table3.setSpacingBefore(10);

			PdfPCell hcell111;
			hcell111 = new PdfPCell(new Phrase("Printed By          :  " + createdBy, redFont));
			hcell111.setBorder(Rectangle.NO_BORDER);
			hcell111.setPaddingLeft(-90f);
			hcell111.setPaddingTop(-5f);
			table3.addCell(hcell111);

			hcell111 = new PdfPCell(new Phrase("Printed Date    :  " + today, redFont));
			hcell111.setBorder(Rectangle.NO_BORDER);
			// hcell11.setPaddingLeft(110f);
			hcell111.setPaddingRight(-70f);
			hcell111.setPaddingTop(-5f);
			hcell111.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table3.addCell(hcell111);

			document.add(table);
			document.add(table3);
			document.close();

			System.out.println("finished");
			pdfByte = byteArrayOutputStream.toByteArray();
			 salesPaymentPdfInfo=salesPaymentPdfRepository.getProcurementPdf(procId);
				System.out.println("finished"+salesPaymentPdfInfo==null);
				
				pdfByte = byteArrayOutputStream.toByteArray();
				
				salesPaymentPdfInfo.setFileuri(salesPaymentPdfInfo.getFileuri());
				salesPaymentPdfInfo.setFileName(salesPaymentPdfInfo.getFileName());
				salesPaymentPdfInfo.setPid(salesPaymentPdfInfo.getPid());
				salesPaymentPdfInfo.setData(pdfByte);
				// System.out.println(discount);
				salesPaymentPdfServiceImpl.save(salesPaymentPdfInfo);

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		return salesPaymentPdf;


}

	public MedicineDetails getManufacturer(String medName)
	{
		return medicineDetailsRepository.findByName(medName);
	}
	
	//new code for get Available medicine for sales

	public List<MedicineProcurement> findByBatchAndMedicine(String batch,String medicine)
	{
		return medicineProcurementRepository.findByBatchAndMedicine(batch, medicine);
	}
	
	public List<MedicineProcurement> findByItemName(String name)
	{
		return medicineProcurementRepository.findByItemName(name);
	}
	
	public List<MedicineProcurement> findByItemNameAndStatus(String name,String status)
	{
		return medicineProcurementRepository.findByItemNameAndStatus(name, status);
	}

	@Override
	@Cacheable(value="procurementCache" ,key="#medId")
	public List<MedicineProcurement> findOneApproved(MedicineDetails medId) {
		return medicineProcurementRepository.findOneApproved(medId);
	}
 
	
	public List<Object> getLatestMedicineNames(Map<String, String> mapInfo)
	{

		
		String medName=mapInfo.get("medicineName");
		List<Object> Allllist = new ArrayList<>();
		
		List<MedicineProcurement>latestmed=	medicineProcurementRepository.getLatestMedicineName(medName);
		 for(MedicineProcurement mednames: latestmed) {
			 Map<String, String> pmap = new HashMap<>();
			 pmap.put( "MedicineName",mednames.getItemName() ); 
			 pmap.put( "PackSize",String.valueOf(mednames.getPackSize() ));
			 pmap.put("vendorName",mednames.getMedicineProcurmentVendors().getVendorName());
			 pmap.put( "Qty",String.valueOf(mednames.getQuantity()) );
			 pmap.put( "Mrp",String.valueOf(mednames.getCostPrice()) );
			 pmap.put( "DateOfProcurement",String.valueOf(mednames.getDateOfProcurement().toString().substring(0, 10)) );
			 Allllist.add(pmap);
			 
			 
		 }
		 
		return Allllist;
	
	}

	
}
