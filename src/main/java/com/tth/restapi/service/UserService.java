package com.tth.restapi.service;

import com.querydsl.core.types.Predicate;
import com.tth.common.exception.BadBusinessException;
import com.tth.common.exception.DataNotFoundException;
import com.tth.persistence.constant.UserStatus;
import com.tth.persistence.entity.User;
import com.tth.persistence.provider.filter.UserFilter;
import com.tth.persistence.repository.UserRepository;
import com.tth.restapi.dto.user.UserCreateInput;
import com.tth.restapi.dto.user.UserDto;
import com.tth.restapi.dto.user.UserUpdateInput;
import com.tth.restapi.exception.ErrorCode;
import com.tth.restapi.projector.UserProjector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepo;

	public User getEntityOrThrowBadRequest(String id) {
		return userRepo.findById(id)
				.orElseThrow(() -> new BadBusinessException(ErrorCode.USER_NOT_FOUND.name(),
						Map.of("id", id)));
	}

	@Transactional
	public UserDto create(UserCreateInput input) {
		User entity = User.builder()
				.status(UserStatus.ACTIVE)
				.name(input.getName())
				.effectiveStart(input.getEffectiveStart())
				.effectiveEnd(input.getEffectiveEnd())
				.build();
		
		userRepo.save(entity);
		
		return UserDto.builder()
				.id(entity.getId())
				.build();
	}

	@Transactional
	public void update(String id, UserUpdateInput input) {
		User entity = getEntityOrThrowBadRequest(id);
		entity.setName(input.getName());
		entity.setStatus(input.getStatus());
		entity.setEffectiveStart(input.getEffectiveStart());
		entity.setEffectiveEnd(input.getEffectiveEnd());
	}

	@Transactional(readOnly = true)
	public UserDto getDetail(String id) {
		User entity = userRepo.findById(id)
				.orElseThrow(() -> new DataNotFoundException(ErrorCode.USER_NOT_FOUND.name(),
						Map.of("id", id)));
		
		UserDto dto = UserProjector.toDetailDto(entity);
		return dto;
	}

	@Transactional(readOnly = true)
	public Page<UserDto> searchPaging(UserFilter filter, Pageable pageable) {
		Page<User> page = userRepo.findPaging(filter, pageable);
		List<UserDto> content = UserProjector.toSearchDto(page.getContent());
		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	@Transactional(readOnly = true)
	public Page<UserDto> search(Predicate predicate, Pageable pageable) {
		Page<User> page = userRepo.findAll(predicate, pageable);
		List<UserDto> content = UserProjector.toSearchDto(page.getContent());
		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

}
