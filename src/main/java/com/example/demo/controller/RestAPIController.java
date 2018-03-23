package com.example.demo.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import staxonutils.StaxonUtils;

@RestController
public class RestAPIController {

	// TODO : XML 데이터 변환
	@RequestMapping(value = "/xml2json", method = RequestMethod.POST, consumes = "application/xml", // xml로 input 된다
			produces = "application/json") // json로 output 된다
	public ResponseEntity<String> xtoj(HttpServletRequest request, @RequestBody String xml) throws JSONException {

		StaxonUtils stx = new StaxonUtils();
		String convertedValue = stx.xml2json(xml);

		System.out.println("staxOnxml2json =>" + convertedValue);

		return new ResponseEntity<String>(convertedValue, HttpStatus.OK);
	}

	// TODO : json 데이터 변환
	@RequestMapping(value = "/json2xml", method = RequestMethod.POST, consumes = "application/json", produces = "application/xml")
	public ResponseEntity<String> jtox(HttpServletRequest request, @RequestBody String json) throws JSONException {

		StaxonUtils stx = new StaxonUtils();
		String convertedValue = stx.json2xml(json);
		Validator v = new Validator();
		int count = 0;

		System.out.println("staxOnJson2Xml =>" + convertedValue);

		if (convertedValue.equals("") || convertedValue.equals("Error")) {
			// 잘못된 값일시 에러출력
			return new ResponseEntity<String>(convertedValue, HttpStatus.BAD_REQUEST);
		} else {
			String a = convertedValue.substring(convertedValue.indexOf("<"), convertedValue.indexOf(">") + 1); // 첫번쨰태그
			Pattern pattern = Pattern.compile(a);
			Matcher matcher = pattern.matcher(convertedValue);
			while (matcher.find()) {
				count++;
			}

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				db.parse("convertedValue");
				System.out.println("에러없음");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("실패");
			}

			// convertedValue = "<DATA>\n" + convertedValue + "</DATA>";
		}

		return new ResponseEntity<String>(convertedValue, HttpStatus.OK);
	}

}
