$(".btn-warning").on("click", function(){
	var id = $(this).parent().attr("id");
	if(confirm("Do you want to Delete this Project? \nOnce Deleted Cant' be revised.")){
		$.ajax({
			type: "PUT", 
			url: "/api/removeProject?id="+ id,
			success: function(){
				displayResponse(201, id);
			},
			statusCode: {
				404: function() {
					displayResponse(404)
				}, 
				500: function(){
					displayResponse(500);	
				},
			},
			error: function(error) {
				console.log(error);
			}
		})
	}
});

$(".btn-info").on("click", function(){
	var projectId = $(this).parent().attr("id");
	console.log(projectId);
	window.location.replace("/revertedProject?project=" +projectId );
})

function displayResponse(status, rowId=""){
	var responseAlert = document.getElementById("removeResponse");
	switch(status){
		case 201:
			responseAlert.textContent = "Successfully Removed Project";
			responseAlert.classList.add("alert-success");
			document.getElementById("id-"+rowId).remove();
			break;
		case 404: 
			responseAlert.textContent = "Project Details Not Found";
			responseAlert.classList.add("alert-danger");
			break;
		case 500: 
			responseAlert.textContent = "500: Internal Server Error. Contact Admin for more information.";
			responseAlert.classList.add("alert-danger");
			break;
	}
	responseAlert.classList.add("d-block");
	responseAlert.classList.remove("d-none");
	setTimeout(function(){
		responseAlert.textContent = "";
		responseAlert.classList.remove("alert-danger", "alert-success","d-block");
		responseAlert.classList.add("d-none");
	},5000)
}

$("#name").on("change", function(){
	var name = $("#name").val();
	$.ajax({
		type: "GET",
		url: "api/checkProjectName",
		data: {name: name},
		success: function(){
			console.log("data found")
			$("#projectNameExistError").removeClass("d-none").addClass("d-block");
			$(".btn-primary").prop("disabled", true);
		},
		error: function() {
			console.log("data not found");
		}
	})
})

$("#name").on("input", function(){
	$("#projectNameExistError").removeClass("d-block").addClass("d-none");
	$(".btn-primary").prop("disabled", false);
})