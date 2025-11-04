package com.tth.restapi.projector;

import com.tth.persistence.entity.UserGroup;
import com.tth.restapi.dto.group.UserGroupDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserGroupProjector {

	public static List<UserGroupDto> toSearchDto(List<UserGroup> entities) {
		return entities.stream()
				.map(e -> toSearchDto(e))
				.collect(Collectors.toList());
	}

	public static UserGroupDto toSearchDto(UserGroup entity) {
		return UserGroupDto.builder()
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.user(UserProjector.toSearchDto(entity.getUser()))
				.build();
	}

}
