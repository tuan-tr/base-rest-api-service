package com.tth.restapi.service;

import com.tth.common.exception.BadBusinessException;
import com.tth.persistence.entity.Group;
import com.tth.persistence.repository.GroupRepository;
import com.tth.restapi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupProviderService {
	private final GroupRepository groupRps;

	public Group getEntityOrThrowBadRequest(String id) {
		return groupRps.findById(id)
				.orElseThrow(() -> new BadBusinessException(ErrorCode.GROUP_NOT_FOUND.name(),
						Map.of("id", id)));
	}

}
