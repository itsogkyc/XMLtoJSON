package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import staxonutils.StaxonUtils;


@RestController
public class RestAPIController {

	// xml 데이터를 받았을 때 처리
	@RequestMapping(value = "/xml2json", method = RequestMethod.POST, consumes = "application/xml", // xml로 input 된다
			produces = "application/json") // json로 output 된다
	public ResponseEntity<String> xtoj(HttpServletRequest request, @RequestBody String xml) throws JSONException {
		
		/* TODO */
		StaxonUtils stx = new StaxonUtils();
		String result = stx.xml2json(xml);
	    
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	// json 데이터를 받았을 때 처리
	@RequestMapping(value = "/json2xml", method = RequestMethod.POST, consumes = "application/json", produces = "application/xml")
	public ResponseEntity<String> jtox(HttpServletRequest request, @RequestBody String json) throws JSONException {

		/* TODO */
		StaxonUtils stx = new StaxonUtils();
		String convertedValue= stx.json2xml(json);
		if(convertedValue.equals("")) {
			return new ResponseEntity<String>(convertedValue, HttpStatus.BAD_REQUEST);
		}else {
			convertedValue = "<DATA>" + convertedValue + "</DATA>"; 
		}
		
		System.out.println(convertedValue);
		return new ResponseEntity<String>(convertedValue, HttpStatus.OK);
	}

	
	
	// 통계 데이터 조회
	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Map<String, Object>> status() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "success");

		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST, produces = "application/xml")
	public ResponseEntity<String> uploadJsonFile(HttpServletRequest request, @RequestParam("file") MultipartFile file)
			throws IOException {
		String json = new String(file.getBytes());

		/* TODO */

		System.out.println("uploadJsonFile success \n" + json);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}
	
	
	
	

}
