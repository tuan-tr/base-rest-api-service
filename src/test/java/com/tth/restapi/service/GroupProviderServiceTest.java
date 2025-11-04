package com.tth.restapi.service;

import com.tth.common.exception.BadBusinessException;
import com.tth.persistence.entity.Group;
import com.tth.persistence.repository.GroupRepository;
import com.tth.restapi.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupProviderServiceTest {
	@Mock private GroupRepository groupRepo;
	@InjectMocks private GroupProviderService groupPrService;

	private final String GROUP_ID_1 = "groupId1";

	@Test
	void getEntity_success() {
		Group group = Group.builder().id(GROUP_ID_1).build();
		when(groupRepo.findById(GROUP_ID_1)).thenReturn(Optional.of(group));
		
		Group result = groupPrService.getEntityOrThrowBadRequest(GROUP_ID_1);
		
		assertEquals(GROUP_ID_1, result.getId());
	}

	@Test
	void getEntity_throw_GROUP_NOT_FOUND() {
		when(groupRepo.findById(GROUP_ID_1)).thenReturn(Optional.empty());
		
		BadBusinessException ex = assertThrows(BadBusinessException.class,
				() -> groupPrService.getEntityOrThrowBadRequest(GROUP_ID_1));
		
		assertEquals(ErrorCode.GROUP_NOT_FOUND.name(), ex.getCode());
	}
}
