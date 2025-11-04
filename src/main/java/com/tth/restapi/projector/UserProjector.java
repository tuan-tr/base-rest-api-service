package com.tth.restapi.projector;

import com.tth.persistence.entity.User;
import com.tth.restapi.dto.user.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserProjector {

	public static UserDto toDetailDto(User entity) {
		return UserDto.builder()
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.id(entity.getId())
				.name(entity.getName())
				.status(entity.getStatus())
				.effectiveStart(entity.getEffectiveStart())
				.effectiveEnd(entity.getEffectiveEnd())
				.build();
	}

	public static List<UserDto> toSearchDto(List<User> entities) {
		return entities.stream()
				.map(e -> toSearchDto(e))
				.collect(Collectors.toList());
	}

	public static UserDto toSearchDto(User entity) {
		return UserDto.builder()
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.id(entity.getId())
				.name(entity.getName())
				.status(entity.getStatus())
				.effectiveStart(entity.getEffectiveStart())
				.effectiveEnd(entity.getEffectiveEnd())
				.build();
	}

}
