package com.tth.restapi.dto.user;

import com.tth.persistence.constant.UserStatus;
import com.tth.restapi.dto.AuditDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class UserDto extends AuditDto {

	private String id;

	private UserStatus status;

	private String name;

	private OffsetDateTime effectiveStart;

	private OffsetDateTime effectiveEnd;

}
