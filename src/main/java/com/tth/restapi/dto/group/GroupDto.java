package com.tth.restapi.dto.group;

import com.tth.persistence.constant.GroupStatus;
import com.tth.restapi.dto.AuditDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class GroupDto extends AuditDto {

	private String id;

	private GroupStatus status;

	private String name;

	private List<UserGroupDto> userGroups;

}
