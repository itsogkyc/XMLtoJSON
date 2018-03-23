package com.example.demo.controller;

public class Validator {

	public String XmlValidator(String xml) {

		boolean isLiteral;

		if(!xml.startsWith("<")){
			return "리터럴값입니다";
		}
		
		
		String a = xml.substring(xml.indexOf("<"),xml.indexOf("/>"));
		
		
		return a;
	}

}
