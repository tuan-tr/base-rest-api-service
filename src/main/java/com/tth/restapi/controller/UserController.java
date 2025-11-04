package com.tth.restapi.controller;

import com.querydsl.core.types.Predicate;
import com.tth.common.http.PageResponseBody;
import com.tth.common.http.SuccessfulResponseBody;
import com.tth.common.util.SortableProvider;
import com.tth.persistence.constant.UserStatus;
import com.tth.persistence.entity.User;
import com.tth.persistence.provider.filter.UserFilter;
import com.tth.restapi.config.auth.AuthorityScope;
import com.tth.restapi.constant.Sortable;
import com.tth.restapi.dto.user.UserCreateInput;
import com.tth.restapi.dto.user.UserDto;
import com.tth.restapi.dto.user.UserUpdateInput;
import com.tth.restapi.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Set;

@RestController
@RequestMapping(value = "user")
@AllArgsConstructor
public class UserController {

	private UserService userService;

	@PostMapping
	public SuccessfulResponseBody<UserDto> create(@RequestBody @Valid UserCreateInput input) {
		return new SuccessfulResponseBody<>(userService.create(input));
	}

	@PutMapping("{id}")
	public void update(@PathVariable String id, @RequestBody @Valid UserUpdateInput input) {
		userService.update(id, input);
	}

	@GetMapping("{id}")
	public SuccessfulResponseBody<UserDto> get(@PathVariable String id) {
		return new SuccessfulResponseBody<>(userService.getDetail(id));
	}

	@GetMapping
	@PreAuthorize("hasAuthority('" + AuthorityScope.DEFAULT + "')")
	public PageResponseBody<UserDto> searchPaging(
			@RequestParam(required = false) Set<String> ids,
			@RequestParam(required = false) Set<UserStatus> statuses,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime effectiveStart,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime effectiveEnd,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdTo,
			@RequestParam(required = false) String keyword,
			@SortDefault(sort = Sortable.UPDATED_AT, direction = Sort.Direction.DESC) Pageable pageable
	) {
		SortableProvider.validate(pageable.getSort(), Sortable.USER);
		UserFilter filter = UserFilter.builder()
				.ids(ids)
				.statuses(statuses)
				.name(name)
				.effectiveStart(effectiveStart)
				.effectiveEnd(effectiveEnd)
				.createdFrom(createdFrom)
				.createdTo(createdTo)
				.keyword(keyword)
				.build();
		
		return new PageResponseBody<>(userService.searchPaging(filter, pageable));
	}

	@GetMapping("querydsl")
	public PageResponseBody<UserDto> search(
			@QuerydslPredicate(root = User.class) Predicate predicate,
			@SortDefault(sort = Sortable.UPDATED_AT, direction = Sort.Direction.DESC) Pageable pageable
	) {
		return new PageResponseBody<>(userService.search(predicate, pageable));
	}

}
