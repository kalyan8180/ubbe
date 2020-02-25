package com.example.test.testingHMS.voucher.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.finalBilling.model.FinalBilling;
import com.example.test.testingHMS.finalBilling.serviceImpl.FinalBillingServiceImpl;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;
import com.example.test.testingHMS.utils.ConstantValues;
import com.example.test.testingHMS.voucher.dto.VoucherDto;
import com.example.test.testingHMS.voucher.model.Voucher;
import com.example.test.testingHMS.voucher.model.VoucherPdf;
import com.example.test.testingHMS.voucher.repository.VoucherRepository;
import com.example.test.testingHMS.voucher.service.VoucherService;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class VoucherServiceImpl implements VoucherService{
	
	private static final Logger Logger=LoggerFactory.getLogger(VoucherServiceImpl.class);
	
	@Value("${hospital.logo}")
	private Resource hospitalLogo;
	
	@Autowired
	VoucherRepository voucherRepository;
	
	@Autowired
	VoucherPdfServiceImpl voucherPdfServiceImpl;

	@Autowired
    ResourceLoader resourceLoader;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	VoucherServiceImpl voucherServiceImpl;
	
	@Autowired
	FinalBillingServiceImpl finalBillingServcieImpl;
	
	public String getNextVoucherId()
	{
		//Vendors vendors=vendorsRepository.findFirstByOrderByVendorIdDesc();
		Voucher voucher = voucherRepository.findFirstByOrderByPaymentNoDesc();
		String nextId=null;
		if(voucher==null)
		{
			nextId="VCP0000001";
		}
		else
		{
			int lastIntId=Integer.parseInt(voucher.getPaymentNo().substring(3));
			lastIntId+=1;
			nextId="VCP"+String.format("%07d",lastIntId);
		}
		return nextId;
	}
	
	public VoucherPdf computeSave(Voucher voucher,Principal principal) 
	{

		// createdBy(Security)
		User user=userServiceImpl.findByUserName(principal.getName());
		String createdBy=user.getFirstName()+" "+user.getLastName();
		
		float finalCash=0; //final billing
		float finalCard=0; //final billing
		float finalCheque=0; //final billing
		float finalDue=0; //final billing
		float finalNetAmount=0; //final billing
		String paidto=null;
		
		User paidUser=null;
		String paidUserTo=voucher.getPaidTo();
		String paidOther=voucher.getOtherName();
		
		String[] paidTo=null;
		paidTo=paidUserTo.split("-");
		
		if(paidUserTo.equalsIgnoreCase("Others"))
		{
		
			paidUser=userServiceImpl.findOneByUserId(null);
		}
		else 
		{
			paidUser=userServiceImpl.findOneByUserId(paidTo[1]);

			  paidto=(paidUser.getMiddleName()!=null)?(paidUser.getFirstName()+" "+paidUser.getMiddleName()+" "+paidUser.getLastName()):(paidUser.getFirstName()+" "+paidUser.getLastName());
			
		}
		voucher.setPaymentNo(getNextVoucherId());
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
		voucher.setPaymentDate(timestamp);
		voucher.setUserVoucher(user);
		if(paidOther!=null)
		{
		voucher.setOtherName(paidOther);
		}
		else if(paidUser!=null) {
			voucher.setPaidTo(paidUser.getUserId());
		}
		voucher.setPreparedBy(createdBy);
		voucher.setPrintedBy(createdBy);
		voucherRepository.save(voucher);
		

		//Final Billing 
		String paymentType=voucher.getPaymentType();
		
		if(paymentType.equalsIgnoreCase(ConstantValues.CASH)) {
			finalCash=voucher.getVoucherAmount();
		}else if(paymentType.equalsIgnoreCase(ConstantValues.CARD)) {
			finalCard=voucher.getVoucherAmount();
			
		}else if(paymentType.equalsIgnoreCase(ConstantValues.CHEQUE)) {
			finalCheque=voucher.getVoucherAmount();
			
		}
		
		System.out.println(paidOther);
		System.out.println(paidto);
		 
		 FinalBilling finalBilling=new FinalBilling();
		 finalBilling.setDueStatus(ConstantValues.NO);
		 finalBilling.setBillNo(voucher.getPaymentNo());
		 finalBilling.setBillType("voucher");
		 finalBilling.setCardAmount(finalCard);
		 finalBilling.setCashAmount(finalCash);
		 finalBilling.setChequeAmount(finalCheque);
		 finalBilling.setUpdatedBy(user.getUserId());
		 finalBilling.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
		 finalBilling.setDueAmount(finalDue);
		 finalBilling.setInsertedDate(Timestamp.valueOf(LocalDateTime.now()));
		 finalBilling.setFinalAmountPaid(finalCash+finalCard+finalCheque+finalDue);
		 finalBilling.setTotalAmount(finalCash+finalCard+finalCheque+finalDue);
		 finalBilling.setFinalBillUser(user);
		 if(paidOther==null) {
			 finalBilling.setName(paidto);	
		 }else {
			 finalBilling.setName(paidOther);
		 }
		 finalBilling.setMobile(0);
		 finalBilling.setPaymentType(voucher.getPaymentType());
		 finalBilling.setTotalAmount(finalNetAmount);
		finalBillingServcieImpl.computeSave(finalBilling);

		String voucherId=null;
		voucherId=voucher.getPaymentNo();
		
		
		VoucherPdf voucherPdf=null;
		 byte[] pdfBytes=null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Date date = Calendar.getInstance().getTime();

			// Display a date in day, month, year format
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date).toString();

			// for convert db date to dmy format
			Timestamp date1 = voucher.getPaymentDate();
			DateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(date1.getTime());
			String paymentDate = format1.format(cal.getTime());
					
		/*
		 * //for address String addr=
		 * "                                             Plot No 14,15,16&17,Nandi Co-op.Society,Main Road   \n                               "
		 * +
		 * "                                        Beside Navya Grand Total,Miyapur,Hyderabad,TS      \n                                                                                          Phone:040-23046789    "
		 * +
		 * "\n                                                                               Email : udbhavahospitals@gmail.com"
		 * ;
		 * 
		 */		try {


			Document document = new Document(PageSize.A4_LANDSCAPE);

			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);

			
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

			Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
			Image img = Image.getInstance(hospitalLogo.getURL());
			

			document.open();
			PdfPTable table = new PdfPTable(2);
			img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
			table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

			Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));

			pq.add(new Chunk(ConstantValues.VOUCHER_ADRESS,redFont1));
			PdfPTable table97 = new PdfPTable(1);
			table97.setWidths(new float[] { 5f });
			table97.setSpacingBefore(10);

			PdfPCell hcell9;
			hcell9 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont2));
			hcell9.setBorder(Rectangle.NO_BORDER);
			hcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell9.setPaddingLeft(20f);

			PdfPCell cellp = new PdfPCell(pq);
			PdfPCell cell1 = new PdfPCell();
			cell1.setBorder(0);
			
			PdfPTable table96 = new PdfPTable(1);
			table96.setWidths(new float[] { 5f });
			table96.setSpacingBefore(10);

			PdfPCell hcell96;
			hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont2));
			hcell96.setBorder(Rectangle.NO_BORDER);
			hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell96.setPaddingLeft(25f);

			table96.addCell(hcell96);
			cell1.addElement(table96);
			
			
			
			// for header end
			cell1.setFixedHeight(107f);
			cell1.addElement(pq);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setColspan(2);
			table.addCell(cell1);

	       PdfPCell cell09 = new PdfPCell();
	       cell09.setBorder(0);
			

			PdfPTable table03 = new PdfPTable(1);
			table03.setWidths(new float[] { 3f });
			table03.setSpacingBefore(15);

			PdfPCell hcell101;
			hcell101 = new PdfPCell(new Phrase("Voucher Payment ", headFont1));
			hcell101.setBorder(Rectangle.NO_BORDER);
			hcell101.setHorizontalAlignment(Element.ALIGN_CENTER);
			table03.addCell(hcell101);

			cell09.setFixedHeight(30f);
			cell09.setColspan(2);
			//cell19.addElement(table21);
			cell09.addElement(table03);
			table.addCell(cell09);

			
			
			
			PdfPCell cell19 = new PdfPCell();

			

			PdfPTable table3 = new PdfPTable(4);
			table3.setWidths(new float[] { 3f, 3f,3f,3f });
			table3.setSpacingBefore(10);

			PdfPCell hcell11;
			hcell11 = new PdfPCell(new Phrase("Payment No                  :  ", headFont));
			hcell11.setBorder(Rectangle.NO_BORDER);
			hcell11.setPaddingLeft(-50f);
			table3.addCell(hcell11);

			hcell11 = new PdfPCell(new Phrase(voucher.getPaymentNo() , redFont1));
			hcell11.setBorder(Rectangle.NO_BORDER);
			hcell11.setPaddingLeft(-40f);
			hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
			table3.addCell(hcell11);
			
			hcell11 = new PdfPCell(new Phrase("Payment Date              :  " , headFont));
			hcell11.setBorder(Rectangle.NO_BORDER);
			hcell11.setPaddingLeft(-20f);
		//	hcell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell11);
			
			hcell11 = new PdfPCell(new Phrase(paymentDate, redFont1));
			hcell11.setBorder(Rectangle.NO_BORDER);
			
			hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell11.setPaddingLeft(-20f);
			table3.addCell(hcell11);
			
			PdfPCell hcell1111;
			hcell1111 = new PdfPCell(new Phrase("Payment Type              :  ", headFont));
			hcell1111.setBorder(Rectangle.NO_BORDER);
			hcell1111.setPaddingLeft(-50f);
			table3.addCell(hcell1111);

			hcell1111 = new PdfPCell(new Phrase(paymentType, redFont1));
			hcell1111.setBorder(Rectangle.NO_BORDER);
			hcell1111.setPaddingLeft(-40f);
			hcell1111.setHorizontalAlignment(Element.ALIGN_LEFT);
			table3.addCell(hcell1111);
			
			
			hcell1111 = new PdfPCell(new Phrase("Voucher Type              :", headFont));
			hcell1111.setBorder(Rectangle.NO_BORDER);
			hcell1111.setPaddingLeft(-20f);
		//	hcell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell1111);
			
			hcell1111 = new PdfPCell(new Phrase(voucher.getVoucherType(), redFont1));
			hcell1111.setBorder(Rectangle.NO_BORDER);
			
			hcell1111.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1111.setPaddingLeft(-20f);
			table3.addCell(hcell1111);
			
			
			PdfPCell hcell112;
			hcell112 = new PdfPCell(new Phrase("" , headFont));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setPaddingLeft(-70f);
			table3.addCell(hcell112);

			hcell112 = new PdfPCell(new Phrase("", redFont1));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setPaddingRight(-70f);
			hcell112.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell112);
			
			

			hcell112 = new PdfPCell(new Phrase("Paid To                         :  " , headFont));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setPaddingLeft(-20f);
			//hcell112.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell112);
			
			if(paidUserTo.equals("Others"))
			{
			hcell112 = new PdfPCell(new Phrase(paidOther, redFont1));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell112.setPaddingLeft(-20f);
			table3.addCell(hcell112);
			}
			else
			{
			hcell112 = new PdfPCell(new Phrase(paidTo[0], redFont1));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell112.setPaddingLeft(-20f);
			table3.addCell(hcell112);
			}
			PdfPCell hcell14;
			hcell14 = new PdfPCell(new Phrase("Voucher Amount         :  " , headFont));
			hcell14.setBorder(Rectangle.NO_BORDER);
			hcell14.setPaddingLeft(-50f);
			table3.addCell(hcell14);

			hcell14 = new PdfPCell(new Phrase(String.valueOf(voucher.getVoucherAmount()) , redFont1));
			hcell14.setBorder(Rectangle.NO_BORDER);
			hcell14.setPaddingLeft(-40f);
			hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
			table3.addCell(hcell14);

			hcell14 = new PdfPCell(new Phrase("" , redFont1));
			hcell14.setBorder(Rectangle.NO_BORDER);
			hcell14.setPaddingLeft(-20f);
			//hcell14.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell14);

			hcell14 = new PdfPCell(new Phrase(" " , redFont1));
			hcell14.setBorder(Rectangle.NO_BORDER);
			hcell14.setPaddingRight(-70f);
			hcell14.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell14);
			
			
			PdfPTable table5 = new PdfPTable(2);
			table5.setWidths(new float[] { 3f, 9f });
			table5.setSpacingBefore(10);

			
			PdfPCell hcell13;
			hcell13 = new PdfPCell(new Phrase("Remarks                       :  ", headFont));
			hcell13.setBorder(Rectangle.NO_BORDER);
			hcell13.setPaddingLeft(-50f);
			table5.addCell(hcell13);

			hcell13 = new PdfPCell(new Phrase(voucher.getRemarks(), redFont1));
			hcell13.setBorder(Rectangle.NO_BORDER);
			hcell13.setPaddingLeft(-40f);
			hcell13.setHorizontalAlignment(Element.ALIGN_LEFT);
			table5.addCell(hcell13);
		
		
			PdfPTable table6 = new PdfPTable(4);
			table6.setWidths(new float[] { 3f, 3f,3f,3f });
			table6.setSpacingBefore(10);

			
			PdfPCell hcell15;
			hcell15 = new PdfPCell(new Phrase("Prepared By                 :  ", headFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-50f);
			hcell15.setPaddingTop(20f);
			table6.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase(createdBy , redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-40f);
			hcell15.setPaddingTop(20f);
			hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
			table6.addCell(hcell15);
			
			hcell15 = new PdfPCell(new Phrase("Prepared On                 :  " , headFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-20f);
			hcell15.setPaddingTop(20f);
			//hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table6.addCell(hcell15);
		
			
			hcell15 = new PdfPCell(new Phrase(today , redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			//hcell15.setPaddingRight(-70f);
			hcell15.setPaddingTop(20f);
			hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell15.setPaddingLeft(-20f);
			//hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table6.addCell(hcell15);
			
			
			
			PdfPCell hcell16;
			hcell16 = new PdfPCell(new Phrase("Printed By                    :  ", headFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-50f);
			//hcell16.setPaddingTop(20f);
			table6.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase(createdBy , redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-40f);
			//hcell16.setPaddingTop(20f);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			table6.addCell(hcell16);
			
			hcell16 = new PdfPCell(new Phrase("Printed On                    :  " , headFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-20f);
			//hcell16.setPaddingTop(20f);
			//hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table6.addCell(hcell16);
			hcell16 = new PdfPCell(new Phrase(today , redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell16.setPaddingLeft(-20f);
			table6.addCell(hcell16);
			
			cell19.setFixedHeight(180f);
			cell19.setColspan(2);
			cell19.addElement(table3);
			cell19.addElement(table5);
			cell19.addElement(table6);
			table.addCell(cell19);

			
			
			
			

			PdfPCell cell191 = new PdfPCell();

			cell191.setBorder(0);

			PdfPTable table4 = new PdfPTable(4);
			table4.setWidths(new float[] { 3f, 4f,3f,3f });
			table4.setSpacingBefore(10);

			PdfPCell hcell01;
			hcell01 = new PdfPCell(new Phrase("Verified By             ", headFont1));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setPaddingTop(30);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-20f);
			table4.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Reciever Signature " , headFont1));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setPaddingTop(30);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-20f);
			table4.addCell(hcell01);
			
			hcell01 = new PdfPCell(new Phrase("Accountant            " , headFont1));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setPaddingTop(30);
			hcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingRight(-40f);
			table4.addCell(hcell01);
			
			hcell01 = new PdfPCell(new Phrase("Director" , headFont1));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setPaddingTop(30);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table4.addCell(hcell01);
			
			

			cell191.setFixedHeight(150f);
			cell191.setColspan(2);
			//cell19.addElement(table21)
			cell191.addElement(table4);
			table.addCell(cell191);

			document.add(table);

			document.close();

			System.out.println("finished");
			pdfBytes = byteArrayOutputStream.toByteArray();
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/voucher/viewFile/")
					.path(voucherPdfServiceImpl.getNextPdfId()).toUriString();

			voucherPdf = new VoucherPdf();
			voucherPdf.setVid(voucherPdfServiceImpl.getNextPdfId());
			voucherPdf.setData(pdfBytes);
			voucherPdf.setFileName(voucherId+"voucher pdf");
			voucherPdf.setFileuri(uri);
			// voucherPdf.setRegId(patientServiceDetails.getRegId());
			voucherPdfServiceImpl.save(voucherPdf);
			
			
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		
		return voucherPdf;
	}
	public 	List<Object> pageLoad()
	{
		
		List<Object> a1=new ArrayList<>();
		Map<String,String> userName=null;
		Map<String,String> nextID=new HashMap<>();
		
		Iterable<User> user=userServiceImpl.findByStatus("ACTIVE");
		List<Object> userlist=new ArrayList<>();

		String fn=null;
		String mn=null;
		String ln=null;
		
		
		for(User user1:user)
		{
			userName=new HashMap<>();
			if(user1.getFirstName()==null)
			{
				fn="";
			}
			else
			{
				fn=user1.getFirstName();
			}
			if(user1.getMiddleName()==null)
			{
				mn="";
			}
			else
			{
				mn=user1.getMiddleName();
			}
			if(user1.getLastName()==null)
			{
				ln="";
			}
			else
			{
				ln=user1.getLastName();
			}

			userName.put("userName",fn+" "+mn+" "+ln+"-"+user1.getUserId());
			userlist.add(userName);
			
		}
		a1.add(userlist);
		nextID.put("nextId", voucherServiceImpl.getNextVoucherId());
		a1.add(nextID);
		
		return a1;
	}
	
	

	public Iterable<Voucher> findAll() {
		return voucherRepository.findAll();
	}


	public  VoucherPdf updateVoucher(VoucherDto voucherdto, String id,Principal principal) {
		Voucher voucher=voucherServiceImpl.findByPaymentNo(id);
		voucher.setBank(voucherdto.getBank());
		voucher.setCheckDate(voucherdto.getCheckDate());
		voucher.setCheckNo(voucherdto.getCheckNo());
		voucher.setPaidTo(voucherdto.getPaidTo());
		voucher.setPaymentDate(voucherdto.getPaymentDate());
		voucher.setPaymentType(voucherdto.getPaymentType());
		voucher.setPreparedBy(voucher.getPreparedBy());
		System.out.println(voucher.getPreparedBy());
		
		voucher.setPrintedBy(voucher.getPrintedBy());
		voucher.setRaisedBy(voucherdto.getRaisedBy());
		voucher.setRemarks(voucherdto.getRemarks());
		voucher.setVoucherAmount(voucherdto.getVoucherAmount());
		voucher.setVoucherType(voucherdto.getVoucherType());
		voucher.setOtherName(voucherdto.getOtherName());
		voucherRepository.save(voucher);
		
		
		User user=userServiceImpl.findByUserName(principal.getName());
		String createdBy=user.getFirstName()+" "+user.getLastName();
		
		User paidUser=null;
		String paidUserTo=voucher.getPaidTo();
		String paidOther=voucher.getOtherName();
		
		String[] paidTo=null;
		paidTo=paidUserTo.split("-");
		
		if(paidUserTo.equalsIgnoreCase("Others"))
		{
		
			paidUser=userServiceImpl.findOneByUserId(null);
		}
		else 
		{
			paidUser=userServiceImpl.findOneByUserId(paidTo[1]);
		}
	//	voucher.setPaymentNo(getNextVoucherId());
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
		voucher.setPaymentDate(timestamp);
		voucher.setUserVoucher(user);
		if(paidOther!=null)
		{
		voucher.setOtherName(paidOther);
		}
		else if(paidUser!=null) {
			voucher.setPaidTo(paidUser.getUserId());
		}
		voucher.setPreparedBy(createdBy);
		voucher.setPrintedBy(createdBy);
		voucherRepository.save(voucher);

		
		VoucherPdf voucherPdf=null;
		 byte[] pdfBytes=null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Date date = Calendar.getInstance().getTime();

			// Display a date in day, month, year format
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh.mm aa");
			String today = formatter.format(date).toString();

			// for convert db date to dmy format
			Timestamp date1 = voucher.getPaymentDate();
			DateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(date1.getTime());
			String paymentDate = format1.format(cal.getTime());
	        String paymentType=voucher.getPaymentType();
					
		/*
		 * //for address String addr=
		 * "                                             Plot No 14,15,16&17,Nandi Co-op.Society,Main Road   \n                               "
		 * +
		 * "                                        Beside Navya Grand Total,Miyapur,Hyderabad,TS      \n                                                                                          Phone:040-23046789    "
		 * +
		 * "\n                                                                               Email : udbhavahospitals@gmail.com"
		 * ;
		 */
		try {


			Document document = new Document(PageSize.A4_LANDSCAPE);

			Font redFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			Font headFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			Font headFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);

			
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

			Resource fileResourcee = resourceLoader.getResource(ConstantValues.IMAGE_PNG_CLASSPATH);
			Image img = Image.getInstance(hospitalLogo.getURL());
			

			document.open();
			PdfPTable table = new PdfPTable(2);
			img.scaleAbsolute(ConstantValues.IMAGE_ABSOLUTE_INTIAL_POSITION, ConstantValues.IMAGE_ABSOLUTE_FINAL_POSITION);
			table.setWidthPercentage(ConstantValues.TABLE_SET_WIDTH_PERECENTAGE);

			Phrase pq = new Phrase(new Chunk(img, ConstantValues.IMAGE_SET_INTIAL_POSITION, ConstantValues.IMAGE_SET_FINAL_POSITION));

			pq.add(new Chunk(ConstantValues.VOUCHER_ADRESS,redFont1));
			PdfPTable table97 = new PdfPTable(1);
			table97.setWidths(new float[] { 5f });
			table97.setSpacingBefore(10);

			PdfPCell hcell9;
			hcell9 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont2));
			hcell9.setBorder(Rectangle.NO_BORDER);
			hcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell9.setPaddingLeft(20f);

			PdfPCell cellp = new PdfPCell(pq);
			PdfPCell cell1 = new PdfPCell();
			cell1.setBorder(0);
			
			PdfPTable table96 = new PdfPTable(1);
			table96.setWidths(new float[] { 5f });
			table96.setSpacingBefore(10);

			PdfPCell hcell96;
			hcell96 = new PdfPCell(new Phrase(ConstantValues.HOSPITAL_NAME, headFont2));
			hcell96.setBorder(Rectangle.NO_BORDER);
			hcell96.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell96.setPaddingLeft(25f);

			table96.addCell(hcell96);
			cell1.addElement(table96);
			
			
			
			// for header end
			cell1.setFixedHeight(107f);
			cell1.addElement(pq);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setColspan(2);
			table.addCell(cell1);

	       PdfPCell cell09 = new PdfPCell();
	       cell09.setBorder(0);
			

			PdfPTable table03 = new PdfPTable(1);
			table03.setWidths(new float[] { 3f });
			table03.setSpacingBefore(15);

			PdfPCell hcell101;
			hcell101 = new PdfPCell(new Phrase("Voucher Payment ", headFont1));
			hcell101.setBorder(Rectangle.NO_BORDER);
			hcell101.setHorizontalAlignment(Element.ALIGN_CENTER);
			table03.addCell(hcell101);

			cell09.setFixedHeight(30f);
			cell09.setColspan(2);
			//cell19.addElement(table21);
			cell09.addElement(table03);
			table.addCell(cell09);

			
			
			
			PdfPCell cell19 = new PdfPCell();

			

			PdfPTable table3 = new PdfPTable(4);
			table3.setWidths(new float[] { 3f, 3f,3f,3f });
			table3.setSpacingBefore(10);

			PdfPCell hcell11;
			hcell11 = new PdfPCell(new Phrase("Payment No                  :  ", headFont));
			hcell11.setBorder(Rectangle.NO_BORDER);
			hcell11.setPaddingLeft(-50f);
			table3.addCell(hcell11);

			hcell11 = new PdfPCell(new Phrase(voucher.getPaymentNo() , redFont1));
			hcell11.setBorder(Rectangle.NO_BORDER);
			hcell11.setPaddingLeft(-40f);
			hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
			table3.addCell(hcell11);
			
			hcell11 = new PdfPCell(new Phrase("Payment Date              :  " , headFont));
			hcell11.setBorder(Rectangle.NO_BORDER);
			hcell11.setPaddingLeft(-20f);
		//	hcell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell11);
			
			hcell11 = new PdfPCell(new Phrase(paymentDate, redFont1));
			hcell11.setBorder(Rectangle.NO_BORDER);
			
			hcell11.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell11.setPaddingLeft(-20f);
			table3.addCell(hcell11);
			
			PdfPCell hcell1111;
			hcell1111 = new PdfPCell(new Phrase("Payment Type              :  ", headFont));
			hcell1111.setBorder(Rectangle.NO_BORDER);
			hcell1111.setPaddingLeft(-50f);
			table3.addCell(hcell1111);

			hcell1111 = new PdfPCell(new Phrase(paymentType, redFont1));
			hcell1111.setBorder(Rectangle.NO_BORDER);
			hcell1111.setPaddingLeft(-40f);
			hcell1111.setHorizontalAlignment(Element.ALIGN_LEFT);
			table3.addCell(hcell1111);
			
			
			hcell1111 = new PdfPCell(new Phrase("Voucher Type              :", headFont));
			hcell1111.setBorder(Rectangle.NO_BORDER);
			hcell1111.setPaddingLeft(-20f);
		//	hcell11.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell1111);
			
			hcell1111 = new PdfPCell(new Phrase(voucher.getVoucherType(), redFont1));
			hcell1111.setBorder(Rectangle.NO_BORDER);
			
			hcell1111.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell1111.setPaddingLeft(-20f);
			table3.addCell(hcell1111);
			
			
			PdfPCell hcell112;
			hcell112 = new PdfPCell(new Phrase("" , headFont));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setPaddingLeft(-70f);
			table3.addCell(hcell112);

			hcell112 = new PdfPCell(new Phrase("", redFont1));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setPaddingRight(-70f);
			hcell112.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell112);
			
			

			hcell112 = new PdfPCell(new Phrase("Paid To                         :  " , headFont));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setPaddingLeft(-20f);
			//hcell112.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell112);
			
			if(paidUserTo.equals("Others"))
			{
			hcell112 = new PdfPCell(new Phrase(paidOther, redFont1));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell112.setPaddingLeft(-20f);
			table3.addCell(hcell112);
			}
			else
			{
			hcell112 = new PdfPCell(new Phrase(paidTo[0], redFont1));
			hcell112.setBorder(Rectangle.NO_BORDER);
			hcell112.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell112.setPaddingLeft(-20f);
			table3.addCell(hcell112);
			}
			PdfPCell hcell14;
			hcell14 = new PdfPCell(new Phrase("Voucher Amount         :  " , headFont));
			hcell14.setBorder(Rectangle.NO_BORDER);
			hcell14.setPaddingLeft(-50f);
			table3.addCell(hcell14);

			hcell14 = new PdfPCell(new Phrase(String.valueOf(voucher.getVoucherAmount()) , redFont1));
			hcell14.setBorder(Rectangle.NO_BORDER);
			hcell14.setPaddingLeft(-40f);
			hcell14.setHorizontalAlignment(Element.ALIGN_LEFT);
			table3.addCell(hcell14);

			hcell14 = new PdfPCell(new Phrase("" , redFont1));
			hcell14.setBorder(Rectangle.NO_BORDER);
			hcell14.setPaddingLeft(-20f);
			//hcell14.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell14);

			hcell14 = new PdfPCell(new Phrase(" " , redFont1));
			hcell14.setBorder(Rectangle.NO_BORDER);
			hcell14.setPaddingRight(-70f);
			hcell14.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table3.addCell(hcell14);
			
			
			PdfPTable table5 = new PdfPTable(2);
			table5.setWidths(new float[] { 3f, 9f });
			table5.setSpacingBefore(10);

			
			PdfPCell hcell13;
			hcell13 = new PdfPCell(new Phrase("Remarks                       :  ", headFont));
			hcell13.setBorder(Rectangle.NO_BORDER);
			hcell13.setPaddingLeft(-50f);
			table5.addCell(hcell13);

			hcell13 = new PdfPCell(new Phrase(voucher.getRemarks(), redFont1));
			hcell13.setBorder(Rectangle.NO_BORDER);
			hcell13.setPaddingLeft(-40f);
			hcell13.setHorizontalAlignment(Element.ALIGN_LEFT);
			table5.addCell(hcell13);
		
		
			PdfPTable table6 = new PdfPTable(4);
			table6.setWidths(new float[] { 3f, 3f,3f,3f });
			table6.setSpacingBefore(10);

			
			PdfPCell hcell15;
			hcell15 = new PdfPCell(new Phrase("Prepared By                 :  ", headFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-50f);
			hcell15.setPaddingTop(20f);
			table6.addCell(hcell15);

			hcell15 = new PdfPCell(new Phrase(createdBy , redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-40f);
			hcell15.setPaddingTop(20f);
			hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
			table6.addCell(hcell15);
			
			hcell15 = new PdfPCell(new Phrase("Prepared On                 :  " , headFont));
			hcell15.setBorder(Rectangle.NO_BORDER);
			hcell15.setPaddingLeft(-20f);
			hcell15.setPaddingTop(20f);
			//hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table6.addCell(hcell15);
		
			
			hcell15 = new PdfPCell(new Phrase(today , redFont1));
			hcell15.setBorder(Rectangle.NO_BORDER);
			//hcell15.setPaddingRight(-70f);
			hcell15.setPaddingTop(20f);
			hcell15.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell15.setPaddingLeft(-20f);
			//hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table6.addCell(hcell15);
			
			
			
			PdfPCell hcell16;
			hcell16 = new PdfPCell(new Phrase("Printed By                    :  ", headFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-50f);
			//hcell16.setPaddingTop(20f);
			table6.addCell(hcell16);

			hcell16 = new PdfPCell(new Phrase(createdBy , redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-40f);
			//hcell16.setPaddingTop(20f);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			table6.addCell(hcell16);
			
			hcell16 = new PdfPCell(new Phrase("Printed On                    :  " , headFont));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setPaddingLeft(-20f);
			//hcell16.setPaddingTop(20f);
			//hcell15.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table6.addCell(hcell16);
			hcell16 = new PdfPCell(new Phrase(today , redFont1));
			hcell16.setBorder(Rectangle.NO_BORDER);
			hcell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell16.setPaddingLeft(-20f);
			table6.addCell(hcell16);
			
			cell19.setFixedHeight(180f);
			cell19.setColspan(2);
			cell19.addElement(table3);
			cell19.addElement(table5);
			cell19.addElement(table6);
			table.addCell(cell19);

			
			
			
			

			PdfPCell cell191 = new PdfPCell();

			cell191.setBorder(0);

			PdfPTable table4 = new PdfPTable(4);
			table4.setWidths(new float[] { 3f, 4f,3f,3f });
			table4.setSpacingBefore(10);

			PdfPCell hcell01;
			hcell01 = new PdfPCell(new Phrase("Verified By             ", headFont1));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setPaddingTop(30);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-20f);
			table4.addCell(hcell01);

			hcell01 = new PdfPCell(new Phrase("Reciever Signature " , headFont1));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setPaddingTop(30);
			hcell01.setHorizontalAlignment(Element.ALIGN_LEFT);
			hcell01.setPaddingLeft(-20f);
			table4.addCell(hcell01);
			
			hcell01 = new PdfPCell(new Phrase("Accountant            " , headFont1));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setPaddingTop(30);
			hcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell01.setPaddingRight(-40f);
			table4.addCell(hcell01);
			
			hcell01 = new PdfPCell(new Phrase("Director" , headFont1));
			hcell01.setBorder(Rectangle.NO_BORDER);
			hcell01.setPaddingTop(30);
			hcell01.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table4.addCell(hcell01);
			
			

			cell191.setFixedHeight(150f);
			cell191.setColspan(2);
			//cell19.addElement(table21)
			cell191.addElement(table4);
			table.addCell(cell191);

			document.add(table);

			document.close();

			System.out.println("finished");
			pdfBytes = byteArrayOutputStream.toByteArray();
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/voucher/viewFile/")
					.path(voucherPdfServiceImpl.getNextPdfId()).toUriString();

			voucherPdf = new VoucherPdf();
			voucherPdf.setVid(voucherPdfServiceImpl.getNextPdfId());
			voucherPdf.setData(pdfBytes);
			voucherPdf.setFileName(voucherPdf.getFileName());
			voucherPdf.setFileuri(uri);
			// voucherPdf.setRegId(patientServiceDetails.getRegId());
			voucherPdfServiceImpl.save(voucherPdf);
			
			
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		
		return voucherPdf;
	}


	public List<Map<String, String>> voucherDetails(String type)
	{
		List<Map<String,String>> display=new ArrayList<>();
		String regDate=null;
		String inpatient=null;
		String outpatient=null;
		String docName="";
		String twodayback="";
		List<Voucher> voucher=null;
		
		String today= Timestamp.valueOf(LocalDateTime.now()).toString().substring(0, 10);
		String nextDay=LocalDate.parse(today).plusDays(1).toString();
		if(type.equalsIgnoreCase("2"))
		{
		twodayback=LocalDate.parse(today).plusDays(-2).toString();
		 voucher= voucherServiceImpl.voucherTwoDays(twodayback,nextDay);
		}
		else if(type.equalsIgnoreCase("7"))
		{
		twodayback=LocalDate.parse(today).plusDays(-7).toString();	
		 voucher= voucherServiceImpl.voucherTwoDays(twodayback,nextDay);
		}
		else if(type.equalsIgnoreCase("15"))
		{
		twodayback=LocalDate.parse(today).plusDays(-15).toString();	
		 voucher= voucherServiceImpl.voucherTwoDays(twodayback,nextDay);
		}
		else if(type.equalsIgnoreCase("30"))
		{
		twodayback=LocalDate.parse(today).plusDays(-30).toString();	
		 voucher= voucherServiceImpl.voucherTwoDays(twodayback,nextDay);
		}else if(type.equalsIgnoreCase("ALL")) {
			
			voucher=voucherRepository.findAllByOrderByPaymentNoDesc();
			
		}
	
		
				
		
			for(Voucher voucherInfo:voucher)
			{
				Map<String,String> displayInfo=new HashMap<>();
				
				float payment=0;
				
				displayInfo.put("paymentNo",voucherInfo.getPaymentNo());
				
				System.out.println("hiiii");
				System.out.println(voucherInfo.getPaymentNo());
				
				//displayInfo.put("paidTo",voucherInfo.getPaidTo());
				
				displayInfo.put("voucherType",voucherInfo.getVoucherType());
			
				displayInfo.put("paymentType",voucherInfo.getPaymentType());
				
				displayInfo.put("voucherAmount",String.valueOf(voucherInfo.getVoucherAmount()));
				
				//for different format
				String daoDate=String.valueOf(voucherInfo.getPaymentDate().toString());
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");
				try {
					daoDate=toFormat.format(fromFormat.parse(daoDate));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				User userName=null;
               String userId=voucherInfo.getPaidTo();
				
				
				if(!userId.equalsIgnoreCase("Others") && userId!=null)
				{
					
					
				userName=userServiceImpl.findOneByUserId(voucherInfo.getPaidTo());
				
				if(userName!=null) {
				//System.out.println(userName.getFirstName()+""+userName.getMiddleName()+""+userName.getLastName());
				
				displayInfo.put("paidTo",userName.getFirstName()+""+userName.getMiddleName()+""+userName.getLastName());
				}else {
					displayInfo.put("paidTo"," ");
				}
				}
				else
				{
					displayInfo.put("paidTo",voucherInfo.getOtherName());	
				}
				
				displayInfo.put("paymentDate",daoDate);
				
								
				display.add(displayInfo);
				
			}
			
			return display;
	}
	
public List<Map<String, String>> getAllvc() {
		
		List<Voucher> voucher	=voucherRepository.findAllByOrderByPaymentNoDesc();
		
		List<Map<String,String>> display=new ArrayList<>();
			for(Voucher voucherinfo:voucher) {
				Map<String,String> displayInfo=new HashMap<>();
				
			//for different format
				String daoDate=String.valueOf(voucherinfo.getPaymentDate().toString());
				SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy hh.mm aa");
			
				User userName=null;
				
				try {
					daoDate=toFormat.format(fromFormat.parse(daoDate));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String userId=voucherinfo.getPaidTo();
				
				
				if(!userId.equalsIgnoreCase("Others") && userId!=null)
				{
				userName=userServiceImpl.findOneByUserId(voucherinfo.getPaidTo());
				displayInfo.put("paidTo",userName.getFirstName()+""+userName.getMiddleName()+""+userName.getLastName());
				}
				else
				{
					displayInfo.put("paidTo",voucherinfo.getOtherName());	
				}
				
				displayInfo.put("paymentDate",daoDate);
				displayInfo.put("paymentNo",voucherinfo.getPaymentNo());
				displayInfo.put("voucherType",voucherinfo.getVoucherType());
				displayInfo.put("paymentType",voucherinfo.getPaymentType());
				displayInfo.put("voucherAmount",String.valueOf(voucherinfo.getVoucherAmount()));
				//displayInfo.put("paidTo",voucherinfo.getPaidTo());
				
				
				display.add(displayInfo);
			}

			return display;
		}


	public List<Voucher> voucherTwoDays(String twoDayBack, String today) {
		return  voucherRepository.voucherTwoDays(twoDayBack, today);
	}

	private Voucher findByPaymentNo(String id) {
		// TODO Auto-generated method stub
		return voucherRepository.findByPaymentNo(id);
	}


	
}