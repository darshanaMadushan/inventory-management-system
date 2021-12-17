$(document).ready(function(){
	$("#productSearchInput").focus();
	$("#selectedProductDetailsTable").removeClass("d-block").addClass("d-none");
});

$("#productSearchInput").on("input", function(){
	var input = $("#productSearchInput").val();
	$("#allProductDetailsTable").removeClass("d-block").addClass("d-none");
	getAllProductDetailsByInput(input);	
		
	$("#selectedProductDetailsTable").removeClass("d-none").addClass("d-block")
	if(input === "") {
		$("#allProductDetailsTable").removeClass("d-none").addClass("d-block");
		$("#selectedProductDetailsTable").removeClass("d-block").addClass("d-none");
		$("#selectedProductDetailsTableBody").empty()
	}
	/*console.log($("#selectedProductDetailsTable").contents());*/
});

function getAllProductDetailsByInput(input) {
	$.ajax({
		type: "GET",
		url: "/api/getDetails",
		data: {input: input},
		success: function(data) {
			refreshTable();
			fetchDataToTable(data)
			/*console.log(data)*/
			$("#noDataRow").removeClass("d-block").addClass("d-none");
		},
		error: function() {
			refreshTable();
		},
	});
}

function fetchDataToTable(result){
	/*$("#selectedProductDetailsTableBody")*/	
	for (let product of result) {
		createTableRows(product);
	}
}

function refreshTable() {
	$("#selectedProductDetailsTableBody").empty();
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	tr.setAttribute("id", "noDataRow");
	td.setAttribute("colspan", "5");
	td.setAttribute("class", "text-danger text-center")
	td.append("--- No Data Available ---");
	tr.append(td);
	$("#selectedProductDetailsTableBody").append(tr);

}

function createTableRows(data){
	var tr = document.createElement("tr");
	var productSerialTd = document.createElement("th")
	productSerialTd.setAttribute("scope", "row");
	productSerialTd.append(data.serialNumber);
	
	var productNameTd = document.createElement("td")
	productNameTd.append(data.productName);
	
	var productDescriptionTd = document.createElement("td");
	productDescriptionTd.append(data.description);
	
	var productTypeTd = document.createElement("td");
	productTypeTd.append(data.description);
	
	var productStatusTd = document.createElement("td")
	productStatusTd.append(data.status);
	
	tr.append(productSerialTd,productNameTd, productDescriptionTd, productTypeTd, productStatusTd);
	$("#selectedProductDetailsTableBody").append(tr);
}