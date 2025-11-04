package com.tth.restapi.dto.group;

import com.tth.restapi.dto.AuditDto;
import com.tth.restapi.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class UserGroupDto extends AuditDto {

	private UserDto user;

	private GroupDto group;

}
