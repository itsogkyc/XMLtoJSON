package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.example.entity.Status;
import com.example.service.StatusService;

import staxonutils.StaxonUtils;
import xmlhandler.SaxHandler;


@RestController
public class RestAPIController {

	
	@Autowired
	StatusService statusService;
	
	Status status = new Status(0,0,0,0);
	
	
	// TODO : XML 데이터 변환
	@RequestMapping(value = "/xml2json", method = RequestMethod.POST, consumes = "application/xml", produces = "application/json")
	public ResponseEntity<String> xtoj(HttpServletRequest request, @RequestBody String xml) throws JSONException {
		
		
		StaxonUtils stx = new StaxonUtils();
		String convertedValue = stx.xml2json(xml);
		
		if(convertedValue.equals("") || convertedValue.equals("Error")) {
			return new ResponseEntity<String>(convertedValue, HttpStatus.BAD_REQUEST);
		}
		
		System.out.println("staxOnxml2json =>" + convertedValue);

		return new ResponseEntity<String>(convertedValue, HttpStatus.OK);
	}

	// TODO : json 데이터 변환
	@RequestMapping(value = "/json2xml", method = RequestMethod.POST, consumes = "application/json", produces = "application/xml")
	public ResponseEntity<String> jtox(HttpServletRequest request, @RequestBody String json) throws JSONException, Exception {

		StaxonUtils stx = new StaxonUtils();
		String convertedValue = stx.json2xml(json);

		System.out.println("staxOnJson2Xml =>" + convertedValue);

		if (convertedValue.equals("") || convertedValue.equals("Error")) {
			// 유효하지 않은 JSON 값
			return new ResponseEntity<String>(convertedValue, HttpStatus.BAD_REQUEST);
			
		} else {
			// 유효한 JSON 값
			try {
			    // convert한 xml값의 유효성 체크
				String strXML = convertedValue;
			    SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			    InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
			    SaxHandler handler = new SaxHandler();
			    saxParser.parse(stream, handler);
			    
			} catch (SAXException e) {
			    // json에서 xml로 변환된 값이 유효하지 않은 형식의 xml일때 처리 
				convertedValue = "<AutoRoot>" + convertedValue + "</AutoRoot>";
			}

		}

		return new ResponseEntity<String>(convertedValue, HttpStatus.OK);

	}
	
	// 통계 데이터 조회
	@RequestMapping(value = "/status", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> status(@RequestBody String json) {
		
		System.out.println("들어온 값=>" + json);
				
		return new ResponseEntity<String>(json, HttpStatus.OK);
	}
	
	

}
