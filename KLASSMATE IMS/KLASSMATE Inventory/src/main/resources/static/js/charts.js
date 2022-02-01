function getCategoriesProductCountAJAX() {
	var labels = [];
	var counts = [];
	var productTypes = [];
	$.ajax({
		type: "GET",
		url: "/api/getProductTypes",
		success: function(data){
			for(var element of data){
				productTypes.push(element);				
			}
		},
		error: function(error){
			console.log(error);
		}
	})
	$.ajax({
		type: "GET",
		url: "/api/getCategoryProductCount",
		success: function(result){
			for(var data of result) {
				productTypes.forEach((element) => {
					if(data.type === element.serial){
						labels.push(element.categoryName);
					}
				});
				counts.push(data.count);
			}	
		},
		error: function(error){
			console.log(error);
		}
	});
	
	var colorPallette  = [
		'#469b75', 
		'#6eaf80', 
		'#93c38d', 
		'#b8d79c', 
		'#dbebae', 
		'#ffffc3', 
		'#f8e39f', 
		'#f3c580', 
		'#efa768', 
		'#e98658', 
		'#e06451', 
		'#d43d51', 
		'#00876c', 
		'#469b83', 
		'#6eaf9a', 
		'#93c3b3', 
		'#b7d7cc', 
		'#dbebe5', 
		'#ffffff', 
		'#fde0e0', 
		'#f9c2c1', 
		'#f3a3a4', 
		'#eb8387', 
		'#e0636b', 
		'#d43d51',
		];
	
	setTimeout(function (){
		var id = document.getElementById('productCategoriesChart');
		var ctx = id.getContext('2d');
	    var myChart = new Chart(ctx, {
	      type: 'pie',
	      data: {
	        labels: labels,
	        datasets: [{
			  label: "Category wise Products Count",
	          backgroundColor: colorPallette,
		      borderColor: '#f3c580',
	          borderWidth: 1,
	          data: counts,
	        }]
	      },
	      options: {
			maintainAspectRatio: false,
	        legend: {
	          display: false,
	          position: 'bottom',
	          align: 'end',
	        },
	
	        pieceLabel: {
	          render: 'percentage',
	          fontColor: ['white'],
	          precision: 2
	        },
	
	        tooltips: {
	          enabled: true
	        },
	        scales: {
	          yAxes: [{
	            ticks: {
	              display: false
	            },
	            gridLines: {
	              drawBorder: false,
	              zeroLineColor: "transparent",
	              color: 'rgba(255,255,255,0.05)'
	            }
	
	          }],
	          xAxes: [{
	            gridLines: {
	              drawBorder: false,
	              color: 'rgba(255,255,255,0.1)',
	              zeroLineColor: "transparent"
	            },
	            ticks: {
	              display: false,
	            }
	          }]
	        }
	      }
	    });
	}, 500)
}

function getStatusProductCount() {
	var count = [];
	var detail = [];
	$.ajax({
		type:"GET",
		url: "api/getStatusProductCount",
		success: function(data){
			/*console.log(data);*/
			for(var result of data) {
				detail.push(result.type);
				count.push(result.count);
			}
		},
		error: function(error){
			console.log(error);
		}
	});
	
	setTimeout(function(){
		var element = document.getElementById('productStatusChart');
		var ctx = element.getContext("2d");
	
	    var myChart = new Chart(ctx, {
	      type: 'bar',
	      data: {
	        labels: detail,
	        datasets: [{
	          label: "Products Count",
	          pointRadius: 0,
	          pointHoverRadius: 0,
	          backgroundColor: '#ffac78',
	          borderWidth: 0,
	          data: count
	        }]
	      },
		   options: {
			maintainAspectRatio: false,
	        tooltips: {
	          enabled: true
	        },
	      }
	    });
	}, 500);
	
}

function getLineChart() {
	var allDetails = [];
	
	for(let i = 0; i <= 6; i++) {
		$.ajax({
			type: "GET",
			url: "/api/productBehavior?status="+i,
			success: function(data) {
				allDetails.push(data);
			},
			error: function(error){
				console.log(error);
			}
		});		
	}
	
	setTimeout(function(){
		var speedCanvas = document.getElementById("productAssemblingChart");
	
		var inventory =[0];
		var workshop = [0];
		var allocated = [0];
		var finished = [0];
		var replaced = [0];
		var labels = [];
		var months = ["january", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
		
		allDetails.map((data) => {
			data.map((object) => {
				labels.push(object.date);
				});
		});
		let dates = [...new Set(labels)].sort();
		var monthNumArr = [];
		var dateNumArr= []
		
		for(let i = 0; i < dates.length; i++) {
			var monthNum = dates[i].split("-", 3)[1];
			var dayNum = dates[i].split("-", 3)[2]
			if(i === 0) {
				monthNumArr.push(monthNum);
			}
			for(let string of monthNumArr) {
				if(string !== monthNum) {
					monthNumArr.push(monthNum);					
				} else {
					dateNumArr.push(dayNum);		
				}
			}
		}
		if(monthNumArr.length < 4) {
			if(dateNumArr.length !== 0) {
				allDetails.map((array) => {
					array.map((object)=>{
						switch(object.status) {
							case"IN_INVENTORY":
								let i = 0;
								while(i < dateNumArr.length) {
									if(dateNumArr[i] === object.date.split("-", 3)[2]){
										inventory[i] = object.count;
									}
									i += 1; 
								}
								break; 
							case "ON_ASSEMBLE":
								let w = 0;
								while(w < dateNumArr.length) {
									if(dateNumArr[w] === object.date.split("-", 3)[2]){
										workshop[w] = object.count;
									}
									w += 1; 
								}
								break;
							case "ALLOCATED" :
								let a = 0;
								while(a < dateNumArr.length) {
									if(dateNumArr[a] === object.date.split("-", 3)[2]){
										allocated[a] = object.count;
									}
									a += 1; 
								}
								break; 
							case "FINISHED":
								let f = 0;
								while(f < dateNumArr.length) {
									if(dateNumArr[f] === object.date.split("-", 3)[2]){
										finished[f] = object.count;
									}
									f += 1; 
								}
								break;
							case "REPLACED":
								let r = 0;
								while(r < dateNumArr.length) {
									if(dateNumArr[r] === object.date.split("-", 3)[2]){
										replaced[r] = object.count;
									}
									r += 1; 
								}
								break;
						}
					})
				})
			}
		} else {
			allDetails.map((array)=>{
				array.map((object)=>{
					var month = object.date.split("-")[1];
					switch(object.status) {
						case"IN_INVENTORY":
							for(var j = 0; j < monthNumArr.length; j++) {
								if(monthNumArr[j] === month){
									inventory[j] += object.count;
								}
							}
							break; 
						case "ON_ASSEMBLE":
							for(var j = 0; j < monthNumArr.length; j++) {
								if(monthNumArr[j]=== month){
									workshop[j] += object.count;
								}
							}
							break;
						case "ALLOCATED" :
							for(var j = 0; j < monthNumArr.length; j++) {
								if(monthNumArr[j]=== month){
									allocated[j] += object.count;
								}
							}
							break; 
						case "FINISHED":
							for(var j = 0; j < monthNumArr.length; j++) {
								if(monthNumArr[j]=== month){
									finished[j] += object.count;
								}
							}
							break;
						case "REPLACED":
							for(var j = 0; j < monthNumArr.length; j++) {
								if(monthNumArr[j]=== month){
									replaced[j] += object.count;
								}
							}
							break;
					}
				})
			});
		}
		let checker = 0
		while(checker < dateNumArr.length) {
			if(inventory[checker] === undefined) {
				inventory[checker] = 0;
			}
			if(workshop[checker] === undefined) {
				workshop[checker] = 0;
			}
			if(allocated[checker] === undefined) {
				allocated[checker] = 0;
			}
			if(finished[checker] === undefined) {
				finished[checker] = 0;
			}
			if(replaced[checker] === undefined) {
				replaced[checker] = 0;
			}
			checker +=1;
		}

	    var inventoryLine = {
	      data: inventory,
	      label: "added into inventories",
	      fill: false,
	      borderColor: '#51CACF',
	      backgroundColor: '#51CACF',
	      pointBorderColor: '#51CACF',
	      pointRadius: 4,
	      pointHoverRadius: 4,
	      pointBorderWidth: 8
	    };
	
		var workshopLine = {
	      data: workshop,
	      label: "workshop assemblings",
	      fill: false,
	      borderColor: '#fbc658',
	      backgroundColor: '#fbc658',
	      pointBorderColor: '#fbc658',
	      pointRadius: 4,
	      pointHoverRadius: 4,
	      pointBorderWidth: 8,
	    };
	
		var allocatedLine = {
	      data: allocated,
	      label: "product allocations",
	      fill: false,
	      borderColor: '#93c38d',
	      backgroundColor: '#93c38d',
	      pointBorderColor: '#93c38d',
	      pointRadius: 4,
	      pointHoverRadius: 4,
	      pointBorderWidth: 8,
	    };

		var finishedLine= {
	      data: finished,
	      label: "product finishes",
	      fill: false,
	      borderColor: '#d43d51',
	      backgroundColor: '#d43d51',
	      pointBorderColor: '#d43d51',
	      pointRadius: 4,
	      pointHoverRadius: 4,
	      pointBorderWidth: 8,
	    };
	    
	    var replacedLine= {
	      data: replaced,
	      label: "product replaces",
	      fill: false,
	      borderColor: '#93c3b3',
	      backgroundColor: '#93c3b3',
	      pointBorderColor: '#93c3b3',
	      pointRadius: 4,
	      pointHoverRadius: 4,
	      pointBorderWidth: 8,
	    };
			
	    var speedData = {
	      labels: monthNumArr.length < 4 ? dates: months,
	      datasets: [workshopLine, allocatedLine, inventoryLine, finishedLine, replacedLine]
	    };
	
	    var chartOptions = {
	      legend: {
	        display: false,
	        position: 'bottom'
	      }
	    };
	
	    var lineChart = new Chart(speedCanvas, {
	      type: 'line',
	      hover: false,
	      data: speedData,
	      options: chartOptions
	    });
	},500);
}

function projectsRadarChart(){
	var labels = [];
	var dataSet = [];	
	$.ajax({
		type : "GET",
		url: "api/projectStats",
		success: function(data){
			for(var item of data) {
				/*console.log(item);*/
				labels.push(item.type);
				dataSet.push(item.count);
			}
		},
		error: function(error){
			console.log(error)
		}
	});
	
	setTimeout(function(){
		const data = {
		  labels: labels,
		  datasets: [{
		    label: 'Project Count',
		    data: dataSet,
		    fill: true,
		    backgroundColor: [
	            '#66a4ff',
	            '#f0bc69',
	            '#e87979',
	            '#44db6d'
	          ], 
		    borderColor: 'white',
		    pointBackgroundColor: 'rgb(255, 99, 132)',
		    pointBorderColor: '#fff',
		    pointHoverBackgroundColor: '#fff',
		    pointHoverBorderColor: 'rgb(255, 99, 132)'
		  }],
		};
		const config = {
		  type: 'pie',
		  data: data,
		  options: {
		    elements: {
		      line: {
		        borderWidth: 1
		      }
		    }
		  },
		};
		var projectRadarChart = document.getElementById("projectsStatisticsRadar")
		var radarChart = new Chart(projectRadarChart, config);
	},500);
}

export {getCategoriesProductCountAJAX, getStatusProductCount, getLineChart, projectsRadarChart};