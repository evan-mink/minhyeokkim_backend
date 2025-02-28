package com.example.remittance.valid.model;

/**
 * @author evan.m.kim
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ValidDto {
	@Size(min = 2, max = 30, message = "이름은 2~30자 사이여야 합니다.")
	@NotNull(message = "이름은 필수 입력값입니다.")
	private String name;

	@Min(value = 18, message = "나이는 최소 18세 이상이어야 합니다.")
	private int age;

	@Email(message = "이메일 형식이 올바르지 않습니다.")
	@NotBlank(message = "이메일은 필수 입력값입니다.")
	private String email;
}
