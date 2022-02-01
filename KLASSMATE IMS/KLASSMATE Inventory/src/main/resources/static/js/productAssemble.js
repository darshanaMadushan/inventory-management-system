$(document).ready(function(){
	$("#assembleDiv").removeClass("d-block").addClass("d-none");
	$("#allocatedProductsToProject").removeClass("d-block").addClass("d-none");
	$("#refreshPageButton").removeClass("d-block").addClass("d-none");
});

$('table .btn').on('click', function() {
	var serialNumber = $(this).parent().attr('id');
	console.log(serialNumber);
	$('#assembleToProductModal #serialNum').val(serialNumber);
	console.log($('#serialNum').val());
})

$("#searchSerial").on("input", function(){
	deleteTableRows();
	$("#allDetailsRow").removeClass("d-block").addClass("d-none");
});

$("#searchProduct").on("click", function(){
	$("#allDetailsRow").removeClass("d-none").addClass("d-block")
	deleteTableRows();
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
			console.log(product);	
			if(product.project === null) {
				$("#assembleProductInputForm").removeClass("d-none").addClass("d-block");
				allocateToProject(product);
				$("#searchSerial").blur();
				$("#searchForAssemble").focus();				
			} else {
				$("#assembleProductInputForm").removeClass("d-block").addClass("d-none");
				alert("This product is assembled to other project.. \nNot allowed to add into this project.")
			}
				
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
			/*console.log(product);*/
			if(product.status === "ON_ASSEMBLE") {
				allocateProductToProduct(product);
			}  else {				
				$("#assembleProductSearchError").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#assembleProductSearchError").removeClass("d-block").addClass("d-none");
					$("#searchForAssemble").val("");
					$("#searchForAssemble").focus();
					
				}, 5000);
			}
		},
		error: function(error){
			console.log(error);
		}
	});
});

$("#sendToReview").on("click", function(e){
	e.preventDefault();
	sendProjectToReview();
})

function sendProjectToReview() {
	if(confirm("This project will be sent to the Workshop Admin to review.\nClick OK to continue")) {
		var serialNumber = $("#serialNumber").text();
		var projectSelect = $("#projectSelect").val();
		$.ajax({
			url: "api/sendToReview",
			method: "GET",
			data: {projectId: projectSelect},
			success: function(result) {
				/*console.log(result);*/
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
}

function allocateProductToProduct(product) {
	var serial = $("#serialNumber").text();
	if(serial !== product.serialNumber){
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
		
	} else {
		$("#sameProductError").removeClass("d-none").addClass("d-block");
		setTimeout(function(){
			$("#sameProductError").removeClass("d-block").addClass("d-none");
		}, 5000)
	}
	
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

function showAssembleDiv() {
	var projectSelect = $("#projectSelect");
	$("#allocatedProductsToProjectBodyTableBody").empty();
	if(projectSelect.val() !== ""){
		$("#refreshPageButton").removeClass("d-none").addClass("d-block");
		$("#startAssembling").removeClass("d-block").addClass("d-none");
		$("#assembleDiv").removeClass("d-none").addClass("d-block");
		$("#searchSerial").focus();
		projectSelect.prop("disabled", true);
		
		$.ajax({
			type: "GET",
			url: "/api/getProjectDetails?id=" + projectSelect.val(),
			success: function(data){
				/*console.log(data);*/
				$("#requiredCount").text(data.quantity);
				$("#allocatedCount").text(data.products.length);
				$("#balanceCount").text(data.quantity - data.products.length);
				
				if(data.products.length !== 0){					
					for(var detail of data.products){
						viewAllocatedProducts(detail);
					}
					$("#allocatedProductsToProjectBodyTable").removeClass("d-none").addClass("d-block");
				} else {
					$("#sendToReview").prop("disabled", true);
					$("#allocatedProductsToProjectBodyTable").removeClass("d-block").addClass("d-none");
				}
			},
			error: function(error) {
				console.log(error);
			}
		})
		$("#allocatedProductsToProject").removeClass("d-none").addClass("d-block");
	} else {
		$("#projectSelectError").text("Please Select a Project");
		setTimeout(function(){
			$("#projectSelectError").text("");
		}, 1000)
	}	
}

function viewAllocatedProducts(data){
	var serialTd = document.createElement("td");
	var productNameTd = document.createElement("td");
	var productTypeTd = document.createElement("td");
	var tr = document.createElement("tr");
	serialTd.append(data.serialNumber);
	productNameTd.append(data.productName);
	productTypeTd.append(data.type.categoryName);
	tr.append(serialTd, productNameTd, productTypeTd);
	$("#allocatedProductsToProjectBodyTableBody").append(tr);
}

function allocateToProject(product) {
	if(product.project === null && product.status === "ON_ASSEMBLE") {
		var projectId = $("#projectSelect").val();
		$.ajax({
			type: "GET",
			url: "/api/allocateToProject",
			data: {
				project: projectId,
				serial: product.serialNumber,
			},
			success: function(){
				viewAllocatedProducts(product)
				$("#sendToReview").prop("disabled", false);
				var allocated = Number($("#allocatedCount").text());
				var balance = Number($("#balanceCount").text());
				$("#allocatedCount").text(allocated+1);
				$("#balanceCount").text(balance-1);
				
				$("#allocatedToProjectAlert").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#allocatedToProjectAlert").removeClass("d-block").addClass("d-none");
				}, 5000);
			},
			error: function(error){
				console.log(error);
			},
		});
	} else if(product.project.projectId == $("#projectSelect").val()) {
		$("#sameProjectAlert").removeClass("d-none").addClass("d-block");
		setTimeout(function(){
			$("#sameProjectAlert").removeClass("d-block").addClass("d-none");
		}, 5000);
	} else{
		$("#allocatedToProjectAlertWarning").removeClass("d-none").addClass("d-block")
		setTimeout(function(){
			$("#allocatedToProjectAlertWarning").removeClass("d-block").addClass("d-none")
		}, 5000);
	}
	/*console.log(product.project.projectId, Numbers($("#projectSelect").val()));*/
}
