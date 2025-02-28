package com.example.remittance.valid.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.remittance.common.model.base.BaseApiResponse;
import com.example.remittance.valid.model.ValidDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * @author evan.m.kim
 */
@Tag(name = "#valid", description = "valid test")
@RestController
@RequestMapping("/valid")
public class ValidController {
	@PostMapping("")
	public String create(@Valid @RequestBody BaseApiResponse<ValidDto> requestBody) {

		return "User created: " + requestBody.getData().getName();
	}
}
