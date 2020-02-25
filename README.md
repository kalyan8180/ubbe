### Server side code base 

	

####__To CREATE USER__ 

localhost:8084/v1/user/create/


	{
		
		"firstName":"Mahesh",
		"middleName":"nagesh",
		"lastName":"revanakar",
		"personalContactNumber":1234567899,
		"workContactNumber":1234567891,
		"userName":"Nikhil",
		"pin":"xcv",
		"email":"nik@gail.com",
		"timezone":"23-08-90",
		"refName":"Dr.Vv",
		"location":"Bangalore",
		"description":"first Commit",
		"department":"Cardiology",
		"roleName":"DOCTOR",
		"passwordStuff":{
			"password":"nikhilrev",
			"confirmPassword":"nikhilrev",
			"hintQuestion1":"Evening",
			"hintAnswer1":"Evening",
			"hintQuestion2":"Evening",
			"hintAnswer2":"Evening"
		},
		"doctorDetails":
			{
				"specilization":"CARDIOLOGY",
				"qualification":"B.E"
			},
		"doctorSpecialization":[
			
		
			{
				"spec":"Nuerology"
			},
				{
				"spec":"Cardiology"
			}
			]
		
	}

__TO UPDATE USER__

localhost:8084/v1/user/update/UB00002   PUT METHOD


	{
		
		"firstName":"PAWAN",
		"middleName":"nagesh",
		"lastName":"Reddy",
		"personalContactNumber":1234567899,
		"workContactNumber":1234567891,
		"userName":"Nikh234",
		"pin":"xcv",
		"email":"nik@gail.com",	
		"timezone":"23-08-90",
		"refName":"Dr.Vv",
		"location":"Bangalore",
		"description":"first Commit",
		"roleName":"DOCTOR",
		"passwordStuff":{
			"password":"nikhilrev",
			"confirmPassword":"nikhilrev",
			"txnPassword":"kill",
			"confirmTxnPassword":"kill",
			"hintQuestion1":"Evening",
			"hintAnswer1":"Evening",
			"hintQuestion2":"Evening",
			"hintAnswer2":"Evening"
		},
		"doctorDetails":
			{
				"specilization":"CARDIOLOGY",
				"qualification":"B.E"
			}
		"doctorSpecialization":[	
			{
				"spec":"Nuerology"
			},
				{
				"spec":"Cardiology"
			}
			]
		
	}
	
	
__For admin no specilization -Then json data will be__

	{
		
		"firstName":"PAWAN",
		"middleName":"nagesh",
		"lastName":"Reddy",
		"personalContactNumber":1234567899,
		"workContactNumber":1234567891,
		"userName":"Nikh234",
		"pin":"xcv",
		"email":"nik@gail.com",	
		"timezone":"23-08-90",
		"refName":"Dr.Vv",
		"location":"Bangalore",
		"description":"first Commit",
		"roleName":"DOCTOR",
		"passwordStuff":{
			"password":"nikhilrev",
			"confirmPassword":"nikhilrev",
			"txnPassword":"kill",
			"confirmTxnPassword":"kill",
			"hintQuestion1":"Evening",
			"hintAnswer1":"Evening",
			"hintQuestion2":"Evening",
			"hintAnswer2":"Evening"
		},
		"doctorDetails":
			{
				"specilization":"CARDIOLOGY",
				"qualification":"B.E"
			}
		"doctorSpecialization":[	
			]
		
	}
	

__TO GET EXISTING USER TYPE__

localhost:8084/v1/user/create __GET METHOD__

__--------------------------------------------------PATIENTS-------------------------------------------------------------__

__TO GET VALUES FOR DROP DOWN__

localhost:8084/v1/patient/create  __GET METHOD__

__TO GET ONE PATIENTS__

localhost:8084/v1/patient/getOne/{regID} __GET METHOD__


__TO GET ALL PATIENTS__

localhost:8084/v1/patient/getAll __GET METHOD__

__TO CREATE PATIENTS__

localhost:8084/v1/patient/create __POST METHOD__


	


	

	

{
						"title":"Mr",
						"firstName":"Test2",
						"middleName":"Nagesh",
						"lastName":"Reddy",
						"dob":"2018-01-08T12:37:20",
						"ageCalculation":100,
						"motherName":"Nagraj",
						"bloodGroup":"B+",
						"gender":"Male",
						"nationality":"india",
						"religion":"hindu",
						"maritialStatus":"single",
						"occupation":"Engineer",
						"aliasName":"niki",
						"refName":{
							"source":"Social Media",
							"refName":"gmail",
							"phone":"9019438563",
							"adr":"anavatti"
							
						},
						"patientTypeName":"OUTPATIENT",
						"marketingName":"Facebook media",
						"mobile":6303788197,
						"telephone":8182,
						"email":"nik@gmail.com",
						"modeOfCommunication":"phone",
						"address":"Bangalore",
						"area":"CV raman",
						"city":"bangalore",
						"state":"Bangalore",
						"country":"india",
						"responsiblePerson":"Friend",
						"responsiblePersonName":"Nagesh",
						"pin":"E244",
						"consultant":"Dr. Mahesh revanakar-UB00001",
						"passportNo":"passport-2345",
						"issueDate":"2018-08-27T18:30:20",
						"issuedAt":"Bangalore",
						"expiryDate":"2018-08-27T18:30:20",
						 "companyName":"VNC DIGITAL",
						 "companyCode":"VNC9019",
						 "companyFee":"10000",
						"vPatientRegistration":[{
							"appNo":10,
							"dateOfJoining":"2019-01-08T05:30:20",
							"regDate":"2019-01-08T05:30:20",
							"referenceNumber":"er35356363",
							"roomBookingDetails":[
								
								{
									"fromDate":"2018-11-14T18:30:20",
									"toDate":"2018-11-20T18:30:20",
									"bedNo":"udhbed4",
									"advanceAmount":2000,
									"estimateAmount":10000
								}],
						"patientPayment":[
							
									{
									"amount":100,
									"typeOfCharge":"Reg Fees",
									"modeOfPaymant":"Cash"
								}
								]
					}]
}
_Changing consultantFee, regFee and Consultant_


	

	{
						"title":"Mr",
						"firstName":"Anan",
						"middleName":"Nagesh",
						"lastName":"Reddy",
						"dob":"2018-12-01T11:01:20",
						"motherName":"Nagraj",
						"bloodGroup":"B+",
						"gender":"Male",
						"nationality":"india",
						"religion":"hindu",
						"occupation":"Engineer",
						"ageCalculation":0,
						"aliasName":"niki",
						"refName":{
							"source":"Family",
							"refName":"Nik",
							"phone":0,
							"adr":""
							
						
							
						},
						"patientTypeName":"INPATIENT",
						"marketingName":"Facebook media",
						"mobile":6303788196,
						"telephone":8182,
						"email":"nik@gmail.com",
						"modeOfCommunication":"phone",
						"address":"Bangalore",
						"area":"CV raman",
						"city":"bangalore",
						"state":"Bangalore",
						"country":"india",
						"responsiblePerson":"Friend",
						"responsiblePersonName":"Nagesh",
						"pin":"E244",
						"consultant":"Dr. Sharath revanakar-UB00002",
						"passportNo":"passport-2345",
						"issueDate":"2018-08-27T18:30:20",
						"issuedAt":"Bangalore",
						"expiryDate":"2018-08-27T18:30:20",
						 "companyName":"VNC DIGITAL",
						 "companyCode":"VNC9019",
						 "companyFee":"10000",
						"vPatientRegistration":[{
							"appNo":10,
							"dateOfJoining":"2019-01-21T12:00:20",
							"regDate":"2019-01-21T12:00:20",
							"referenceNumber":"er35356363",
							"roomBookingDetails":[
								
								{
									"fromDate":"2018-11-14",
									"toDate":"",
									"bedNo":"udhbed8",
									"advanceAmount":100,
									"estimateAmount":500
								}],
						"multimode":[
							{
								"mode":"cash",
								"amount":"100"
							},
							{
								"mode":"card",
								"amount":"300"
							}],
						"patientPayment":[
							
									{
									"amount":200,
									"typeOfCharge":"Reg Fees",
									"modeOfPaymant":"Cash+Card"
								}
								,
								
									{
									"amount":200,
									"typeOfCharge":"Doctor Fee",
									"modeOfPaymant":"Cash+Card"
								}
								]
					}]
	}
					
		
	
		
	

__Update existing patient__	
	
localhost:8084/v1/patient/updateAll/{UMR} PUT Method
	
	
	

{
		"title":"Mr",
		"firstName":"Jaffa",
		"middleName":"Nagesh",
		"lastName":"Reddy",
		"dob":"1994-01-08T12:37:20",
		"ageCalculation":100,
		"motherName":"Nagraj",
		"bloodGroup":"B+",
		"gender":"Male",
		"nationality":"india",
		"religion":"hindu",
		"occupation":"Engineer",
		"aliasName":"niki",
		"refName":{
		
			
		},
		"patientTypeName":"OUTPATIENT",
		"marketingName":"Facebook media",
		"mobile":6303788196,
		"telephone":8182,
		"email":"nik@gmail.com",
		"modeOfCommunication":"phone",
		"address":"Bangalore",
		"area":"CV raman",
		"city":"bangalore",
		"state":"Bangalore",
		"country":"india",
		"responsiblePerson":"Friend",
		"responsiblePersonName":"Nagesh",
		"pin":"E244",
		"consultant":"Dr. Mahesh revanakar-UB00001",
		"passportNo":"passport-2345",
		"issueDate":"2018-08-27T18:30:20",
		"issuedAt":"Bangalore",
		"expiryDate":"2018-08-27T18:30:20",
		 "companyName":"VNC DIGITAL",
		 "companyCode":"VNC9019",
		 "companyFee":"10000"
}
		
	

	
	
	

__TO UPDATE PATIENTS__

localhost:8084/v1/patient/update/UMR0000001 PUT METHOD

	{
		
						"mobile":9019438586,
						"email":"nikhil.revanakar@gmail.com",
						"address":"Singapore"
		
	}

__EXISTING PATIENT NEW ENROLLMENT__

__TO GET NEW VALUE FOR REG ID__

localhost:8084/v1/registration/patient __GET METHOD__

__TO ENROLL EXISTING PATIENT__

localhost:8084/v1/registration/patient/{UMR} __POST METHOD__


	
_With Referral Name_


	{
						"refName":{
								"source":"Walkin",
								"refName":"Walkin",
								"phone":0,
								"adr":""
								},
						"rePatientType":"OUTPATIENT",
						"reConsultant":"Dr PAWAN Reddy-UB00002",
						"referenceNumber":"er35356363",
						"roomBookingDetails":[
								{
									"fromDate":"2018-11-09T00:00:00",
									"toDate":"2018-11-16T00:00:00",
									"bedNo":"udhbed7",
									"advanceAmount":2000,
									"estimateAmount":10000
									
									
								}],
						"patientPayment":[
							
								{
							"amount": 200,
							"typeOfCharge": "Reg Fees",
							"modeOfPaymant": "cash",
							"paid": "NO"
							
						},
							{
							"amount": 200,
							"typeOfCharge": "Doctor Fee",
							"modeOfPaymant": "cash",
							"paid": "NO"
							
						}
							]
	}				


_Changing consultantfee,regfee and consultnt_



	{
		"refName":{
								"source":"Walkin",
								"refName":"Walkin",
								"phone":0,
								"adr":""
								},
						"rePatientType":"INPATIENT",
						"reConsultant":"Dr Mahesh Reddy-UB00002",
						"referenceNumber":"er35356363",
						"roomBookingDetails":[
								{
									"fromDate":"2018-11-09T00:00:00",
									"toDate":"2018-11-16T00:00:00",
									"bedNo":"udhbed7",
									"advanceAmount":2000,
									"estimateAmount":10000
									
									
								}],
						"multimode":[
							{
								"mode":"Cash",
								"amount":1000
							},
							{
								"mode":"Card",
								"amount":1200
							}
							],
						"patientPayment":[
							
								{
							"amount": 200,
							"typeOfCharge": "Doctor Fee",
							"modeOfPaymant": "Cash+Card",
							"paid": "NO"
							
						}
							]
	}	
	



__To get all onboarded patients__

localhost:8084/v1/patient/patientDetails

__Patients paying advance multiple times__

localhost:8084/v1/patient/advanceAmount/{regID}


	{
		"advance":"5000",
		"mode":"Cheque",
		"referenceNumber":"er35356363"
	}
	
	
__To add referral details__

localhost:8084/v1/patient/refdetails


	

	{
		"source":"Social Media",
		"refName":"Yahoo",
		"refAdd":"Anavatti",
		"refPhone":9090909090
		
	}
		

_To edit RegFee, DoctorFee, Cosultant and patient Type_

localhost:8084/v1/patient/consultant/change { POST METHOD }

		{
			"consultant":"Dr. Nikhil revanakar-UB00003",
			"regId":"PR00000004",
			"regFee":"990"

		}


	
_To get dicharged and not discharged patient_
	
localhost:8084/v1/patient/discharge/NOT DISCHARGED GET METHOD FOR NOT DISCHARGED PATIENTS

localhost:8084/v1/patient/discharge/DISCHARGED GET METHOD FOR DISCHARGED PATIENTS
	

__---------------------------------------------------------------------Ambulance Service------------------------------------------------------------------------__
	
	
__Page LOaD__

localhost:8084/v1/ambulance/create __GET METHOD__
	
__Book Ambulance__

localhost:8084/v1/ambulance/create __POST METHOD__

	{
		"patName":"Amit",
		"fromLocation":"madanapalle",
		"toLocation":"tirupati",
		"mobileNo":9703601903,
		"fromTime":"2018-12-13T00:00:20",
		"driverName":"Amit",
		"ambulanceNo":"amb4"
	}
	
__Tag PATIENT__

localhost:8084/v1/ambulance/update/APD0000002

	{	
		"regNo":"PR00000003",
		"billAmount":200.1,
		"paidTo":"narendra",
		"amountStatus":"YES",
		"toTime":"2018-12-13T12:00:20"
	}

__---------------------------------------------------------------------Extend Room Booking------------------------------------------------------------------------__
	
localhost:8084/v1/patient/extend
	
	
	{
	"toDate":"2018-11-30T18:30:20",
	"regId":"PR00000001",
	"room":"udhbed10"
	}

	
__-------------------------------------------------------------------MEDICINE------------------------------------------------------------------__

__DEPRICATED METHOD -NOT USING__

						__TO GET VALUES AFTER PAGE LOaD__

						localhost:8084/v1/pharmacist/medicine/create __GET METHOD__

						__TO INSERT NEW MEDICINE__

						localhost:8084/v1/pharmacist/medicine/create __POST METHOD__

							{
								"name":"Zoxy",
								"itemLevel":"Fulll",
								"batchNo":"B2",
								"manufacturer":"dsds",
								"vendorPackage":"dwsd",
								"brand":"dswwd",
								"drugType":"dwdwe",
								"strengthUnits":"dswdw",
								"saleUnits":89,
								"quantityPerDay":10,
								"minPurchaseQuantity":30,
								"maxPurchaseQuantity":20
								
							}

						__TO GET ALL MEDICINE__

						localhost:8084/v1/pharmacist/medicine/getAll __GET METHOD__

						__Update medicine__

						localhost:8084/v1/pharmacist/medicine/update __POST METHOD__

							{
								"medicineId":"ID"
								"name":"Zoxy",
								"itemLevel":"Fulll",
								"batchNo":"B2",
								"manufacturer":"dsds",
								"vendorPackage":"dwsd",
								"brand":"dswwd",
								"drugType":"dwdwe",
								"strengthUnits":"dswdw",
								"saleUnits":89,
								"quantityPerDay":10,
								"minPurchaseQuantity":30,
								"maxPurchaseQuantity":20
								
							}

__--------NEWLY CREATED METHOD---------__

__TO INSERT NEW LIST OF MEDICINE__

localhost:8084/v1/pharmacist/medicine/listcreate 

{
		
		"refMedicine":
        [
       {
         
        "name":"hyberabad test 3",
		"batchNo":"B2",
		"manufacturer":"dsds",
		"brand":"dswwd",
		"strengthUnits":"dswdw",
		"saleUnits":89,
		"quantityPerDay":10,
		"minPurchaseQuantity":30,
		"maxPurchaseQuantity":2,
		"itemLevel":"Fulll",
		"drugType":"dwdwe",
		"vendorPackage":"dwsd"
	
         
       },
       {
       	"name":"hyberabad test 4",
		"batchNo":"B2",
		"manufacturer":"dsds",
		"brand":"dswwd",
		"strengthUnits":"dswdw",
		"saleUnits":89,
		"quantityPerDay":10,
		"minPurchaseQuantity":30,
		"maxPurchaseQuantity":20,
		"itemLevel":"Fulll",
		"drugType":"dwdwe",
		"vendorPackage":"dwsd"
       }
		
		
		
		
   ]
}						
							

__--------------------------------------------------------------------VENDORS---------------------------------------------------------------------------__

__TO GET VALUES AFTER PAGE LOADED__

localhost:8084/v1/pharmacist/vendor/create __GET METHOD__


__TO INSERT NEW VENDORS__

localhost:8084/v1/pharmacist/vendor/create __POST METHOD__

				{
	
					"vendorType":"Medical",
					"businessType":"Foreign",
					"vendorName":"VNC PHARMA",
					"globalId":"GB001",
					"regName":"VNC PHARMA",
					"amount":1000,
					"panNo":"CSDPR1059R",
					"gstNo":"GST090",
					"deliveryDays":20,
					"paymentTerms":"ANything",
					"brand":"Paracitamol",
					"medicineId":29,
					"suppliers":"Local",
					"adress1":"bnglr",
					"adress2":"hybd",
					"area":"whitefield",
					"city":"Bnglr",
					"state":"Karnataka",
					"country":"IndiA",
					"pinCode":4569,
					"contactPerson":"Company head",
					"mobile":90194387,
					"mail":"Nik@gmail.com",
					"bankName":"Union Bank",
					"bankBranch":"Shimoga",
					"branchAdress":"Shimoga",
					"accountNo":"890354hgee",
					"accountType":"Saving",
					"ifscCode":"CD456",
					"micrCode":"34444",
					"beneficiaryBankName":"State bank"
					
				}


__TO UPDATE EXISTING VENDORS__

localhost:8084/v1/pharmacist/vendor/update/VEN0000002

	{
		
						"vendorType":"Medical",
						"businessType":"Foreign",
						"vendorName":"VNC PHARMA",
						"regNo":"REGNIK9019",
						"globalId":"GB001",
						"regName":"VNC digital PHARMA",
						"amount":1000,
						"panNo":"CSDPR1059R",
						"gstNo":"GST090",
						"deliveryDays":20,
						"paymentTerms":"ANything",
						"brand":"Paracitamol",
						"medicineId":29,
						"suppliers":"Local",
						"adress1":"bnglr",
						"adress2":"hybd",
						"area":"whitefield",
						"city":"Bnglr",
						"state":"Karnataka",
						"country":"IndiA",
						"pinCode":4569,
						"contactPerson":"Company head",
						"mobile":90194387,
						"mail":"Nik@gmail.com",
						"bankName":"Union Bank",
						"bankBranch":"Shimoga",
						"branchAdress":"Shimoga",
						"accountNo":"890354hgee",
						"accountType":"Saving",
						"ifscCode":"CD456",
						"micrCode":"34444",
						"beneficiaryBankName":"State bank"
						
	}
	
	
__To pay vendors__

localhost:8084/v1/pharmacist/invoice/pay/PRO0000004


	{
		"location":"Miyapur",
		"balanceAmount":200,
		"paid_amount":100,
		"paymentType":"Cheque"
	}


__------------------------------------------------------------MEDICINE PROCUREMNET-------------------------------------------------------__


__TO GET VALUES AFTER PAGE LOADED__

localhost:8084/v1/pharmacist/procurement/create __GET METHOD__

__TO INSERT NEW PROCUREMENT VALUE__

	
	{

		"location":"Miyapur",
		"vendorName":"LK Pharma",
		"invoiceNo":"IN99087",
		"poNo":"dwd",
		"draft":"NO"
		"procurementType":"Cheque",
		"currency":"Indian",
		"refMedicineDetails":[
			{
				"mrp":5,
				"quantity":10,
				"costPrice":50,
				
				"tax":330,
				
				"freeSample":"10",
				"itemName":"Movex",
				"batch":"B1",
				
				"packing":"Yes",
				"expDate":"Todq",
				"manufacturedDate":"Dontknow",
				"discount":6,
				"gst":5
			},
			{
				"mrp":5,
				"quantity":10,
				"costPrice":50,
				"tax":330,
				
				"freeSample":"20",
				"itemName":"Zoxy",
				"batch":"B1",
				"packing":"Yes",
				"expDate":"Todq",
				"manufacturedDate":"Dontknow",
				"discount":10,
				"gst":12
			}]
		
	}

	
	
__Update procurement__

localhost:8084/v1/pharmacist/procurement/update PUT Method
	
	{
		
		"procurementId":"PRO0000012",
		"location":"Miyapur",
		"vendorName":"LK Pharma",
		"invoiceNo":"IN99087",
		"poNo":"dwd",
		"draft":"NO",
		"procurementType":"Cheque",
		"currency":"Indian",
		"refMedicineDetails":[
			{
				"mrp":200,
				"quantity":2,
				"costPrice":400,
				
				"tax":330,
				
				"freeSample":"Yes",
				"itemName":"AMBRODIL S",
				"batch":"B1",
				
				"packing":"Yes",
				"expDate":"Todq",
				"manufacturedDate":"Dontknow",
				"discount":6,
				"gst":5
			}]
		
	}




AFTER PROCUREMENT PAYMENT THE INSERTED DATA WILL BE IN NOT-APPROVED STATE. IT HAS TO BE APPROVED BY ADMIN 

__TO GET ALL PROCUREMENT__

localhost:8084/v1/pharmacist/procurement/getAll __GET METHOD__


AFTER THAT SELECT THE METHOD WHICH NEEDS TO BE APPROVED

localhost:8084/v1/pharmacist/procurement/approve/{procId} PUT METHOD

SEND IN THIS FORMAT( INSIDE ARRAY )

	[

		{
		
			"masterProcurementId": "MPRO0000001",
			"procurementId": "PRO0000001",
			"costPrice": 50,
			"mrp": 5,
			"dateOfProcurement": "2018-10-15T12:29:47.000+0000",
			"poNo": "dwd",
			"insertedDate": "2018-10-15T12:29:47.000+0000",
			"modifiedDate": null,
			"quantity": 10,
			"freeSample": "Yes",
			"itemName": "Movex",
			"batch": "B1",
			"procurementType": "Cheque",
			"status": "Not-Approved",
			"currency": "Indian",
			"amount": 2000000,
			"manufacturedDate": "Dontknow",
			"packing": "Yes",
			"expDate": "Todq",
			"tax": 330,
			"vendorName": "LK PHARMA",
			"medName": null,
			"location": "Miyapur",
			"refMedicineDetails": null
		}
	]

AFTER APPROVING YOU HAVE TO PAY FOR VENDORS

__TO GET ALL PROCUREMENT WHICH ARE APPROVED__

localhost:8084/v1/pharmacist/invoice/getApproved __GET METHOD__

__TO PAY FOR VENDORS__

localhost:8084/v1/pharmacist/invoice/pay/{PROCUREMENTID} __POST METHOD__


	{
		"location":"Miyapur",
		"balanceAmount":3600000,
		"paid_amount":3600000,
		"paymentType":"Cheque"
	}


__-------------------------------------------------------------------------SALES CONTROLLER-----------------------------------------------------__

__TO GET VALUES AFTER PAGE LOADED__

localhost:8084/v1/sales/create __GET METHOD__

__TO FIND THE MRP AND BATCH NO FOR PARTICULAR MEDICINE__

localhost:8084/v1/sales/return/findMed/{MEDICINE NAME} __GET METHOD__


__To get cost for all medicine__

 localhost:8084/v1/sales/getmrp
 
	[

		{
		   "med":"Movex",
		   "quantity":23
		
		},
		{
		   "med":"Zoxy",
		   "quantity":23
		
		}
		
	]
	
__To get Available quantity for same medicine with diff batch no__

localhost:8084/v1/sales/getMedPro __POST METHOD__
	
	{
		"quantity":"2",
		"medName":"Zoxy"
		
	}

	250 
	shivram prasad

__TO INSERT DATA FOR WALKIN PATIENT__

localhost:8084/v1/sales/create __POST METHOD__

	{

		"name":"nikhil",
		"location":"Miyapur", 
		"referenceNumber":"67676767",
		
							
							"multiplePayment":[
							
							{	"payType":"Due",
								"amount":250
								
							},							{	"payType":"cash",
								"amount":250
								
							}
								],
		"total":250,
		"refSales":[
			{
				"medicineName":"Dcold vnc",
				"mrp":2,
				"batchNo":"B1",
				"discount":0,
				"quantity":50.5,
				"amount":100,
				"gst":20,
				"expDate":""
			},
			{
				"medicineName":"Zoxy vnc",
				"mrp":5,
				"batchNo":"B1",
				"discount":0,
				"quantity":25,
				"amount":50,
				"gst":20,
				"expDate":""
			}]
	}


__TO INSERT DATA FOR INPATIENTS PATIENT__

localhost:8084/v1/sales/create __POST METHOD__

		{

		"regId":"OP00000002",
		"location":"Miyapur", 
		"referenceNumber":"67676767",
		
							
							"multiplePayment":[
							
							{	"payType":"Due",
								"amount":250
								
							},{	"payType":"cash",
								"amount":250
								
							}
								],
		"total":250,
		"refSales":[
			{
				"medicineName":"Dcold vnc",
				"mrp":2,
				"batchNo":"B1",
				"discount":0,
				"quantity":50.5,
				"amount":100,
				"gst":20,
				"expDate":""
			},
			{
				"medicineName":"Zoxy vnc",
				"mrp":5,
				"batchNo":"B1",
				"discount":0,
				"quantity":25,
				"amount":50,
				"gst":20,
				"expDate":""
			}]
	}

_------------------Sales Cash+Card----------------------_
	
	{

		"regId":"PR00000002",
		"location":"Miyapur", 
		"paymentType":"Cash+Card",
		"referenceNumber":"ERTYUI123",
		
						"multimode":[
							{
								"mode":"cash",
								"amount":"100"
							},
							{
								"mode":"card",
								"amount":"300"
							}],
		"total":250,
		"refSales":[
			{
				"medicineName":"Dcold vnc",
				"mrp":2,
				"batchNo":"B1",
				"discount":0,
				"quantity":50,
				"amount":100,
				"gst":20,
				"expDate":"2019-09-03"
			},
			{
				"medicineName":"Zoxy vnc",
				"mrp":5,
				"batchNo":"B1",
				"discount":0,
				"quantity":25,
				"amount":50,
				"gst":20,
				"expDate":"2019-09-03"
			}]
	}
	
__------------------------------------------------------------SALES RETURN --------------------------------------------------------------__

__TO FIND THE LIST OF MEDICINE FOR ONE BILL__

localhost:8084/v1/sales/return/find/{bill no} __GET METHOD__


localhost:8084/v1/sales/return/create

__TO RETURN THE MEDICINE__

	{

		   "location":"Miyapur",
			"billNo": "BL0000006",
			"regId":"PR00000006",
			"paymentType":"Cash",
			"total":20,
		   "refSalesReturns":[
			{
			 
				"batchNo":"B020",
				"mrp":5,
				"expDate":"2028-12-13 07:00:20",
				"amount": 45,
				"quantity": 10,
				"medicineName": "Movex vnc"
				
			},
			{
				"batchNo":"B020",
				"mrp":5,
				"expDate":"2028-12-13 07:00:20",
				"amount": 45,
				"quantity": 10,
				"medicineName": "Movex vnc"
			
			}]
			
			
	 }
 
__--------------------------------------------------------------SALES REFUND---------------------------------------------------------------__
 
__TO GET THE UNAPPROVED SALES__

localhost:8084/v1/sales/refund/create __GET METHOD__

__TO APPROVE THE SALES RETURN__
 
 localhost:8084/v1/sales/refund/create PUT METHOD
 
	 [
	 
		{
			"returnId": "RS0000022",
			"returnAmount": 10,
			"umr": "UMR0000001",
			"status": "Not-Approved",
			"name": null,
			"mobileNo": 0,
			"amount": 10,
			"paymentType": "Cash",
			"refundBy": "Apporva revanakar",
			"refundDate": "2018-10-15T13:28:12.000+0000",
			"billNo": "BL0000006",
			"refSalesReturns": null
		},
		{
			"returnId": "RS0000021",
			"returnAmount": 10,
			"umr": "UMR0000001",
			"status": "Not-Approved",
			"name": null,
			"mobileNo": 0,
			"amount": 10,
			"paymentType": "Cash",
			"refundBy": "Apporva revanakar",
			"refundDate": "2018-10-15T13:28:12.000+0000",
			"billNo": "BL0000006",
			"refSalesReturns": null
		},
	   
	]

__-------------------------------------------------------------------Doctor---------------------------------------------------------------------------__


__Printing blank prescription__

localhost:8084/v1/patient/blank/{regId}

	{
		"consultant":"Dr Mahesh revanakar",
		"umr":"UMR0000001"
		
	}

__TO CREATE NOTES__

localhost:8084/v1/doctor/create/notes __POST METHOD__

	{

		"regId":"PR00000006",
		"writeNotes":"Run continous for 5km \n do swimming for next 2 hours "

	}


__TO GET NOTES PDF__

localhost:8084/v1/doctor/notes/PR00000006 __POST METHOD__

__TO WRITE PHARMACY NOTES__

localhost:8084/v1/doctor/create/pharmacyNotes __POST METHOD__

	{

		"regId":"PR00000001",
		"pharmacyNotes":"zoxy \n movex"
	}


__TO WRITE PRESCRITION__

localhost:8084/v1/doctor/prescription/ __POST METHOD__


	{

		"regId":"PR00000006",
		"presentillness":"D1\nd2\nd3",
		"physicalExamination":"p1p\n2p3p1p\n2p3p2p3p1p\n2p3p1p\n2p32p3p1p\n2p3p1p\n2p3p1p\n2p3p1p\n2p3p1p\n2p1p\n2p3p1p\n2p3p1p\n21p\n2p3p1p\n2p3p1p\n2p3p1p\n2p3p1p\n2p3p1p\n2p3p1p\n2p32p3p1p\n2p3p1p\n2p3p1p\n2p3p1p\n2p3p1p\n2",
		"investigationAdviced":"p1np\n2np3",
		"medicationNameDosage":"p1\np\np3",
		"patientInstruction":"p1\np2\np3",
		"recommendation":"p1\np2\np3",
		"docId":"UB00001"

	}

__TO GET PRESCRIPTION__

localhost:8084/v1/doctor/prescription/{REG ID} __GET METHOD__


__-------------------------------------------------------------------Admin Doctor view------------------------------------------------------------------------------__

_To get Doctor list_

localhost:8084/v1/admin/getlist getmethod

_To get All the year_

localhost:8084/v1/admin/getyears get method for years


_Get patient count for particular year_

localhost:8084/v1/admin/getmonthwise/UB00001 postmethod  

{
	
	"year":"2018"
}


_To Get patient for particular month of that year_

localhost:8084/v1/admin/getPatientpost  post method


{
	"userId":"UB00001",
	"year":"2019",
	"month":"01"
}

__-------------------------------------------------------------------Nurse------------------------------------------------------------------------------__

__To get ALL DETAILS__

localhost:8084/v1/nurse/getAll __GET METHOD__


__---------------------------------------------------------------------OSP Service---------------------------------------------------------------------__-------------------------------------------------------------------------SALES


localhost:8084/v1/osp/create get method for dropdown



localhost:8084/v1/osp/getcost post method

{
"serviceName":"Ward Charge"
}


localhost:8084/v1/osp/create post method
{"patientName":"abc", "mobile":908766, "refferedById":"mahesh-UB00001", "dob":"1996-02-14T12:12:10", 
"enteredDate":"2019-01-31T12:12:10",  "gender":"Male",   

"referenceNumber":"tytytyty",

"multiplePayment":[
							{	"payType":"Paid In KPHB",
								"amount":100
								
							}
								],

"refLaboratoryRegistrations":[ {

"serviceName":"2D ECHO",
"price":200,
"discount":10,
"quantity":2,
"amount":190
    
},
 {

"serviceName":"ANTI TPO ANTIBODIES",
"price":200,
"discount":10,
"quantity":2,
"amount":190
    
}]
}



__---------------------------------------------------------------------Adding services-------------------------------------------------------------------__

__Adding services__
`````````````````

localhost:8084/v1/service/getid  get method for dropdown



localhost:8084/v1/service/create post method

{
"serviceName":"SERUM ELECTROLYTES",
"insertedDate":"2019-02-08T12:00:00",
"addService":[
	
	{
		"patientType":"INPATIENT",
		"roomType":"DOUBLESHARING",
		"serviceType":"Lab",
		"cost":"200"
	},
	{
		"patientType":"INPATIENT",
		"roomType":"General Ward-Male",
		"serviceType":"Lab",
		"cost":"200"
	},
		{
		"patientType":"INPATIENT",
		"roomType":"General Ward-FeMale",
		"serviceType":"Lab",
		"cost":"200"
	},	{
		"patientType":"INPATIENT",
		"roomType":"EMERGENCY",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"INPATIENT",
		"roomType":"DayCare",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"INPATIENT",
		"roomType":"SINGLE SHARING",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"INPATIENT",
		"roomType":"NICU",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"INPATIENT",
		"roomType":"ADULT ICU",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"INPATIENT",
		"roomType":"PICU",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"INPATIENT",
		"roomType":"ISOLATION",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"INPATIENT",
		"roomType":"POST OP&PRE OP",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"OUTPATIENT",
		"roomType":"NOT APPLICABLE",
		"serviceType":"Lab",
		"cost":200
	},	{
		"patientType":"OSP",
		"roomType":"NOT APPLICABLE",
		"serviceType":"Lab",
		"cost":200
	}
	
	]
	
	
	
}
	
__Updating services__
`````````````````

localhost:8084/v1/service/update/LFT - LIVER FUNCTON TESTS/{serviceName} postmethod

{
	"serviceName":"LFT - LIVER FUNCTON TESTS",
	"department":"BIOCHEMISTRY",
	"addService":[
		{
		"serviceId":"SER000001",	
		"roomType":"DOUBLESHARING",
		"patientType":"INPATIENT",
		"cost":"250"
			
		},
		{
		"serviceId":"SER000002",
		"roomType":"NOT ALLOCATED",
		"patientType":"OUTPATIENT",
		"cost":"250"
		}
		
		]
	
	
	
}



localhost:8084/v1/service/getService/LFT - LIVER FUNCTON TESTS/{serviceName} getmethod for display of all services for service name

localhost:8084/v1/service/list get method for list of services
	
	

__---------------------------------------------------------------------Laboratory------------------------------------------------------------------------__


__TO GET VALUES AFTER PAGE LOADED__

localhost:8084/v1/lab/register __GET METHOD__

__To register and pay lab controller (new Controller)__

localhost:8084/v1/lab/register/patient

__Dicount In %__


	{

		"reg_id":"PR00000003",	
		"invoiceNo":"INV0000033",
      "paymentType":"cash",
	  "referenceNumber":"RTTYTUTU7878787",
		"refLaboratoryRegistrations":[
			{
				"serviceName":"THYROIDTEST",
				"discount":0
			}
		]
	}


localhost:8084/v1/lab/register/patient

__Dicount In Number__
{
	"reg_id":"IP00000001",	

  "referenceNumber":"RTTYTUTU7878787",
  		
	"multiplePayment":[
							{	"payType":"due",
								"amount":100
								
							}
								],
				
    "refLaboratoryRegistrations":[
        {
            "serviceName":"HCV I & II",
            "discount":-2,
            "amount":88
        },
         {
            "serviceName":"HCV I & II",
            "discount":-2,
            "amount":88
        }
    ]
}	
_Registering Services using CASH+CARD_

	{

		"reg_id":"PR00000005",	
	
      "paymentType":"Cash+Card",
	  "referenceNumber":"RTTYTUTU7878787",
      "multimode":[
      	{
      		"mode":"Cash",
      		"amount":"60"
      	},
      	{
      		"mode":"Card",
      		"amount":"26"
      	}
      	
      	],
		"refLaboratoryRegistrations":[
			{
				"serviceName":"SERUM ELECTROLYTES",
				"discount":2,
				"quantity":2,
				"amount":88
			}
		]
	}	

__To get lab meausre report for particular Service In Lab Admin View(Tracker in Admin view)__

localhost:8084/v1/lab/measure/{regId}/{MeasureNAme} __GET METHOD_
	
__TO GET COST FOR PARTICULAR SERVICE__

localhost:8084/v1/lab/service/BLOODTEST/PR00000009 __GET METHOD__

__TO GET MEASURES FOR PARTICULAR SERVICE FOR THAT PARTICULAR PATIENT'S AGE__

localhost:8084/v1/lab/report/BLOODTEST/PR00000003 __GET METHOD__

__TO ADD SERVICE FOR ONE PATIENT , USING MEASURES__

localhost:8084/v1/lab/service

	{
      "regId":"PR00000003",
      "serviceName":"BLOODTEST",
	  "comment":"",
      "refMeasureDetails":[
      	{
      		"measure":"WBC",
      		"value":1200
      	},
      	{
      		"measure":"RBC",
      		"value":1500
      	}]
	}
	
	
__CALCIUM SERUM__

	{
		"regId":"PR00000001",
	  "serviceName":"CALCIUM SERUM",
	  "comment":"hiiiiii \n how are you",
	  "refMeasureDetails":[
		{
			"measure":"SERUM CALCIUM(Ca+)",
			"value":"10.4"
		}
	  
		
		]
	}
	
__MAGNESIUM-SERUM__
	
	{
		"regId":"PR00000001",
	  "serviceName":"MAGNESIUM-SERUM",
	  "comments":"HIIII",
	  "refMeasureDetails":[
		{
			"measure":"SERUM MAGNESIUM",
			"value":"10.4"
		}
	  
		
		]
	}
	
	
__COMPLETE STOOL EXAMINATION__
	
	{
		"regId":"PR00000001",
	  "serviceName":"COMPLETE STOOL EXAMINATION",
	  "comments":"HIIII",
	  "refMeasureDetails":[
		{
			"measure":"COLOUR",
			"value":"GreenishYellow"
		},
			{
			"measure":"APPEARANCE",
			"value":"SemiSolid"
		},
		{
			"measure":"REACTION",
			"value":"Alkaline"
		},
		{
			"measure":"MUCOUS",
			"value":"Present"
		},
		{
			"measure":"BLOOD",
			"value":"NIL"
		},
		{
			"measure":"OVA",
			"value":"NIL"
		},
		{
			"measure":"CYSTS",
			"value":"NIL"
		},
		{
			"measure":"PUS CELLS",
			"value":"3-5"
		},
		{
			"measure":"Epithelial Cells",
			"value":"1-2"
		},
		{
			"measure":"FAT GLOBULES",
			"value":"NIL"
		},
		{
			"measure":"STARCH",
			"value":"NIL"
		},
		{
			"measure":"VEGTETABLE CELLS/FIBERS",
			"value":"NIL"
		},
		{
			"measure":"BACTERIA",
			"value":"NIL"
		},
		{
			"measure":"OTHERS",
			"value":"NIL"
		}
		
		]
	}

	
	
__Addinge measure for SERUM ELECTROLATE__

	{
	  "regId":"PR00000001",
	  "serviceName":"SERUM ELECTROLYTES",
		"comment":"",
	  "refMeasureDetails":[
		{
			"measure":"Sodium",
			"value":148
		},
		{
			"measure":"Potassium",
			"value":4
		},
		
			{
			"measure":"Floride",
			"value":110
		},
			{
			"measure":"Serum Creatinine",
			"value":1
		}
		
		]
	}
	
__To pay for the lab service__

localhost:8084/v1/lab/register/pay/{Regid}/{Invoice}
      
	  
AFTER COMPLETING REPORT , ADMIN HAS TO CHANGE STATUS FROM NOT-COMPLETED TO COMPLETED

__TO GET NOT-COMPLETED DATA__

localhost:8084/v1/lab/adminLab/approve __GET METHOD__

__TO CHANGE STATUS FROM NOT-COMPLETED TO COMPLETED

SELECT THE ROW WHICH NEED TO BE APPROVED__

localhost:8084/v1/lab/adminLab/approve PUT METHOD


__Adding measures for thaat particular service__

__SERUM ELECTROLYTE__
	
		{
			"regId":"PR00000001",
			  "serviceName":"SERUM ELECTROLYTES",
			  "comment":"",
			  "refMeasureDetails":[
				{
					"measure":"Sodium",
					"value":148
				},
				{
					"measure":"Potassium",
					"value":4
				},
				
					{
					"measure":"Floride",
					"value":110
				},
					{
					"measure":"Serum Creatinine",
					"value":1
				}
				
				]
		}
		
		
__LFT LIVER FUNCTION__
	
	{
			"regId":"PR00000001",
			"comment":"",
		  "serviceName":"LIFT LIVER FUNCTON TEST",
		  "refMeasureDetails":[
			{
				"measure":"Total Bilirubin",
				"value":3
			},
			{
				"measure":"Direct Bilirubin",
				"value":1
			},
			
				{
				"measure":"Indirect Bilirubin",
				"value":2
			},
				{
				"measure":"SGPT(ALT)",
				"value":19
			},
			{
				"measure":"SGOT",
				"value":23
			},
				{
				"measure":"Alkaline Phosphatase",
				"value":122
			},
				{
				"measure":"Total Proteins",
				"value":7
			},
				{
				"measure":"Serum Albumin",
				"value":3
			},
		  {
				"measure":"Serum Globulin",
				"value":3
			},
		   {
				"measure":"Albumin/Globulin Ratio",
				"value":1
			}
		  
			
			]
		}

__RENAL FUNCTION TEST__
		
		
		{
			"regId":"PR00000005",
			"comment":"",
		  "serviceName":"RENAL FUNCTION TEST(RFT)",
		  "refMeasureDetails":[
			{
				"measure":"Random Blood Sugar",
				"value":86
			},
			{
				"measure":"Corresponding Urine Sugar",
				"value":0
			},
			
				{
				"measure":"Blood Urea",
				"value":2
			},
				{
				"measure":"Serum Creatinine",
				"value":1
			},
			{
				"measure":"Sodium",
				"value":148
			},
			{
				"measure":"Potassium",
				"value":4
			},
			
				{
				"measure":"Chloride",
				"value":110
			}
			
			
			]
		}
		
		
__BLOODTEST__
	
	
	{
			"regId":"PR00000001",
			"comment":"",
		  "serviceName":"BLOODTEST",
		  "refMeasureDetails":[
			{
				"measure":"Haemoglobin",
				"value":7
			},
			{
				"measure":"RBC",
				"value":2
			},
			
				{
				"measure":"WBC",
				"value":21
			},
			{
				"measure":"Neutrophils",
				"value":80
			},
				{
				"measure":"Lymphocytes",
				"value":16
			},
				{
				"measure":"Eosinophils",
				"value":2
			},
				{
				"measure":"Monocytes",
				"value":2
			},
				{
				"measure":"Basophils",
				"value":0
			},
				{
				"measure":"PLATELET COUNT",
				"value":1
			}
			
			
			]
		}
		
__WIDAL TEST__
		
		
		{
		  "regId":"PR00000001",
		  "serviceName":"WIDAL TEST",
		"comment":"",
		  "refMeasureDetails":[
			{
				"measure":"S.TYPHI O",
				"value":"1 IN 40 DILLUTION"
			},
			{
				"measure":"S.TYPHI H",
				"value":"1 IN 40 DILLUTION"
			},
			
				{
				"measure":"S.PARA TYPHI H",
				"value":"1 IN 20 DILLUTION"
			},
				{
				"measure":"S.PARA TYPHI BH",
				"value":"1 IN 20 DILLUTION"
			}
			
			]
		}
		
__C-Reactive protien__
		
		{
		  "regId":"PR00000001",
		  "serviceName":"C-reactive protien",
			"comment":"",
		  "refMeasureDetails":[
			{
				"measure":"C-reactive protien",
				"value":"0.4"
			}
			
			]
		}


__MALARIA TEST__
		
		{
			"regId":"PR00000001",
			"serviceName":"Malaria Test",
			"comment":"",
			"refMeasureDetails":[
			{
				"measure":"Malaria Test",
				"value":"P.f-Negitive"
			}

			]
		}
		
__PROTHROMBIN TIME__
		
		
		{
		  "regId":"PR00000001",
		  "serviceName":"PROTHROMBIN TIME",
			"comment":"",
		  "refMeasureDetails":[
			{
				"measure":"Test",
				"value":"16.5"
			},
				{
				"measure":"Control",
				"value":"13.5"
			},
				{
				"measure":"PR",
				"value":"1.1"
			}
			,	{
				"measure":"INR",
				"value":"1.2"
			},
				{
				"measure":"Therapeutic Ranges",
				"value":""
			},	{
				"measure":"P.Ratio",
				"value":"2.4-4.0"
			},	{
				"measure":"INR",
				"value":"2-3"
			},	{
				"measure":"ACTIVATED PARTIAL THROMBOPLASIN TIME",
				"value":"27.0"
			},	{
				"measure":"Reference Range",
				"value":"20-40"
			}
			
			]
		}
		
__Lipid Profile__
		
		
			{
			  "regId":"PR00000001",
			  "serviceName":"Lipid Profile",
				"comment":"",
			  "refMeasureDetails":[
				{
					"measure":"Total Cholestral",
					"value":"151"
				},
					{
					"measure":"HDL Cholestral",
					"value":"30.2"
				},
					{
					"measure":"LDL Cholestral",
					"value":"97.2"
				},
					{
					"measure":"VLDL Cholestral",
					"value":"23.6"
				},
					{
					"measure":"Triglycerides",
					"value":"118"
				},
					{
					"measure":"Total cholest/HDL Cholesterol Ratio",
					"value":"5.0"
				},
						{
					"measure":"Triglycerides/HDL Cholesterol Ratio",
					"value":"3.9"
				}
				
				
				]
			}
			
		
__BILIRUBINTEST__
		
		
		{
		  "regId":"PR00000001",
		  "serviceName":"BILIRUBINTEST",
			"comment":"",
		  "refMeasureDetails":[
			{
				"measure":"TOTAL BILIRUBIN",
				"value":"0.7"
			},
				{
				"measure":"DIRECT BILIRUBIN",
				"value":"0.15"
			},
				{
				"measure":"INDIRECT BILIRUBIN",
				"value":"0.58"
			}
			
			
			]
		}

__URINETEST__
		
		
		{

			"regId":"PR00000001",
			  "serviceName":"URINETEST",
				"comment":"",
			 "refMeasureDetails":[
				{
					"measure":"Color",
					"value":"Pale Yellow"
				},
				{
					"measure":"Appearance",
					"value":"Clear"
				},
					{
					"measure":"Ph",
					"value":"6.0"
				},	{
					"measure":"Specific Gravity",
					"value":"1.010"
				},	{
					"measure":"Albumin",
					"value":"Nil"
				},	{
					"measure":"Sugar",
					"value":"Nil"
				},	{
					"measure":"Bile Salts",
					"value":"--"
				},	{
					"measure":"Bile Pigments",
					"value":"--"
				},{
					"measure":"Pus Cells",
					"value":"02-03/HPF"
				},{
					"measure":"Epithelial Cells",
					"value":"01-01/HPF"
				},{
					"measure":"RBC",
					"value":"Nil"
				},
				{
					"measure":"Crystals",
					"value":"Nil"
				},{
					"measure":"Casts",
					"value":"Nil"
				},{
					"measure":"Amorpous Material",
					"value":"Nil"
				},{
					"measure":"Others",
					"value":"Nil"
				}
				
				]
		}
		
		
__2 D ECHO REPORT__

			{
			"regId":"PR00000001",
			  "serviceName":"2 D ECHO REPORT",
			  "comment":"hiiiiii \n how are you",
			  "refMeasureDetails":[{}

				
				]
			}

__ULTRA SOUND WHOLE ABDOMEN__

		{
			"regId":"PR00000001",
			"serviceName":"ULTRA SOUND WHOLE ABDOMEN",
			"comment":"hiiiiii \n how are you",
			"refMeasureDetails":[
			{
			"measure":"Liver",
			"value":"Normal in size liver 65mm  and normal echostructure Normal in size liver 65mm  and normal echostructure"
			},
			{
			"measure":"Gall bladder",
			"value":"Well distended."
			},

			{
			"measure":"Spleen",
			"value":"Normal in size spleen 42mm and echotexture"
			},
			{
			"measure":"Pancreas",
			"value":"normal in size and echotexture"
			},
			{
			"measure":"Right kidney",
			"value":"Measure 45 * 20mm."
			},
			{
			"measure":"Left kidney",
			"value":"Measure 46 * 22mm."
			},
			{
			"measure":"Urinary bladder",
			"value":"Well distended."
			},
			{
			"measure":"Bowels",
			"value":"Normal."
			}


			]
		}

__----------------------------------------------------------Final billing for patients------------------------------------------------------------__

localhost:8084/v1/bill/charge/discharge/{id}
	{

"amount":400, 
"discount":100, 
"returnAmount":1000,
"referenceNumber":"RTYYTYU",
					"multiplePayment":[
							{	"payType":"Cash",
								"amount":250
								
							},{	"payType":"Card",
								"amount":250
								
							},{	"payType":"Due",
								"amount":250
								
							}
								]
								


	}

localhost:8084/v1/bill/charge/pay/{regNo}

	
	{
		  "regId":"PR00000003",
		  
		  "refBillDetails":[
			{
				"amount":50,
				"chargeName":"LIPID PROFILE",
				"quantity":2,
				"dicount":0,
				"mrp":20,
				"netAmount":46
			},
			{
				"amount":50,
				"chargeName":"XRAY",
				"quantity":2,
				"dicount":0,
				"mrp":20,
				"netAmount":46
			}]
	}
	
localhost:8084/v1/bill/charge/discharge/PR00000010


{
	"mode":"cash",
	"amount":"1900",
	"referenceNumber":"RTYYTYU",
	
}

localhost:8084/v1/bill/approximate/{regId}


_---------Update bill------------_

localhost:8084/v1/bill/charge/update/{regId} post method


	{
		"updateCharge":[
			
			
			
			
				{
					"chargeBillId":"CB0000021",
				"mrp":120,
				"chargeName":"Ward Charge",
				"quantity":3,
				"discount":0,
				"netAmount":360
			},
			{"chargeBillId":"CB0000022",
				"mrp":3000,
				"quantity":5,
				"chargeName":"Anastesya",
				"discount":0,
				"netAmount":360
			},
						{
							"chargeBillId":"CB0000023",
				"mrp":"1111300",
				"chargeName":"Ward Charge",
				"quantity":"2",
				"discount":"10",
				"netAmount":"5090"
			},
			{"chargeBillId":"CB0000024",
				"mrp":"310",
				"quantity":"6",
				"chargeName":"Anastesya",
				"discount":"20",
				"netAmount":"14080"
			}]	}


_----------------------------------------------------------Pharmacy due bill-----------------------------------------------------------_


_To get due bill for particular patient based on umr_

 localhost:8084/v1/due/get/{UMR}  GET method

_To get particular method based on umr_
	
localhost:8084/v1/due/get/create/{umr} postmethod

{
	
	"dueType":"Pharmacy"
}


_To pay for due bill_

localhost:8084/v1/due/duepay/{BillNO}  postmethod

{

"dueType":"Pharmacy",
"mode":"Cash+card",
"amount":400,
"multimode":[
							{
								"mode":"cash",
								"amount":"100"
							},
							{
								"mode":"card",
								"amount":"300"
							}]

}
	
	

_Fetching record based on Reg No_	
localhost:8084/v1/due/get/details/{regNo}

_Based on reg_no
localhost:8084/v1/due/get/createdetails/{regNo} post  method

{

"dueType":"Pharmacy"
}
_----------------------------------------------------------Outpatient Lab Registering-------------------------------------------------_

_To get Page info_

localhost:8084/v1/lab/opservice { Get method }


_To register Service_

localhost:8084/v1/lab/opservice { Post method }

{
		  "regId":"PR00000006",
		  "paymentType":"Cash",
		  "refBillDetails":[
			{
				"amount":400,
				"chargeName":"Anastesiya",
				"quantity":1,
				"discount":10,
				"netAmount":385
			}
			
			]
}

__----------------------------------------------------------Bed allocation-------------------------------------------------------------__

__Bed Allocation For Nurse__

	{
	"patId":"PR00000002",
	"bedName":"udhbed7"
	}


	
	
__----------------------------------------------------------User wise shift summary---------------------------------------------------------__
	

{
	"fromDate":"2018-11-10 22:47:14",
	"toDate":"2018-12-30 17:02:27",
	"soldBy":"Dr Nagesh revanakar"
}


__----------------------------------------------------------Pharmacist Stock summary------------------------------------------------------------__

__----------------------------------------------------------All reports------------------------------------------------------------__

localhost:8084/v1/sales/getReport

{
	"fromDate":"2019-02-22",
	"toDate":"2019-02-22",
	"fromTime":"12:00:00 PM",
	"toTime":"01:20:00 PM",
	"soldBy":"Dr Nagesh revanakar-UB00001",
	"reportName":"UserWiseIpOpDetailedPatientWise"
}

__-------------------------------------------------------------Security-------------------------------------------------------------------------__

	{

	"usernameOrEmail":"UB00005",
	"password":"rakesh"

	}

__-------------------------------------------------------------Voucher-------------------------------------------------------------------------__
	
	
__Voucher__

FOR PAGE LOAD INFO

localhost:8084/v1/voucher/create __GET METHOD__


localhost:8084/v1/voucher/create __POST METHOD__

	
	{
    "bank":"voucher",
    "checkDate":"2019-12-06T06:11:20",
    "paymentType":"ca",
    "checkNo":"CH",
    "remarks":" pt:praveen consultationarestrdyftugihoijiusyfryydrfuyrdfgy8wedgscfygds8yfgywsdgfwedysgfyugofuygfy",
    "voucherAmount":900,
    "paymentDate":"2019-12-06T06:11:20",
    "paidTo":"Dr. Mahesh nagesh revanakar-UB00001",
    "otherName":"",
    "voucherType":"cashs"
	}
	
	
localhost:8084/v1/updatevoucher/VCP0000023   __put method__
	{
    "bank":"voucher",
    "checkDate":"2019-12-06T06:11:20",
    "paymentType":"ca",
    "checkNo":"CH",
    "remarks":" pt:praveen consultationarestrdyftugihoijiusyfryydrfuyrdfgy8wedgscfygds8yfgywsdgfwedysgfyugofuygfy",
    "voucherAmount":900,
    "paymentDate":"2019-12-06T06:11:20",
    "paidTo":"Dr. Mahesh nagesh revanakar-UB00001",
    "otherName":"",
    "voucherType":"cashs"
  
   }
	
__-------------------------------------------------------------Doctor Appointment-------------------------------------------------------------------------__

__Page Load Info__

localhost:8084/v1/appointment/create __GET METHOD__

__Insert Appointment__

localhost:8084/v1/appointment/create __POST METHOD__


_--------------------------------------------------------------OP Billing-----------------------------------------------------------------------------------_--------------------------------------------------------------OP



{
		  "regId":"PR00000002",
		  "paymentType":"Cash+Card",
		  						"multimode":[
							{
								"mode":"cash",
								"amount":"100"
							},
							{
								"mode":"card",
								"amount":"300"
							}],
		  "refBillDetails":[
			{
				"amount":220,
				"chargeName":"Anastesya",
				
				"discount":10,
				"netAmount":385
			}
			
			]
}

_-----------------------------------------------------------------Yesterday sales-----------------------------------------------_

localhost:8084/v1/sales/getprevious/salereport POST METHOD FOR PDF

localhost:8084/v1/sales/getprevious GETMETHOD

_----------------------------------------------------------OSP --------------------------------------------------------------------------_


{

"patientName":"Anand",
"mobile":908766,
"refferedById":"Dr. Nikhil Ambagade-UB00001",
"dob":"1996-02-14T12:12:10",
"enteredDate":"2019-01-31T12:12:10",
"paymentType":"Due",
"gender":"Male",
"refLaboratoryRegistrations":[
	{
		
	"serviceName":"Ward Charge",
	"price":200,
	"quantity":2,
	"discount":10,
	"amount":390
		
	}
	
	]
}

localhost:8084/v1/osp/findAll getmethod for details
  


localhost:8084/v1/osp/pdf/{ospServiceId}   getmethod for pdf


_-------------------------------------------------------------ONLINE WARD ISSUE-----------------------------------------------------_-----------------------------------------------------------------Yesterday


_To save ward issue_

localhost:8084/v1/sales/ward/create _POST METHOD_


	{
		"location":"Miyapur",
		"name":"ICU",
		"total":250,
		"refSales":[
			{
				"medicineName":"Movex vnc",
				"mrp":5,
				"batchNo":"B2",
				"discount":10,
				"quantity":100,
				"amount":475,
				"gst":5,
				"expDate":"2028-12-13 07:00:20"
			
			}
			]
	}
	
_--------------------------------------------------------------Sales Return for IP Screen----------------------------------------------------------_

_To get List of sales for One patient_

localhost:8084/v1/sales/oneSalesList/{regID} _GET METHOD_



_------------------------------------------------------------------------Maven------------------------------------------------------------------_

mvn package
	or
mvn clean install

java -jar -Dspring.profiles.active=prod target/application.jar
java -jar -Xmx512m -Dspring.profiles.active=prod target/application.jar
 
