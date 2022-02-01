$(document).ready( function(){
	$("#mainContainer").addClass("d-none");
	$("#productDetailsTable").addClass("d-none");
	$("#barcodeScanInput").focus();
	$("#insertProductField").on("input", function(){
		$("#insertProductFieldAlert").removeClass("d-block").addClass("d-none")
	})
});

$("#barcodeScanInput").on("input", function() {
	var id = $(this).val();
	getProductDetailsAjax(id);
});

$("#assembleIntoButton").on("click", function() {
	var serial = $("#productDetailsSerialNumber").text();
	var assemble = $("#insertProductField").val();
	if(assemble !== "") {
		$("#insertProductFieldAlert").removeClass("d-block").addClass("d-none");
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
		});
		$("#insertProductField").val("").focus();
	} else {
		$("#insertProductFieldAlert").removeClass("d-none").addClass("d-block");
	}
	
});

function getProductDetailsAjax(id) {
	$("#assembledProductDetailsTableBody").empty();
	$("#mainContainer").removeClass("d-none").addClass("d-block");
	$("#productDetailsTable").removeClass("d-none").addClass("d-block");
	$("#productDetailsSerialNumber").text("");
	$("#productDetailsProductName").text("");
	$("#productDetailsDescription").text("");
	$("#productDetailsStatus").text("");
	$("#productDetailsProject").text("");
	$("#allocatedProductDetailsCol").removeClass("d-block").addClass("d-none");	
	$("#addProductField").removeClass("d-block").addClass("d-none");
	$("#assembledProductDetailsAccordion").empty();
	$("#assembledProductsDetailsAccordionContainer").removeClass("d-block").addClass("d-none");
	$("#assembleDetailsRow").empty();
	
	$.ajax({
		type: "GET",
		url: "api/getProduct/"+id,
		success: function(result){
			$("#productDetailsSerialNumber").text(result.serialNumber);
			$("#productDetailsProductName").text(`${result.productName} - ${result.description}`);
			$("#productDetailsDescription").text(result.description);
			$("#productDetailsStatus").text(result.status);
			$("#productDetailsProject").text(result.project.name);
			$("#addProductField").removeClass("d-none").addClass("d-block");
			getAllAssembledProductDetails(result.serialNumber, 1);
			$(".text-danger").removeClass("d-block").addClass("d-none");
			console.log(result);
			
			if(result.project === null) {
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

function getAllAssembledProductDetails(serialNumber, type=0) {
	$.ajax({
		type: "GET",
		url: "api/getAssembled/" + serialNumber,
		success: function(result){
			if(type == 1) {
				for(var detail of result) {
					createAssembleDetailsDivs(detail)
				}				
			} else {
				for(var data of result){
					createAssembleDetailsDivs(data)
					console.log(data)
				}
			}
		},
		error: function(error){
			console.log(error);
		},
	})
}

function createAssembleDetailsDivs(data, isReplaced = false) {
	var divRow = document.createElement("div");
	divRow.classList.add("row");
	
	var serialDiv = document.createElement("div");
	serialDiv.classList.add("col-md-2");
	serialDiv.append(data.serialNumber);
	
	var descriptionDiv = document.createElement("div");
	descriptionDiv.classList.add("col-md-3");
	descriptionDiv.append(data.productName + " " + data.description);
	
	var replaceDiv = document.createElement("div");
	replaceDiv.classList.add("col-md-4");
	
	if(isReplaced || data.replaced) {
		var replacedBadge = document.createElement("div");
		replacedBadge.classList.add("text-danger")
		replacedBadge.append("Replaced with " + data.replacedWith.serialNumber );
		replaceDiv.append(replacedBadge);
	} else {
		var replaceInput = document.createElement("input");
		replaceInput.setAttribute("type", "text");
		replaceInput.setAttribute("class", "form-control");
		
		var replaceButton = document.createElement("button");
		replaceButton.setAttribute("type", "button");
		replaceButton.innerText = "Replace Item";
		replaceButton.classList.add("btn", "btn-success");
		
		var replaceInputCol = document.createElement("div");
		replaceInputCol.classList.add("col-sm-9");
		replaceInputCol.append(replaceInput);
		var replaceButtonCol = document.createElement("div");
		replaceButtonCol.classList.add("col-sm-2");
		replaceButtonCol.append(replaceButton);
		var replaceSmall = document.createElement("small");
		
		var replaceDivRow = document.createElement("div");
		replaceDivRow.classList.add("row");
		replaceDivRow.append(replaceInputCol, replaceButtonCol);
		replaceDiv.append(replaceDivRow);		

		replaceButton.addEventListener("click", function(){
			if(replaceInput.value !== "") {
				replaceBtnClick(data.serialNumber, replaceInput.value);		
			} else {
				replaceSmall.classList.add("text-danger");
				replaceSmall.innerText = "Please scan the replacing product Serial Number";
				replaceInputCol.append(replaceSmall);
			}
		});
	}
	
	var removeDiv = document.createElement("div");
	removeDiv.classList.add("col-md-2");
	
	var removeButton = document.createElement("button");
	removeButton.innerText = "Remove Item";
	removeButton.classList.add("btn", "btn-warning");
	removeButton.addEventListener("click", function(){
		removeElement(data.serialNumber);
	});
	
	removeDiv.append(removeButton);
	
	divRow.append(serialDiv, descriptionDiv, replaceDiv, removeDiv);
	
	$("#assembleDetailsRow").append(divRow);
}

function replaceBtnClick(element, element2 ) {
	console.log(`${element} || ${element2}`);
	$("#replacingError").empty();
	$.ajax({
		type: 'GET',
		url: 'api/replaceProduct',
		data: {
			replacedBy: element2,
			removed: element,
		},
		success: function(data) {
			console.log(data);
			$("#replacingSuccess").removeClass("d-none").addClass("d-block");
			createAssembleDetailsDivs(data);
			setTimeout(function(){
				$("#replacingSuccess").removeClass("d-block").addClass("d-none");
			}, 5000)
		},
		statusCode: {
			400: function(){
				$("#replacingError").removeClass("d-none").addClass("d-block");
				$("#replacingError").append("Product is not in the workshop. Can't assemble into this product. Contact admin or store keeper to get help!!")
				setTimeout(function(){
					$("#replacingError").removeClass("d-block").addClass("d-none");
					$("#replacingError").empty();
				}, 5000)
			},
			404: function() {
				$("#replacingError").removeClass("d-none").addClass("d-block");
				$("#replacingError").append("Entered Product serial number is not found. scan the serial number instead of typing ;)");
				setTimeout(function(){
					$("#replacingError").removeClass("d-block").addClass("d-none");
					$("#replacingError").empty();
				}, 5000)
			},
		},
		error: function(error) {
			console.log(error);
		}
	});
}
function removeElement(removeSerial) {
	var mainSerial = $("#productDetailsSerialNumber").text();
	if(confirm('Do you want to remove this item ?\nClick OK to continue..')) {
		removeProduct(removeSerial, mainSerial);		
	}
}

function removeProduct(removingSerial, fromProduct) {
	$("#assembleDetailsRow").empty();
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
			404: function(error){
				removeProductAlertDisplay(404);
				console.log(error);
			},
			400: function(detail){
				removeProductAlertDisplay(400);
				console.log(detail);
			},
			500: function() {
				removeProductAlertDisplay(500);
			}
		},
		error: function(error) {
			console.log(error);
		},
	});
}

function removeProductAlertDisplay(error) {
	switch(error) {
		case 404 :
			$("#removeProductErrorAlertContent").append("Error 404: " + removingSerial + " not found!");
			break;
		case 400:
			$("#removeProductErrorAlertContent").append("Error 400: Bad Request");
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
