package com.tth.restapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public abstract class AuditDto {

	private OffsetDateTime createdAt;

	private String createdBy;

	private OffsetDateTime updatedAt;

	private String updatedBy;

}
