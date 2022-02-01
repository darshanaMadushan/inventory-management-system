$(".btn-info").on("click", function(){
	var id = $(this).parent().attr('id');
	
	$.ajax({
		type: "GET",
		url: "/api/getProductType",
		data: { "id" : id },
		success: function(data) {
			$("#updateProductTypeModalTitle").text("Update Product Type: " + data.categoryName);
			$("#updateProductTypeName").val(data.categoryName);
			$("#updateProductTypeSerialAjax").val(data.serial);
		},
		error: function(error) {
			console.log(error);
		}
	})
});

$("#updateProductTypeModalSave").on("click", function(){
	var serial = $("#updateProductTypeSerialAjax").val();
	var newCategoryName = $("#updateProductTypeName").val();
	
	$.ajax({
		type: "PUT",
		url: "/api/updateProductType",
		data: { serial : serial, categoryName : newCategoryName},
		success: function() {
			$("#productTypeAddSuccess").removeClass("d-none").addClass("d-block");
			location.replace("#productTypeAddSuccess");
			setTimeout(function(){
				$("#productTypeAddSuccess").removeClass("d-block").addClass("d-none");
				location.reload();
			}, 5000);
		},
		error: function(){
			$("#productTypeAddError").removeClass("d-none").addClass("d-block");
			setTimeout(function(){
				$("#productTypeAddError").removeClass("d-block").addClass("d-none");
			}, 5000);
		}
	})
});

$(".btn-warning").on("click", function(){
	if(confirm("Removing this Product Category Cant' be revised anyhow.\nClick OK to proceed.")) {
		var typeId = $(this).parent().attr("id");
		console.log(typeId);
		$.ajax({
			type: "DELETE",
			url: "api/removeProductType",
			data: { typeSerial : typeId}, 
			success: function() {
				$("#productTypeRemoveSuccess").removeClass("d-none").addClass("d-block");
				document.focus("#productTypeRemoveSuccess");
				setTimeout(function(){
					$("#productTypeRemoveSuccess").removeClass("d-block").addClass("d-none");
					location.reload();
				}, 3000);
			},
			statusCode: {
				400: function(){
					$("#productTypeRemoveError").text("There are Products registered under this category. Can't remove this category'");
					$("#productTypeRemoveError").removeClass("d-none").addClass("d-block");
					setTimeout(function(){
						$("#productTypeRemoveError").removeClass("d-block").addClass("d-none");
						$("#productTypeRemoveError").text("");
					}, 5000);
				},
			},
			error: function() {
				$("#productTypeRemoveError").removeClass("d-none").addClass("d-block");
				$("#productTypeRemoveError").text("Error in Product Category Deletion.");
				setTimeout(function(){
					$("#productTypeRemoveError").removeClass("d-block").addClass("d-none");
					$("#productTypeRemoveError").text("");
				}, 3000);
			}
		})		
	}
});