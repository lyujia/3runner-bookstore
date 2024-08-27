package com.nhnacademy.bookstore.purchase.pointpolicy.repository;

import com.nhnacademy.bookstore.entity.pointpolicy.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
    Optional<PointPolicy> findByPolicyName(String policyName);
}
