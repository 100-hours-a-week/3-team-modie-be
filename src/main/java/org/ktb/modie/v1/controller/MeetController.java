package org.ktb.modie.v1.controller;

import org.ktb.modie.core.response.SuccessResponse;
import org.ktb.modie.v1.dto.MeetDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetController implements MeetApi {

	@PostMapping("/api/v1/meets")
	public ResponseEntity<SuccessResponse<Void>> createMeet(
		@RequestBody MeetDto request
	) {
		return SuccessResponse.ofNoData().asHttp(HttpStatus.OK);
	}

}
