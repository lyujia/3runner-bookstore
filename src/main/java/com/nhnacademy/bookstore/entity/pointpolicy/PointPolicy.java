package com.nhnacademy.bookstore.entity.pointpolicy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PointPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String policyName;
	private Integer policyValue;

	public PointPolicy(String policyName, Integer policyValue) {
		this.policyName = policyName;
		this.policyValue = policyValue;
	}
}
