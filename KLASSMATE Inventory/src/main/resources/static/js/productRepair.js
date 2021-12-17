$(document).ready( function(){
	$("#mainContainer").addClass("d-none");
	$("#productDetailsTable").addClass("d-none");
	$("#barcodeScanInput").focus();
	$("#assembledProductsDetailsAccordionContainer").addClass("d-none");
});

$("#barcodeScanInput").on("input", function() {
	var id = $(this).val();
	getProductDetailsAjax(id);
});

$("#confirmReplace").on("click", function(){
	var removingSerial = $("#removingSerial").val()
	var replacingSerial = $("#replacingSerial").val();
 
	$.ajax({
		type: "GET",
		url: "api/replaceProduct",
		data: {replacedBy: replacingSerial, removed: removingSerial},
		success: function(data){
			displayReplaceResponse("alert-success", 201, "repaired: " + data );
			getProductDetailsAjax(data);
		},
		statusCode: {
			400: function(error) {
				displayReplaceResponse("alert-danger", error, "Error 400");
			},
			404: function(error) {
				displayReplaceResponse("alert-danger", error, "Error 404");
			},
			500: function (error){
				displayReplaceResponse("alert-danger", error, "Error 500");
			},
		}
	});
	setTimeout(function(){
		$("#replacingSerial").val("");
	},5000);
});

$("#assembleIntoButton").on("click", function() {
	var serial = $("#productDetailsSerialNumber").text();
	var assemble = $("#insertProductField").val();
	$.ajax({
		type: "PUT",
		url: "/api/allocateProduct",
		data: {assembleToSerial: serial, assemble: assemble},
		success: function() {
			$("#assembleSuccessAlert").removeClass("d-none").addClass("d-block");
			getProductDetailsAjax(serial);
			$("#insertProductField").val("");
			setTimeout(function(){
				$("#assembleSuccessAlert").removeClass("d-block").addClass("d-none");
			},3000)
		},
		error: function(error) {
			console.log(error);
			$("#assembleErrorAlert").removeClass("d-none").addClass("d-block");
			setTimeout(function(){
				$("#assembleErrorAlert").removeClass("d-block").addClass("d-none");	
			},3000);
		}
	})
});

$("#removeItemSerialButton").on("click", function(event){
	event.preventDefault();
	var removingSerial = $("#removeItemSerial").val();
	var fromProduct = $("#productDetailsSerialNumber").text();
	removeProduct(removingSerial, fromProduct);
})

function removeProduct(removingSerial, fromProduct) {
	$.ajax({
		type: "PUT",
		url: "/api/removeProduct",
		data: {
			remove : removingSerial,
			from : fromProduct,
		},
		success: function(){
			$("#removeProductSuccessAlert").removeClass("d-none").addClass("d-block");
			getProductDetailsAjax(fromProduct);
			$("#removeItemSerial").val("");
			setTimeout(function(){
				$("#removeProductSuccessAlert").removeClass("d-block").addClass("d-none");
			},3000)	
		},
		statusCode: {
			404: function(){
				removeProductAlertDisplay(404);
			},
			400: function(detail){
				removeProductAlertDisplay(400, detail.responseText);
			},
			500: function() {
				removeProductAlertDisplay(500);
			}
		},
	});
	
}

function removeProductAlertDisplay(error, detail = "") {
	switch(error) {
		case 404 :
			$("#removeProductErrorAlertContent").append("Error 404: " + removingSerial + " not found!");
			break;
		case 400:
			$("#removeProductErrorAlertContent").append("Error 400: " + detail.responseText);
			break;
		case 500: 
			$("#removeProductErrorAlertContent").append("Error 500: INTERNAL SERVER ERROR. CONTACT ADMIN");
			break;		
	}
	$("#removeProductErrorAlert").removeClass("d-none").addClass("d-block");
	setTimeout(function(){
		$("#removeProductErrorAlertContent").text("");
		$("#removeProductErrorAlert").removeClass("d-block").addClass("d-none");		
	}, 3000)
}

function getProductDetailsAjax(id) {
	$("#assembledProductDetailsTableBody").empty();
	$("#mainContainer").removeClass("d-none").addClass("d-block");
	$("#productDetailsTable").removeClass("d-none").addClass("d-block");
	$("#productDetailsSerialNumber").text("");
	$("#productDetailsProductName").text("");
	$("#productDetailsDescription").text("");
	$("#productDetailsStatus").text("");
	$("#allocatedProductDetailsCol").removeClass("d-block").addClass("d-none");	
	$("#addProductField").removeClass("d-block").addClass("d-none");
	$("#assembledProductDetailsAccordion").empty();
	$("#assembledProductsDetailsAccordionContainer").removeClass("d-block").addClass("d-none");
	
	$.ajax({
		type: "GET",
		url: "api/getProduct/"+id,
		success: function(result){
			$("#productDetailsSerialNumber").text(result.serialNumber);
			$("#productDetailsProductName").text(result.productName);
			$("#productDetailsDescription").text(result.description);
			$("#productDetailsStatus").text(result.status);
			$("#addProductField").removeClass("d-none").addClass("d-block");
			getAllAssembledProductDetails(result.serialNumber, 1);
			$(".text-danger").removeClass("d-block").addClass("d-none");
			
			if(result.status !== "FINISHED") {
				$("#addAndRemoveProductSection").removeClass("d-block").addClass("d-none");
			} else {
				$("#addAndRemoveProductSection").removeClass("d-none").addClass("d-block");
			}
		},
		error: function(){
			$(".text-danger").removeClass("d-none").addClass("d-block");
		},
	});
	
	$("#viewDetailsTable").removeClass("d-none").addClass("d-block");
	$("#repairTable").removeClass("d-block").addClass("d-none");
	$("#allTableContents").removeClass("d-none").addClass("d-block");
}

function displayReplaceResponse(alertType, error, displayMessage="") {
	if(error === 201) {
		document.getElementById("assembleProductResponseDetail").innerHTML = "<strong>Successfully "+ displayMessage + "</strong>";	
	} else {
		document.getElementById("assembleProductResponseDetail").innerHTML = displayMessage + " : <strong>"+ error.responseText + "</strong>";		
	}
	$("#assembleProductResponse").removeClass("d-none").addClass("d-block");
	$("#assembleProductResponseAlert").addClass(alertType);
	setTimeout(function(){
		$("#assembleProductResponse").removeClass("d-block").addClass("d-none");
		document.getElementById("assembleProductResponseDetail").innerHTML = "";
		$("#assembleProductResponseAlert").removeClass(alertType);
	},3000);
}

function getAllAssembledProductDetails(serialNumber, type=0) {
	
	$.ajax({
		type: "GET",
		url: "api/getAssembled/" + serialNumber,
		success: function(result){
			if(type == 1) {
				for(var detail of result) {
					/*console.log(detail);*/
					/*createRowsToTable(detail);*/
					createAccordion(detail);
				}				
			} else {
				for(var data of result){
					/*var array = [data.serialNumber, data.productName+ "-" + data.description];*/
					/*createDataToTable(array);*/
					createAccordion(data);
					console.log(data)
				}
			}
		},
		error: function(error){
			console.log(error);
		},
	})
}

function replaceBtnClick(element) {
	var serial = element.parentNode.id;
	$("#removingSerial").val(serial);
}

function createAccordion(data) {
	
	var accordionTitle = "Included : " + data.serialNumber;
	var headingId = "hello-" + data.productName;
	var elementId = data.serialNumber;
	
	var mainDiv = document.createElement("div");
	mainDiv.setAttribute("class", "accordion-item");
	
	var heading = document.createElement("h2");
	heading.setAttribute("class", "accordion-header");
	heading.setAttribute("id", headingId);
	
	var headingButton = document.createElement("button");
	headingButton.setAttribute("class", "accordion-button collapsed");
	headingButton.setAttribute("type", "button");
	headingButton.setAttribute("data-bs-toggle", "collapse");
	headingButton.setAttribute("data-bs-target", "#" + elementId);
	headingButton.setAttribute("aria-controls", elementId);
	headingButton.append(accordionTitle)
	heading.append(headingButton);
	
	var accordionLabel = document.createElement("div");
	accordionLabel.setAttribute("id", elementId);
	accordionLabel.setAttribute("class", "accordion-collapse collapse");
	accordionLabel.setAttribute("aria-labelledby", headingId);
	accordionLabel.setAttribute("data-bs-parent", "#assembledProductDetailsAccordion");
	
	
	var accordionBody = document.createElement("div");
	accordionBody.setAttribute("class", "accordion-body");
	
	var table = document.createElement("table");
	table.setAttribute("class", "table");
	
	var tr = document.createElement("tr");

	var productNameTd = document.createElement("td");
	var descriptionTd = document.createElement("td");
	productNameTd.append(data.productName);
	descriptionTd.append(data.description);
	tr.append(productNameTd, descriptionTd);
	
	if (data.status === "REPLACED" ) {
		var span = "<span class='badge bg-secondary'>REPLACED</span>"
		headingButton.innerHTML += span;
		var replacedTd = document.createElement("td");
		replacedTd.append("Replaced with " + data.replacedWith.serialNumber);
		tr.append(replacedTd);
	}
	
	var removeButton = document.createElement("button");
	removeButton.classList.add("btn", "btn-primary");
	removeButton.innerText = "Remove item";
	removeButton.setAttribute("onclick", "removeElement(\'" + data.serialNumber + "\')");
	var removeButtonTd = document.createElement("td");
	removeButtonTd.append(removeButton);
	tr.append(removeButtonTd);
	
	
	table.append(tr);
	accordionBody.append(table);
	accordionLabel.append(accordionBody);
	mainDiv.append(heading, accordionLabel);
	
	/*table.append(createDatatoTable);*/
	
	$("#assembledProductDetailsAccordion").append(mainDiv);
	$("#assembledProductsDetailsAccordionContainer").removeClass("d-none").addClass("d-block");
}

function removeElement(removeSerial) {
	var mainSerial = $("#productDetailsSerialNumber").text();
	removeProduct(removeSerial, mainSerial);
}
/*function createRowsToTable(detail){
	var{serialNumber, description, productName, replaced} = detail;
	var serialTr = document.createElement("tr");
	var serialTd = document.createElement("td");
	/*serialTd.setAttribute("colspan", "2");
	serialTd.setAttribute("class", "text-primary");
	serialTd.setAttribute("id", serialNumber);
	serialTd.innerHTML = "Included: <strong>" + serialNumber + "</strong>";
	
	var repairButtonTd = document.createElement("td");
	serialTr.append(serialTd, repairButtonTd);
	
	if(replaced) {
		var span = document.createElement("span");
		span.classList.add("badge", "bg-secondary");
		span.append("replaced");
		repairButtonTd.append(span);
		
	} else {
		repairButtonTd.setAttribute("id", serialNumber);
		var repairButton = document.createElement("button");
		repairButton.setAttribute("class", "btn btn-primary");
		repairButton.setAttribute("type", "button");
		repairButton.setAttribute("rowspan", "3");
		repairButton.setAttribute("data-bs-toggle", "modal");
		repairButton.setAttribute("data-bs-target", "#replaceModal");
		repairButton.setAttribute("onclick", "replaceBtnClick(this)");
		repairButton.append("replace");
		repairButtonTd.append(repairButton);
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
	
	var tr = document.createElement("tr");
	var firstTd = document.createElement("td");
	var secondTd = document.createElement("td");
	tr.append(firstTd, secondTd);
	
	$("#assembledProductDetailsTableBody").append(serialTr,productNameTr, descriptionTr, tr);
	$("#allocatedProductDetailsCol").removeClass("d-none").addClass("d-block");	
}

function createDataToTable(dataArray) {
	var tr = document.createElement("tr");
	var button = document.createElement("button")
	button.setAttribute("class", "btn btn-primary");
	button.setAttribute("type", "button");
	button.append("replace");
	
	for(var data of dataArray) {
		var td = document.createElement("td");
		td.append(data);
		tr.append(td);
		if(data === dataArray[dataArray.length-1]){
			/*console.log(data);
			var buttonTd = document.createElement("td");
			button.setAttribute("id", dataArray[0]);
			button.setAttribute("data-bs-toggle", "modal");
			button.setAttribute("data-bs-target", "#replaceModal")
			buttonTd.appendChild(button);
			tr.append(buttonTd);
		}
	}
	
	/*$("#repairProductDetailsTableBody").append(tr);
}*/
