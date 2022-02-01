$(document).ready(function(){
	$("#projectDetailsDiv").addClass("d-none");
})

$(".btn-info").on("click", function(){
	var serial = $(this).parent().attr("id");
	$("#projectDetailsTableBody").empty();
	$("#assembledProductsDetailsRow").empty();
	$.ajax({
		type: "GET",
		url: "api/getProject?id="+ serial,
		success: function(data){
			/*console.log(data);*/
			$("#projectDetailsTitle").text("Details for Project: " + data.name );
			$("#projectDescription").text("Description: " + data.description);
			$("#projectQuantity").text("Requested Quantity: " + data.quantity);
			$("#projectDueDate").text("Due date: " + data.dueDate);
			$("#projectPriority").text(data.priority);
			$("#projectIdPara").text(data.id);
			if(data.priority === "HIGH") {
				$("#projectPriority").removeClass("bg-danger", "bg-secondary", "bg-info").addClass("bg-warning");
			} else if (data.priority === "MEDIUM") {
				$("#projectPriority").removeClass("bg-danger", "bg-warning", "bg-info").addClass("bg-secondary");
			} else if (data.priority === "URGENT"){
				$("#projectPriority").removeClass("bg-warning", "bg-secondary", "bg-info").addClass("bg-danger");
			} else {
				$("#projectPriority").removeClass("bg-danger", "bg-secondary", "bg-warning").addClass("bg-info");
			}
			
			for(var product of data.products) {
				createTableRows(product);
			}
			
		},
		error: function(error){
			console.log(error);
		}
	})
	window.location.href="#projectDetailsDiv";
	$("#projectDetailsDiv").addClass("d-block").removeClass("d-none");
})

$("#finishProject").on("click", function(){
	var projectId = $("#projectIdPara").text();
	if(confirm("This Project will be finished after Confirmation.\nClick OK to Confirm..")){
		/*console.log(projectId);*/
		$.ajax({
			type: "GET",
			url: "api/finishAssemble",
			data: {projectId : projectId},
			success: function(){
				$("#successFinish").removeClass("d-none").addClass("d-block");
				setTimeout(function(){	
					$("#successFinish").removeClass("d-block").addClass("d-none");
					location.reload();
				}, 5000)
			},
			error: function(){
				$("#errorFinish").removeClass("d-none").addClass("d-block");
				setTimeout(function(){	
					$("#errorFinish").removeClass("d-block").addClass("d-none");
				}, 5000);
			}
		});
	}
})

function viewDetailsOfProject(serial) {
	console.log(serial);
}


$("#confirmRevert").on("click", function(){
	if($("#revertDescription").val() !== ""){
		var description = $("#revertDescription").val();
		var projectId = $("#projectIdPara").text();
		var user = $("#revertUsername").val();
		$.ajax({
			type: 'PUT',
			url: "api/revertReview",
			data: {
				projectId: projectId,
				reason: description,
				username: user,
			},
			success: function(){
				$("#successRevert").removeClass("d-none").addClass("d-block");
				setTimeout(function(){	
					$("#successRevert").removeClass("d-block").addClass("d-none");
					location.reload();
				}, 5000)
			}, 
			error: function() {
				$("#errorRevert").removeClass("d-none").addClass("d-block");
				setTimeout(function(){	
					$("#errorRevert").removeClass("d-block").addClass("d-none");
				}, 5000)
			}
		})
	} else {
		$("#revertDescriptionAlert").removeClass("d-none").addClass("d-block");
		$("#confirmRevert").removeAttr("data-bs-dismiss", "modal");	
	}
})

$("#revertDescription").on("input", function(){
	if($(this).val() !== "") {
		$("#revertDescriptionAlert").removeClass("d-block").addClass("d-none");
		$("#confirmRevert").attr("data-bs-dismiss", "modal");		
	} else {
		$("#revertDescriptionAlert").removeClass("d-none").addClass("d-block");
		$("#confirmRevert").removeAttr("data-bs-dismiss", "modal");		
	}
});

function createTableRows(data){
	var tr = document.createElement("tr");
	var serialTd = document.createElement("td");
	var productNameTd = document.createElement("td");
	var noOfAssembledTd = document.createElement("td");
	var assembledDate = document.createElement("td");
	var productTypeTd = document.createElement("td");
	
	$.ajax({
		type: "GET",
		url: "api/getAssembled/"+data.serialNumber,
		success: function(data){
			noOfAssembledTd.textContent = data.length;
			/*console.log(data);*/
			createCardForAssembledProductDetails(data);
		},
		error: function(error){
			console.log(error);
		}
	})
	
	serialTd.textContent = data.serialNumber;
	productNameTd.textContent = data.productName;
	assembledDate.textContent = data.finishedDate; 
	productTypeTd.textContent = data.type.categoryName;
	
	tr.append(serialTd, productNameTd, noOfAssembledTd, assembledDate, productTypeTd);
	$("#projectDetailsTableBody").append(tr);
}

function createCardForAssembledProductDetails(data) {	
	var mainRow = document.createElement("div");
	mainRow.classList.add("card", "border", "border-secondary");
	var header = document.createElement("h5");
	header.classList.add("card-title");
	header.innerText = `Main Product: ${data[0].assembledTo.serialNumber}`;
	var cardHeader = document.createElement("div");
	cardHeader.classList.add("card-header");
	cardHeader.append(header);
	mainRow.append(cardHeader);
	var mainCardBody = document.createElement("div");
	mainCardBody.classList.add("card-body");
	var rowDiv = document.createElement("div");
	rowDiv.classList.add("row");
	
	for(var object of data){
		var mainDiv = document.createElement("div");
		mainDiv.classList.add("card", "col-md-4");
		
		var cardBody = document.createElement("div");
		cardBody.classList.add("card-body", "border", "border-warning", "rounded");
		var bodyContent = `<p>Serial Number: ${object.serialNumber}</p><p>Product Detail: ${object.productName} ${object.description}</p><p>Product Category: ${object.type.categoryName}</p>`;
		cardBody.innerHTML = bodyContent;
		
		var cardFooter = document.createElement("div");
		cardFooter.classList.add("card-footer");
		
		mainDiv.append(cardBody, cardFooter);
		rowDiv.append(mainDiv);
		mainCardBody.append(rowDiv);
	}
	mainRow.append(mainCardBody);	
	$("#assembledProductsDetails").append(mainRow);
}


var date1 = new Date("2012-12-12");