package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.master.repository.TenantRepository;
import com.sqool.sqoolbus.tenant.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TenantResolutionService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Value("${sqoolbus.multitenancy.default-tenant}")
    private String defaultTenant;

    public String resolveTenantIdForSchool(Long schoolId) {
        String originalTenant = TenantContext.getTenantId();
        try {
            TenantContext.clear();

            if (schoolId == null) {
                return defaultTenant;
            }

            List<String> tenantIds = new ArrayList<>(tenantRepository.findAllActiveTenantIds());
            if (!tenantIds.contains(defaultTenant)) {
                tenantIds.add(0, defaultTenant);
            }

            for (String tenantId : tenantIds) {
                TenantContext.setTenantId(tenantId);
                if (schoolRepository.existsById(schoolId)) {
                    return tenantId;
                }
            }

            return null;
        } finally {
            TenantContext.clear();
            if (originalTenant != null && !originalTenant.isBlank()) {
                TenantContext.setTenantId(originalTenant);
            }
        }
    }
}
