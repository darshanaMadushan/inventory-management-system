$(document).ready(function(){
	$("#sortedTable").removeClass("d-block").addClass("d-none");
})
$("#prodctCategorySelect").on("change", function(){
	var category = $(this).val();
	var status = $("#status").val();
	if(category !== ""){
		$("#allDetailsTable").removeClass("d-block").addClass("d-none");
		$.ajax({
			type: "GET",
			url: "api/getProductsByStatusAndType",
			data: {
				status: status,
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

function createProductsTableRows(data) {
	$("#sortedTableBody").empty();
	$("#sortedTableBody").focus();
	if(data.length > 0 ) {
		data.map(function(element){
			var tr = document.createElement('tr');
			var serialNumberTd = document.createElement("td");
			var productNameTd = document.createElement("td");
			var descriptionTd = document.createElement("td");
			var typeTd = document.createElement("td");
			
			serialNumberTd.innerText = element.serialNumber;
			productNameTd.innerText = element.productName; 
			descriptionTd.innerText = element.description;
			typeTd.innerText= `${element.type.serial} - ${element.type.categoryName}`;
			
			tr.append(serialNumberTd, productNameTd, descriptionTd, typeTd);
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