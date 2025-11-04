package com.tth.restapi.controller;

import com.querydsl.core.types.Predicate;
import com.tth.common.http.PageResponseBody;
import com.tth.common.http.SuccessfulResponseBody;
import com.tth.persistence.entity.Group;
import com.tth.persistence.entity.UserGroup;
import com.tth.restapi.constant.Sortable;
import com.tth.restapi.dto.group.GroupAddUserInput;
import com.tth.restapi.dto.group.GroupCreateInput;
import com.tth.restapi.dto.group.GroupDto;
import com.tth.restapi.dto.group.GroupRemoveUserInput;
import com.tth.restapi.dto.group.GroupUpdateInput;
import com.tth.restapi.dto.group.UserGroupDto;
import com.tth.restapi.service.GroupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "group")
@AllArgsConstructor
public class GroupController {

	private GroupService groupService;

	@PostMapping
	public SuccessfulResponseBody<GroupDto> create(@RequestBody @Valid GroupCreateInput input) {
		return new SuccessfulResponseBody<>(groupService.create(input));
	}

	@PutMapping("{id}")
	public void update(@PathVariable String id, @RequestBody @Valid GroupUpdateInput input) {
		groupService.update(id, input);
	}

	@PostMapping("{id}/user")
	public void addUsers(@PathVariable String id, @RequestBody @Valid GroupAddUserInput input) {
		groupService.addUsers(id, input);
	}

	@DeleteMapping("{id}/user")
	public void removeUsers(@PathVariable String id, @RequestBody @Valid GroupRemoveUserInput input) {
		groupService.removeUsers(id, input);
	}

	@GetMapping("{id}")
	public SuccessfulResponseBody<GroupDto> get(@PathVariable String id) {
		return new SuccessfulResponseBody<>(groupService.getDetail(id));
	}

	@GetMapping("querydsl")
	public PageResponseBody<GroupDto> search(
			@QuerydslPredicate(root = Group.class) Predicate predicate,
			@SortDefault(sort = Sortable.UPDATED_AT, direction = Sort.Direction.DESC) Pageable pageable
	) {
		return new PageResponseBody<>(groupService.search(predicate, pageable));
	}

	@GetMapping("{id}/user/querydsl")
	public PageResponseBody<UserGroupDto> searchUser(@PathVariable String id,
			@QuerydslPredicate(root = UserGroup.class) Predicate predicate,
			@SortDefault(sort = Sortable.UPDATED_AT, direction = Sort.Direction.DESC) Pageable pageable
	) {
		return new PageResponseBody<>(groupService.searchUser(id, predicate, pageable));
	}

}
