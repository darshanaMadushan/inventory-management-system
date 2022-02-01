$(document).ready(function(){
	let paramString = window.location.search.split("=")[1];
	loadProjectDetails(paramString);
	$("#projectDetailsError").addClass("d-block");
});

$("#sendToReview").on("click", function(){
	if(confirm("Are you sure that you have completed the project right way?? \nClick OK to send to workshop")) {
		var projectId = $("#projectId").val()
		console.log(projectId);
		$.ajax({
			type: "GET",
			url: "api/sendToReview",
			data: {projectId : projectId},
			success: function(){
				$("#projectSentToReviewSuccessAlert").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#projectSentToReviewSuccessAlert").removeClass("d-block").addClass("d-none");
					location.replace("/projects");
				}, 5000);
			},
			error: function(){
				$("#projectSentToReviewErrorAlert").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#projectSentToReviewErrorAlert").removeClass("d-block").addClass("d-none");
				},5000)
			}
		})
	}
});

function loadProjectDetails(projectId) {
	$.ajax({
		type: "GET",
		url: "api/getProject",
		data: {id : projectId},
		success: function(data){
			$("#projectId").val(data.id);
			$("#projectDetailsError").removeClass("d-block").addClass("d-none");
			$("#projectDetails").text(data.name + " - " + data.description);
			$("#rejectionReason").text(data.rejectionReason);
			$("#noOfProducts").text(data.products.length);
			createProductsCard(data.products);
		},
		error: function(error){
			$("#projectDetailsError").removeClass("d-none").addClass("d-block");
		},
	});
}

$("#addIntoProjectButton").on("click", function(){
	var serialNumber = $("#addIntoProjectInput").val();
	var projectId = $("#projectId").val();
	if(serialNumber !== ""){
		$.ajax({
			type: "GET",
			url: "/api/allocateToProject",
			data: {
				project: projectId,
				serial: serialNumber,
			},
			success: function(data){
				alert(`${serialNumber} product successfully added to the project`);
				createProductsCard([data]);
				$("#addIntoProjectInput").val("");
				$("#noOfProducts").text(parseInt($("#noOfProducts").text())+1);
			},
			statusCode: {
				400: function() {
					$("#addIntoProjectInputError").text("This Product has already added to another project. You can't alocate to this project");
				},
				404: function(){
					$("#addIntoProjectInputError").text("Product not found. scan the serial instead of typing ;)");
				}
			},
			error: function(){
				alert("Error in the operation. Please contact Admin.");
			}
		})
	} else {
		$("#addIntoProjectInputError").text("Please scan the barcode or type the serial number here.");
	}
});

$("#addIntoProjectInput").on("input", function(){
	$("#addIntoProjectInputError").text("");
});

function createProductsCard(productsOfProject) {
	productsOfProject.map(function(product){	
		cardForProductDetails(product);
	});
}

function cardForProductDetails(object){
	var assembledProductDetails = [];
	$.ajax({
		type: "GET", 
		url: "api/getAssembled/"+ object.serialNumber,
		success : function(data) {
			data.map(function(element){
				assembledProductDetails.push(element);
			});
		},
	});
	
	setTimeout(function(){
		var card = document.createElement("div");
		card.classList.add("container-fluid", "card", "border", "border-warning");
		
		var header = document.createElement("div");
		header.classList.add("card-header");
		
		var title = document.createElement("h6");
		title.classList.add("card-title");
		title.append(object.productName + " - " + object.description);
		
		var serialNumber = document.createElement("p");
		serialNumber.append("Serial Number : " + object.serialNumber);
		
		var titleCol = document.createElement("div");
		titleCol.classList.add("col-md-7");
		titleCol.append(title);
		
		var serialCol = document.createElement("div");
		serialCol.classList.add("col-md-5");
		serialCol.append(serialNumber);
		
		var headerRow = document.createElement("div");
		headerRow.classList.add("row");
		
		headerRow.append(titleCol, serialCol);
		
		var category = document.createElement("p");
		category.classList.add("category");
		category.innerText = "Total Assembled Items: " + assembledProductDetails.length;
		
		header.append(headerRow, category);
		
		var body = document.createElement("div");
		body.classList.add("card-body");
		
		var inlineForm = document.createElement("div");
		inlineForm.classList.add("row");
		
		var inputDiv = document.createElement("div");
		inputDiv.classList.add("col-md-8");
		
		var input = document.createElement("input");
		input.setAttribute("type", "text");
		input.classList.add("form-control");
		input.setAttribute("placeholder", "Scan the product barcode.")
		inputDiv.append(input);
		
		var small = document.createElement("small");
		small.classList.add("text-danger");
		small.append("Please scan the product's serial number'");
		
		var buttonDiv = document.createElement("div");
		buttonDiv.classList.add("col-md-4");
		buttonDiv.addEventListener("click", function(){
			if(input.value !== "") {
				addItemToProduct(object.serialNumber, input.value, body, input);				
			} else {
				inputDiv.append(small);
			}
		})
		
		input.addEventListener("change", function(){
			inputDiv.removeChild(small);	
		})
		var button = document.createElement("button");
		button.classList.add("btn", "btn-primary");
		button.innerText= "Add Item"
		buttonDiv.append(button);
		
		inlineForm.append(inputDiv, buttonDiv);
		body.append(inlineForm);
		
		if(assembledProductDetails.length > 0){
			var h6 = document.createElement("h6");
			h6.append("Assembled Product Details");
			body.append(h6);
		} 
		
		cardForAssembledProducts(assembledProductDetails, body);
			
		var footer = document.createElement("div");
		footer.classList.add("card-footer");
		
		var removeButton = document.createElement("button");
		var buttonAttributes = {
			type: "button",
			class: "btn btn-outline-danger",
		}
		for(var key in buttonAttributes) {
			removeButton.setAttribute(key, buttonAttributes[key]);
		}
		removeButton.innerText="remove from project";
		removeButton.addEventListener("click", function(){
			var projectId = $("#projectId").val();
			console.log(projectId, object.serialNumber);
			if(confirm(`Are you sure to remove the product '${object.serialNumber}' from project '${object.project.name}'\nClick OK to continue..`)) {
				removeFromProject(object.serialNumber, projectId);				
			}
		});
		
		footer.append(removeButton);
		
		card.append(header, body, footer);
		
		$("#productDetails").append(card);
	}, 100);

}

function addItemToProduct(mainProduct, newItem, append, input) {
	console.log(mainProduct, newItem);
	$.ajax({
		type: "PUT",
		url: "api/allocateProduct",
		data: {
			assembleToSerial: mainProduct, 
			assemble: newItem, 
		},
		success: function(result){
			console.log(result);
			cardForNewItem(result, append);
			alert(`Successfully added to the product: ${mainProduct}`);
			input.value="";
		},
		error: function() {
			alert(`error in product insertion : ${newItem} may not found or not in the workshop`)
			input.value="";
		}
	});
}

function cardForAssembledProducts(assembledProducts, container) {
	var template = document.createElement("div");
	template.classList.add("row");
	
	assembledProducts.map(function(element) {
		var card = document.createElement("div");
		card.classList.add("card", "col-sm-5", "border", "border-secondary", "rounded", "m-3");
		card.setAttribute("id", element.serialNumber);
		
		var body = document.createElement("div");
		body.classList.add("card-body");
		
		var productName = document.createElement("p");
		productName.innerText = "Product Name: " + element.productName + " - " + element.description;
		
		var serialNumber = document.createElement("p");
		serialNumber.innerText = "Serial Number: " + element.serialNumber;
		
		var description = document.createElement("p");
		description.append(element.type.categoryName);
		 
		body.append(productName, serialNumber, description);
		
		var footer = document.createElement("div");
		footer.classList.add("card-footer");
		
		if(element.replaced){
			var replaced = document.createElement("p");
			replaced.classList.add("text-warning");
			replaced.innerText = `REPLACED with ${element.replacedWith.serialNumber}`;
			body.append(replaced);			
		}
			
		var button = document.createElement("button");
		button.classList.add("btn", "btn-outline-danger");
		button.innerText = "Remove Item";
		button.addEventListener("click", function(){
			removeItem(element.serialNumber, element.assembledTo.serialNumber);
		});
		
		footer.append(button);
	
		card.append(body, footer);
		template.append(card)
		container.append(template);
	});
}

function removeItem(serial, mainSerial) {
	if(confirm(`Are you sure to remove this item${serial} from ${mainSerial}?`)){
			console.log(serial, mainSerial);
			$.ajax({
				type:"PUT",
				url: "api/removeProduct",
				data: {
					remove: serial, 
					from: mainSerial,
				},
				success: function(){
					alert("Successfully removed item.");
					$(`#${serial}`).remove();
				},
				error: function() {
					alert("Error in removing items.. Contact Admin for more information.");
				},
			})
	}
}

function cardForNewItem(product, append){
	
	var card = document.createElement("div");
	card.classList.add("card", "col-sm-5", "border", "border-secondary", "rounded", "m-3");
	card.setAttribute("id", product.serialNumber);
	
	var body = document.createElement("div");
	body.classList.add("card-body");
	
	var productName = document.createElement("p");
	productName.innerText = "Product Name: " + product.productName + " - " + product.description;
	
	var serialNumber = document.createElement("p");
	serialNumber.innerText = "Serial Number: " + product.serialNumber;
	
	var description = document.createElement("p");
	description.append(product.type.categoryName);
	 
	body.append(productName, serialNumber, description);
	
	var footer = document.createElement("div");
	footer.classList.add("card-footer");
	
	var button = document.createElement("button");
	button.classList.add("btn", "btn-outline-danger");
	button.innerText = "Remove Item";
	button.addEventListener("click", function(){
		removeItem(product.serialNumber, product.assembledTo.serialNumber);
	});
	
	footer.append(button);
	card.append(body, footer);
	append.firstChild.append(card);
}

function removeFromProject(productSerial, projectId){
	$.ajax(({
		type: "PUT",
		url: "api/removeFromProject",
		data: {
			productSerial : productSerial,
			projectId: projectId,
		},
		success: function(){
			alert("Successfully deleted from the project.");
			location.reload();
		},
		error: function() {
			alert("Error in operation. Contact Admin for more information.")
		}
		
	}))
}