
	//로컬 호스트
	var url = "http://192.168.23.96:8080/";
	 
	// 1. XML TO JSON 변환
	var xml2json = function(_data) {
		$.ajax({
			type : "POST",
			url : url + "xml2json",
			data : _data,
			contentType : "application/xml",
			cache : false,
			success : function(data) { //통신 성공시   
			
				if(data.hasOwnProperty("AutoRoot")){
					//AutoRoot안의 데이터만 남기기 
					data = data["AutoRoot"];
					$("#input-json").val(JSON.stringify(data, null, 4));
					$("#checkValue").html('<font color="orange">유효한 값입니다 (*자동생성된 루트태그 삭제)</font>')
						.fadeOut(function() { $("#checkValue").fadeIn(); });
				}else{
					$("#input-json").val(JSON.stringify(data, null, 4));
					$("#checkValue").html('<font color="blue">유효한 값입니다</font>')
						.fadeOut(function() { $("#checkValue").fadeIn(); });
				}
				  
				saveResult("success_XMLtoJSON");
			},
			error : function() { //잘못 된 값을 입력할시 
				$("#checkValue").html('<font color="red">유효하지 않은 XML 값입니다</font>')
				  .fadeOut(function() { $("#checkValue").fadeIn(); });
				$("#input-json").val("");
				saveResult("fail_XMLtoJSON");
			}
		});
	}
	
	
	// 2. JSON TO XML 변환
	var json2xml = function(_data) {
		$.ajax({
			type : "POST",
			url : url + "json2xml",
			data : _data,
			contentType : "application/json",
			dataType : "xml",
			cache : false,
			success : function(data) {
				var xmlText = new XMLSerializer().serializeToString(data);  //데이어 직렬화
				xmlText = '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n' + xmlText;  //xml 프롤로그 추가
				$("#input-xml").val(xmlText);
				
				if(xmlText.indexOf("<AutoRoot>") != -1){
					$("#checkValue").html('<font color="orange">유효한 값입니다 (*루트 태그 자동생성)</font>')
					   .fadeOut(function(){ $("#checkValue").fadeIn(); });
					
				} else {
					$("#checkValue").html('<font color="blue">유효한 값입니다</font>')
					   .fadeOut(function(){ $("#checkValue").fadeIn(); });
					
				}

				saveResult("success_JSONtoXML");
			},
			error : function() {
				$("#checkValue").html('<font color="red">유효하지 않은 JSON 값입니다</font>')
				  .fadeOut(function() { $("#checkValue").fadeIn(); });
				$("#input-xml").val("");
				saveResult("fail_JSONtoXML");
			}
		});
	}

	// XML to JSON Button Control
	$("#convert-xml-to-json").on("click", function() {
		var data = $("#input-xml").val();
		if (!data) {
			data = "invalid data(xml to json)";
		}
		xml2json(data);
	});
	
	// JSON to XML Button Control
	$("#convert-json-to-xml").on("click", function() {
		var data = $("#input-json").val();
		if (!data) {
			data = "invalid data(json to xml)";
		}
		json2xml(data);
	});
	
	
	// 3. 통계결과 제어 부분
	var success_XMLtoJSON;	// xml -> json 성공
	var fail_XMLtoJSON;		// xml -> json 실패
	var success_JSONtoXML;	// json -> xml 성공
	var fail_JSONtoXML;		// json -> xml 실패
	
	$(document).ready(function(){
		// 어플리케이션 작동, 최초에 통계값 로드
		statisticalDataController("startApplication");
		$("#XMLtoJSON_chartValue").html("성공 : " + success_XMLtoJSON + " 실패 : " + fail_XMLtoJSON);
		$("#JSONtoXML_chartValue").html("성공 : " + success_JSONtoXML + " 실패 : " + fail_JSONtoXML);
		loadXmlChart();
		loadJsonChart();
		
		// 통계창 on/off 관리
		$("#displayControl").on("click", function() {
			var con = document.getElementById("result");
		    if(con.style.display=='none'){
		        con.style.display = 'block';
		    }else{
		        con.style.display = 'none';
		    }
		});
		
	});
	
	
	// 통계값 관련 Ajax 통신 제어기
	var statisticalDataController = function(inputValue){
		
		$.ajax({
			type : "POST",
			url : url + "status",
			data : JSON.stringify(inputValue),
			contentType : "application/json",
			dataType: "json",
			cache : false,
			async: false,
			success : function(output) {
				
				// Set statistical Data
				success_XMLtoJSON = output["XMLtoJSON"].success * 1;
				fail_XMLtoJSON = output["XMLtoJSON"].error * 1;
				success_JSONtoXML = output["JSONtoXML"].success * 1;
				fail_JSONtoXML = output["JSONtoXML"].error * 1;
				
			},
			error : function(request,status,error) {
				alert("에러코드 : " + request.status + "\r\n" + "에러메시지:" + request.responseText + "\r\n" + "진짜에러:" + error);
			}
		});
		
	}
	
	
	// 데이타 변환 값 업데이트에 필요한 함수
	function saveResult(value){
		
		var isChanged;
		
		statisticalDataController("startApplication");
		
		//호출 유형에 따라 값 증가
		if(value == "success_XMLtoJSON"){
			success_XMLtoJSON = success_XMLtoJSON + 1;
			isChanged = "xml";
		} else if ( value == "fail_XMLtoJSON" ) {
			fail_XMLtoJSON = fail_XMLtoJSON + 1;
			isChanged = "xml";
		} else if ( value == "success_JSONtoXML" ) {
			success_JSONtoXML = success_JSONtoXML + 1;
			isChanged = "json";
		} else {
			fail_JSONtoXML = fail_JSONtoXML + 1;
			isChanged = "json";
		}
		
		//변동된 값 H2 DB에 저장
		var saveValue = success_XMLtoJSON+","+fail_XMLtoJSON+","+success_JSONtoXML+","+fail_JSONtoXML;
		statisticalDataController( saveValue );
		
		//리셋된 결과값 반영
		if(isChanged=="xml"){
			$("#XMLtoJSON_chartValue").html("성공 : " + success_XMLtoJSON + " 실패 : " + fail_XMLtoJSON);
			$("#XMLtoJSON_chart").remove();
			$("#xmlChart").append("<canvas id='XMLtoJSON_chart'></canvas>");
			loadXmlChart();
		}else{
			$("#JSONtoXML_chartValue").html("성공 : " + success_JSONtoXML + " 실패 : " + fail_JSONtoXML);
			$("#JSONtoXML_chart").remove();
			$("#jsonChart").append("<canvas id='JSONtoXML_chart'></canvas>");
			loadJsonChart();
		}
		
	}
	
	// 7. 리셋버튼
	function resetValue(){
		
		success_XMLtoJSON=0;
		fail_XMLtoJSON=0;
		success_JSONtoXML=0;
		fail_JSONtoXML=0;
		
		statisticalDataController("initialize");	
		
		$("#XMLtoJSON_chartValue").html("성공 : " + success_XMLtoJSON + " 실패 : " + fail_XMLtoJSON);
		$("#JSONtoXML_chartValue").html("성공 : " + success_JSONtoXML + " 실패 : " + fail_JSONtoXML);
		$("#XMLtoJSON_chart").remove();
		$("#JSONtoXML_chart").remove();
		$("#xmlChart").append("<canvas id='XMLtoJSON_chart'></canvas>");
		$("#jsonChart").append("<canvas id='JSONtoXML_chart'></canvas>");
		loadXmlChart();
		loadJsonChart();
		
	}

	
	// 차트옵션
	var opt = {
	    events: false,
	    tooltips: {
	        enabled: false
	    },
	    hover: {
	        animationDuration: 0
	    },
	    animation: {
	        duration: 1,
	        onComplete: function () {
	            var chartInstance = this.chart,
	            ctx = chartInstance.ctx;
                var fontSize = 16;
                var fontStyle = 'normal';
                var fontFamily = 'Helvetica Neue';
                ctx.font = Chart.helpers.fontString(fontSize, fontStyle, fontFamily);
	            ctx.fillStyle = "#FF0000";
	            ctx.textAlign = 'center';
	            ctx.textBaseline = 'bottom';
	            
	            this.data.datasets.forEach(function (dataset, i) {
	                var meta = chartInstance.controller.getDatasetMeta(i);
	                meta.data.forEach(function (bar, index) {
	                	var data = dataset.data[index];
	                	// 0보다 클때만 차트바에 값 표시
	                	if(data > 0){
	                		ctx.fillText(data, bar._model.x - 1, bar._model.y +9);
	                	}
	                });
	            });
	        }
	    },
	    scales : {
			xAxes : [ {
				ticks : {
					beginAtZero : true
				}
			} ]
		}
	};
	
	// XML차트
	function loadXmlChart(){
		
		var ctx = document.getElementById("XMLtoJSON_chart");
		chart = new Chart(ctx,{
			type : "horizontalBar",
		    data : 
		    	{
				datasets : [ {
					label : "성공",
					data : [success_XMLtoJSON],
					fill : false,
					backgroundColor : ["rgba(54, 162, 235, 0.2)"],
					borderColor : ["rgb(54, 162, 235)"],
					borderWidth : 0
				},
				{
					label : "실패",
					data : [fail_XMLtoJSON],
					fill : false,
					backgroundColor : ["rgba(255, 99, 132, 0.2)"],
					borderColor : ["rgb(255, 99, 132)"],
					borderWidth : 0
				}]
			},
			options : opt
		});
	}
	
	// JSON차트
	function loadJsonChart(){
		var ctx = document.getElementById("JSONtoXML_chart");
		
		chart = new Chart(ctx,{
			type : "horizontalBar",
		    data : 
	    	{
			datasets : [ {
				label : "성공",
				data : [success_JSONtoXML],
				fill : false,
				backgroundColor : ["rgba(54, 162, 235, 0.2)"],
				borderColor : ["rgb(54, 162, 235)"],
				borderWidth : 0
			},
			{
				label : "실패",
				data : [fail_JSONtoXML],
				fill : false,
				backgroundColor : ["rgba(255, 99, 132, 0.2)"],
				borderColor : ["rgb(255, 99, 132)"],
				borderWidth : 0
			}]
		},
			options : opt     
		});
		
	}
	
	function clearXmlConsole(){
		$("#input-xml").val("");
		$("#checkValue").html("&nbsp");
	}
	
	function clearJsonConsole(){
		$("#input-json").val("");
		$("#checkValue").html("&nbsp");
	}
	
	function testXmlValue(){
		$("#input-xml").val("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
							"<bookstore>\n"+
								"\t<book category=\"cooking\">\n"+
									"\t\t<title lang=\"en\">Everyday Italian</title>\n"+
									"\t\t<author>Giada De Laurentiis</author>\n"+
									"\t\t<year>2005</year>\n"+
									"\t\t<price>30</price>\n"+
								"\t</book>\n"+
								"\t<book category=\"children\">\n"+
									"\t\t<title lang=\"en\">Harry Potter</title>\n"+
									"\t\t<author>J K. Rowling</author>\n"+
									"\t\t<year>2005</year>\n"+
									"\t\t<price>29.99</price>\n"+
								"\t</book>\n"+
							"</bookstore>\n");
		$("#input-json").val("");
		$("#checkValue").html("&nbsp");
	}
	
	function testJsonValue(){
		$("#input-json").val("{\n" + 
								"\t\"books\" : [\n"+
								"\t\t{ \"language\" : \"java\" , \"edition\" : \"second\" },\n"+
								"\t\t{ \"language\" : \"C++\" , \"lastName\" : \"third\" },\n"+
								"\t\t{ \"language\" : \"C\" , \"lastName\" : \"fifth\" }\n"+
								"\t]\n"+
							  "}");
		$("#input-xml").val("");
		$("#checkValue").html("&nbsp");
	}
	
