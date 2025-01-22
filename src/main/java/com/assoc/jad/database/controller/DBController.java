package com.assoc.jad.database.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assoc.jad.database.service.DatabaseService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/REST")
public class DBController {
	@Autowired
	DatabaseService databaseService;
	
	@GetMapping("/verifyTables")
	public String verifyTables(HttpServletRequest req) {
		
		JSONObject json = databaseService.verifyTables(req);
		
		return json.toJSONString();
	}
	@GetMapping("/bldTable")
	public String createTable(HttpServletRequest req) {
		
		JSONObject json = databaseService.createTable(req);		
		return json.toJSONString();
	}
	@PostMapping("/bldConnection")
	public String loginDB(@RequestBody JSONObject reqBodyJson, HttpServletResponse resp,HttpServletRequest req) {
				
		JSONObject jsonOut = databaseService.bldConnections(reqBodyJson);
		return jsonOut.toJSONString();
	}
	@PostMapping("/uploadFile")
	public String uploadFile(HttpServletResponse resp,HttpServletRequest req) {
//		 List<String> filenames = databaseService.uploadFile(req);
		return (new JSONObject()).toJSONString();
	}
	@PostMapping("/jarUploadFiles")
	public String jarUploadFiles(HttpServletResponse resp,HttpServletRequest req) {
		JSONObject json = databaseService.uploadJarFile(req);
		return json.toJSONString();
	}
	
}
