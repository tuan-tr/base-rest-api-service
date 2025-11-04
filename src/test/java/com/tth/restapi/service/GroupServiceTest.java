package com.tth.restapi.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.tth.common.exception.DataNotFoundException;
import com.tth.persistence.constant.GroupStatus;
import com.tth.persistence.entity.Group;
import com.tth.persistence.entity.User;
import com.tth.persistence.entity.UserGroup;
import com.tth.persistence.repository.GroupRepository;
import com.tth.persistence.repository.UserGroupRepository;
import com.tth.persistence.repository.UserRepository;
import com.tth.restapi.dto.group.GroupAddUserInput;
import com.tth.restapi.dto.group.GroupCreateInput;
import com.tth.restapi.dto.group.GroupDto;
import com.tth.restapi.dto.group.GroupRemoveUserInput;
import com.tth.restapi.dto.group.GroupUpdateInput;
import com.tth.restapi.dto.group.UserGroupDto;
import com.tth.restapi.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
	@Mock private GroupProviderService groupPrvService;
	@Mock private GroupRepository groupRps;
	@Mock private UserGroupRepository userGroupRps;
	@Mock private UserRepository userRps;
	@InjectMocks private GroupService groupService;

	private final String GROUP_ID_1 = "groupId1";
	private final String USER_ID_1 = "userId1";
	private final String USER_ID_2 = "userId2";
	private final String USER_ID_3 = "userId3";
	private final String USER_ID_4 = "userId4";

	@Test
	void create_success_withoutUsers() {
		GroupCreateInput input = GroupCreateInput.builder()
				.status(GroupStatus.ACTIVE)
				.name("Group 1")
				.userIds(Set.of(USER_ID_1, USER_ID_2))
				.build();
	
		when(groupRps.save(any(Group.class))).thenAnswer(invocation -> {
			Group group = invocation.getArgument(0);
			group.setId(GROUP_ID_1);
			return group;
		});

		GroupDto dto = groupService.create(input);

		assertEquals(GROUP_ID_1, dto.getId());
	}

	@Test
	void create_success_withUsers() {
		GroupCreateInput input = GroupCreateInput.builder()
				.status(GroupStatus.ACTIVE)
				.name("Group 1")
				.userIds(Set.of(USER_ID_1, USER_ID_2))
				.build();
	
		User user1 = User.builder().id(USER_ID_1).build();
		User user2 = User.builder().id(USER_ID_2).build();
		
		when(userRps.findByIdIn(input.getUserIds())).thenReturn(List.of(user1, user2));
		when(groupRps.save(any(Group.class))).thenAnswer(invocation -> {
			Group group = invocation.getArgument(0);
			group.setId(GROUP_ID_1);
			return group;
		});

		GroupDto dto = groupService.create(input);

		assertEquals(GROUP_ID_1, dto.getId());
	}

	@Test
	void update_success() {
		GroupUpdateInput input = GroupUpdateInput.builder()
				.name("updated name")
				.status(GroupStatus.INACTIVE)
				.build();
		Group group = Group.builder().id(GROUP_ID_1).build();
		
		when(groupPrvService.getEntityOrThrowBadRequest(GROUP_ID_1)).thenReturn(group);
		
		groupService.update(GROUP_ID_1, input);
		
		assertEquals(input.getName(), group.getName());
		assertEquals(input.getStatus(), group.getStatus());
	}

	@Test
	void addUsers_success() {
		GroupAddUserInput input = new GroupAddUserInput(Set.of(USER_ID_2, USER_ID_3));
		Group group = Group.builder().id(GROUP_ID_1).build();
		UserGroup userGroup1 = UserGroup.builder().userId(USER_ID_1).build();
		UserGroup userGroup2 = UserGroup.builder().userId(USER_ID_2).build();
		User user3 = User.builder().id(USER_ID_3).build();
		
		when(groupPrvService.getEntityOrThrowBadRequest(GROUP_ID_1)).thenReturn(group);
		when(userGroupRps.findByGroupIdAndUserIdIn(GROUP_ID_1, input.getUserIds()))
				.thenReturn(List.of(userGroup1, userGroup2));
		when(userRps.findByIdIn(List.of(USER_ID_3))).thenReturn(List.of(user3));
		
		groupService.addUsers(GROUP_ID_1, input);
		
		verify(userGroupRps).saveAll(anyList());
	}

	@Test
	void removeUsers_success() {
		GroupRemoveUserInput input = new GroupRemoveUserInput(Set.of(USER_ID_2, USER_ID_3, USER_ID_4));
		Group group = Group.builder().id(GROUP_ID_1).build();
		UserGroup userGroup2 = UserGroup.builder().userId(USER_ID_2).build();
		
		when(groupPrvService.getEntityOrThrowBadRequest(GROUP_ID_1)).thenReturn(group);
		when(userGroupRps.findByGroupIdAndUserIdIn(GROUP_ID_1, input.getUserIds())).thenReturn(List.of(userGroup2));
		
		groupService.removeUsers(GROUP_ID_1, input);
		
		verify(userGroupRps).deleteAll(List.of(userGroup2));
	}

	@Test
	void getDetail_throw_GROUP_NOT_FOUND() {
		when(groupRps.findById(GROUP_ID_1)).thenReturn(Optional.empty());
		
		DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> groupService.getDetail(GROUP_ID_1));
		
		assertEquals(ErrorCode.GROUP_NOT_FOUND.name(), ex.getCode());
	}

	@Test
	void getDetail_success() {
		Group group = Group.builder().id(GROUP_ID_1).build();
		
		when(groupRps.findById(GROUP_ID_1)).thenReturn(Optional.of(group));
		
		GroupDto dto = groupService.getDetail(GROUP_ID_1);
		
		assertEquals(GROUP_ID_1, dto.getId());
	}

	@Test
	void search_success() {
		Pageable pageable = PageRequest.of(0, 10);
		Group group = Group.builder().id(GROUP_ID_1).build();
		
		when(groupRps.findAll(any(Predicate.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(group), pageable, 1));
		
		Page<GroupDto> page = groupService.search(mock(Predicate.class), pageable);
		
		assertEquals(1, page.getTotalElements());
	}

	@Test
	void searchUser_success() {
		Pageable pageable = PageRequest.of(0, 10);
		UserGroup userGroup1 = UserGroup.builder().userId(USER_ID_1).user(User.builder().build()).build();
		UserGroup userGroup2 = UserGroup.builder().userId(USER_ID_2).user(User.builder().build()).build();
		
		when(userGroupRps.findAll(any(BooleanBuilder.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(userGroup1, userGroup2), pageable, 2));
		
		Page<UserGroupDto> page = groupService.searchUser(GROUP_ID_1, mock(Predicate.class), pageable);
		
		assertEquals(2, page.getTotalElements());
	}
}
