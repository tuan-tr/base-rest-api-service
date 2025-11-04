package com.tth.restapi.dto.user;

import com.tth.common.validation.AfterCurrentTime;
import com.tth.common.validation.ValidRangeDateTime;
import com.tth.persistence.constant.UserStatus;
import com.tth.restapi.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ValidRangeDateTime(from = "effectiveStart", to = "effectiveEnd")
public class UserCreateInput {

	@NotNull
	private UserStatus status;

	@NotBlank(message = ErrorCode.NAME_IS_REQUIRED)
	private String name;

	@AfterCurrentTime
	private OffsetDateTime effectiveStart;

	private OffsetDateTime effectiveEnd;

}
