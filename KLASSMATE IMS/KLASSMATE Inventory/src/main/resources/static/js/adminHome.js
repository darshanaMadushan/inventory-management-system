import {getCategoriesProductCountAJAX,
 		getStatusProductCount,
 		getLineChart
 		} from './charts.js';

$(document).ready(function(){
	$("#chartCount").text(0);
})

$("#allUserUpdate").on("click", function(e){
	e.preventDefault();
	var count = getUserCount();
	$("#userCount").text(count);
});

function getUserCount() {
	$.ajax({
		type: "GET",
		url: "/api/userCount",
		success: function(data){
			console.log(data);	
			return data;
		},
		error: function(error){
			console.log(error);
			return null;
		}
	});
}

getCategoriesProductCountAJAX();
getStatusProductCount();
getLineChart();


let value = 0; 
setInterval(function(){
	value += 1;
	$("#chartCount").text(value);
}, 60000);