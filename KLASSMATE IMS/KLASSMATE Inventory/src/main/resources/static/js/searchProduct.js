
$('table .btn').on('click', function() {
	var serialNumber = $(this).parent().attr('id');
	console.log(serialNumber);
	$('#assembleToProductModal #serialNum').val(serialNumber);
	console.log($('#serialNum').val());
})

$("#searchProduct").on("click", function(){
	$("#productDetailsTable").removeClass("d-block").addClass("d-none");
	var serial = $("#searchSerial").val();
	$.ajax({
		type: "GET",
		url: "/api/getProduct/" + serial,
		success: function(product){
			$("#serialNumber").text(product.serialNumber);
			$("#headingSerial").text(product.serialNumber);
			$("#productName").text(product.productName);
			$("#description").text(product.description);
			$("#type").text(product.type.categoryName);
			$("#status").text(product.status);
			$("#productDetailsTable").removeClass("d-none").addClass("d-block");
			
			if(product.status === "ALLOCATED") {
				$("#status").addClass("text-success");	
				$("#allocatedProductSerialNumberData").text(product.assembledTo.serialNumber);
				$("#allocatedProductNameData").text(product.assembledTo.productName + " -" + product.assembledTo.description);
				$("#allocateProductDetailsTable").removeClass("d-none").addClass("d-block");
			} else {
				$("#allocatedProductSerialNumberData").text("");
				$("#allocatedProductNameData").text("");
				$("#allocateProductDetailsTable").removeClass("d-block").addClass("d-none");
			}
		},
		error: function(error){
			$("#productSearchError").removeClass("d-none").addClass("d-block");
			if(error.status == 404) {
				$("#errorStatus").text("Error " + error.status + " - Requested product not found");	
			} else if (error.status == 500) {				
				$("#errorStatus").text("Error " + error.status + " - INTERNAL SERVER ERROR :( CONTACT ADMIN.");
			}
			setTimeout(function(){
				$("#productSearchError").removeClass("d-block").addClass("d-none");
				$("#errorStatus").text("");
				$("#searchSerial").val("");
				$("#searchSerial").focus();
			}, 5000);
		}
	});
});

$("#searchProduct").on("click", function() {
	$("#assembledDetails").removeClass("d-block").addClass("d-none");
	var serial = $("#searchSerial").val();
	$.ajax({
		type:"GET",
		url:"api/getAssembled/" + serial,
		success: function(products) {			
			for( var product of products) {
				createTableRows(product);
			}
			$("#assembledDetails").removeClass("d-none").addClass("d-block");
			
		},
		error: function(error) {
			console.log(error);
		}
	});
});



function createTableRows(product){
	var {serialNumber, productName, description, replaced} = product; 		
	var tr = document.createElement("tr");
	var firstTd = document.createElement("td");
	var secondTd = document.createElement("td");
	tr.append(firstTd, secondTd);
	
	var serialTr = document.createElement("tr");
	var serialTd = document.createElement("td");
	serialTd.setAttribute("colspan", "2");
	serialTd.innerHTML = "Included: <strong>" + serialNumber + "</strong>";
	serialTr.append(serialTd);
	
	if(replaced) {
		var span = document.createElement("span");
		span.classList.add("badge", "bg-warning");
		span.append("Replaced");
		serialTd.append(span);	
	}
	
	var productNameTr = document.createElement("tr");
	var productNameTd = document.createElement("td");
	var productNameLabel = document.createElement("td");
	productNameLabel.innerText = "Product Name";
	productNameTd.innerText = productName;
	productNameTr.append(productNameLabel, productNameTd);
	
	var descriptionTr = document.createElement("tr"); 
	var descriptionTd = document.createElement("td");
	var descriptionLabel = document.createElement("td")
	descriptionLabel.innerText = "Description";
	descriptionTd.innerText = description;
	descriptionTr.append(descriptionLabel, descriptionTd);
	 
	$("#assembleDetailsTableBody").append(serialTr,productNameTr, descriptionTr, tr);
	
}