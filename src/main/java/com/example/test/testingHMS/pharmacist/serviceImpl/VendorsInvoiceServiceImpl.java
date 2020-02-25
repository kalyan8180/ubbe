package com.example.test.testingHMS.pharmacist.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.pharmacist.helper.RefApprovedListHelper;
import com.example.test.testingHMS.pharmacist.helper.RefInvoiceIds;
import com.example.test.testingHMS.pharmacist.model.MedicineProcurement;
import com.example.test.testingHMS.pharmacist.model.VendorsInvoice;
import com.example.test.testingHMS.pharmacist.repository.MedicineProcurementRepository;
import com.example.test.testingHMS.pharmacist.repository.VendorsInvoiceRepository;
import com.example.test.testingHMS.pharmacist.service.VendorsInvoiceService;
@Service
public class VendorsInvoiceServiceImpl implements VendorsInvoiceService {

	@Autowired
	VendorsInvoiceRepository vendorsInvoiceRepository;
	
	@Autowired
	VendorsInvoiceServiceImpl vendorsInvoiceServiceImpl;
	
	@Autowired
	LocationServiceImpl locationServiceImpl;
	
	@Autowired
	MedicineProcurementRepository medicineProcurementRepository;
	
	@Autowired
	MedicineProcurementServiceImpl  medicineProcurementServiceImpl;

	
	@Autowired
	RefApprovedListHelper refApprovedListHelper;
	
	@Autowired
	RefInvoiceIds refInvoiceIds;
	
	@Override
	public List<VendorsInvoice> findByVendorInvoiceMedicineProcurement(String id) {
		return vendorsInvoiceRepository.findByVendorInvoiceMedicineProcurement(id);
	}


	@Override
	public List<String> findOneInvoice(String id) {
		return vendorsInvoiceRepository.findOneInvoice(id);
	}


	public String getNextInvoice() {
		VendorsInvoice vendorsInvoice=vendorsInvoiceRepository.findFirstByOrderByInvoiceIdDesc();
		String nextInvoiceId=null;
		if(vendorsInvoice==null)
		{
			nextInvoiceId="INV0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(vendorsInvoice.getInvoiceId().substring(3));
			nextIntId+=1;
			nextInvoiceId="INV"+String.format("%07d",nextIntId);
		}
		return nextInvoiceId;
	}
	
	public void computeSave(VendorsInvoice vendorsInvoice)
	{
		vendorsInvoice.setInvoiceId(getNextInvoice());
		vendorsInvoice.setVendorInvoiceLocation(locationServiceImpl.findByLocationName(vendorsInvoice.getLocation()));
		List<MedicineProcurement> medicineProcurement=medicineProcurementRepository.findByProcurementId(vendorsInvoice.getVendorInvoiceMedicineProcurement());
		/*if(vendorsInvoice.getPaid_amount()==medicineProcurementRepository.findSumOfCost(medicineProcurement.get(0).getProcurementId()))
		{
			vendorsInvoice.setPaidInFull("yes");
		}
		else
		{
			vendorsInvoice.setPaidInFull("no");
		}*/
		vendorsInvoice.setDate(Timestamp.valueOf(LocalDateTime.now()));
		if(vendorsInvoice.getBalanceAmount()==0)
		{
			vendorsInvoice.setPaidInFull("Yes");
		}
		else
		{
			vendorsInvoice.setPaidInFull("No");
		}
		vendorsInvoiceRepository.save(vendorsInvoice);
	}
	
	public long findSumOfPaidAmount(String pid)
	{
		return vendorsInvoiceRepository.findSumOfPaidAmount(pid);
	}
	
	public List<RefApprovedListHelper> getApprovedProcurement()
	{
		List<RefApprovedListHelper> displayList=new ArrayList<>();
		List<String> procurementId=new ArrayList<>();
		
		List<MedicineProcurement> medicineProcurementsInfo=medicineProcurementRepository.findAllApproved(); 
		
		for(MedicineProcurement medicineProcurementList:medicineProcurementsInfo)
		{
			if(!procurementId.contains(medicineProcurementList.getProcurementId()))
			{
				refApprovedListHelper=new RefApprovedListHelper();
				long sum=medicineProcurementRepository.findSumOfCost(medicineProcurementList.getProcurementId());
				refApprovedListHelper.setCost(sum);
				refApprovedListHelper.setProcurementId(medicineProcurementList.getProcurementId());
				refApprovedListHelper.setProcurementDate(medicineProcurementList.getInsertedDate().toString().substring(0, 10));
				refApprovedListHelper.setBalanceAmount(sum);
				refApprovedListHelper.setDueAmount(sum);
				refApprovedListHelper.setPaid_amount(0);
				procurementId.add(medicineProcurementList.getProcurementId());
				List<VendorsInvoice> vendorsInvoices=null;
				if((vendorsInvoices=vendorsInvoiceServiceImpl.findByVendorInvoiceMedicineProcurement(medicineProcurementList.getProcurementId()))!=null)
				{
					for(VendorsInvoice vendorsInvoiceList:vendorsInvoices)
					{
						if(vendorsInvoiceList.getBalanceAmount()!=0 && vendorsInvoiceList.getDueAmount()!=0)
						{
							refApprovedListHelper.setBalanceAmount(vendorsInvoiceList.getBalanceAmount());
							refApprovedListHelper.setInvoice(vendorsInvoiceServiceImpl.findOneInvoice(medicineProcurementList.getProcurementId()));
							refApprovedListHelper.setDueAmount(vendorsInvoiceList.getDueAmount());
							long totalPaid=vendorsInvoiceServiceImpl.findSumOfPaidAmount(vendorsInvoiceList.getVendorInvoiceMedicineProcurement());
							System.out.println("Total paid"+totalPaid);
							refApprovedListHelper.setPaid_amount(totalPaid);
						}
						else
						{
							refApprovedListHelper.setBalanceAmount(0);
							List<String> invoice=new ArrayList<>();
							invoice.add(vendorsInvoiceServiceImpl.getNextInvoice());
							refApprovedListHelper.setInvoice(vendorsInvoiceServiceImpl.findOneInvoice(medicineProcurementList.getProcurementId()));
							refApprovedListHelper.setDueAmount(0);
							long totalPaid=vendorsInvoiceServiceImpl.findSumOfPaidAmount(vendorsInvoiceList.getVendorInvoiceMedicineProcurement());
							
							refApprovedListHelper.setPaid_amount(totalPaid);

							
						}
						
					}
					
					
				}
				
				displayList.add(refApprovedListHelper);
			}
		}
		return displayList;

	}
	
}
