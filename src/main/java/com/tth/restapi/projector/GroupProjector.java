package com.tth.restapi.projector;

import com.tth.persistence.entity.Group;
import com.tth.restapi.dto.group.GroupDto;

import java.util.List;
import java.util.stream.Collectors;

public class GroupProjector {

	public static GroupDto toDetailDto(Group entity) {
		return GroupDto.builder()
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.id(entity.getId())
				.status(entity.getStatus())
				.name(entity.getName())
				.build();
	}

	public static List<GroupDto> toSearchDto(List<Group> entities) {
		return entities.stream()
				.map(e -> toSearchDto(e))
				.collect(Collectors.toList());
	}

	public static GroupDto toSearchDto(Group entity) {
		return GroupDto.builder()
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.id(entity.getId())
				.status(entity.getStatus())
				.name(entity.getName())
				.build();
	}

}
