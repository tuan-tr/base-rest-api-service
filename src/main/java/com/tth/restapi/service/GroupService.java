package com.tth.restapi.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.tth.common.exception.DataNotFoundException;
import com.tth.persistence.entity.Group;
import com.tth.persistence.entity.QUserGroup;
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
import com.tth.restapi.projector.GroupProjector;
import com.tth.restapi.projector.UserGroupProjector;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
	private final GroupProviderService groupPrvService;
	private final GroupRepository groupRps;
	private final UserGroupRepository userGroupRps;
	private final UserRepository userRps;

	@Transactional
	public GroupDto create(GroupCreateInput input) {
		Group entity = Group.builder()
				.status(input.getStatus())
				.name(input.getName())
				.build();
		
		if (CollectionUtils.isNotEmpty(input.getUserIds())) {
			List<User> users = userRps.findByIdIn(input.getUserIds());
			List<UserGroup> userGroups = users.stream()
					.map(e -> UserGroup.builder()
							.user(e)
							.group(entity)
							.build())
					.collect(Collectors.toList());
			
			entity.setUserGroups(userGroups);
		}
		
		groupRps.save(entity);
		return GroupDto.builder()
				.id(entity.getId())
				.build();
	}

	@Transactional
	public void update(String id, GroupUpdateInput input) {
		Group entity = groupPrvService.getEntityOrThrowBadRequest(id);
		entity.setStatus(input.getStatus());
		entity.setName(input.getName());
	}

	@Transactional
	public void addUsers(String id, GroupAddUserInput input) {
		Group entity = groupPrvService.getEntityOrThrowBadRequest(id);
		
		List<UserGroup> existingUserGroups = userGroupRps.findByGroupIdAndUserIdIn(id, input.getUserIds());
		Set<String> existingUserIds = existingUserGroups.stream().map(e -> e.getUserId()).collect(Collectors.toSet());
		
		Collection<String> newUserIds = CollectionUtils.removeAll(input.getUserIds(), existingUserIds);
		List<User> users = userRps.findByIdIn(newUserIds);
		
		List<UserGroup> userGroups = users.stream()
				.map(e -> UserGroup.builder()
						.user(e)
						.group(entity)
						.build())
				.collect(Collectors.toList());
		
		userGroupRps.saveAll(userGroups);
	}

	@Transactional
	public void removeUsers(String id, GroupRemoveUserInput input) {
		groupPrvService.getEntityOrThrowBadRequest(id);
		
		List<UserGroup> userGroups = userGroupRps.findByGroupIdAndUserIdIn(id, input.getUserIds());
		
		userGroupRps.deleteAll(userGroups);
	}

	@Transactional(readOnly = true)
	public GroupDto getDetail(String id) {
		Group entity = groupRps.findById(id)
				.orElseThrow(() -> new DataNotFoundException(ErrorCode.GROUP_NOT_FOUND.name(),
						Map.of("id", id)));
		
		GroupDto dto = GroupProjector.toDetailDto(entity);
		return dto;
	}

	@Transactional(readOnly = true)
	public Page<GroupDto> search(Predicate predicate, Pageable pageable) {
		Page<Group> page = groupRps.findAll(predicate, pageable);
		List<GroupDto> content = GroupProjector.toSearchDto(page.getContent());
		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	@Transactional(readOnly = true)
	public Page<UserGroupDto> searchUser(String groupId, Predicate predicate, Pageable pageable) {
		QUserGroup qType = QUserGroup.userGroup;
		BooleanBuilder booleanBuilder = new BooleanBuilder()
				.and(qType.groupId.eq(groupId))
				.and(predicate);
		
		Page<UserGroup> page = userGroupRps.findAll(booleanBuilder, pageable);
		List<UserGroupDto> content = UserGroupProjector.toSearchDto(page.getContent());
		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

}
