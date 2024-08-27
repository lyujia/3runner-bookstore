package com.nhnacademy.bookstore.purchase.pointpolicy.service.impl;

import com.nhnacademy.bookstore.entity.pointpolicy.PointPolicy;
import com.nhnacademy.bookstore.purchase.pointpolicy.dto.PointPolicyResponseRequest;
import com.nhnacademy.bookstore.purchase.pointpolicy.repository.PointPolicyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PointPolicyServiceImplTest {

    @InjectMocks
    private PointPolicyServiceImpl pointPolicyService;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        String policyName = "Test Policy";
        Integer policyValue = 10;
        PointPolicy pointPolicy = new PointPolicy(policyName, policyValue);

        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(pointPolicy);

        Long savedId = pointPolicyService.save(policyName, policyValue);

        assertThat(savedId).isEqualTo(pointPolicy.getId());
        verify(pointPolicyRepository, times(1)).save(any(PointPolicy.class));
    }

    @Test
    void testUpdate_PolicyExists() {
        String policyName = "Test Policy";
        Integer policyValue = 20;
        PointPolicy pointPolicy = new PointPolicy(policyName, policyValue);
        pointPolicy.setPolicyValue(policyValue);

        when(pointPolicyRepository.findByPolicyName(anyString())).thenReturn(Optional.of(pointPolicy));

        Long updatedId = pointPolicyService.update(policyName, policyValue);

        assertThat(updatedId).isEqualTo(pointPolicy.getId());
        assertThat(pointPolicy.getPolicyValue()).isEqualTo(policyValue);
        verify(pointPolicyRepository, times(1)).save(pointPolicy);
    }

    @Test
    void testUpdate_PolicyDoesNotExist() {
        String policyName = "New Policy";
        Integer policyValue = 30;
        PointPolicy pointPolicy = new PointPolicy(policyName, policyValue);

        when(pointPolicyRepository.findByPolicyName(anyString())).thenReturn(Optional.empty());
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(pointPolicy);

        Assertions.assertThrows(IllegalStateException.class, ()->{

            Long updatedId = pointPolicyService.update(policyName, policyValue);
        });
    }

    @Test
    void testReadAll() {
        PointPolicy policy1 = new PointPolicy("Policy1", 10);
        PointPolicy policy2 = new PointPolicy("Policy2", 20);

        when(pointPolicyRepository.findAll()).thenReturn(Arrays.asList(policy1, policy2));

        List<PointPolicyResponseRequest> policies = pointPolicyService.readAll();

        assertThat(policies).hasSize(2);
        assertThat(policies.get(0).policyKey()).isEqualTo("Policy1");
        assertThat(policies.get(1).policyKey()).isEqualTo("Policy2");
    }

    @Test
    void testRead() {
        String policyName = "Test Policy";
        Integer policyValue = 10;
        PointPolicy pointPolicy = new PointPolicy(policyName, policyValue);

        when(pointPolicyRepository.findByPolicyName(anyString())).thenReturn(Optional.of(pointPolicy));

        PointPolicyResponseRequest response = pointPolicyService.read(policyName);

        assertThat(response.policyKey()).isEqualTo(policyName);
        assertThat(response.policyValue()).isEqualTo(policyValue);
    }
}