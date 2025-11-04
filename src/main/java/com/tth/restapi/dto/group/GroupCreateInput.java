package com.tth.restapi.dto.group;

import com.tth.persistence.constant.GroupStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GroupCreateInput {

	@NotNull
	private GroupStatus status;

	@NotBlank
	private String name;

	private Set<String> userIds;

}
