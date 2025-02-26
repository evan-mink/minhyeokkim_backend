package com.example.remittance.common.model.base;

import java.io.Serializable;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.example.remittance.common.util.DateUtil;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * 공통 entity class
 * - 모든 entity는 해당 클래스를 상속받아 구현합니다.
 * @author evan.m.kim
 */
@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseEntity implements Serializable {
	@Column(name = "reg_date")
	@Schema(description = "등록일자",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String regDate;

	@Column(name = "upt_date")
	@Schema(description = "수정일자",
			accessMode = Schema.AccessMode.READ_ONLY)
	private String uptDate;

	@PrePersist
	public void prePersist() {
		this.regDate = DateUtil.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.uptDate = DateUtil.now();
	}
}
