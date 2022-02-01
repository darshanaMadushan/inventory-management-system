$(document).ready(function(){
	loadProjectDetails();
	$("#sortedTable").removeClass("d-block").addClass("d-none");
});

function loadProjectDetails(){
	var projectId = $("#projectId").val();
	if(projectId !== null){
		$.ajax({
			type: "GET",
			url: "api/getProject",
			data: { "id" : projectId },
			success: function(data) {
				/*console.log(data);*/
				$("#cardTitle").text("Requirement for Project: " + data.name);
				$("#projectName").text(data.name);
				$("#projectDescription").text(data.description);
				$("#projectQuantity").text(data.quantity);
				$("#descriptionForProject").text("You need to allocate products for " + data.quantity + " quantities.");
			}, 
			error: function(error) {
				console.log(error);
			}
		})
	}	
}

$("#sendToWorkshopButton").on("click", function(){
	if(confirm("This project will goto the workshop if you confirmed.\nClick OK to confirm..")) {
		var projectName = $("#projectName").text();
		console.log(projectName);
		$.ajax({
			type: "GET",
			url: "api/sendToWorkshopProject",
			data: {"projectName": projectName},
			success: function() {
				/*console.log(data);*/
				$("#projectSentToWorkshopSuccess").removeClass("d-none").addClass("d-block");
				setTimeout(function() {
					$("#projectSentToWorkshopSuccess").removeClass("d-block").addClass("d-none");
					location.replace("/projects?inventoryProjects")
				}, 5000);
			}, 
			error: function(error){
				console.log(error);
				$("#projectSentToWorkshopError").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#projectSentToWorkshopError").removeClass("d-block").addClass("d-none");
				}, 5000);
			}
		})
	}
})

$("#scanInput").on("input", function(){
	var value = $(this).val();
	$.ajax({
		type: "GET",
		url: "api/getProduct/" + value ,
		success: function(product){
			/*console.log(product);*/
			$("#scannedProductName").text(product.productName);
			$("#scannedProductSerialNumber").text(product.serialNumber);
			$("#scannedProductType").text(product.type.categoryName);
			$("#scanSendToWorkshop").prop('disabled', false);
		},
		error: function(){
			/*console.log(error);*/
			$("#scannedProductName").text("Product Not Found");
			$("#scannedProductSerialNumber").text("Product Not Found");
			$("#scannedProductType").text("Product Not Found");
			$("#scanSendToWorkshop").prop('disabled', true);
		}
	});
})

$("#scanSendToWorkshop").on("click", function(){
	var id = $("#scannedProductSerialNumber").text();
	$.ajax({
		type: "GET",
		url: "api/sendToWorkshopProduct",
		data: {"id" : id},
		success: function(){
			alert("Product Sent to the workshop Succefully.");
			location.reload();
		},
		error: function() {
			alert("Product is not found or not in the workshop");
			location.reload();
		},
	});
});

$(".btn-success").on("click", function(){
	var id = $(this).parent().attr("id");
	console.log(id);
	sendToWorkshopClick(id);
});

$("#prodctCategorySelect").on("change", function(){
	var category = $(this).val();
	if(category !== ""){
		$("#allDetailsTable").removeClass("d-block").addClass("d-none");
		$.ajax({
			type: "GET",
			url: "api/getProductsByStatusAndType",
			data: {
				status: 0,
				type: category,
			},
			success : function(data) {
				createProductsTableRows(data);
			},
		});		
	} else {
		$("#allDetailsTable").removeClass("d-none").addClass("d-block");
		$("#sortedTable").removeClass("d-block").addClass("d-none");
	}
});

function sendToWorkshopClick(id) {
	$.ajax({
		type: "GET",
		url: "api/sendToWorkshopProduct",
		data: {"id" : id},
		success: function(){
			alert("Product Sent to the workshop Succefully.");
			location.reload();
		},
		error: function() {
			alert("Product is not found or not in the workshop");
			location.reload();
		},
	});
}

function createProductsTableRows(data) {
	$("#sortedTableBody").empty();
	if(data.length > 0 ) {
		data.map(function(element){
			var tr = document.createElement('tr');
			var serialNumberTd = document.createElement("td");
			var productNameTd = document.createElement("td");
			var descriptionTd = document.createElement("td");
			var typeTd = document.createElement("td");
			var buttonTd = document.createElement("td");
			
			serialNumberTd.innerText = element.serialNumber;
			productNameTd.innerText = element.productName; 
			descriptionTd.innerText = element.description;
			typeTd.innerText= `${element.type.serial} - ${element.type.categoryName}`;
			
			var button = document.createElement("button");
			button.setAttribute("class", "btn btn-success");
			button.addEventListener("click", function(){
				sendToWorkshopClick(element.serialNumber);
			}); 
			button.innerText = "Send To Workshop";
			buttonTd.append(button);
			
			tr.append(serialNumberTd, productNameTd, descriptionTd, typeTd, buttonTd);
			$("#sortedTableBody").append(tr);
		})		
	} else {
		var errorTr = document.createElement("tr");
		var errorTd = document.createElement("td");
		errorTd.setAttribute("colspan", "5");
		errorTd.setAttribute("class", "text-danger text-center");
		errorTd.append("--No Data Available -- ");
		errorTr.append(errorTd);
		$("#sortedTableBody").append(errorTr);
	}
	$("#sortedTable").removeClass("d-none").addClass("d-block");
}