$(document).ready(function(){
	$("#searchSerial").focus();
});

$('table .btn').on('click', function() {
	var serialNumber = $(this).parent().attr('id');
	console.log(serialNumber);
	$('#assembleToProductModal #serialNum').val(serialNumber);
	console.log($('#serialNum').val());
})

$("#searchSerial").on("input", function(){
	deleteTableRows();
});

$("#searchProduct").on("click", function(){
	$("#productDetailsTable").removeClass("d-block").addClass("d-none");
	var serial = $("#searchSerial").val();
	$.ajax({
		type: "GET",
		url: "/api/getProduct/" + serial,
		success: function(product){
			/*console.log(product)*/
			$("#serialNumber").text(product.serialNumber);
			$("#headingSerial").text(product.serialNumber);
			$("#productName").text(product.productName);
			$("#description").text(product.description);
			$("#type").text(product.type.categoryName);
			$("#status").text(product.status);
			$("#productDetailsTable").removeClass("d-none").addClass("d-block");
			
			switch(product.status){
				case "IN_INVENTORY" :
					$("#status").removeClass("text-primary text-success").addClass("text-secondary");
					$("#allocatedProductSerialNumberData").text("");
					$("#allocatedProductNameData").text("");
					$("#allocateProductDetailsTable").removeClass("d-block").addClass("d-none");
					$("#assembleProductInputForm").removeClass("d-block").addClass("d-none");
					break;
				case "ON_ASSEMBLE":
					$("#status").removeClass("text-primary text-secondary").addClass("text-success");
					$("#allocatedProductSerialNumberData").text("");
					$("#allocatedProductNameData").text("");
					$("#allocateProductDetailsTable").removeClass("d-block").addClass("d-none");
					$("#assembleProductInputForm").removeClass("d-none").addClass("d-block");
					break;						
				case "ALLOCATED" :
					$("#status").removeClass("text-secondary text-success").addClass("text-primary");
					$("#allocatedProductSerialNumberData").text(product.assembledTo.serialNumber);
					$("#allocatedProductNameData").text(product.assembledTo.productName + " - " + product.assembledTo.description);
					$("#allocateProductDetailsTable").removeClass("d-none").addClass("d-block");
					$("#assembleProductInputForm").removeClass("d-block").addClass("d-none");
					break;
				case "FINISHED" :
					$("#status").removeClass("text-secondary text-success").addClass("text-primary");
					$("#allocatedProductSerialNumberData").text("");
					$("#allocatedProductNameData").text("");
					$("#allocateProductDetailsTable").removeClass("d-block").addClass("d-none");
					$("#assembleProductInputForm").removeClass("d-block").addClass("d-none");
					break;
			}
			$("#searchSerial").blur();
			$("#searchForAssemble").focus();
		},
		error: function(data){
			$("#productSearchError").removeClass("d-none").addClass("d-block");
			switch(data.status) {
				case 404:
					$("#errorDetails").text("Error " + data.status + " - Requested Product Not Found");
					break;
				case 500: 
					$("#errorDetails").text("Error " + data.status + " - INTERNAL SERVER ERROR :(  Contact Admin");
					break;
			}
			console.log(data);
			setTimeout(function(){
				$("#productSearchError").removeClass("d-block").addClass("d-none");
				$("#errorDetails").text("");
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
				createTableRows(product.serialNumber, product.productName, product.description);
			}
			$("#searchSerial").blur();
			$("#searchForAssemble").focus();
			$("#assembledDetails").removeClass("d-none").addClass("d-block");
		},
		error: function(error) {
			console.log(error);	
		}
	});
});

$("#assembleProduct").on("click", function(){
	var searchSerial = $("#searchForAssemble").val();
	
	$.ajax({
		type: "GET",
		url: "api/getProduct/" + searchSerial,
		success: function(product){
			/*console.log(product); */
			if(product.status === "ON_ASSEMBLE") {
				allocateProductToProduct(product);
				
			} else {
				$("#assembleProductSearchError").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#assembleProductSearchError").removeClass("d-block").addClass("d-none");
					$("#searchForAssemble").focus();
				}, 5000);
			}
		},
		error: function(error){
			console.log(error);
		}
	});
});

$("#finishAssemble").on("click", function(e){
	if(confirm("This products is going to finish the Assmble Process.\nClick OK to continue")) {
		var serialNumber = $("#serialNumber").text();
		e.preventDefault();
		$.ajax({
			url: "api/finishAssemble",
			method: "GET",
			data: {serial: serialNumber},
			success: function(result) {
				console.log(result);
				$('#assembleSuccessAlert').append(result + ": " + serialNumber);
				$('#assembleSuccess').removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#assembleSuccess").removeClass("d-block").addClass("d-none");
					$("#assembleSuccessAlert").text("");
					location.reload();
				},5000);
			},
			error: function(error) {
				console.log(error);
			}
		})
	}
})

function allocateProductToProduct(product) {
	var serial = $("#serialNumber").text();
	$.ajax({
		type: "PUT",
		url: "api/allocateProduct",
		data: { assembleToSerial: serial, assemble: product.serialNumber}, 
		success: function(result) {
			/*console.log(result);*/
			createTableRows(product.serialNumber, product.productName, product.description);
			$("#assembledDetails").removeClass("d-none").addClass("d-block");
			$('#assembleSuccessAlert').append(result + ": " + product.serialNumber + " to " + serial);
			$('#assembleSuccess').removeClass("d-none").addClass("d-block");
			setTimeout(function(){
				$("#assembleSuccess").removeClass("d-block").addClass("d-none");
				$("#assembleSuccessAlert").text("");
				$("#searchForAssemble").val("");
			},5000);
		},
		error: function(error) { 
			console.log(error);	
		}
	});
	
}

/*$("#assembleSuccessAlert").append("darshana madushan Samarasinghe");*/

function createTableRows(serialNumber, productName, description){		
	var tr = document.createElement("tr");
	var firstTd = document.createElement("td");
	var secondTd = document.createElement("td");
	tr.append(firstTd, secondTd);
	
	var serialTr = document.createElement("tr");
	var serialTd = document.createElement("td");
	serialTd.setAttribute("colspan", "2");
	serialTd.innerHTML = "Included: <strong>" + serialNumber + "</strong>";
	serialTr.append(serialTd);
	
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

function deleteTableRows(){
	var tableBody = document.getElementById("assembleDetailsTableBody");
	var child = tableBody.lastElementChild;
	while (child) {
		tableBody.removeChild(child);
		child=tableBody.lastElementChild;
	}
}