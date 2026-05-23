

console.log("this is the console page of java script");

const toggleSideBar = () => {
    const sidebar = document.querySelector(".sidebar");
    const contents = document.querySelector(".contents");

    if (sidebar.style.display === "none" || sidebar.style.left === "-250px") {
        sidebar.style.display = "block";
        sidebar.style.left = "0";
        contents.classList.remove("full-width");
    } else {
        sidebar.style.left = "-250px";
        setTimeout(() => {
            sidebar.style.display = "none";
        }, 300); // wait for slide animation
        contents.classList.add("full-width");
    }
};
const search =()=>{
    //console.log("searching...");
    let query = $("#search-input").val();
    
    if(query==''){
       // jab query empty hogi tab kuchh nahi karna hai
      $(".search-result").hide();
    }else{
        //search karunga
        console.log(query);
        // sending request to server

        let url = `http://localhost:8282/search/${encodeURIComponent(query)}`;
        fetch(url).then((response) =>{

            return response.json();
        }).then((data) =>{

            //data 
            //console.log(data);
            let text = `<div class='list-group'>`

            data.forEach((contact) => {
                text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action '>${contact.name} </a>`
            });
             text+= `</div>`

             $(".search-result").html(text);
              $(".search-result").show();
        });
    }
}
 
// first request to server to create order
const paymentStart = () => {
    console.log("payment started...");

     var amount = $("#payment_field").val();
     console.log(amount);
     if(amount == '' || amount == null){
        //alert("amount is required !!");
		swal("OOH!","amount is required !!", "error");
        return;
     }

     //code 
     // we will use ajax to send request to server to create order jquery
     $.ajax(
        {
        url: '/user/create_order',
        data: JSON.stringify({amount: amount, info: 'order_request'}),
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        success: function(response){

            console.log(response);
            if(response.status == "created"){
                // open payment form
                let options = {
                    "key": "rzp_test_SgQly9ICP0t15w", // Enter the Key ID generated from the Dashboard
                    "amount": response.amount, // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
                    "currency": "INR",
                    "name": "Smart Contact Manager",
                    "description": "Donation for project",
                    "image": "https://cdn-icons-png.flaticon.com/512/7265/726559.png",
                    "order_id": response.id, //This is a sample Order ID. Pass the `id` obtained in the response of Step 1
                    "handler": function (response){
                        console.log(response.razorpay_payment_id);
                        console.log(response.razorpay_order_id);
                        console.log(response.razorpay_signature);
                       // alert("congratulations !! payment successful !!");
                       updatePaymentOnServer(razorpay_payment_id,
						razorpay_order_id,
						paid
					);
						
                },
                    prefill:{
						name:"",
						email:"",
						contact:""
                            },
                    notes: {
                        "address": "anuj yadav @smart contact manager",
                    },
                    theme: {
                        "color": "#3399cc",
                    },
            };
                var rzp1 = new Razorpay(options);
                rzp1.on('payment.failed', function (response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata);
                   // alert("oops payment failed !!");
					
					
                });
                rzp1.open();

            }},
            // invoke the payment gateway
            error: function(error){
                console.log(error);
                alert("something went wrong !!");
                // code to display error message in the frontend
            }
        })};

       function updatePaymentOnServer (razorpay_payment_id,razorpay_order_id,status){
         $.ajax({

         url: '/user/update_order',
        data: JSON.stringify({payment_id : razorpay_payment_id, order_id : razorpay_order_id, status : status}),
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',

        success:function(response){
          swal("Good job!", "congratulations !! payment successful !!", "success");
        },
        error:function(error){
            swal(" OOH!"," Your payment successful  but we did not get on server , we will contact you as soon as possiable! ", "error");
        }

            }) };

