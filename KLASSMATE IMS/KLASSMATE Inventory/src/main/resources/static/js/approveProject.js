$(".btn-primary").on("click", function(){
	var projectId = $(this).parent().attr("id");
	if(confirm('Are you sure?')){
		$.ajax({
			type: "GET",
			url: "api/approveProject",
			data: {projectId: projectId},
			success: function(){
				console.log("changed the status");
				$("#approvalSuccessAlert").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#approvalSuccessAlert").removeClass("d-block").addClass("d-none");
					location.reload();
				}, 3000);
			},
			error: function(){
				console.log("error in change");
			}
		});
		
	}
})

$("#rejectProject").on("click", function(){
	var projectId = $("#modalProjectId").val();
	var reason = $("#rejectionReason").val();
	var username = $("#rejectionUserId").val();
	
	if(reason !== "") {
		$("#rejectProject").attr("data-dismiss", "modal");
		$.ajax({
			type: "PUT",
			url: "api/rejectProject",
			data: {projectId: projectId, reason: reason, username: username},
			success: function(){
				$("#approvalRejectionAlert").removeClass("d-none").addClass("d-block");
				$("#approvalRejectionAlert").append("Project has been rejected due to " + reason + ".");
				setTimeout(function(){
					$("#approvalRejectionAlert").removeClass("d-block").addClass("d-none");
					$("#approvalRejectionAlert").empty();
					location.reload();
				}, 5000)
			},
			error: function(error){
				console.log(error);	
			}
		})		
	} else {
		$("#reasonError").removeClass("d-none").addClass("d-block");
		setTimeout(function(){
			$("#reasonError").removeClass("d-block").addClass("d-none");
		}, 3000)
	}
})

$(".btn-secondary").on("click", function(){
	var projectId = $(this).parent().attr("id"); 
	$("#modalProjectId").val(projectId);
	$("#rejectionReason").focus();
	
});