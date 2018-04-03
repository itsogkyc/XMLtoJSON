package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONException;
import org.json.JSONObject;
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
import com.google.gson.Gson;

import staxonutils.StaxonUtils;
import xmlhandler.SaxHandler;

@RestController
public class RestAPIController {

	@Autowired
	StatusService statusService;
	Status status;

	/**
	 * XML to JSON 변환
	 */
	@RequestMapping(value = "/xml2json", method = RequestMethod.POST, consumes = "application/xml", produces = "application/json")
	public ResponseEntity<String> xtoj(HttpServletRequest request, @RequestBody String xml) throws JSONException, Exception {

		StaxonUtils stx = new StaxonUtils();
		String convertedValue = stx.xml2json(xml);
		
		if (convertedValue.equals("") || convertedValue.equals("Error")) {
			return new ResponseEntity<String>(convertedValue, HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<String>(convertedValue, HttpStatus.OK);
	}

	/**
	 * JSON to XML 변환
	 */
	@RequestMapping(value = "/json2xml", method = RequestMethod.POST, consumes = "application/json", produces = "application/xml")
	public ResponseEntity<String> jtox(HttpServletRequest request, @RequestBody String json) throws JSONException, Exception {

		StaxonUtils stx = new StaxonUtils();
		String convertedValue = stx.json2xml(json);

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
				// 올바른 JSON 값이지만, 유효하지 않은 XML 값일 때 루트태그 추가
				convertedValue = "<AutoRoot>" + convertedValue + "</AutoRoot>";
			}

		}

		return new ResponseEntity<String>(convertedValue, HttpStatus.OK);

	}

	/**
	 * 통계 데이터 조회
	 */
	@RequestMapping(value = "/status", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> status(@RequestBody String json) {

		int success_xj2;
		int success_j2x;
		int fail_x2j;
		int fail_j2x;

		// 1. 최초 어플리케이션 작동시 값 로드
		if (json.equals("\"startApplication\"")) {
			Long countRecord = statusService.countRecord();

			if (countRecord == 0) {
				status = new Status(0,0,0,0);				
				statusService.save(status);
				json = "{ \"XMLtoJSON\":{\"success\":0,\"error\":0} , \"JSONtoXML\":{\"success\":0,\"error\":0} }";
				return new ResponseEntity<String>(json, HttpStatus.OK);

			} else {
				List<Status> statusValue = statusService.findAll();
				success_xj2 = statusValue.get(0).getSuccess_x2j();
				success_j2x = statusValue.get(0).getSuccess_j2x();
				fail_x2j = statusValue.get(0).getFail_x2j();
				fail_j2x = statusValue.get(0).getFail_j2x();
				json = "{ \"XMLtoJSON\":{\"success\":" + success_xj2 + ",\"error\":" + fail_x2j + 
					   "},\"JSONtoXML\":{\"success\":" + success_j2x + ",\"error\": " + fail_j2x + "} }";
				return new ResponseEntity<String>(json, HttpStatus.OK);
			}

		// 2. 통계값 초기화
		} else if (json.equals("\"initialize\"")) {
			
			success_xj2 = 0;
			success_j2x = 0;
			fail_x2j = 0;
			fail_j2x = 0;

			statusService.updateStatusValue(success_xj2, fail_x2j, success_j2x, fail_j2x);
			
			json = "{ \"XMLtoJSON\":{\"success\":0,\"error\":0} , \"JSONtoXML\":{\"success\":0,\"error\":0} }";
			
			return new ResponseEntity<String>(json, HttpStatus.OK);

		}
		
		// 3. 통계값 저장
		json = json.replaceAll("\"", "");
		String[] eachStatusValue = json.split(",");
		
		success_xj2 = Integer.parseInt(eachStatusValue[0]);
		fail_x2j = Integer.parseInt(eachStatusValue[1]);
		success_j2x = Integer.parseInt(eachStatusValue[2]);
		fail_j2x = Integer.parseInt(eachStatusValue[3]);
		
		statusService.updateStatusValue(success_xj2, fail_x2j, success_j2x, fail_j2x);
		
		json = "{ \"XMLtoJSON\":{\"success\":" + success_xj2 + ",\"error\":" + fail_x2j
				+ "},\"JSONtoXML\":{\"success\":" + success_j2x + ",\"error\": " + fail_j2x + "} }";
		
		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

}
