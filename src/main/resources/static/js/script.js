//fr small screeen. if sidebar is visble diable it and vice versa

const toggleSidebar =() => {
	if($(".sidebar").is(":visible")){
		
		//true 
		//band krna  hai
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
		
		}else{
			//false
			//show krna hai
	   $(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");	
		}
		
		
	};
	
	//search fn
	const search = () => {
		let query= $("#search-input").val();

		if(query==""){
			$(".search-result").hide();
		}else{
			console.log(query);

			//sending req to server
			let url=`http://localhost:8080/search/${query}`;

			//usinng promise 
			fetch(url)
			.then((response) => {  //. response wl cme in  this fn
				return response.json(); //cnvrtng response into json
			})
			.then((data) =>{ //gettng retrned data from abve in this data variable
				console.log(data);


				//sending this html data to showcontacts form
				let text= ` <div class='list-group'>`;

				data.forEach((contact) => {

					 text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`

				});
				text += `</div>`;

				$(".search-result").html(text); //cnvrtng rslt to html
				$(".search-result").show();
			});


		}
	};
	
//first request to server--- to create order
const paymentStart = () =>{
	
	console.log("payment started");
	var amount=$("#payment_field").val();
	console.log(amount);
	if(amount ==="" || amount ==null){
		swal("Oops", "amount is required !", "error");
		return;
	}
	
	//code
	//we will use ajax to send reuqest to server to create order ->jquery  add its link in base.html from jquery cdn
$.ajax({
			url:"/user/create_order", //url where to send request.. data ill reach at this url
			data:JSON.stringify({amount:amount,info:"order_request"}), //since its in  json we cant pass obj directly
			contentType:"application/json",
			type:"POST",
			dataType:"json",
			success:function(response){
				//invokes when success, callback fn
				console.log(response)
				if(response.status=='created')
				{
					let options={
						key:'rzp_test_XrL132tX1s33Hg',
						amount:response.amount,
						currency:'INR',
						name:'sanas App',
						description:'contact manager',
						image:'https://www.morisadam.com/assets/images/moris-adam-favicon.png',
						order_id:response.id,
						handler:function(response){
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature);
							console.log("payment done");
					//		alert("Payment Successful!");
							swal("done", "Payment Successful !", "success");
						},
						prefill:{
							name:"",
							email:"",
							contact:"",
						},
						notes:{
							address : "sana wani",
								},
					theme:{
						color:"#33399cc",
						
					},					
											
					};

					let rzp = new Razorpay(options);
					rzp.on('payment.failed', function(response){
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);
					//	alert("Alert. Payment Failed!");
						swal("failed", " Payment Failed! ", "error");
				});
					rzp.open();
				}
			},
			error:function(error){
				console.log(error)
				alert("something went wrong !")
			}
		}) 
		
};

